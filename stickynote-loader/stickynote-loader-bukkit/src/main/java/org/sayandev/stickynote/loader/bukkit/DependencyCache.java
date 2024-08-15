package org.sayandev.stickynote.loader.bukkit;

import org.sayandev.loader.common.Dependency;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class DependencyCache {

    private static final String CACHE_FILE_NAME = "dependency_cache.dat";
    private final File cacheFile;

    public DependencyCache(File dataFolder) {
        this.cacheFile = new File(dataFolder, CACHE_FILE_NAME);
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
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(cacheFile))) {
            oos.writeObject(dependencies);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}