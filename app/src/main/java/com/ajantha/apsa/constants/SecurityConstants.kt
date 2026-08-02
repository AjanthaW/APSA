package com.ajantha.msc.constants

object SecurityConstants {

    // ML Thresholds
    const val HIGH_RISK_THRESHOLD = 0.70f
    const val MEDIUM_RISK_THRESHOLD = 0.40f

    // Dataset Label Threshold
    const val HIGH_RISK_LABEL_THRESHOLD = 50f

    // Normalization
    const val MAX_PERMISSION_COUNT = 50f
    const val MAX_DANGEROUS_PERMISSION_COUNT = 10f

    const val MAX_EXPORTED_ACTIVITY_COUNT = 10f
    const val MAX_EXPORTED_SERVICE_COUNT = 10f
    const val MAX_EXPORTED_RECEIVER_COUNT = 10f
    const val MAX_EXPORTED_PROVIDER_COUNT = 10f

    const val MAX_ANDROID_SDK = 50f

    // Risk Score Weights
    const val DANGEROUS_PERMISSION_WEIGHT = 6f
    const val EXPORTED_ACTIVITY_WEIGHT = 2f
    const val EXPORTED_SERVICE_WEIGHT = 4f
    const val EXPORTED_RECEIVER_WEIGHT = 2f
    const val EXPORTED_PROVIDER_WEIGHT = 3f

    const val DEBUGGABLE_WEIGHT = 20f
    const val CLEARTEXT_WEIGHT = 15f
    const val BACKUP_WEIGHT = 5f
}