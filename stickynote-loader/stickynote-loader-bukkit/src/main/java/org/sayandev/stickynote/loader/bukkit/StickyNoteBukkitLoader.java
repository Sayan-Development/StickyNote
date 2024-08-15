package org.sayandev.stickynote.loader.bukkit;

import com.alessiodp.libby.BukkitLibraryManager;
import com.alessiodp.libby.Library;
import com.alessiodp.libby.LibraryManager;
import com.alessiodp.libby.PaperLibraryManager;
import com.alessiodp.libby.logging.LogLevel;
import org.bukkit.plugin.java.JavaPlugin;
import org.sayandev.common.Dependency;
import org.sayandev.stickynote.bukkit.WrappedStickyNotePlugin;

import java.io.UncheckedIOException;
import java.nio.file.FileSystemException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class StickyNoteBukkitLoader {

    private static final ConcurrentHashMap<String, CompletableFuture<Void>> loadingLibraries = new ConcurrentHashMap<>();
    private static final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void load(JavaPlugin plugin) {
        plugin.getLogger().info("Loading dependencies. This may take a while if it's the first time loading the plugin.");

        long startTime = System.currentTimeMillis();

        try {
            Class<?> stickyNotes = Class.forName("org.sayandev.stickynote.generated.StickyNotes");
            List<Dependency> dependencies = getDependencies(stickyNotes);
            List<String> repositories = getRepositories(stickyNotes);

            Object relocation = stickyNotes.getField("RELOCATION").get(stickyNotes);
            String relocationFrom = (String) relocation.getClass().getMethod("getFrom").invoke(relocation);
            String relocationTo = (String) relocation.getClass().getMethod("getTo").invoke(relocation);

            LibraryManager libraryManager = getLibraryManager(plugin);
            libraryManager.setLogLevel(LogLevel.WARN);
            libraryManager.addRepository("https://repo.sayandev.org/snapshots");
            libraryManager.addMavenLocal();

            Map<String, String> manualRelocations = getManualRelocations();

            List<CompletableFuture<Void>> futures = dependencies.stream()
                    .map(dependency -> loadDependencyAsync(plugin.getLogger(), dependency, relocationFrom, relocationTo, manualRelocations, libraryManager))
                    .toList();

            CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

            scheduler.scheduleAtFixedRate(() -> {
                long completedCount = futures.stream().filter(CompletableFuture::isDone).count();
                double percentage = (completedCount / (double) dependencies.size()) * 100;
                plugin.getLogger().info(String.format("Loading progress: %.2f%%", percentage));
            }, 5, 5, TimeUnit.SECONDS);

            /*CompletableFuture<Void> delay = CompletableFuture.runAsync(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });*/

            CompletableFuture.allOf(allOf/*, delay*/).join();

            long endTime = System.currentTimeMillis();
            plugin.getLogger().info("Loaded " + dependencies.size() + " dependencies in " + (endTime - startTime) + " ms.");

            new WrappedStickyNotePlugin(plugin);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
            scheduler.shutdown();
        }
    }

    private static List<Dependency> getDependencies(Class<?> stickyNotes) {
        return Arrays.stream(stickyNotes.getFields())
                .filter(field -> field.getName().startsWith("DEPENDENCY_"))
                .map(field -> {
                    try {
                        Object dependencyObject = field.get(null);
                        Class<?> dependencyFieldClass = dependencyObject.getClass();
                        return new Dependency(
                                (String) dependencyFieldClass.getMethod("getGroup").invoke(dependencyObject),
                                (String) dependencyFieldClass.getMethod("getName").invoke(dependencyObject),
                                (String) dependencyFieldClass.getMethod("getVersion").invoke(dependencyObject)
                        );
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }).toList();
    }

    private static List<String> getRepositories(Class<?> stickyNotes) {
        return Arrays.stream(stickyNotes.getFields())
                .filter(field -> field.getName().startsWith("REPOSITORY_"))
                .map(field -> {
                    try {
                        return (String) field.get(null);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }).toList();
    }

    private static LibraryManager getLibraryManager(JavaPlugin plugin) {
        if (plugin.getResource("paper-plugin.yml") != null) {
            plugin.getLogger().info("paper-plugin detected, using paper library loader...");
            return new PaperLibraryManager(plugin);
        } else {
            return new BukkitLibraryManager(plugin);
        }
    }

    private static Map<String, String> getManualRelocations() {
        Map<String, String> manualRelocations = new HashMap<>();
        manualRelocations.put("com.github.patheloper.pathetic", "patheloper");
        manualRelocations.put("com.github.cryptomorin", "cryptomorin");
        manualRelocations.put("com.google.code.gson", "gson");
        return manualRelocations;
    }

    private static CompletableFuture<Void> loadDependencyAsync(Logger logger, Dependency dependency, String relocationFrom, String relocationTo, Map<String, String> manualRelocations, LibraryManager libraryManager) {
        return loadingLibraries.computeIfAbsent(dependency.getGroup() + ":" + dependency.getName(), key -> CompletableFuture.runAsync(() -> {
            try {
                Library.Builder libraryBuilder = createLibraryBuilder(dependency, dependency.getGroup(), dependency.getName(), relocationFrom, relocationTo, manualRelocations);
                Library library = libraryBuilder.build();
                retryWithDelay(() -> {
                    libraryManager.downloadLibrary(library);
                    libraryManager.loadLibrary(library);
                }, 3, 1000);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, executorService));
    }

    private static void retryWithDelay(Runnable task, int maxRetries, long delayMillis) throws Exception {
        int attempt = 0;
        while (attempt < maxRetries) {
            try {
                task.run();
                return;
            } catch (UncheckedIOException e) {
                if (e.getCause() instanceof FileSystemException) {
                    attempt++;
                    if (attempt >= maxRetries) {
                        throw e;
                    }
                    Thread.sleep(delayMillis);
                } else {
                    throw e;
                }
            }
        }
    }

    private static Library.Builder createLibraryBuilder(Dependency dependency, String group, String name, String relocationFrom, String relocationTo, Map<String, String> manualRelocations) throws Exception {
        Library.Builder libraryBuilder = Library.builder()
                .groupId(group)
                .artifactId(name)
                .version(dependency.getVersion());

        if (!name.contains("stickynote") && !name.equals("kotlin-stdlib") && !name.equals("kotlin-reflect")) {
            if (manualRelocations.containsKey(group)) {
                libraryBuilder.relocate(group, relocationTo + "{}libs{}" + manualRelocations.get(group));
            } else {
                String replacedGroup = group.replace("{}", ".");
                String[] groupParts = replacedGroup.split("\\.");
                libraryBuilder.relocate(group, relocationTo + "{}libs{}" + groupParts[groupParts.length - 1]);
            }
        }
        if (name.contains("stickynote")) {
            libraryBuilder.relocate(relocationFrom, relocationTo);
        } else {
            libraryBuilder.resolveTransitiveDependencies(true);
        }
        return libraryBuilder;
    }
}