package com.example.scratchdetecter.detection

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class SensorCollector(
    context: Context,
    private val onSample: (SensorSample) -> Unit
) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    private var latestAccX = 0f
    private var latestAccY = 0f
    private var latestAccZ = 0f
    private var latestGyroX = 0f
    private var latestGyroY = 0f
    private var latestGyroZ = 0f
    private var lastSampleTime = 0L
    private var started = false

    private val listener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            event ?: return
            when (event.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> {
                    latestAccX = event.values[0]
                    latestAccY = event.values[1]
                    latestAccZ = event.values[2]
                }
                Sensor.TYPE_GYROSCOPE -> {
                    latestGyroX = event.values[0]
                    latestGyroY = event.values[1]
                    latestGyroZ = event.values[2]
                }
            }

            val now = System.currentTimeMillis()
            if (now - lastSampleTime < SAMPLE_INTERVAL_MS) return
            lastSampleTime = now

            onSample(
                SensorSample(
                    latestAccX,
                    latestAccY,
                    latestAccZ,
                    latestGyroX,
                    latestGyroY,
                    latestGyroZ
                )
            )
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
    }

    fun start() {
        if (started) return
        requireNotNull(accelerometer) { "가속도 센서를 사용할 수 없습니다." }
        requireNotNull(gyroscope) { "자이로 센서를 사용할 수 없습니다." }

        sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        sensorManager.registerListener(listener, gyroscope, SensorManager.SENSOR_DELAY_GAME)
        started = true
    }

    fun stop() {
        if (!started) return
        sensorManager.unregisterListener(listener)
        started = false
    }
}
