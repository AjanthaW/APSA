package com.ajantha.apsa.ui.dashboard

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ajantha.apsa.R
import com.ajantha.apsa.ui.dashboard.component.DeviceInfoCard
import com.ajantha.apsa.ui.dashboard.component.ScanSummarySection
import com.ajantha.apsa.ui.dashboard.component.SecurityScannerCard
import com.ajantha.apsa.ui.dashboard.component.SecurityStatusCard

@Composable
fun DashboardRoute(
    modifier: Modifier = Modifier,
    onOpenApps: () -> Unit
) {

    val application = LocalContext.current.applicationContext as Application

    val viewModel: DashboardViewModel = viewModel(
        factory = DashboardViewModelFactory(application)
    )

    val state by viewModel.uiState.collectAsState()

    DashboardScreen(
        modifier = modifier,
        uiState = state,
        onScanApps = viewModel::scanApps,
        onViewApps = onOpenApps
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    uiState: DashboardUiState,
    onScanApps: () -> Unit,
    onViewApps: () -> Unit
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.security_dashboard),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                SecurityStatusCard(uiState)
            }

            item {
                DeviceInfoCard(deviceInfo = uiState.deviceInfo)
            }

            if (!uiState.isScanning) {
                item {
                    ScanSummarySection(
                        highRiskApps = uiState.highRiskApps,
                        mediumRiskApps = uiState.mediumRiskApps,
                        safeApps = uiState.safeApps
                    )
                }
            }

            item {
                SecurityScannerCard(
                    isScanning = uiState.isScanning,
                    canViewApps = uiState.canViewApps,
                    onScanApps = onScanApps,
                    onViewApps = onViewApps
                )
            }

        }
    }
}
