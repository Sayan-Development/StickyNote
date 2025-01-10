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
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(cacheFile))) {
            oos.writeObject(dependencies);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}