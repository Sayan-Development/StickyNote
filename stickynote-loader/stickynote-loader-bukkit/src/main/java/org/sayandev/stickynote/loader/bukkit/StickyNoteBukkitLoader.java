package org.sayandev.stickynote.loader.bukkit;

import com.alessiodp.libby.BukkitLibraryManager;
import com.alessiodp.libby.LibraryManager;
import com.alessiodp.libby.PaperLibraryManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.sayandev.loader.common.StickyNoteLoader;
import org.sayandev.stickynote.bukkit.WrappedStickyNotePlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.logging.Logger;

public class StickyNoteBukkitLoader extends StickyNoteLoader {

    public JavaPlugin javaPlugin;

    public StickyNoteBukkitLoader(JavaPlugin javaPlugin) {
        this(javaPlugin, false);
    }

    public StickyNoteBukkitLoader(JavaPlugin javaPlugin, boolean reloadStickyNote) {
        this.javaPlugin = javaPlugin;

        File dataFolder = javaPlugin.getDataFolder();
        Logger logger = javaPlugin.getLogger();

        if (reloadStickyNote) {
            File libDirectory = new File(dataFolder, "lib");
            if (!libDirectory.exists()) return;
            File orgDirectory = new File(libDirectory, "org");
            if (!orgDirectory.exists()) return;
            File sayandevDirectory = new File(orgDirectory, "sayandev");
            if (sayandevDirectory.exists()) {
                logger.info("Deleting old sayandev directory...");
                try {
                    Files.walk(sayandevDirectory.toPath()).sorted(Comparator.reverseOrder()).forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        LibraryManager libraryManager = getLibraryManager(javaPlugin);
        this.load(javaPlugin.getName(), dataFolder, logger, libraryManager);
    }

    @Override
    protected void onComplete() {
        new WrappedStickyNotePlugin(javaPlugin);
    }

    private static LibraryManager getLibraryManager(JavaPlugin plugin) {
        if (plugin.getResource("paper-plugin.yml") != null) {
            plugin.getLogger().info("paper-plugin detected, using paper library loader...");
            return new PaperLibraryManager(plugin);
        } else {
            return new BukkitLibraryManager(plugin);
        }
    }
}