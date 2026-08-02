package com.ajantha.apsa.model

import kotlinx.serialization.Serializable

@Serializable
data class AppUiModel(
    val app: InstalledApp,
    val riskPercent: Int,
    val riskLevel: RiskLevel
)