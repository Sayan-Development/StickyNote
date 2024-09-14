package org.sayandev.stickynote.loader.bungee;

import com.alessiodp.libby.LibraryManager;
import com.alessiodp.libby.VelocityLibraryManager;
import com.velocitypowered.api.proxy.ProxyServer;
import org.sayandev.loader.common.StickyNoteLoader;
import org.sayandev.stickynote.velocity.WrappedStickyNotePlugin;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

public class StickyNoteVelocityLoader extends StickyNoteLoader {

    private final Object plugin;
    private final String id;
    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;

    public StickyNoteVelocityLoader(Object plugin, String id, ProxyServer server, Logger logger, Path dataDirectory) {
        this(plugin, id, server, logger, dataDirectory, false);
    }

    public StickyNoteVelocityLoader(Object plugin, String id, ProxyServer server, Logger logger, Path dataDirectory, boolean reloadStickyNote) {
        this.plugin = plugin;
        this.id = id;
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;

        if (reloadStickyNote) {
            File libDirectory = new File(dataDirectory.toFile(), "lib");
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

        LibraryManager libraryManager = new VelocityLibraryManager<>(plugin, logger, dataDirectory, server.getPluginManager());
        this.load(id, dataDirectory.toFile(), java.util.logging.Logger.getLogger(id), libraryManager);
    }

    @Override
    protected void onComplete() {
        new WrappedStickyNotePlugin(plugin, id, server, logger, dataDirectory).initialize();
    }

}
