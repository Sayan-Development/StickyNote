// Dependency.java
package org.sayandev.common;

public class Dependency {
    private final String group;
    private final String name;
    private final String version;

    public Dependency(String group, String name, String version) {
        this.group = group;
        this.name = name;
        this.version = version;
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
}