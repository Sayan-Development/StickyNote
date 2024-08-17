package org.sayandev.plugin

import org.gradle.api.Project

open class StickyLoadDependencyExtension(private val project: Project) {
    fun implementation(notation: String) {
        val parts = notation.split(":")
        if (parts.size != 3) {
            throw IllegalArgumentException("Invalid dependency notation: $notation")
        }
        val dependency = StickyLoadDependency(parts[0], parts[1], parts[2])
        project.dependencies.add("stickyload", "${dependency.group}:${dependency.name}:${dependency.version}")
    }
}