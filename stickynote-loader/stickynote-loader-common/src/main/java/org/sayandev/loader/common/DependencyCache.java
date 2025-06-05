package org.sayandev.loader.common;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class DependencyCache {

    private final String project;
    private final String cacheFileName;
    private final File cacheFile;

    public DependencyCache(String project, File dataFolder) {
        this.project = project;
        this.cacheFileName = "dependency_cache_" + project + ".dat";
        this.cacheFile = new File(dataFolder, cacheFileName);
    }

    public Set<Dependency> loadCache() {
        if (!cacheFile.exists() || cacheFile.length() == 0) {
            return new HashSet<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cacheFile))) {
            Object obj = ois.readObject();
            if (obj instanceof Set<?>) {
                return handleDeserializedSet((Set<?>) obj);
            }
            return new HashSet<>();
        } catch (IOException | ClassNotFoundException e) {
            // If we encounter corruption, delete the cache file to prevent future errors
            cacheFile.delete();
            e.printStackTrace();
            return new HashSet<>();
        }
    }

    private Set<Dependency> handleDeserializedSet(Set<?> set) {
        Set<Dependency> result = new HashSet<>();
        for (Object o : set) {
            if (o instanceof Dependency) {
                result.add((Dependency) o);
            }
        }
        return result;
    }

    public void saveCache(Set<Dependency> dependencies) {
        if (!cacheFile.exists()) {
            try {
                cacheFile.getParentFile().mkdirs();
                cacheFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(cacheFile))) {
            oos.writeObject(dependencies);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Set<Dependency> loadCacheFromFile(File cacheFile) {
        if (!cacheFile.exists() || cacheFile.length() == 0) {
            return new HashSet<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cacheFile))) {
            Object obj = ois.readObject();
            if (obj instanceof Set<?>) {
                return handleDeserializedSet((Set<?>) obj);
            }
            return new HashSet<>();
        } catch (Exception e) {
            // Delete corrupted cache files
            cacheFile.delete();
            e.printStackTrace();
            return new HashSet<>();
        }
    }
}