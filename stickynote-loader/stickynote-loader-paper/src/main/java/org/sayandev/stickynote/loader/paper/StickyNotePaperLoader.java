package org.sayandev.stickynote.loader.paper;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Exclusion;
import org.eclipse.aether.repository.RemoteRepository;
import org.sayandev.loader.common.Dependency;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class StickyNotePaperLoader {

    public PluginLoader pluginLoader;
    public PluginClasspathBuilder pluginClasspathBuilder;

    public StickyNotePaperLoader(PluginLoader pluginLoader, PluginClasspathBuilder pluginClasspathBuilder) throws ClassNotFoundException {
        this.pluginLoader = pluginLoader;
        this.pluginClasspathBuilder = pluginClasspathBuilder;

        load();
    }

    public Class<?> stickyNotes = Class.forName("org.sayandev.stickynote.generated.StickyNotes");

    public void load() {
        MavenLibraryResolver resolver = new MavenLibraryResolver();

        for (String repository : getRepositories(stickyNotes)) {
            String formattedRepository = repository.replace("{}", ".");
            if (formattedRepository.contains("repo.maven.apache.org/maven2")) {
                resolver.addRepository(new RemoteRepository.Builder("central", "default", "https://repo.papermc.io/repository/maven-public/").build());
                continue;
            }
            resolver.addRepository(new RemoteRepository.Builder(formattedRepository, "default", formattedRepository).build());
        }

        for (Dependency dependency : getDependencies(stickyNotes)) {
            String formattedDependency = (dependency.getGroup() + ":" + dependency.getName() + ":" + dependency.getVersion())
                    .replace("{}", ".");

            String dependencyGroup = dependency.getGroup().replace("{}", ".");
            boolean isStickyNoteDependency = dependencyGroup.equals("org.sayandev") && dependency.getName().startsWith("stickynote-");
            Collection<Exclusion> exclusions = isStickyNoteDependency
                    ? List.of(new Exclusion("*", "*", "*", "*"))
                    : Collections.emptyList();

            System.out.println("Dependency: " + formattedDependency);
            resolver.addDependency(new org.eclipse.aether.graph.Dependency(new DefaultArtifact(formattedDependency), null, false, exclusions));
        }

        pluginClasspathBuilder.addLibrary(resolver);
    }

    public List<Dependency> getDependencies(Class<?> stickyNotes) {
        return Arrays.stream(stickyNotes.getFields())
                .filter(field -> field.getName().contains("DEPENDENCY_"))
                .map(field -> {
                    try {
                        Object dependencyObject = field.get(null);
                        Class<?> dependencyFieldClass = dependencyObject.getClass();
                        return new Dependency(
                                (String) dependencyFieldClass.getMethod("getGroup").invoke(dependencyObject),
                                (String) dependencyFieldClass.getMethod("getName").invoke(dependencyObject),
                                (String) dependencyFieldClass.getMethod("getVersion").invoke(dependencyObject),
                                (String) dependencyFieldClass.getMethod("getRelocation").invoke(dependencyObject),
                                (boolean) dependencyFieldClass.getMethod("isStickyLoad").invoke(dependencyObject)
                        );
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }).toList();
    }

    public List<String> getRepositories(Class<?> stickyNotes) {
        return Arrays.stream(stickyNotes.getFields())
                .filter(field -> field.getName().startsWith("REPOSITORY_"))
                .map(field -> {
                    try {
                        return (String) field.get(null);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }).toList();
    }
}
