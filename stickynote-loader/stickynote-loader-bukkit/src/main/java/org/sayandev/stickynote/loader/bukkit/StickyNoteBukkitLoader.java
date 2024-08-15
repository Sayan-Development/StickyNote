package org.sayandev.stickynote.loader.bukkit;

import com.alessiodp.libby.BukkitLibraryManager;
import com.alessiodp.libby.Library;
import com.alessiodp.libby.LibraryManager;
import com.alessiodp.libby.PaperLibraryManager;
import com.alessiodp.libby.logging.LogLevel;
import com.alessiodp.libby.transitive.TransitiveDependencyHelper;
import org.bukkit.plugin.java.JavaPlugin;
import org.sayandev.loader.common.Dependency;
import org.sayandev.stickynote.bukkit.WrappedStickyNotePlugin;

import java.io.File;
import java.io.UncheckedIOException;
import java.nio.file.FileSystemException;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class StickyNoteBukkitLoader {

    private static final ConcurrentHashMap<String, CompletableFuture<Void>> loadingLibraries = new ConcurrentHashMap<>();
    private static final ExecutorService executorService = Executors.newWorkStealingPool(Runtime.getRuntime().availableProcessors());
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void load(JavaPlugin plugin) {
        File[] files = new File(plugin.getDataFolder(), "lib").listFiles();
        if (files == null || !Arrays.stream(files).map(File::getName).toList().contains("lib")) {
            plugin.getLogger().info("Initializing first-time setup.. This may take up to a minute depending on your connection.");
        } else {
            plugin.getLogger().info("Loading libraries... this might take a few seconds.");
        }

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
            repositories.forEach(libraryManager::addRepository);
            TransitiveDependencyHelper transitiveDependencyHelper = new TransitiveDependencyHelper(libraryManager, new File(plugin.getDataFolder(), "lib").toPath());

            DependencyCache dependencyCache = new DependencyCache(plugin.getDataFolder());
            Set<Dependency> cachedDependencies = dependencyCache.loadCache();
            Set<Dependency> missingDependencies = new HashSet<>(dependencies);
            missingDependencies.removeIf(missingDependency -> cachedDependencies.stream().map(dependency -> dependency.getGroup() + ":" + dependency.getName()).toList().contains(missingDependency.getGroup() + ":" + missingDependency.getName()));

            if (!missingDependencies.isEmpty()) {
                List<CompletableFuture<Void>> resolveFutures = missingDependencies.stream()
                        .map(dependency -> resolveTransitiveDependenciesAsync(transitiveDependencyHelper, plugin, libraryManager, dependency))
                        .toList();

                CompletableFuture<Void> resolveAll = CompletableFuture.allOf(resolveFutures.toArray(new CompletableFuture[0]));

                // Schedule progress logging every 5 seconds
                scheduler.scheduleAtFixedRate(() -> logProgress(plugin.getLogger(), resolveFutures, dependencies.size()), 3, 5, TimeUnit.SECONDS);

                resolveAll.thenRunAsync(() -> {
                    dependencyCache.saveCache(new HashSet<>(dependencies));

                    List<CompletableFuture<Void>> futures = missingDependencies.stream()
                            .map(dependency -> {
                                List<CompletableFuture<Void>> loadingFutures = new ArrayList<>();
                                for (Dependency transitiveDependency : dependency.getTransitiveDependencies()) {
                                    loadingFutures.add(loadDependencyAsync(plugin, plugin.getLogger(), transitiveDependency, relocationFrom, relocationTo, libraryManager));
                                }
                                loadingFutures.add(loadDependencyAsync(plugin, plugin.getLogger(), dependency, relocationFrom, relocationTo, libraryManager));
                                return loadingFutures;
                            }).flatMap(Collection::stream).toList();

                    CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
                    allOf.join();
                }, executorService).join();
            } else {
                plugin.getLogger().info("Library cache found, loading cached libraries...");
                cachedDependencies.forEach(dependency -> {
                    try {
                        Library library = createLibraryBuilder(plugin, libraryManager, dependency, dependency.getGroup(), dependency.getName(), relocationFrom, relocationTo).build();
                        libraryManager.loadLibrary(library);

                        if (dependency.getTransitiveDependencies() != null) {
                            for (Dependency transitiveDependency : dependency.getTransitiveDependencies()) {
                                Library transitiveLibrary = createLibraryBuilder(plugin, libraryManager, transitiveDependency, transitiveDependency.getGroup(), transitiveDependency.getName(), relocationFrom, relocationTo).build();
                                libraryManager.loadLibrary(transitiveLibrary);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }

            long endTime = System.currentTimeMillis();
            plugin.getLogger().info("Loaded " + dependencies.size() + " library in " + (endTime - startTime) + " ms.");

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

    private static CompletableFuture<Void> loadDependencyAsync(JavaPlugin plugin, Logger logger, Dependency dependency, String relocationFrom, String relocationTo, LibraryManager libraryManager) {
        return loadingLibraries.computeIfAbsent(dependency.getGroup() + ":" + dependency.getName(), key -> CompletableFuture.runAsync(() -> {
            try {
                Library.Builder libraryBuilder = createLibraryBuilder(plugin, libraryManager, dependency, dependency.getGroup(), dependency.getName(), relocationFrom, relocationTo);
                Library library = libraryBuilder.build();
                retryWithDelay(() -> {
//                    libraryManager.downloadLibrary(library);
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

    private static Library.Builder createLibraryBuilder(JavaPlugin plugin, LibraryManager libraryManager, Dependency dependency, String group, String name, String relocationFrom, String relocationTo) {
        Library.Builder libraryBuilder = Library.builder()
                .groupId(group)
                .artifactId(name)
                .version(dependency.getVersion());

        if (!name.contains("stickynote") && !name.equals("kotlin-stdlib") && !name.equals("kotlin-reflect")) {
            String replacedGroup = group.replace("{}", ".");
            String[] groupParts = replacedGroup.split("\\.");
            libraryBuilder.relocate(group, relocationTo + "{}libs{}" + groupParts[groupParts.length - 1]);
        }
        if (name.contains("stickynote")) {
            libraryBuilder.relocate(relocationFrom, relocationTo);
        }
        return libraryBuilder;
    }

    private static CompletableFuture<Void> resolveTransitiveDependenciesAsync(TransitiveDependencyHelper transitiveDependencyHelper, JavaPlugin plugin, LibraryManager libraryManager, Dependency dependency) {
        return CompletableFuture.runAsync(() -> {
            dependency.setTransitiveResolved(true);
            dependency.setTransitiveDependencies(resolveTransitiveLibraries(transitiveDependencyHelper, plugin, libraryManager, dependency).stream()
                    .map(library -> new Dependency(library.getGroupId(), library.getArtifactId(), library.getVersion()))
                    .toList());
        }, executorService);
    }

    private static List<Library> resolveTransitiveLibraries(TransitiveDependencyHelper transitiveDependencyHelper, JavaPlugin plugin, LibraryManager libraryManager, Dependency dependency) {
        List<Library> transitiveDependencies = new ArrayList<>();
        try {
            // Assuming LibraryManager has a method to resolve transitive dependencies
            Collection<Library> libraries = transitiveDependencyHelper.findTransitiveLibraries(
                    Library.builder()
                            .groupId(dependency.getGroup())
                            .artifactId(dependency.getName())
                            .version(dependency.getVersion())
                            .build()
            );

            for (Library library : libraries) {
                transitiveDependencies.add(library);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return transitiveDependencies;
    }

    private static void logProgress(Logger logger, List<CompletableFuture<Void>> futures, int totalDependencies) {
        long completed = futures.stream().filter(CompletableFuture::isDone).count();
        int percentage = (int) ((completed * 100) / totalDependencies);
        logger.info("Progress: " + percentage + "% (" + completed + "/" + totalDependencies + " dependencies loaded)");
    }
}