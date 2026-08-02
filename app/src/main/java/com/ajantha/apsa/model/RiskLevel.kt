package com.ajantha.apsa.model

import kotlinx.serialization.Serializable

@Serializable
enum class RiskLevel(val title: String) {
    HIGH("High"),
    MEDIUM("Medium"),
    LOW("Low")
}