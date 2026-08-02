package com.ajantha.apsa.model

import android.graphics.drawable.Drawable
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class InstalledApp(

    // Identity
    val packageName: String,
    val appName: String,
    val apkPath: String,
    @Transient
    val icon: Drawable? = null,

    // SDK
    val targetSdk: Int,
    val minSdk: Int,

    // Security flags
    val isDebuggable: Boolean,
    val allowBackup: Boolean,
    val isSystemApp: Boolean,
    val usesCleartextTraffic: Boolean,

    // Source
    val installSource: String,

    // Permissions
    val permissions: List<String>,
    val dangerousPermissions: List<String>,

    // Components (RAW)
    val exportedActivities: List<String>,
    val exportedServices: List<String>,
    val exportedReceivers: List<String>,
    val exportedProviders: List<String>,

    // Derived counts (IMPORTANT)
    val permissionCount: Int,
    val dangerousPermissionCount: Int,
    val exportedActivityCount: Int,
    val exportedServiceCount: Int,
    val exportedReceiverCount: Int,
    val exportedProviderCount: Int
)