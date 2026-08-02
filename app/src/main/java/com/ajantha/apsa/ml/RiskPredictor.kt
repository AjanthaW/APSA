package com.ajantha.apsa.ml

import android.content.Context
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder

class RiskPredictor(context: Context) {
    private val interpreter: Interpreter

    init {
        val file = context.assets.open("risk_model.tflite")
        val bytes = file.readBytes()
        val buffer = ByteBuffer.allocateDirect(bytes.size)
        buffer.order(ByteOrder.nativeOrder())
        buffer.put(bytes)

        interpreter = Interpreter(buffer)
    }

    fun predict(features: FloatArray): Float {
        val input = arrayOf(features)
        val output = Array(1) { FloatArray(1) }

        interpreter.run(
            input,
            output
        )

        return output[0][0] // risk score (0–1)
    }
}