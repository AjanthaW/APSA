package com.ajantha.apsa.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ajantha.apsa.model.RiskLevel
import com.ajantha.apsa.ui.dashboard.DashboardUiState
import kotlinx.coroutines.flow.first

private val Context.dashboardDataStore by preferencesDataStore(
    name = "dashboard_preferences"
)

class DashboardPreferences(
    private val context: Context
) {

    companion object {

        private val LAST_SCAN = longPreferencesKey("last_scan")
        private val TOTAL_APPS = intPreferencesKey("total_apps")
        private val HIGH_RISK = intPreferencesKey("high_risk")
        private val MEDIUM_RISK = intPreferencesKey("medium_risk")
        private val SAFE_APPS = intPreferencesKey("safe_apps")
        private val AVG_RISK = intPreferencesKey("avg_risk")
        private val OVERALL_RISK = stringPreferencesKey("overall_risk")
    }

    suspend fun saveDashboardState(
        state: DashboardUiState
    ) {
        context.dashboardDataStore.edit { pref ->
            pref[LAST_SCAN] = state.lastScanTime ?: 0L
            pref[TOTAL_APPS] = state.totalApps
            pref[HIGH_RISK] = state.highRiskApps
            pref[MEDIUM_RISK] = state.mediumRiskApps
            pref[SAFE_APPS] = state.safeApps
            pref[AVG_RISK] = state.averageRiskScore
            pref[OVERALL_RISK] = state.overallRiskLevel.toPreferenceValue()
        }
    }

    suspend fun loadDashboardState(): DashboardUiState {
        val pref = context.dashboardDataStore.data.first()

        return DashboardUiState(
            totalApps = pref[TOTAL_APPS] ?: 0,
            highRiskApps = pref[HIGH_RISK] ?: 0,
            mediumRiskApps = pref[MEDIUM_RISK] ?: 0,
            safeApps = pref[SAFE_APPS] ?: 0,
            averageRiskScore = pref[AVG_RISK] ?: 0,
            overallRiskLevel = (pref[OVERALL_RISK] ?: "").toRiskLevel(),
            lastScanTime = pref[LAST_SCAN]
        )
    }
}

fun RiskLevel.toPreferenceValue(): String = name

fun String.toRiskLevel(): RiskLevel =
    runCatching { RiskLevel.valueOf(this) }.getOrDefault(RiskLevel.LOW)