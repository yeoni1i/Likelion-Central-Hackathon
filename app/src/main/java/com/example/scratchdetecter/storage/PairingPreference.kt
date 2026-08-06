package com.example.scratchdetecter.storage

import android.content.Context

class PairingPreference(context: Context) {
    private val preferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    fun isPaired(): Boolean = preferences.getBoolean(KEY_IS_PAIRED, false)

    fun savePairedDevice(serverDeviceId: Long?, deviceName: String) {
        preferences.edit()
            .putBoolean(KEY_IS_PAIRED, true)
            .putLong(KEY_SERVER_DEVICE_ID, serverDeviceId ?: -1L)
            .putString(KEY_DEVICE_NAME, deviceName)
            .apply()
    }

    fun serverDeviceIdOrDefault(defaultValue: Long = 1L): Long {
        val stored = preferences.getLong(KEY_SERVER_DEVICE_ID, -1L)
        return if (stored > 0) stored else defaultValue
    }

    fun clear() {
        preferences.edit().clear().apply()
    }

    private companion object {
        const val FILE_NAME = "watch_pairing"
        const val KEY_IS_PAIRED = "is_paired"
        const val KEY_SERVER_DEVICE_ID = "server_device_id"
        const val KEY_DEVICE_NAME = "device_name"
    }
}
