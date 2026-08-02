package com.ajantha.apsa.ui.appdetail

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ajantha.apsa.ui.appdetail.analysis.SecurityAiDialog
import com.ajantha.apsa.ui.appdetail.component.AIAnalysisCard
import com.ajantha.apsa.ui.appdetail.component.AppHeaderCard
import com.ajantha.apsa.ui.appdetail.component.ApplicationInformationCard
import com.ajantha.apsa.ui.appdetail.component.RiskRow
import com.ajantha.apsa.ui.appdetail.component.SecuritySection
import com.ajantha.apsa.ui.appdetail.component.SecuritySummaryCard

@Composable
fun AppDetailRoute(
    packageName: String,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {

    val context = LocalContext.current
    val application = context.applicationContext as Application

    val detailViewModel: AppDetailViewModel = viewModel(
        factory = AppDetailViewModelFactory(application)
    )

    val gemmaViewModel: GemmaViewModel = viewModel()

    val detailState by detailViewModel.uiState.collectAsState()
    val gemmaState by gemmaViewModel.uiState.collectAsState()
    val analysisTitle by gemmaViewModel.analysisTitle.collectAsState()

    LaunchedEffect(packageName) {
        detailViewModel.load(packageName)
    }

    LaunchedEffect(Unit) {
        gemmaViewModel.initializeModel(context)
    }

    AppDetailScreen(
        modifier = modifier,
        packageName = packageName,
        detailState = detailState,
        gemmaState = gemmaState,
        analysisTitle = analysisTitle,
        onBack = onBack,
        onAnalyzeSummary = {
            val state = detailState
            if (state is AppDetailUiState.Success) {
                gemmaViewModel.analyzeAppSummary(
                    state.uiModel
                )
            }
        },
        onAnalyzePermission = { appName, permission ->
            gemmaViewModel.analyzePermission(
                appName,
                permission
            )
        },
        onAnalyzeComponent = { appName, type, name ->
            gemmaViewModel.analyzeComponent(
                appName = appName,
                componentType = type,
                componentName = name
            )
        },
        onDismissDialog = {
            gemmaViewModel.resetState()
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDetailScreen(
    modifier: Modifier = Modifier,
    packageName: String,
    detailState: AppDetailUiState,
    gemmaState: GemmaUiState,
    analysisTitle: String,
    onBack: () -> Unit,
    onAnalyzeSummary: () -> Unit,
    onAnalyzePermission: (String, String) -> Unit,
    onAnalyzeComponent: (
        appName: String,
        componentType: String,
        componentName: String
    ) -> Unit,
    onDismissDialog: () -> Unit
) {
    var showPermissions by rememberSaveable { mutableStateOf(false) }
    var showActivities by rememberSaveable { mutableStateOf(false) }
    var showServices by rememberSaveable { mutableStateOf(false) }
    var showReceivers by rememberSaveable { mutableStateOf(false) }
    var showProviders by rememberSaveable { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "App Details",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = packageName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        when (val state = detailState) {
            is AppDetailUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is AppDetailUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
            }

            is AppDetailUiState.Success -> {
                val uiModel = state.uiModel

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AppHeaderCard(uiModel)

                    ApplicationInformationCard(app = uiModel.app)

                    SecuritySummaryCard(app = uiModel.app)

                    AIAnalysisCard(
                        enabled = gemmaState !is GemmaUiState.Loading,
                        onClick = {
                            onAnalyzeSummary()
                            showDialog = true
                        }
                    )

                    SecuritySection(
                        title = "Permissions (${uiModel.app.permissionCount})",
                        expanded = showPermissions,
                        onToggle = { showPermissions = !showPermissions }
                    ) {
                        uiModel.app.permissions.sorted().forEach { permission ->
                            RiskRow(
                                title = permission,
                                subtitle = "Permission",
                                onExplain = {
                                    onAnalyzePermission(
                                        uiModel.app.appName,
                                        permission
                                    )
                                    showDialog = true
                                }
                            )
                        }
                    }

                    SecuritySection(
                        title = "Exported Activities (${uiModel.app.exportedActivityCount})",
                        expanded = showActivities,
                        onToggle = { showActivities = !showActivities }
                    ) {
                        uiModel.app.exportedActivities.sorted().forEach { activity ->
                            RiskRow(
                                title = activity,
                                subtitle = "Exported activity",
                                onExplain = {
                                    onAnalyzeComponent(
                                        uiModel.app.appName,
                                        "Exported Activity",
                                        activity
                                    )
                                    showDialog = true
                                }
                            )
                        }
                    }

                    SecuritySection(
                        title = "Exported Services (${uiModel.app.exportedServiceCount})",
                        expanded = showServices,
                        onToggle = { showServices = !showServices }
                    ) {
                        uiModel.app.exportedServices.sorted().forEach { service ->
                            RiskRow(
                                title = service,
                                subtitle = "Exported service",
                                onExplain = {
                                    onAnalyzeComponent(
                                        uiModel.app.appName,
                                        "Exported Service",
                                        service
                                    )
                                    showDialog = true
                                }
                            )
                        }
                    }

                    SecuritySection(
                        title = "Exported Receivers (${uiModel.app.exportedReceiverCount})",
                        expanded = showReceivers,
                        onToggle = { showReceivers = !showReceivers }
                    ) {
                        uiModel.app.exportedReceivers.sorted().forEach { receiver ->
                            RiskRow(
                                title = receiver,
                                subtitle = "Exported receiver",
                                onExplain = {
                                    onAnalyzeComponent(
                                        uiModel.app.appName,
                                        "Exported Receiver",
                                        receiver
                                    )
                                    showDialog = true
                                }
                            )
                        }
                    }

                    SecuritySection(
                        title = "Exported Providers (${uiModel.app.exportedProviderCount})",
                        expanded = showProviders,
                        onToggle = { showProviders = !showProviders }
                    ) {
                        uiModel.app.exportedProviders.sorted().forEach { provider ->
                            RiskRow(
                                title = provider,
                                subtitle = "Exported provider",
                                onExplain = {
                                    onAnalyzeComponent(
                                        uiModel.app.appName,
                                        "Exported Provider",
                                        provider
                                    )
                                    showDialog = true
                                }
                            )
                        }
                    }

                    if (showDialog && gemmaState.shouldShowDialog) {
                        SecurityAiDialog(
                            title = analysisTitle,
                            state = gemmaState,
                            onDismiss = {
                                showDialog = false
                                onDismissDialog()

                            }
                        )
                    }
                }
            }
        }
    }
}
