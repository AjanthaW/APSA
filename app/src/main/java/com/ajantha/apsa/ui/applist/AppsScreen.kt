package com.ajantha.apsa.ui.applist

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import com.ajantha.apsa.model.RiskLevel
import com.ajantha.apsa.ui.applist.component.AppItem
import com.ajantha.apsa.ui.applist.component.EmptyApps

@Composable
fun AppListRoute(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onAppClick: (String) -> Unit
) {

    val application = LocalContext.current.applicationContext as Application

    val viewModel: AppsViewModel = viewModel(
        factory = AppsViewModelFactory(application)
    )

    val state by viewModel.uiState.collectAsState()

    AppListScreen(
        modifier = modifier,
        uiState = state,
        onAppClick = onAppClick,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppListScreen(
    modifier: Modifier = Modifier,
    uiState: AppsUiState,
    onAppClick: (String) -> Unit,
    onBack: () -> Unit
) {

    var search by rememberSaveable { mutableStateOf("") }
    var selectedFilter by rememberSaveable { mutableStateOf<RiskLevel?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Installed Applications",
                            fontWeight = FontWeight.Bold
                        )

                        if (uiState is AppsUiState.Success) {
                            Text(
                                text = "${(uiState as AppsUiState.Success).apps.size} Apps",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = search,
                onValueChange = {
                    search = it
                },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Search,
                        null
                    )
                },
                placeholder = {
                    Text("Search applications")
                },
                singleLine = true,
                shape = RoundedCornerShape(18.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedFilter == null,
                        onClick = { selectedFilter = null },
                        label = { Text("All") }
                    )
                }

                items(RiskLevel.entries) { riskLevel ->
                    FilterChip(
                        selected = selectedFilter == riskLevel,
                        onClick = { selectedFilter = riskLevel },
                        label = { Text(riskLevel.title) }
                    )
                }
            }

            when (uiState) {

                is AppsUiState.Loading -> {
                    Box(
                        modifier = modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is AppsUiState.Error -> {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            uiState.message,
                            color = MaterialTheme.colorScheme.error
                        )

                    }

                }

                is AppsUiState.Success -> {

                    val apps = remember(
                        key1 = uiState.apps,
                        key2 = search,
                        key3 = selectedFilter
                    ) {
                        uiState.apps
                            .filter { item ->

                                val matchesSearch =
                                    item.app.appName.contains(search, ignoreCase = true) ||
                                            item.app.packageName.contains(search, ignoreCase = true)

                                val matchesFilter =
                                    selectedFilter == null ||
                                            item.riskLevel == selectedFilter

                                matchesSearch && matchesFilter
                            }
                            .sortedByDescending { it.app.appName }
                    }

                    if (apps.isEmpty()) {

                        EmptyApps()

                    } else {

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 24.dp)
                        ) {

                            items(
                                items = apps,
                                key = { it.app.packageName }
                            ) {

                                AppItem(
                                    item = it,
                                    modifier = Modifier.animateItem(),
                                    onClick = {
                                        onAppClick(it.app.packageName)
                                    }
                                )

                            }

                        }

                    }

                }

            }

        }

    }
}