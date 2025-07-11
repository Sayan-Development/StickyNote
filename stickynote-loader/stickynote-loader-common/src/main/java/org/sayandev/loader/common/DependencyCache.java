package org.sayandev.loader.common;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

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
            result.add(fromObject(o));
        }
        return result;
    }

    /**
     * We can't get Dependency just from casting an object to Dependency class because of relocation
     * For example plugin A will have dependency in package org.sayandev.plugin_a.libs.loader.common.Dependency
     * but plugin B will have dependency in package org.sayandev.plugin_b.libs.loader.common.Dependency
     * so casting doesn't work. but we can still get the fields we want from the object
     *
     * @param obj
     * @return Dependency object created from the given object
     */
    private Dependency fromObject(Object obj) {
        Class<?> clazz = obj.getClass();
        try {
            Dependency dependency = new Dependency(
                    clazz.getMethod("getGroup").invoke(obj).toString(),
                    clazz.getMethod("getName").invoke(obj).toString(),
                    clazz.getMethod("getVersion").invoke(obj).toString(),
                    (String) clazz.getMethod("getRelocation").invoke(obj),
                    (boolean) clazz.getMethod("isStickyLoad").invoke(obj)
            );
            dependency.setTransitiveResolved((boolean) clazz.getMethod("isTransitiveResolved").invoke(obj));
            dependency.setTransitiveDependencies((List<Dependency>) clazz.getMethod("getTransitiveDependencies").invoke(obj));
            return dependency;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
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


    public Set<Dependency> loadCacheFromFile(Logger logger, File cacheFile) {
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