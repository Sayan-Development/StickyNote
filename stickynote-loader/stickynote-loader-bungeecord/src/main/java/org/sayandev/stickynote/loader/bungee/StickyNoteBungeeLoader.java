package org.sayandev.stickynote.loader.bungee;

import com.alessiodp.libby.BungeeLibraryManager;
import com.alessiodp.libby.LibraryManager;
import net.md_5.bungee.api.plugin.Plugin;
import org.sayandev.loader.common.StickyNoteLoader;
import org.sayandev.stickynote.bungeecord.WrappedStickyNotePlugin;

import java.io.File;
import java.util.logging.Logger;

public class StickyNoteBungeeLoader extends StickyNoteLoader {

    Plugin plugin;

    public StickyNoteBungeeLoader(Plugin plugin) throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
        super(plugin.getDescription().getName());
        this.plugin = plugin;

        File dataFolder = plugin.getDataFolder();
        Logger logger = plugin.getLogger();

        LibraryManager libraryManager = new BungeeLibraryManager(plugin, new File(dataFolder.getParentFile(), "stickynote").getAbsolutePath());
        this.load(plugin.getDescription().getName(), dataFolder.getParentFile(), logger, libraryManager, false);
    }

    @Override
    protected void onComplete() {
        new WrappedStickyNotePlugin(plugin).initialize();
    }

}
