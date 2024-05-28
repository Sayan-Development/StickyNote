package org.sayandev.stickynote.core.utils

import com.google.gson.GsonBuilder

object Gson {

    @JvmStatic
    val gson = GsonBuilder().setPrettyPrinting().create()
}