package org.sayandev.stickynote.loader.bungee;

import com.alessiodp.libby.LibraryManager;
import com.alessiodp.libby.VelocityLibraryManager;
import com.velocitypowered.api.proxy.ProxyServer;
import org.sayandev.loader.common.StickyNoteLoader;
import org.sayandev.stickynote.velocity.WrappedStickyNotePlugin;
import org.slf4j.Logger;

import java.nio.file.Path;

public class StickyNoteVelocityLoader extends StickyNoteLoader {

    private final Object plugin;
    private final String id;
    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;

    public StickyNoteVelocityLoader(Object plugin, String id, ProxyServer server, Logger logger, Path dataDirectory) {
        this.plugin = plugin;
        this.id = id;
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;

        LibraryManager libraryManager = new VelocityLibraryManager(plugin, logger, dataDirectory, server.getPluginManager());
        this.load(id, dataDirectory.toFile(), java.util.logging.Logger.getLogger(id), libraryManager);
    }

    @Override
    protected void onComplete() {
        new WrappedStickyNotePlugin(plugin, id, server, logger, dataDirectory).initialize();
    }

}
