package org.sayandev.loader.common;

import org.sayandev.KotlinLight;
import org.sayandev.LightClassLoader;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.logging.Logger;

public abstract class StickyNoteLightLoader {

    private static final ConcurrentHashMap<Dependency, CompletableFuture<Void>> loadingLibraries = new ConcurrentHashMap<>();
    private static final ExecutorService executorService = Executors.newWorkStealingPool(Runtime.getRuntime().availableProcessors());
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    public static final List<String> exclusions = Arrays.asList("kotlin-stdlib", "kotlin-reflect", "kotlin", "kotlin-stdlib-jdk8", "kotlin-stdlib-jdk7", "kotlinx", "kotlinx-coroutines", "takenaka", "mappings", "gson");
    public static final Map<String, String> relocations = new HashMap<>();

    private static final String LIB_FOLDER = "lib";

    private final List<String> transitiveExcluded = Arrays.asList("xseries", "stickynote");

    protected StickyNoteLightLoader() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
    }

    protected abstract void onComplete();


    Class<?> stickyNotes = Class.forName("org.sayandev.stickynote.generated.StickyNotes");
    Boolean relocate = (Boolean) stickyNotes.getField("RELOCATE").get(stickyNotes);

    public void load(String id, File dataDirectory, Logger logger, LightClassLoader classLoader) {
        File libFolder = generateLibDirectory(dataDirectory);

        KotlinLight.shine(classLoader, libFolder);
        new StickyNoteLightLoaderKt() {
            @Override
            protected void onComplete() {
                StickyNoteLightLoader.this.onComplete();
            }
        }.load(id, dataDirectory, logger, classLoader);
    }

    public static File generateLibDirectory(File root) {
        return new File(new File(root, "stickynote"), LIB_FOLDER);
    }
}
