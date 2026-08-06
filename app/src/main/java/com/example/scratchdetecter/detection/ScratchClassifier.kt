package com.example.scratchdetecter.detection

import android.content.Context
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class ScratchClassifier(context: Context) {

    private val interpreter: Interpreter

    init {
        interpreter = Interpreter(
            loadModelFile(context)
        )
    }

    private fun loadModelFile(
        context: Context
    ): MappedByteBuffer {

        val fileDescriptor =
            context.assets.openFd("scratch_binary_model.tflite")

        val inputStream =
            FileInputStream(fileDescriptor.fileDescriptor)

        val fileChannel =
            inputStream.channel

        return fileChannel.map(
            FileChannel.MapMode.READ_ONLY,
            fileDescriptor.startOffset,
            fileDescriptor.declaredLength
        )
    }

    fun predict(
        features: FloatArray
    ): Float {

        val input =
            arrayOf(features)

        val output =
            Array(1) { FloatArray(1) }

        interpreter.run(input, output)

        return output[0][0]
    }

    fun close() {
        interpreter.close()
    }
}