package org.sayandev.stickynote.core.utils

import java.text.DecimalFormat
import kotlin.math.pow

class MilliCounter {
    var decimalFormat = DecimalFormat("#.00")

    private var time: Long = 0
    private var elapsed = 0.0
    var stopped: Boolean = false

    fun start() {
        stopped = false
        time = System.nanoTime()
    }

    fun stop() {
        elapsed = (System.nanoTime() - time) / 1_000_000.0
        stopped = true
    }

    fun get(): Float {
        return if (!stopped) {
            ((System.nanoTime() - time) / 1_000_000.0).toFloat()
        } else decimalFormat.format(elapsed).toFloat()
    }

    fun getExact(): Double {
        return elapsed
    }
}