package org.sayandev.stickynote.bukkit.nms.enum

enum class ModelPart(val mask: Byte) {
    CAPE(0x01),
    JACKET(0x02),
    LEFT_SLEEVE(0x04),
    RIGHT_SLEEVE(0x08),
    LEFT_PANTS(0x10),
    RIGHT_PANTS(0x20),
    HAT(0x40);

    companion object {
        fun getMasks(vararg parts: ModelPart): Byte {
            var bytes: Byte = 0
            for (part in parts) {
                bytes = (bytes + part.mask).toByte()
            }
            return bytes
        }

        val allBitMasks: Byte = entries.fold(0.toByte()) { acc, modelPart -> (acc + modelPart.mask).toByte() }

    }
}