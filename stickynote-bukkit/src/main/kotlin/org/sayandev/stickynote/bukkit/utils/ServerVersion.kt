package org.sayandev.stickynote.bukkit.utils

import com.cryptomorin.xseries.reflection.XReflection

object ServerVersion {

    /**
     * @return The server's version without "1.", That would be an integer. Example return: 1.19 -> 19
     */
    @JvmStatic
    fun version() = XReflection.MINOR_NUMBER

    /**
     * @return The complete server's version. Example return: "v1_19_R2" which is 1.19.3
     */
    @JvmStatic
    fun completeVersion() = XReflection.NMS_VERSION
    
    /**
     * @return The server's patch number. Example return: 1.19.3 -> 3
     */
    @JvmStatic
    fun patchNumber() = XReflection.PATCH_NUMBER

    /**
     * @return true if the server is running on 1.8 - 1.12.2
     */
    @JvmStatic
    fun isLegacy() = !supports(13)

    /**
     * @return true if the server is running on 1.8.* or lower.
     */
    @JvmStatic
    fun isSuperLegacy() = !supports(9)

    /**
     * Checks whether the server version is equal or greater than the given version.
     * @param version the version to compare the server version with
     * @return true if the version is equal or newer, otherwise false
     */
    @JvmStatic
    fun supports(version: Int) = XReflection.supports(version)
}