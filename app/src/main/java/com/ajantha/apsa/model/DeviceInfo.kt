package com.ajantha.apsa.model

import android.os.Build

data class DeviceInfo(
    val model: String = Build.MODEL,
    val brand: String = Build.BRAND,
    val androidVersion: String = Build.VERSION.RELEASE
)