package org.sayandev.stickynote.core.color

data class ARGB(
        val alpha: Int,
        val red: Int,
        val green: Int,
        val blue: Int,
    ) {

        constructor(red: Int, green: Int, blue: Int): this(255, red, green, blue)

        fun asInt(): Int {
            return (alpha shl 24) or (red shl 16) or (green shl 8) or blue
        }

        init {
            require(alpha in 0..255) { "Alpha must be between 0 and 255" }
            require(red in 0..255) { "Red must be between 0 and 255" }
            require(green in 0..255) { "Green must be between 0 and 255" }
            require(blue in 0..255) { "Blue must be between 0 and 255" }
        }

        companion object {
            fun fromInt(argb: Int): ARGB {
                return ARGB(
                    (argb shr 24) and 0xFF,
                    (argb shr 16) and 0xFF,
                    (argb shr 8) and 0xFF,
                    argb and 0xFF
                )
            }
        }
    }