package com.ajantha.apsa.util

import com.ajantha.apsa.model.RiskLevel
import com.ajantha.msc.constants.SecurityConstants

object RiskMapper {

    fun fromScore(score: Float): RiskLevel {
        return when {
            score >= SecurityConstants.HIGH_RISK_THRESHOLD ->
                RiskLevel.HIGH

            score >= SecurityConstants.MEDIUM_RISK_THRESHOLD ->
                RiskLevel.MEDIUM

            else ->
                RiskLevel.LOW
        }
    }
}
