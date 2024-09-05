package org.sayandev.plugin

import java.io.Serializable

data class StickyLoadDependency(
    val group: String,
    val name: String,
    val version: String,
    val relocation: String?
): Serializable {

    companion object {
        private const val serialVersionUID = 1L
    }
}