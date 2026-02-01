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

    public final String projectName;
    public static final ConcurrentHashMap<Dependency, CompletableFuture<Void>> loadingLibraries = new ConcurrentHashMap<>();
    public static final ExecutorService executorService = Executors.newWorkStealingPool(Runtime.getRuntime().availableProcessors());
    public static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    public static final List<String> exclusions = Arrays.asList(/*"kotlin-stdlib", "kotlin-reflect", "kotlin", "kotlin-stdlib-jdk8", "kotlin-stdlib-jdk7", "kotlinx", "kotlinx-coroutines", "kotlinx-coroutines-core-jvm",*/ "takenaka", "mappings", "gson", /*"exposed",*/ "adventure");
    public static final Map<String, String> relocations = new HashMap<>();

    public final List<String> transitiveExcluded = Arrays.asList("xseries", "stickynote");

    protected StickyNoteLoader(String projectName) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        this.projectName = projectName;
    }

    protected abstract void onComplete();


    public Class<?> stickyNotes = Class.forName("org.sayandev.stickynote.generated.StickyNotes");
    public Boolean relocate = (Boolean) stickyNotes.getField("RELOCATE").get(stickyNotes);

    public void load(String id, File dataDirectory, Logger logger, LibraryManager libraryManager, boolean withLibDirectory) {
        File libDirectory = generateLibDirectory(dataDirectory, withLibDirectory);

        File[] files = libDirectory.listFiles();
        logger.info("Loading libraries... this might take up to a few minutes depending on your connection.");

        long startTime = System.currentTimeMillis();

        try {
            List<Dependency> generatedDependencies = new ArrayList<>(getDependencies(stickyNotes));
//            generatedDependencies.add(new Dependency("org{}jetbrains{}kotlin", "kotlin-stdlib", "2.1.0", null, false));
            List<String> repositories = getRepositories(stickyNotes);

            Object relocation = stickyNotes.getField("RELOCATION").get(stickyNotes);
            String relocationFrom = (String) relocation.getClass().getMethod("getFrom").invoke(relocation);
            String relocationTo = (String) relocation.getClass().getMethod("getTo").invoke(relocation);

            configureLibraryManager(libraryManager, repositories);

            TransitiveDependencyHelper transitiveDependencyHelper = new TransitiveDependencyHelper(libraryManager, libDirectory.toPath());

            relocations.put("com{}mysql", relocationTo + "{}lib{}mysql");
            /*String kot = "kot";
            String lin = "lon";
            relocations.put(kot + lin.replace("o", "i"), relocationTo + "{}lib{}kotlin");*/

//            relocations.put("org{}sqlite", relocationTo + "{}lib{}sqlite");
            relocations.put("kotlinx{}coroutines", relocationTo + "{}lib{}kotlinx{}coroutines");
            relocations.put("org{}jetbrains{}exposed", relocationTo + "{}lib{}exposed");

            DependencyCache dependencyCache = new DependencyCache(id, libDirectory);

            Set<Dependency> cachedDependencies = new HashSet<>(dependencyCache.loadCache());
            Set<Dependency> missingDependencies = new HashSet<>(getMissingDependencies(generatedDependencies, cachedDependencies));

            if (!missingDependencies.isEmpty()) {
                logger.info(String.format("There are %d missing dependencies:", missingDependencies.size()));
                for (Dependency dependency : generatedDependencies) {
                    String symbol;
                    if (missingDependencies.contains(dependency)) {
                        symbol = "[New]   +";
                    } else if (cachedDependencies.contains(dependency)) {
                        symbol = "[Cache] #";
                    } else {
                        symbol = "[Old]   -";
                    }
                    logger.info(String.format("%s %s (%s) version %s", symbol, dependency.getName(), dependency.getGroup().replace("{}", "."), dependency.getVersion()));
                }
                logger.info("Missing dependencies will be downloaded and cached.");
            }

            List<Dependency> allDependencies = new ArrayList<>();
            allDependencies.addAll(generatedDependencies);
            allDependencies.addAll(getTransitiveDependencies(stickyNotes));
            for (Dependency generatedDependency : allDependencies) {
                String name = generatedDependency.getName();
                String group = generatedDependency.getGroup();
                if (exclusions.stream().anyMatch(excluded -> generatedDependency.getName().contains(excluded))) continue;
                if (generatedDependency.isStickyLoad()) {
                    if (generatedDependency.getRelocation() != null) {
                        String[] splitted = generatedDependency.getGroup().split("\\{}");
                        relocations.put(generatedDependency.getRelocation(), relocationTo + "{}lib{}" + splitted[splitted.length - 1]);
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
            relocations.remove("org{}sayandev");
            relocations.put("org{}sayandev{}loader", relocationTo + "{}lib{}stickynote");
            relocations.put("org{}sayandev{}stickynote", relocationTo + "{}lib{}stickynote");

            boolean hasMissingDependency = !missingDependencies.isEmpty();
            if (hasMissingDependency) {
                loadMissingDependencies(libDirectory, id, logger, libraryManager, transitiveDependencyHelper, dependencyCache, generatedDependencies, missingDependencies, relocationFrom, relocationTo);
            } else {
                loadCachedDependencies(id, logger, libraryManager, cachedDependencies, relocationFrom, relocationTo);
            }

            long endTime = System.currentTimeMillis();
            logger.info("Loaded " + generatedDependencies.size() + " library in " + (endTime - startTime) + " ms.");

            List<Dependency> allProjectsCachedDependencies = getAllProjectsCachedDependencies(logger, libDirectory);
            File[] libDirectoryFiles = libDirectory.listFiles();
            if (libDirectoryFiles != null) {
                for (Dependency projectDependency : allProjectsCachedDependencies) {
                    List<String> versions = getAllVersions(libDirectory, projectDependency.getGroup(), projectDependency.getName());
                    List<Dependency> toBeRemovedVersions = versions.stream()
                            .map(version -> new Dependency(projectDependency.getGroup(), projectDependency.getName(), version, projectDependency.getRelocation(), projectDependency.isStickyLoad()))
                            .filter(dependencyOfVersion -> !allProjectsCachedDependencies.contains(dependencyOfVersion))
                            .toList();
                    for (Dependency dependency : toBeRemovedVersions) {
                        deleteOldVersionDirectory(libDirectory, dependency.getGroup(), dependency.getName(), dependency.getVersion());
                    }
                }
            }

            onComplete();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
            scheduler.shutdown();
        }
    }

    public List<File> getVersionFiles(File libDirectory, String group, String name) {
        File[] files = versionsDirectory(libDirectory, group, name).listFiles();
        if (files == null) return Collections.emptyList();
        return Arrays.stream(files).filter(File::isDirectory).collect(Collectors.toList());
    }

    public static void configureLibraryManager(LibraryManager libraryManager, List<String> repositories) {
        libraryManager.setLogLevel(LogLevel.WARN);
        libraryManager.addMavenCentral();
//        libraryManager.addSonatype();
//        libraryManager.addJitPack();
//        libraryManager.addJCenter();
        libraryManager.addRepository("https://repo.sayandev.org/snapshots");
        libraryManager.addMavenLocal();
        repositories.forEach(libraryManager::addRepository);
    }

    public Set<Dependency> getMissingDependencies(List<Dependency> generatedDependencies, Set<Dependency> cachedDependencies) {
        Set<Dependency> missingDependencies = new HashSet<>(generatedDependencies);
        missingDependencies.removeIf(missingDependency -> cachedDependencies.stream()
                .toList()
                .contains(missingDependency));
        return missingDependencies;
    }

    public void loadMissingDependencies(File libDirectory, String id, Logger logger, LibraryManager libraryManager, TransitiveDependencyHelper transitiveDependencyHelper, DependencyCache dependencyCache, List<Dependency> dependencies, Set<Dependency> missingDependencies, String relocationFrom, String relocationTo) throws InterruptedException, ExecutionException {
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

    public List<CompletableFuture<Void>> loadDependencyAndTransitives(String id, LibraryManager libraryManager, Dependency dependency, String relocationFrom, String relocationTo) {
        List<CompletableFuture<Void>> loadingFutures = new ArrayList<>();
        for (Dependency transitiveDependency : dependency.getTransitiveDependencies()) {
            loadingFutures.add(loadDependencyAsync(id, transitiveDependency, libraryManager));
        }
        loadingFutures.add(loadDependencyAsync(id, dependency, libraryManager));
        return loadingFutures;
    }

    public void loadCachedDependencies(String id, Logger logger, LibraryManager libraryManager, Set<Dependency> cachedDependencies, String relocationFrom, String relocationTo) {
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

    public List<Dependency> getDependencies(Class<?> stickyNotes) {
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

    public List<Dependency> getTransitiveDependencies(Class<?> stickyNotes) {
        return Arrays.stream(stickyNotes.getFields())
                .filter(field -> field.getName().startsWith("TRANSITIVE_DEPENDENCY_"))
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

    public List<String> getRepositories(Class<?> stickyNotes) {
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

    public CompletableFuture<Void> loadDependencyAsync(String id, Dependency dependency, LibraryManager libraryManager) {
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

    public void retryWithDelay(Runnable task, int maxRetries, long delayMillis) throws Exception {
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

    public Library.Builder createLibraryBuilder(Dependency dependency) {
        Library.Builder libraryBuilder = Library.builder()
                .groupId(dependency.getGroup())
                .artifactId(dependency.getName())
                .version(dependency.getVersion())
                .resolveTransitiveDependencies(false);
      
        if (relocate) {
            if (dependency.getRelocation() != null || !dependency.isStickyLoad()) {
                for (Map.Entry<String, String> relocation : relocations.entrySet()) {
                    libraryBuilder.relocate(relocation.getKey(), relocation.getValue());
                }
            }
        }
        return libraryBuilder;
    }

    public CompletableFuture<Void> resolveTransitiveDependenciesAsync(String id, TransitiveDependencyHelper transitiveDependencyHelper, Dependency dependency) {
        return CompletableFuture.runAsync(() -> {
            dependency.setTransitiveResolved(transitiveExcluded.stream().anyMatch(excluded -> dependency.getName().contains(excluded)));
            dependency.setTransitiveDependencies(resolveTransitiveLibraries(id, transitiveDependencyHelper, dependency).stream()
                    .map(library -> new Dependency(library.getGroupId(), library.getArtifactId(), library.getVersion(), dependency.getRelocation(), dependency.isStickyLoad()))
                    .toList());
        }, executorService);
    }

    public List<Library> resolveTransitiveLibraries(String id, TransitiveDependencyHelper transitiveDependencyHelper, Dependency dependency) {
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
  
    public String getLatestVersion(List<String> versions) {
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

    public List<String> getAllVersions(File libDirectory, String group, String name) {
        File[] files = versionsDirectory(libDirectory, group, name).listFiles();
        if (files == null) return Collections.emptyList();
        return Arrays.stream(files).map(File::getName).collect(Collectors.toList());
    }

    public List<Dependency> getAllProjectsCachedDependencies(Logger logger, File libDirectory) {
        List<Dependency> allDependencies = new ArrayList<>();
        File[] cacheFiles = libDirectory.listFiles((dir, name) -> name.endsWith(".dat"));
        if (cacheFiles == null) return allDependencies;
        for (File cacheFile : cacheFiles) {
            try {
                Set<Dependency> cachedDependencies = new DependencyCache("temp", libDirectory).loadCacheFromFile(logger, cacheFile);
                allDependencies.addAll(cachedDependencies);
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }
        return allDependencies;
    }

    public void deleteOldVersionDirectory(File libFolder, String group, String name, String version) {
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

    public File versionsDirectory(File libFolder, String group, String name) {
        String groupPath = group.replace("{}", "/").replace(".", "/");
        File dependencyVersionsFile = new File(libFolder, groupPath + "/" + name);
        return dependencyVersionsFile;
    }

    public File versionDirectory(File libFolder, String group, String name, String version) {
        String groupPath = group.replace("{}", "/").replace(".", "/");
        return new File(libFolder, groupPath + "/" + name + "/" + version);
    }

    public boolean isFileInUse(File file) {
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            return false;
        } catch (IOException e) {
            return true;
        }
    }

    final boolean checkTimeBeforeDelete = true;

    public void deleteDirectory(File directory) throws IOException {
        if (!directory.exists()) return;

        // Check if directory or its files are in use or recently used (last modified < 5 minutes ago)
        long now = System.currentTimeMillis();
        long fiveMinutesMillis = 15 * 60 * 1000;
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // Recursively check subdirectories
                    deleteDirectory(file);
                } else {
                    // If file is in use or recently used, skip deletion
                    if (isFileInUse(file) || (file.lastModified() > now - fiveMinutesMillis && checkTimeBeforeDelete)) {
                        continue;
                    }
                    file.delete();
                }
            }
        }
        if (isFileInUse(directory) || directory.lastModified() > now - fiveMinutesMillis) {
            return;
        }
        if (!directory.delete()) {
            directory.deleteOnExit();
            System.err.println("Warning: Failed to delete directory (in use?): " + directory.getAbsolutePath());
        }
    }

    public void logProgress(Logger logger, List<CompletableFuture<Void>> futures, int totalDependencies) {
        long completed = futures.stream().filter(CompletableFuture::isDone).count();
        int percentage = (int) ((completed * 100) / totalDependencies);
        logger.info(String.format("Progress: %d%% (%d/%d dependencies loaded)", percentage, completed, totalDependencies));
    }

    public static File generateLibDirectory(File root, boolean withLibDirectory) {
        File file;
        if (!withLibDirectory) {
            file = new File(root, "stickynote");
        } else {
            file = new File(new File(root, "stickynote"), "lib");
        }
        return file;
    }
}
