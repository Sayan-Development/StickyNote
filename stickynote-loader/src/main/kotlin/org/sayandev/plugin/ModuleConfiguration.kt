package org.sayandev.plugin

import java.io.Serializable

data class ModuleConfiguration(
    var moduleId: String,
    var version: String
) : Serializable {

    companion object {
        private const val serialVersionUID = 1L
    }
}
