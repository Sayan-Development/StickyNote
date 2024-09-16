package org.sayandev.stickynote.loader.bungee;

import com.alessiodp.libby.BungeeLibraryManager;
import com.alessiodp.libby.LibraryManager;
import net.md_5.bungee.api.plugin.Plugin;
import org.sayandev.loader.common.StickyNoteLoader;
import org.sayandev.stickynote.bungeecord.WrappedStickyNotePlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.logging.Logger;

public class StickyNoteBungeeLoader extends StickyNoteLoader {

    Plugin plugin;

    public StickyNoteBungeeLoader(Plugin plugin) {
        this(plugin, false);
    }

    public StickyNoteBungeeLoader(Plugin plugin, boolean reloadStickyNote) {
        this.plugin = plugin;

        File dataFolder = plugin.getDataFolder();
        Logger logger = plugin.getLogger();

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

        LibraryManager libraryManager = new BungeeLibraryManager(plugin);
        this.load(plugin.getDescription().getName(), dataFolder, logger, libraryManager);
    }

    @Override
    protected void onComplete() {
        new WrappedStickyNotePlugin(plugin).initialize();
    }

}
