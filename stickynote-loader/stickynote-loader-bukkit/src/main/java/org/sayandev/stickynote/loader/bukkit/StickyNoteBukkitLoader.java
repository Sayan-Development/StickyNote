package org.sayandev.stickynote.loader.bukkit;

import com.alessiodp.libby.BukkitLibraryManager;
import com.alessiodp.libby.Library;
import com.alessiodp.libby.LibraryManager;
import com.alessiodp.libby.PaperLibraryManager;
import com.alessiodp.libby.logging.LogLevel;
import org.bukkit.plugin.java.JavaPlugin;
import org.sayandev.stickynote.bukkit.WrappedStickyNotePlugin;

import java.io.UncheckedIOException;
import java.nio.file.FileSystemException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class StickyNoteBukkitLoader {

    private static final ConcurrentHashMap<String, CompletableFuture<Void>> loadingLibraries = new ConcurrentHashMap<>();
    private static final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void load(JavaPlugin plugin) {
        plugin.getLogger().info("Loading dependencies. This may take a while if it's the first time loading the plugin.");

        long startTime = System.currentTimeMillis();

        try {
            Class<?> stickyNotes = Class.forName("org.sayandev.stickynote.generated.StickyNotes");
            List<Object> dependencies = getDependencies(stickyNotes);
            List<String> repositories = getRepositories(stickyNotes);

            Object relocation = stickyNotes.getField("RELOCATION").get(stickyNotes);
            String relocationFrom = (String) relocation.getClass().getMethod("getFrom").invoke(relocation);
            String relocationTo = (String) relocation.getClass().getMethod("getTo").invoke(relocation);

            LibraryManager libraryManager = getLibraryManager(plugin);
            libraryManager.setLogLevel(LogLevel.WARN);
            libraryManager.addRepository("https://repo.sayandev.org/snapshots");

            Map<String, String> manualRelocations = getManualRelocations();

            List<CompletableFuture<Void>> futures = dependencies.stream()
                    .map(dependency -> loadDependencyAsync(plugin.getLogger(), dependency, relocationFrom, relocationTo, manualRelocations, libraryManager))
                    .collect(Collectors.toList());

            CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

            scheduler.scheduleAtFixedRate(() -> {
                long completedCount = futures.stream().filter(CompletableFuture::isDone).count();
                double percentage = (completedCount / (double) dependencies.size()) * 100;
                plugin.getLogger().info(String.format("Loading progress: %.2f%%", percentage));
            }, 0, 5, TimeUnit.SECONDS);

            CompletableFuture<Void> delay = CompletableFuture.runAsync(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            CompletableFuture.allOf(allOf, delay).join();

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

    private static List<Object> getDependencies(Class<?> stickyNotes) {
        return Arrays.stream(stickyNotes.getFields())
                .filter(field -> field.getName().startsWith("DEPENDENCY_"))
                .map(field -> {
                    try {
                        return field.get(null);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
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

    private static CompletableFuture<Void> loadDependencyAsync(Logger logger, Object dependency, String relocationFrom, String relocationTo, Map<String, String> manualRelocations, LibraryManager libraryManager) {
        String group;
        String name;
        try {
            group = ((String) dependency.getClass().getMethod("getGroup").invoke(dependency)).replace(".", "{}");
            name = (String) dependency.getClass().getMethod("getName").invoke(dependency);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String finalGroup = group;
        String finalName = name;
        return loadingLibraries.computeIfAbsent(group + ":" + name, key -> CompletableFuture.runAsync(() -> {
            try {
                Library.Builder libraryBuilder = createLibraryBuilder(dependency, finalGroup, finalName, relocationFrom, relocationTo, manualRelocations);
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

    private static Library.Builder createLibraryBuilder(Object dependency, String group, String name, String relocationFrom, String relocationTo, Map<String, String> manualRelocations) throws Exception {
        Library.Builder libraryBuilder = Library.builder()
                .groupId(group)
                .artifactId(name)
                .version((String) dependency.getClass().getMethod("getVersion").invoke(dependency));

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