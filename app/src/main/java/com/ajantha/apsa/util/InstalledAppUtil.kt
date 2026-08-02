package com.ajantha.apsa.util

import android.Manifest
import com.ajantha.apsa.model.InstalledApp
import com.ajantha.msc.constants.SecurityConstants

fun InstalledApp.toFeatureRow(): String {

    val hasSms = permissions.contains(Manifest.permission.READ_SMS)
    val hasLocation = permissions.contains(Manifest.permission.ACCESS_FINE_LOCATION)
    val hasContacts = permissions.contains(Manifest.permission.READ_CONTACTS)
    val hasMic = permissions.contains(Manifest.permission.RECORD_AUDIO)
    val hasCamera = permissions.contains(Manifest.permission.CAMERA)

    return listOf(
        permissionCount,
        dangerousPermissionCount,
        exportedActivityCount,
        exportedServiceCount,
        exportedReceiverCount,
        exportedProviderCount,
        isDebuggable.toInt(),
        allowBackup.toInt(),
        isSystemApp.toInt(),
        usesCleartextTraffic.toInt(),
        targetSdk,
        minSdk,
        hasSms.toInt(),
        hasLocation.toInt(),
        hasContacts.toInt(),
        hasMic.toInt(),
        hasCamera.toInt()
    ).joinToString(",")
}

fun InstalledApp.toFeatureVector(): FloatArray {

    val hasSms = permissions.contains(Manifest.permission.READ_SMS)
    val hasLocation = permissions.contains(Manifest.permission.ACCESS_FINE_LOCATION)
    val hasContacts = permissions.contains(Manifest.permission.READ_CONTACTS)
    val hasMic = permissions.contains(Manifest.permission.RECORD_AUDIO)
    val hasCamera = permissions.contains(Manifest.permission.CAMERA)

    return floatArrayOf(

        permissionCount
            .coerceAtMost(SecurityConstants.MAX_PERMISSION_COUNT.toInt()) /
                SecurityConstants.MAX_PERMISSION_COUNT,

        dangerousPermissionCount
            .coerceAtMost(SecurityConstants.MAX_DANGEROUS_PERMISSION_COUNT.toInt()) /
                SecurityConstants.MAX_DANGEROUS_PERMISSION_COUNT,

        exportedActivityCount
            .coerceAtMost(SecurityConstants.MAX_EXPORTED_ACTIVITY_COUNT.toInt()) /
                SecurityConstants.MAX_EXPORTED_ACTIVITY_COUNT,

        exportedServiceCount
            .coerceAtMost(SecurityConstants.MAX_EXPORTED_SERVICE_COUNT.toInt()) /
                SecurityConstants.MAX_EXPORTED_SERVICE_COUNT,

        exportedReceiverCount
            .coerceAtMost(SecurityConstants.MAX_EXPORTED_RECEIVER_COUNT.toInt()) /
                SecurityConstants.MAX_EXPORTED_RECEIVER_COUNT,

        exportedProviderCount
            .coerceAtMost(SecurityConstants.MAX_EXPORTED_PROVIDER_COUNT.toInt()) /
                SecurityConstants.MAX_EXPORTED_PROVIDER_COUNT,

        isDebuggable.toInt().toFloat(),
        allowBackup.toInt().toFloat(),
        isSystemApp.toInt().toFloat(),
        usesCleartextTraffic.toInt().toFloat(),

        targetSdk / SecurityConstants.MAX_ANDROID_SDK,
        minSdk / SecurityConstants.MAX_ANDROID_SDK,

        hasSms.toInt().toFloat(),
        hasLocation.toInt().toFloat(),
        hasContacts.toInt().toFloat(),
        hasMic.toInt().toFloat(),
        hasCamera.toInt().toFloat()
    )
}

fun InstalledApp.toFullCsvRow(): String {

    return listOf(
        packageName.safeCsv(),
        appName.safeCsv(),
        apkPath.safeCsv(),

        targetSdk,
        minSdk,

        isDebuggable.toInt(),
        allowBackup.toInt(),
        isSystemApp.toInt(),
        usesCleartextTraffic.toInt(),

        installSource.safeCsv(),

        permissionCount,
        dangerousPermissionCount,
        exportedActivityCount,
        exportedServiceCount,
        exportedReceiverCount,
        exportedProviderCount,

        permissions.toSafeString()
            .safeCsv(),
        dangerousPermissions.toSafeString()
            .safeCsv(),

        exportedActivities.toSafeString()
            .safeCsv(),
        exportedServices.toSafeString()
            .safeCsv(),
        exportedReceivers.toSafeString()
            .safeCsv(),
        exportedProviders.toSafeString()
            .safeCsv()
    ).joinToString(",")
}

fun InstalledApp.getLabel(): Int =
    if (calculateRiskScore() >= SecurityConstants.HIGH_RISK_LABEL_THRESHOLD) 1 else 0

fun InstalledApp.calculateRiskScore(): Float {

    var score = 0f

    val dangerousPermissions = dangerousPermissionCount
        .coerceAtMost(SecurityConstants.MAX_DANGEROUS_PERMISSION_COUNT.toInt())

    val exportedActivities = exportedActivityCount
        .coerceAtMost(SecurityConstants.MAX_EXPORTED_ACTIVITY_COUNT.toInt())

    val exportedServices = exportedServiceCount
        .coerceAtMost(SecurityConstants.MAX_EXPORTED_SERVICE_COUNT.toInt())

    val exportedReceivers = exportedReceiverCount
        .coerceAtMost(SecurityConstants.MAX_EXPORTED_RECEIVER_COUNT.toInt())

    val exportedProviders = exportedProviderCount
        .coerceAtMost(SecurityConstants.MAX_EXPORTED_PROVIDER_COUNT.toInt())

    score += dangerousPermissions * SecurityConstants.DANGEROUS_PERMISSION_WEIGHT
    score += exportedActivities * SecurityConstants.EXPORTED_ACTIVITY_WEIGHT
    score += exportedServices * SecurityConstants.EXPORTED_SERVICE_WEIGHT
    score += exportedReceivers * SecurityConstants.EXPORTED_RECEIVER_WEIGHT
    score += exportedProviders * SecurityConstants.EXPORTED_PROVIDER_WEIGHT

    if (usesCleartextTraffic)
        score += SecurityConstants.CLEARTEXT_WEIGHT

    if (isDebuggable)
        score += SecurityConstants.DEBUGGABLE_WEIGHT

    if (allowBackup)
        score += SecurityConstants.BACKUP_WEIGHT

    return score.coerceIn(0f, 100f)
}