package org.sayandev.stickynote.bukkit.nms

class PacketContainer(
    val packet: Any
) {
    /**
     * Returns the name of the packet.
     * Example of returning string: "PacketPlayOutEntityMetadata"
     * @return The packet name
     */
    var name: String? = null

    init {
        try {
            val rawNameSplit = packet.toString().split("\\.".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            val rawNameSplit2 = rawNameSplit[rawNameSplit.size - 1].split("@".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            this.name = rawNameSplit2[0]
        } catch (e: Exception) {
            throw IllegalArgumentException("Given object is not a packet instance.")
        }
    }
}
