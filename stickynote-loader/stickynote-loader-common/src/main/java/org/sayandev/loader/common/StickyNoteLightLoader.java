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

    private static final String LIB_FOLDER = "lib";

    protected abstract void onComplete();


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
