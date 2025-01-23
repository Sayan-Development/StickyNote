package org.sayandev.stickynote.loader.velocity;

import com.alessiodp.libby.LibraryManager;
import com.alessiodp.libby.VelocityLibraryManager;
import com.github.shynixn.mccoroutine.velocity.SuspendingPluginContainer;
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
    private final SuspendingPluginContainer suspendingPluginContainer;

    public StickyNoteVelocityLoader(Object plugin, String id, ProxyServer server, Logger logger, Path dataDirectory) throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
        this(plugin, id, server, logger, dataDirectory, null);
    }

    public StickyNoteVelocityLoader(Object plugin, String id, ProxyServer server, Logger logger, Path dataDirectory, SuspendingPluginContainer suspendingPluginContainer) throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
        super();
        this.plugin = plugin;
        this.id = id;
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.suspendingPluginContainer = suspendingPluginContainer;

        LibraryManager libraryManager = new VelocityLibraryManager<>(plugin, logger, generateLibDirectory(dataDirectory.getParent().toFile()).toPath(), server.getPluginManager());
        this.load(id, dataDirectory.toFile().getParentFile(), java.util.logging.Logger.getLogger(id), libraryManager);
    }

    @Override
    protected void onComplete() {
        new WrappedStickyNotePlugin(plugin, id, server, logger, dataDirectory, suspendingPluginContainer).initialize();
    }

}
