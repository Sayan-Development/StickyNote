package org.sayandev.loader.common;

import com.alessiodp.libby.Library;
import com.alessiodp.libby.LibraryManager;
import com.alessiodp.libby.logging.LogLevel;
import com.alessiodp.libby.transitive.ExcludedDependency;
import com.alessiodp.libby.transitive.TransitiveDependencyHelper;

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
    public static final List<String> exclusions = Arrays.asList("kotlin-stdlib", "kotlin-reflect", "kotlin", "kotlin-stdlib-jdk8", "kotlin-stdlib-jdk7", "kotlinx", "kotlinx-coroutines", "kotlinx-coroutines-core-jvm", "takenaka", "mappings", "gson");
    public static final Map<String, String> relocations = new HashMap<>();

    // name - group
    public static final Map<String, String> transitiveLoadExclusion = new HashMap<>();

    private static final String LIB_FOLDER = "lib";

    private final List<String> transitiveExcluded = Arrays.asList("xseries", "stickynote");

    protected StickyNoteLoader(String projectName) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        this.projectName = projectName;

        transitiveLoadExclusion.put("kotlinx-coroutines-core-jvm", "org{}jetbrains{}kotlinx".replace("{}", "."));
        transitiveLoadExclusion.put("kotlinx-coroutines-core", "org{}jetbrains{}kotlinx".replace("{}", "."));
        transitiveLoadExclusion.put("kotlin-reflect", "org{}jetbrains{}kotlin".replace("{}", "."));
        transitiveLoadExclusion.put("kotlin-stdlib", "org{}jetbrains{}kotlin".replace("{}", "."));
        transitiveLoadExclusion.put("kotlin-stdlib-common", "org{}jetbrains{}kotlin".replace("{}", "."));
        transitiveLoadExclusion.put("kotlin-stdlib-jdk7", "org{}jetbrains{}kotlin".replace("{}", "."));
        transitiveLoadExclusion.put("kotlin-stdlib-jdk8", "org{}jetbrains{}kotlin".replace("{}", "."));
        transitiveLoadExclusion.put("annotations", "org{}jetbrains".replace("{}", "."));
        transitiveLoadExclusion.put("checker-qual", "org{}checkerframework".replace("{}", "."));
        transitiveLoadExclusion.put("javassist", "org{}javassist".replace("{}", "."));
        transitiveLoadExclusion.put("snakeyaml", "org{}yaml".replace("{}", "."));
        transitiveLoadExclusion.put("gson", "com{}google{}gson".replace("{}", "."));
        transitiveLoadExclusion.put("error_prone_annotations", "com{}google{}errorprone".replace("{}", "."));
        transitiveLoadExclusion.put("geantyref", "io{}leangen{}geantyref".replace("{}", "."));
        transitiveLoadExclusion.put("sqlite-jdbc", "org{}xerial".replace("{}", "."));
    }

    protected abstract void onComplete();


    Class<?> stickyNotes = Class.forName("org.sayandev.stickynote.generated.StickyNotes");
    Boolean relocate = (Boolean) stickyNotes.getField("RELOCATE").get(stickyNotes);

    public void load(String id, File dataDirectory, Logger logger, LibraryManager libraryManager) {
        File libDirectory = generateLibDirectory(dataDirectory);

        File[] files = libDirectory.listFiles();
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

            TransitiveDependencyHelper transitiveDependencyHelper = new TransitiveDependencyHelper(libraryManager, libDirectory.toPath());

            relocations.put("com{}mysql", relocationTo + "{}lib{}mysql");
//            relocations.put("kotlinx{}coroutines", relocationTo + "{}lib{}kotlinx{}coroutines");
            relocations.put("org{}jetbrains{}exposed", relocationTo + "{}lib{}exposed");
//            relocations.put("org{}yaml", relocationTo + "{}lib{}yaml");
//            relocations.put("org{}spongepowered{}configurate", relocationTo + "{}lib{}configurate");
//            relocations.put("org{}slf4j", relocationTo + "{}lib{}slf4j");

            boolean hasSQLite = false;
            try {
                Class.forName("org{}sqlite{}JDBC".replace("{}", "."));
                hasSQLite = true;
            } catch (Exception ignored) {}

            if (hasSQLite) {
                dependencies.removeIf(dependency -> dependency.getName().equals("sqlite-jdbc"));
            }

            DependencyCache dependencyCache = new DependencyCache(id, libDirectory);
            Set<Dependency> cachedDependencies = new HashSet<>(dependencyCache.loadCache());
            Set<Dependency> missingDependencies = new HashSet<>(getMissingDependencies(dependencies, cachedDependencies));

            if (hasSQLite) {
                cachedDependencies.removeIf(dependency -> dependency.getName().equals("sqlite-jdbc"));
                missingDependencies.removeIf(dependency -> dependency.getName().equals("sqlite-jdbc"));
            }

            for (Dependency cachedDependency : dependencies) {
                String name = cachedDependency.getName();
                String group = cachedDependency.getGroup();
                if (exclusions.stream().anyMatch(excluded -> cachedDependency.getName().contains(excluded))) continue;
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
                String[] groupParts = group.split("\\{}");
                relocations.put(group, relocationTo + "{}lib{}" + groupParts[groupParts.length - 1]);
            }

            boolean hasMissingDependency = !missingDependencies.isEmpty();
            if (hasMissingDependency) {
                loadMissingDependencies(libDirectory, id, logger, libraryManager, transitiveDependencyHelper, dependencyCache, dependencies, missingDependencies, relocationFrom, relocationTo);
            } else {
                loadCachedDependencies(id, logger, libraryManager, cachedDependencies, relocationFrom, relocationTo);
            }

            long endTime = System.currentTimeMillis();
            logger.info("Loaded " + dependencies.size() + " library in " + (endTime - startTime) + " ms.");
            updateDependencyCache(libDirectory, new HashSet<>(dependencies));

            onComplete();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
            scheduler.shutdown();
        }
    }

    private static void configureLibraryManager(LibraryManager libraryManager, List<String> repositories) {
        libraryManager.setLogLevel(LogLevel.WARN);
        libraryManager.addMavenCentral();
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
                .version(dependency.getVersion())
                .resolveTransitiveDependencies(false);

        for (Map.Entry<String, String> downloadExclusion : transitiveLoadExclusion.entrySet()) {
            libraryBuilder.excludeTransitiveDependency(new ExcludedDependency(downloadExclusion.getValue(), downloadExclusion.getKey()));
        }

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
            Collection<Library> libraries = transitiveDependencyHelper.findTransitiveLibraries(createLibraryBuilder(dependency).build());

            transitiveDependencies.addAll(libraries);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return transitiveDependencies;
    }

    private void updateDependencyCache(File libDirectory, Set<Dependency> dependencies) {
        for (Map.Entry<String, String> downloadExclusions : transitiveLoadExclusion.entrySet()) {
            String group = downloadExclusions.getValue();
            String name = downloadExclusions.getKey();
            List<String> allVersions = getAllVersions(libDirectory, group, name);
            Dependency stickyNotesDependency = dependencies.stream().filter(dependency -> dependency.getGroup().replace("{}", ".").equals(group) && dependency.getName().equals(name)).findFirst().orElse(null);
            if (stickyNotesDependency != null) {
                allVersions.remove(stickyNotesDependency.getVersion());
            } else {
                continue;
            }
            for (String version : allVersions) {
                deleteOldVersionDirectory(libDirectory, group, name, version);
            }
        }

        /*List<Dependency> allDependencies = new ArrayList<>();
        List<Dependency> cachedDependencies = getAllProjectsCachedDependencies(libDirectory, true);
        for (Dependency dependency : cachedDependencies) {
            allDependencies.add(dependency);
            if (dependency.getTransitiveDependencies() != null) {
                allDependencies.addAll(dependency.getTransitiveDependencies());
            }
        }

        for (Dependency dependency : allDependencies) {
            String group = dependency.getGroup();
            String name = dependency.getName();
            List<String> allVersions = getAllVersions(libDirectory, group, name);
            if (allVersions.isEmpty()) {
                continue;
            }
            String latestVersion = getLatestVersion(allVersions);
            allVersions.remove(latestVersion);
            for (String version : allVersions) {
                deleteOldVersionDirectory(libDirectory, group, name, version);
            }
        }

        try {
            for (File subDirectory : Files.walk(libDirectory.toPath(), FileVisitOption.FOLLOW_LINKS).map(Path::toFile).filter(File::isDirectory).collect(Collectors.toList())) {
                if (!subDirectory.exists()) continue;
                if (subDirectory.getAbsolutePath().contains("libby")) continue;
                if (Arrays.stream(subDirectory.listFiles()).anyMatch(file -> file.isDirectory() || file.getName().endsWith(".jar"))) {
                    continue;
                }
                System.out.println("deleting directory: " + subDirectory.getAbsolutePath());
                deleteDirectory(subDirectory);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        /*List<Dependency> cachedDependencies = getAllProjectsCachedDependencies(libDirectory, false);
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
        }*/

        /*try {
            deleteUnusedDependencyDirectory(libDirectory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
    }

    private String getLatestVersion(List<String> versions) {
        if (versions == null || versions.isEmpty()) {
            return null;
        }

        return versions.stream()
                .max((v1, v2) -> {
                    String[] parts1 = v1.split("\\.");
                    String[] parts2 = v2.split("\\.");

                    // Compare each part numerically
                    for (int i = 0; i < Math.min(parts1.length, parts2.length); i++) {
                        try {
                            int num1 = Integer.parseInt(parts1[i]);
                            int num2 = Integer.parseInt(parts2[i]);
                            if (num1 != num2) {
                                return Integer.compare(num1, num2);
                            }
                        } catch (NumberFormatException e) {
                            // If parts can't be parsed as integers, fall back to string comparison
                            int compare = parts1[i].compareTo(parts2[i]);
                            if (compare != 0) {
                                return compare;
                            }
                        }
                    }

                    // If one version has more parts than the other, the longer one is considered newer
                    return Integer.compare(parts1.length, parts2.length);
                })
                .orElse(null);
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

    private File versionDirectory(File libFolder, String group, String name, String version) {
        String groupPath = group.replace("{}", "/").replace(".", "/");
        return new File(libFolder, groupPath + "/" + name + "/" + version);
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
