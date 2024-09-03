package org.sayandev.loader.common;

import com.alessiodp.libby.Library;
import com.alessiodp.libby.LibraryManager;
import com.alessiodp.libby.logging.LogLevel;
import com.alessiodp.libby.transitive.TransitiveDependencyHelper;

import java.io.File;
import java.io.UncheckedIOException;
import java.nio.file.FileSystemException;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

public abstract class StickyNoteLoader {

    private static final ConcurrentHashMap<Dependency, CompletableFuture<Void>> loadingLibraries = new ConcurrentHashMap<>();
    private static final ExecutorService executorService = Executors.newWorkStealingPool(Runtime.getRuntime().availableProcessors());
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    public static final List<String> exclusions = Arrays.asList("kotlin-stdlib", "kotlin-reflect", "kotlin", "kotlin-stdlib-jdk8", "kotlin-stdlib-jdk7", "kotlinx", "kotlinx-coroutines");
    public static final Map<String, String> relocations = new HashMap<>();

    private static final String LIB_FOLDER = "lib";

    private final List<String> transitiveExcluded = Arrays.asList("xseries", "stickynote");

    protected abstract void onComplete();

    public void load(String id, File dataFolder, Logger logger, LibraryManager libraryManager) {
        File libFolder = new File(dataFolder, LIB_FOLDER);

        File[] files = libFolder.listFiles();
        if (files == null || !Arrays.stream(files).map(File::getName).toList().contains(LIB_FOLDER)) {
            logger.info("Initializing first-time setup.. This may take up to a minute depending on your connection.");
        } else {
            logger.info("Loading libraries... this might take a few seconds.");
        }

        long startTime = System.currentTimeMillis();

        try {
            Class<?> stickyNotes = Class.forName("org.sayandev.stickynote.generated.StickyNotes");
            List<Dependency> dependencies = getDependencies(stickyNotes);
            List<String> repositories = getRepositories(stickyNotes);

            Object relocation = stickyNotes.getField("RELOCATION").get(stickyNotes);
            String relocationFrom = (String) relocation.getClass().getMethod("getFrom").invoke(relocation);
            String relocationTo = (String) relocation.getClass().getMethod("getTo").invoke(relocation);

            configureLibraryManager(libraryManager, repositories);

            TransitiveDependencyHelper transitiveDependencyHelper = new TransitiveDependencyHelper(libraryManager, libFolder.toPath());

            DependencyCache dependencyCache = new DependencyCache(libFolder);
            Set<Dependency> cachedDependencies = dependencyCache.loadCache();
            Set<Dependency> missingDependencies = getMissingDependencies(dependencies, cachedDependencies);

            for (Dependency cachedDependency : dependencies) {
                String name = cachedDependency.getName();
                String group = cachedDependency.getGroup();
                if (name.contains("adventure")) continue;
                if (name.contains("stickynote")) {
                    relocations.put(relocationFrom, relocationTo + "{}lib{}stickynote");
                }
                if (exclusions.stream().anyMatch(excluded -> cachedDependency.getName().contains(excluded))) continue;
                String[] groupParts = group.split("\\{}");
                relocations.put(group, relocationTo + "{}lib{}" + groupParts[groupParts.length - 1]);
            }

            if (!missingDependencies.isEmpty()) {
                loadMissingDependencies(id, logger, libraryManager, transitiveDependencyHelper, dependencyCache, dependencies, missingDependencies, relocationFrom, relocationTo);
            } else {
                loadCachedDependencies(id, logger, libraryManager, cachedDependencies, relocationFrom, relocationTo);
            }

            long endTime = System.currentTimeMillis();
            logger.info("Loaded " + dependencies.size() + " library in " + (endTime - startTime) + " ms.");

            onComplete();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
            scheduler.shutdown();
        }
    }

    private static void configureLibraryManager(LibraryManager libraryManager, List<String> repositories) {
        libraryManager.setLogLevel(LogLevel.ERROR);
//        libraryManager.addMavenCentral();
//        libraryManager.addSonatype();
//        libraryManager.addJitPack();
//        libraryManager.addJCenter();
        libraryManager.addRepository("https://repo.sayandev.org/snapshots");
        libraryManager.addMavenLocal();
        repositories.forEach(libraryManager::addRepository);
    }

    private Set<Dependency> getMissingDependencies(List<Dependency> dependencies, Set<Dependency> cachedDependencies) {
        Set<Dependency> missingDependencies = new HashSet<>(dependencies);
        missingDependencies.removeIf(missingDependency -> cachedDependencies.stream()
                .toList()
                .contains(missingDependency));
        return missingDependencies;
    }

    private void loadMissingDependencies(String id, Logger logger, LibraryManager libraryManager, TransitiveDependencyHelper transitiveDependencyHelper, DependencyCache dependencyCache, List<Dependency> dependencies, Set<Dependency> missingDependencies, String relocationFrom, String relocationTo) throws InterruptedException, ExecutionException {
        List<CompletableFuture<Void>> resolveFutures = dependencies.stream()
                .map(dependency -> resolveTransitiveDependenciesAsync(id, transitiveDependencyHelper, dependency))
                .toList();

        CompletableFuture<Void> resolveAll = CompletableFuture.allOf(resolveFutures.toArray(new CompletableFuture[0]));

        scheduler.scheduleAtFixedRate(() -> logProgress(logger, resolveFutures, dependencies.size()), 3, 5, TimeUnit.SECONDS);

        resolveAll.thenRunAsync(() -> {
            dependencyCache.saveCache(new HashSet<>(dependencies));

            List<CompletableFuture<Void>> futures = dependencies.stream()
                    .map(dependency -> loadDependencyAndTransitives(id, libraryManager, dependency, relocationFrom, relocationTo))
                    .flatMap(Collection::stream)
                    .toList();

            CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            allOf.join();
        }, executorService).join();
    }

    private List<CompletableFuture<Void>> loadDependencyAndTransitives(String id, LibraryManager libraryManager, Dependency dependency, String relocationFrom, String relocationTo) {
        List<CompletableFuture<Void>> loadingFutures = new ArrayList<>();
        for (Dependency transitiveDependency : dependency.getTransitiveDependencies()) {
            loadingFutures.add(loadDependencyAsync(id, transitiveDependency, libraryManager));
        }
        loadingFutures.add(loadDependencyAsync(id, dependency, libraryManager));
        return loadingFutures;
    }

    private void loadCachedDependencies(String id, Logger logger, LibraryManager libraryManager, Set<Dependency> cachedDependencies, String relocationFrom, String relocationTo) {
        logger.info("Library cache found, loading cached libraries...");
        cachedDependencies.forEach(dependency -> {
            try {
                Library library = createLibraryBuilder(dependency).build();
                libraryManager.loadLibrary(library);

                if (dependency.getTransitiveDependencies() != null) {
                    for (Dependency transitiveDependency : dependency.getTransitiveDependencies()) {
                        Library transitiveLibrary = createLibraryBuilder(transitiveDependency).build();
                        libraryManager.loadLibrary(transitiveLibrary);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private List<Dependency> getDependencies(Class<?> stickyNotes) {
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

    private List<String> getRepositories(Class<?> stickyNotes) {
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

    private CompletableFuture<Void> loadDependencyAsync(String id, Dependency dependency, LibraryManager libraryManager) {
        return loadingLibraries.computeIfAbsent(dependency, key -> CompletableFuture.runAsync(() -> {
            try {
                Library.Builder libraryBuilder = createLibraryBuilder(dependency);
                Library library = libraryBuilder.build();
                retryWithDelay(() -> {
                    libraryManager.loadLibrary(library);
                }, 3, 1000);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, executorService));
    }

    private void retryWithDelay(Runnable task, int maxRetries, long delayMillis) throws Exception {
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

    private Library.Builder createLibraryBuilder(Dependency dependency) {
        Library.Builder libraryBuilder = Library.builder()
                .groupId(dependency.getGroup())
                .artifactId(dependency.getName())
                .version(dependency.getVersion());

        for (Map.Entry<String, String> relocation : relocations.entrySet()) {
            libraryBuilder.relocate(relocation.getKey(), relocation.getValue());
        }
        return libraryBuilder;
    }

    private CompletableFuture<Void> resolveTransitiveDependenciesAsync(String id, TransitiveDependencyHelper transitiveDependencyHelper, Dependency dependency) {
        return CompletableFuture.runAsync(() -> {
            dependency.setTransitiveResolved(transitiveExcluded.stream().anyMatch(excluded -> dependency.getName().contains(excluded)));
            dependency.setTransitiveDependencies(resolveTransitiveLibraries(id, transitiveDependencyHelper, dependency).stream()
                    .map(library -> new Dependency(library.getGroupId(), library.getArtifactId(), library.getVersion()))
                    .toList());
        }, executorService);
    }

    private List<Library> resolveTransitiveLibraries(String id, TransitiveDependencyHelper transitiveDependencyHelper, Dependency dependency) {
        List<Library> transitiveDependencies = new ArrayList<>();
        if (transitiveExcluded.stream().anyMatch(excluded -> dependency.getName().toLowerCase().contains(excluded))) return Collections.emptyList();
        try {
            Collection<Library> libraries = transitiveDependencyHelper.findTransitiveLibraries(
                    Library.builder()
                            .groupId(dependency.getGroup())
                            .artifactId(dependency.getName())
                            .version(dependency.getVersion())
//                            .loaderId(id + "_" + dependency.getName())
//                            .isolatedLoad(true)
                            .build()
            );

            transitiveDependencies.addAll(libraries);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return transitiveDependencies;
    }

    private void logProgress(Logger logger, List<CompletableFuture<Void>> futures, int totalDependencies) {
        long completed = futures.stream().filter(CompletableFuture::isDone).count();
        int percentage = (int) ((completed * 100) / totalDependencies);
        logger.info(String.format("Progress: %d%% (%d/%d dependencies loaded)", percentage, completed, totalDependencies));
    }
}
