package com.example.scratchdetecter.vibration

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log

object WatchVibrator {

    fun warning(context: Context) {
        try {
            val vibrator: Vibrator =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val manager = context.getSystemService(
                        Context.VIBRATOR_MANAGER_SERVICE
                    ) as VibratorManager

                    manager.defaultVibrator
                } else {
                    @Suppress("DEPRECATION")
                    context.getSystemService(
                        Context.VIBRATOR_SERVICE
                    ) as Vibrator
                }

            if (!vibrator.hasVibrator()) return

            vibrator.cancel()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        300L,
                        255
                    )
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(300L)
            }
        } catch (exception: Exception) {
            Log.e(
                "VIBRATION",
                "Warning vibration failed",
                exception
            )
        }
    }

    fun stop(context: Context) {
        try {
            val vibrator: Vibrator =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val manager = context.getSystemService(
                        Context.VIBRATOR_MANAGER_SERVICE
                    ) as VibratorManager

                    manager.defaultVibrator
                } else {
                    @Suppress("DEPRECATION")
                    context.getSystemService(
                        Context.VIBRATOR_SERVICE
                    ) as Vibrator
                }

            vibrator.cancel()
        } catch (exception: Exception) {
            Log.e(
                "VIBRATION",
                "Vibration stop failed",
                exception
            )
        }
    }
}
