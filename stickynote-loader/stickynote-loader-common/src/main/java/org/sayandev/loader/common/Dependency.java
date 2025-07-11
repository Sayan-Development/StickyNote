package org.sayandev.loader.common;

import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.List;
import java.util.Objects;

public class Dependency implements Serializable {
    private static final long serialVersionUID = 1L;

    private String group;
    private String name;
    private String version;
    private String relocation;
    private boolean stickyLoad;
    private boolean transitiveResolved;
    private List<Dependency> transitiveDependencies;

    public Dependency(String group, String name, String version, String relocation, boolean stickyLoad) {
        this.group = group;
        this.name = name;
        this.version = version;
        this.stickyLoad = stickyLoad;
        this.relocation = relocation;
        this.transitiveResolved = false;
    }

    public String getGroup() {
        return group;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public @Nullable String getRelocation() {
        return relocation;
    }

    public boolean isStickyLoad() {
        return stickyLoad;
    }

    public void setStickyLoad(boolean stickyLoad) {
        this.stickyLoad = stickyLoad;
    }

    public boolean isTransitiveResolved() {
        return transitiveResolved;
    }

    public void setTransitiveResolved(boolean transitiveResolved) {
        this.transitiveResolved = transitiveResolved;
    }

    public List<Dependency> getTransitiveDependencies() {
        return transitiveDependencies;
    }

    public void setTransitiveDependencies(List<Dependency> transitiveDependencies) {
        this.transitiveDependencies = transitiveDependencies;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Dependency) {
            Dependency other = (Dependency) obj;
            return group.equals(other.group) && name.equals(other.name) && version.equals(other.version);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(group, name, version);
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeUTF(group);
        oos.writeUTF(name);
        oos.writeUTF(version);
        oos.writeObject(relocation);
        oos.writeBoolean(stickyLoad);
        oos.writeBoolean(transitiveResolved);
        oos.writeObject(transitiveDependencies);
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        try {
            this.group = ois.readUTF();
            this.name = ois.readUTF();
            this.version = ois.readUTF();
            this.relocation = (String) ois.readObject();
            this.stickyLoad = ois.readBoolean();
            this.transitiveResolved = ois.readBoolean();
            this.transitiveDependencies = (List<Dependency>) ois.readObject();
        } catch (OptionalDataException e) {
            // Probably an old cache file with missing fields
        }
    }
}