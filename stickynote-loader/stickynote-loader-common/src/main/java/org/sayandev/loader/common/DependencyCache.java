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
        if (!cacheFile.exists()) {
            return new HashSet<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cacheFile))) {
            return (Set<Dependency>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new HashSet<>();
        }
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
        Set<Dependency> cache = new HashSet<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cacheFile))) {
            Object obj = ois.readObject();
            if (obj instanceof Set<?>) {
                Set<?> set = (Set<?>) obj;
                for (Object o : set) {
                    if (o instanceof Dependency) {
                        cache.add((Dependency) o);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cache;
    }
}