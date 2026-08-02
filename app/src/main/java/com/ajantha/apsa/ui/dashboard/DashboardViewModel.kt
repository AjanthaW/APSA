package com.ajantha.apsa.ui.dashboard

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ajantha.apsa.model.DeviceInfo
import com.ajantha.apsa.model.RiskLevel
import com.ajantha.apsa.repository.AppRepository
import com.ajantha.apsa.util.DashboardPreferences
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class DashboardViewModel(
    private val repository: AppRepository,
    private val preferences: DashboardPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = preferences.loadDashboardState()
        }
    }

    fun scanApps() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isScanning = true)
            }

            delay(1500.milliseconds)

            val apps = repository.getApps()

            val averageRisk =
                apps.map { it.riskPercent }
                    .average()
                    .toInt()

            _uiState.value =
                DashboardUiState(
                    isScanning = false,
                    canViewApps = true,

                    totalApps = apps.size,

                    highRiskApps =
                        apps.count {
                            it.riskLevel == RiskLevel.HIGH
                        },

                    mediumRiskApps =
                        apps.count {
                            it.riskLevel == RiskLevel.MEDIUM
                        },

                    safeApps =
                        apps.count {
                            it.riskLevel == RiskLevel.LOW
                        },

                    averageRiskScore = averageRisk,

                    overallRiskLevel =
                        calculateOverallRisk(
                            averageRisk
                        ),

                    lastScanTime =
                        System.currentTimeMillis()
                )

            preferences.saveDashboardState(
                _uiState.value
            )
        }
    }
}

data class DashboardUiState(
    val isScanning: Boolean = false,
    val canViewApps: Boolean = false,
    val totalApps: Int = 0,
    val highRiskApps: Int = 0,
    val mediumRiskApps: Int = 0,
    val safeApps: Int = 0,
    val averageRiskScore: Int = 0,
    val overallRiskLevel: RiskLevel = RiskLevel.LOW,
    val lastScanTime: Long? = null,
    val deviceInfo: DeviceInfo = DeviceInfo()
)

private fun calculateOverallRisk(
    averageRiskScore: Int
): RiskLevel {

    return when {
        averageRiskScore >= 75 -> RiskLevel.HIGH
        averageRiskScore >= 40 -> RiskLevel.MEDIUM
        else -> RiskLevel.LOW
    }
}

class DashboardViewModelFactory(
    application: Application
) : ViewModelProvider.Factory {

    private val repository = AppRepository(application)
    private val preferences = DashboardPreferences(application)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {

            return DashboardViewModel(
                repository,
                preferences
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel")
    }
}