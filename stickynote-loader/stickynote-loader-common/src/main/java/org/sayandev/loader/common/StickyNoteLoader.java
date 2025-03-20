package org.sayandev.loader.common;

import com.alessiodp.libby.Library;
import com.alessiodp.libby.LibraryManager;
import com.alessiodp.libby.logging.LogLevel;
import com.alessiodp.libby.transitive.TransitiveDependencyHelper;

import javax.swing.text.html.parser.Entity;
import java.io.*;
import java.nio.file.FileSystemException;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public abstract class StickyNoteLoader {

    private final String projectName;
    private static final ConcurrentHashMap<Dependency, CompletableFuture<Void>> loadingLibraries = new ConcurrentHashMap<>();
    private static final ExecutorService executorService = Executors.newWorkStealingPool(Runtime.getRuntime().availableProcessors());
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    public static final List<String> exclusions = Arrays.asList("kotlin-stdlib", "kotlin-reflect", "kotlin", "kotlin-stdlib-jdk8", "kotlin-stdlib-jdk7"/*, "kotlinx", "kotlinx-coroutines", "kotlinx-coroutines-core-jvm"*/, "takenaka", "mappings", "gson");
    public static final Map<String, String> relocations = new HashMap<>();

    private static final String LIB_FOLDER = "lib";

    private final List<String> transitiveExcluded = Arrays.asList("xseries", "stickynote");

    protected StickyNoteLoader(String projectName) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        this.projectName = projectName;
    }

    protected abstract void onComplete();


    Class<?> stickyNotes = Class.forName("org.sayandev.stickynote.generated.StickyNotes");
    Boolean relocate = (Boolean) stickyNotes.getField("RELOCATE").get(stickyNotes);

    public void load(String id, File dataDirectory, Logger logger, LibraryManager libraryManager) {
        File libFolder = generateLibDirectory(dataDirectory);

        File[] files = libFolder.listFiles();
        if (files == null || !Arrays.stream(files).map(File::getName).toList().contains(LIB_FOLDER)) {
            logger.info("Some of the libraries are missing, Loading libraries... this might take up to a minute depending on your connection.");
        } else {
            logger.info("Loading libraries... this might take a few seconds.");
        }

        long startTime = System.currentTimeMillis();

        try {
            List<Dependency> dependencies = new ArrayList<>(getDependencies(stickyNotes));
            List<String> repositories = getRepositories(stickyNotes);

            Object relocation = stickyNotes.getField("RELOCATION").get(stickyNotes);
            String relocationFrom = (String) relocation.getClass().getMethod("getFrom").invoke(relocation);
            String relocationTo = (String) relocation.getClass().getMethod("getTo").invoke(relocation);

            configureLibraryManager(libraryManager, repositories);

            TransitiveDependencyHelper transitiveDependencyHelper = new TransitiveDependencyHelper(libraryManager, libFolder.toPath());

            relocations.put("com{}mysql", relocationTo + "{}lib{}mysql");
            relocations.put("kotlinx{}coroutines", relocationTo + "{}lib{}kotlinx{}coroutines");
            relocations.put("org{}jetbrains{}exposed", relocationTo + "{}lib{}exposed");
            relocations.put("org{}yaml", relocationTo + "{}lib{}yaml");
            relocations.put("org{}spongepowered{}configurate", relocationTo + "{}lib{}configurate");
//            relocations.put("org{}slf4j", relocationTo + "{}lib{}slf4j");

            boolean hasSQLite = false;
            try {
                Class.forName("org{}sqlite{}JDBC".replace("{}", "."));
                hasSQLite = true;
            } catch (Exception ignored) {}

            if (hasSQLite) {
                dependencies.removeIf(dependency -> dependency.getName().equals("sqlite-jdbc"));
            }

            DependencyCache dependencyCache = new DependencyCache(id, libFolder);
            Set<Dependency> cachedDependencies = new HashSet<>(dependencyCache.loadCache());
            Set<Dependency> missingDependencies = new HashSet<>(getMissingDependencies(dependencies, cachedDependencies));

            if (hasSQLite) {
                cachedDependencies.removeIf(dependency -> dependency.getName().equals("sqlite-jdbc"));
                missingDependencies.removeIf(dependency -> dependency.getName().equals("sqlite-jdbc"));
            }

            for (Dependency cachedDependency : dependencies) {
                String name = cachedDependency.getName();
                String group = cachedDependency.getGroup();
                if (cachedDependency.isStickyLoad()) {
                    if (cachedDependency.getRelocation() != null) {
                        String[] splitted = cachedDependency.getGroup().split("\\{}");
                        relocations.put(cachedDependency.getRelocation(), relocationTo + "{}lib{}" + splitted[splitted.length - 1]);
                    }
                    continue;
                }
                if (name.contains("adventure")) {
                    continue;
                }
                if (name.contains("stickynote")) {
                    relocations.put(relocationFrom, relocationTo + "{}lib{}stickynote");
                }
                if (exclusions.stream().anyMatch(excluded -> cachedDependency.getName().contains(excluded))) continue;
                String[] groupParts = group.split("\\{}");
                relocations.put(group, relocationTo + "{}lib{}" + groupParts[groupParts.length - 1]);
            }

            if (!missingDependencies.isEmpty()) {
                loadMissingDependencies(libFolder, id, logger, libraryManager, transitiveDependencyHelper, dependencyCache, dependencies, missingDependencies, relocationFrom, relocationTo);
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

    private void loadMissingDependencies(File libDirectory, String id, Logger logger, LibraryManager libraryManager, TransitiveDependencyHelper transitiveDependencyHelper, DependencyCache dependencyCache, List<Dependency> dependencies, Set<Dependency> missingDependencies, String relocationFrom, String relocationTo) throws InterruptedException, ExecutionException {
        updateDependencyCache(libDirectory, new HashSet<>(dependencies));

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
        for (Dependency dependency : cachedDependencies) {
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
        }
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
                                (String) dependencyFieldClass.getMethod("getVersion").invoke(dependencyObject),
                                (String) dependencyFieldClass.getMethod("getRelocation").invoke(dependencyObject),
                                (boolean) dependencyFieldClass.getMethod("isStickyLoad").invoke(dependencyObject)
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
                }, 5, 1000);
            } catch (Exception e) {
                e.printStackTrace();
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

        if (relocate) {
            if (dependency.getRelocation() != null || !dependency.isStickyLoad()) {
                for (Map.Entry<String, String> relocation : relocations.entrySet()) {
                    libraryBuilder.relocate(relocation.getKey(), relocation.getValue());
                }
            }
        }
        return libraryBuilder;
    }

    private CompletableFuture<Void> resolveTransitiveDependenciesAsync(String id, TransitiveDependencyHelper transitiveDependencyHelper, Dependency dependency) {
        return CompletableFuture.runAsync(() -> {
            dependency.setTransitiveResolved(transitiveExcluded.stream().anyMatch(excluded -> dependency.getName().contains(excluded)));
            dependency.setTransitiveDependencies(resolveTransitiveLibraries(id, transitiveDependencyHelper, dependency).stream()
                    .map(library -> new Dependency(library.getGroupId(), library.getArtifactId(), library.getVersion(), dependency.getRelocation(), dependency.isStickyLoad()))
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
                            .excludeTransitiveDependency("org{}jetbrains{}kotlinx".replace("{}", "."), "kotlinx-coroutines-core-jvm")
                            .excludeTransitiveDependency("org{}jetbrains{}kotlinx".replace("{}", "."), "kotlinx-coroutines-core")
                            .excludeTransitiveDependency("org{}jetbrains{}kotlin".replace("{}", "."), "kotlin-stdlib")
                            .excludeTransitiveDependency("org{}jetbrains{}kotlin".replace("{}", "."), "kotlin-stdlib")
                            .excludeTransitiveDependency("org{}jetbrains{}kotlin".replace("{}", "."), "kotlin-stdlib")
                            .excludeTransitiveDependency("org{}jetbrains{}kotlin".replace("{}", "."), "kotlin-stdlib-common")
                            .excludeTransitiveDependency("org{}jetbrains{}kotlin".replace("{}", "."), "kotlin-stdlib-jdk7")
                            .excludeTransitiveDependency("org{}jetbrains{}kotlin".replace("{}", "."), "kotlin-stdlib-jdk8")
                            .excludeTransitiveDependency("org{}jetbrains".replace("{}", "."), "annotations")
                            .excludeTransitiveDependency("org{}checkerframework".replace("{}", "."), "checker-qual")
                            .excludeTransitiveDependency("org{}javassist".replace("{}", "."), "javassist")
//                            .excludeTransitiveDependency("org{}slf4j".replace("{}", "."), "slf4j-api")
                            .excludeTransitiveDependency("org{}yaml".replace("{}", "."), "snakeyaml")
                            .excludeTransitiveDependency("com{}google{}gson".replace("{}", "."), "gson")
                            .excludeTransitiveDependency("com{}google{}errorprone".replace("{}", "."), "error_prone_annotations")
                            .excludeTransitiveDependency("io{}leangen{}geantyref".replace("{}", "."), "geantyref")
                            .excludeTransitiveDependency("org{}xerial".replace("{}", "."), "sqlite-jdbc")
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

    private void updateDependencyCache(File libDirectory, Set<Dependency> dependencies) {
        List<Dependency> cachedDependencies = getAllProjectsCachedDependencies(libDirectory, false);
        Map<String, Set<String>> inUseDependencyVersions = new HashMap<>();

        // Map all cached dependencies by their group+name and collect all versions
        for (Dependency dependency : cachedDependencies) {
            String key = dependency.getGroup() + ":" + dependency.getName();
            inUseDependencyVersions.computeIfAbsent(key, k -> new HashSet<>()).add(dependency.getVersion());
        }

        // Map all cached dependencies by their group+name and collect all versions
        for (Dependency dependency : dependencies) {
            String key = dependency.getGroup() + ":" + dependency.getName();
            inUseDependencyVersions.computeIfAbsent(key, k -> new HashSet<>()).add(dependency.getVersion());
        }

        for (Map.Entry<String, Set<String>> inUseDependencyVersion : inUseDependencyVersions.entrySet()) {
            System.out.println("checking dependency " + inUseDependencyVersion.getKey() + " with versions: " + inUseDependencyVersion.getValue());
            String[] groupName = inUseDependencyVersion.getKey().split(":");
            String group = groupName[0];
            String name = groupName[1];

            // Get all versions of the dependency
            List<String> allVersions = getAllVersions(libDirectory, group, name);

            // Check if the version is in use
            for (String version : allVersions) {
                if (!inUseDependencyVersion.getValue().contains(version)) {
                    deleteOldVersionDirectory(libDirectory, group, name, version);
                }
            }
        }
    }

    private List<String> getAllVersions(File libDirectory, String group, String name) {
        File[] files = versionsDirectory(libDirectory, group, name).listFiles();
        if (files == null) return Collections.emptyList();
        return Arrays.stream(files).map(File::getName).collect(Collectors.toList());
    }

    private List<Dependency> getAllProjectsCachedDependencies(File libDirectory, boolean includeSelf) {
        List<Dependency> allDependencies = new ArrayList<>();
        File[] cacheFiles = libDirectory.listFiles((dir, name) -> name.endsWith(".dat") && (includeSelf || !name.contains(projectName)));
        if (cacheFiles == null) return allDependencies;
        for (File cacheFile : cacheFiles) {
            System.out.println("scanning cache file: " + cacheFile.getName());
            try {
                allDependencies.addAll(new DependencyCache("temp", libDirectory).loadCacheFromFile(cacheFile));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return allDependencies;
    }

    private void deleteOldVersionDirectory(File libFolder, String group, String name, String version) {
        // Convert group:name:version to path
        String groupPath = group.replace("{}", "/").replace(".", "/");
        String path = groupPath + "/" + name + "/" + version;

        File versionDir = new File(libFolder, path);

        System.out.println("version directory: " + versionDir.getAbsolutePath());
        if (versionDir.exists() && versionDir.isDirectory()) {
            try {
                deleteDirectory(versionDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private File versionsDirectory(File libFolder, String group, String name) {
        String groupPath = group.replace("{}", "/").replace(".", "/");
        return new File(libFolder, groupPath + "/" + name);
    }

    private void deleteDirectory(File directory) throws IOException {
        if (!directory.exists()) return;

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    if (!file.delete()) {
                        throw new IOException("Failed to delete file: " + file);
                    }
                }
            }
        }

        if (!directory.delete()) {
            throw new IOException("Failed to delete directory: " + directory);
        }
    }

    private void logProgress(Logger logger, List<CompletableFuture<Void>> futures, int totalDependencies) {
        long completed = futures.stream().filter(CompletableFuture::isDone).count();
        int percentage = (int) ((completed * 100) / totalDependencies);
        logger.info(String.format("Progress: %d%% (%d/%d dependencies loaded)", percentage, completed, totalDependencies));
    }

    public static File generateLibDirectory(File root) {
        return new File(root, "stickynote");
    }
}
