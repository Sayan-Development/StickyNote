package org.sayandev.stickynote.loader.bukkit;

import org.bukkit.plugin.java.JavaPlugin;
import org.sayandev.BukkitClassLoader;
import org.sayandev.loader.common.StickyNoteLightLoader;
import org.sayandev.stickynote.bukkit.WrappedStickyNotePlugin;

public class StickyNoteBukkitLightLoader extends StickyNoteLightLoader {

    public JavaPlugin javaPlugin;

    public StickyNoteBukkitLightLoader(JavaPlugin javaPlugin) throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
        super();
        this.javaPlugin = javaPlugin;

        this.load(javaPlugin.getName(), javaPlugin.getDataFolder().getParentFile(), javaPlugin.getLogger(), new BukkitClassLoader(javaPlugin));
    }

    @Override
    protected void onComplete() {
        new WrappedStickyNotePlugin(javaPlugin);
    }
}