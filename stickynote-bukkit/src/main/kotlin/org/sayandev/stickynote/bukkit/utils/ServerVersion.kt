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

    /**
     * Checks whether the server version is equal to the given version and patch.
     * @param version the version to compare the server version with
     * @param patchNumber the patch number to compare the server version with
     * @return true if the version and patch are equal, otherwise false
     */
    @JvmStatic
    fun equals(version: Int, patchNumber: Int): Boolean = version() == version && patchNumber() == patchNumber

    /**
     * Checks whether the server version is equal to the given version and patch is equal or newer.
     * @param version the version to compare the server version with
     * @param patchNumber the patch number to compare the server version with
     * @return true if the version is equal and the patch is equal or newer, otherwise false
     */
    @JvmStatic
    fun containsPatch(version: Int, patchNumber: Int): Boolean = version() == version && patchNumber() >= patchNumber

    /**
     * Checks whether the server version is equal to the given version and patch or newer.
     * @param version the version to compare the server version with
     * @param patchNumber the patch number to compare the server version with
     * @return true if the version and patch are equal or newer, otherwise false
     */
    @JvmStatic
    fun isAtLeast(version: Int, patchNumber: Int): Boolean {
        return if (version() == version) {
            patchNumber() >= patchNumber
        } else {
            version() > version
        }
    }
}