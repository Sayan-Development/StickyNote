package org.sayandev.stickynote.loader.bukkit;

import com.alessiodp.libby.BukkitLibraryManager;
import com.alessiodp.libby.Library;
import com.alessiodp.libby.logging.LogLevel;
import com.alessiodp.libby.transitive.ExcludedDependency;
import com.alessiodp.libby.transitive.TransitiveDependencyHelper;
import org.bukkit.plugin.java.JavaPlugin;
import org.sayandev.stickynote.bukkit.WrappedStickyNotePlugin;

import java.io.File;
import java.util.*;

public class StickyNoteBukkitLoader {

    public static void load(JavaPlugin plugin) {
        load(plugin, false);
    }

    public static void load(JavaPlugin plugin, boolean usePaperLoader) {
        try {
            Class<?> stickyNotes = Class.forName("org.sayandev.stickynote.generated.StickyNotes");
            List<Object> dependencies = Arrays.stream(stickyNotes.getFields()).filter(field -> field.getName().startsWith("DEPENDENCY_")).map(field -> {
                try {
                    return field.get(null);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }).toList();
            List<String> repositories = Arrays.stream(stickyNotes.getFields()).filter(field -> field.getName().startsWith("REPOSITORY_")).map(field -> {
                try {
                    return (String) field.get(null);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }).toList();

            Object relocation = stickyNotes.getField("RELOCATION").get(stickyNotes);
            String relocationFrom = (String) relocation.getClass().getMethod("getFrom").invoke(relocation);
            String relocationTo = (String) relocation.getClass().getMethod("getTo").invoke(relocation);

            // TODO: utilize paper library manager
            BukkitLibraryManager libraryManager = new BukkitLibraryManager(plugin);
            libraryManager.setLogLevel(LogLevel.DEBUG);
            libraryManager.addRepository("https://repo.sayandev.org/snapshots");

            Map<String, String> manualRelocations = new HashMap<>();
            manualRelocations.put("com.github.patheloper.pathetic", "patheloper");
            manualRelocations.put("com.github.cryptomorin", "cryptomorin");
            manualRelocations.put("com.google.code.gson", "gson");

            for (Object dependency : dependencies) {
                String group = ((String) dependency.getClass().getMethod("getGroup").invoke(dependency)).replace(".", "{}");
                String name = (String) dependency.getClass().getMethod("getName").invoke(dependency);
                Library.Builder libraryBuilder = Library.builder()
                        .groupId(group)
                        .artifactId(name)
                        .version((String) dependency.getClass().getMethod("getVersion").invoke(dependency));

                if (!name.contains("stickynote") && !name.equals("kotlin-stdlib") && !name.equals("kotlin-reflect")) {
                    if (manualRelocations.containsKey(group)) {
                        libraryBuilder.relocate(group, relocationTo + "{}libs{}" + manualRelocations.get(group));
                    } else {
                        String replacedGroup = group.replace("{}", ".");
                        String[] groupParts = replacedGroup.split("\\.");
                        libraryBuilder.relocate(group, relocationTo + "{}libs{}" + groupParts[groupParts.length - 1]);
                    }
                }
                if (name.contains("stickynote")) {
                    libraryBuilder.relocate(relocationFrom, relocationTo);
                } else {
                    libraryBuilder.resolveTransitiveDependencies(true);
                }

                libraryManager.loadLibrary(libraryBuilder.build());
            }

            new WrappedStickyNotePlugin(plugin);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
