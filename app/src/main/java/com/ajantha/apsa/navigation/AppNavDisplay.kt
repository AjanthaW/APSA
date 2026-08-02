package com.ajantha.apsa.navigation

import android.app.Application
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.ajantha.apsa.ui.appdetail.AppDetailRoute
import com.ajantha.apsa.ui.applist.AppListRoute
import com.ajantha.apsa.ui.dashboard.DashboardRoute
import com.ajantha.msc.ui.navigation.Destination

@Composable
fun AppNavDisplay() {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val backStack = remember { mutableStateListOf<Destination>(Destination.Dashboard) }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        NavDisplay(
            backStack = backStack,
            onBack = {
                if (backStack.size > 1) {
                    backStack.removeAt(backStack.lastIndex)
                }
            },
            entryProvider = entryProvider {

                entry<Destination.Dashboard> {
                    DashboardRoute(
                        modifier = Modifier.padding(innerPadding),
                        onOpenApps = {
                            backStack.add(Destination.AppList)
                        }
                    )
                }

                entry<Destination.AppList> {
                    AppListRoute(
                        modifier = Modifier.padding(innerPadding),
                        onBack = {
                            backStack.removeAt(backStack.lastIndex)
                        },
                        onAppClick = {
                            backStack.add(
                                Destination.AppDetail(it)
                            )
                        }
                    )
                }

                entry<Destination.AppDetail> { key ->
                    AppDetailRoute(
                        packageName = key.packageName,
                        modifier = Modifier.padding(innerPadding),
                        onBack = {

                            if (backStack.size > 1) {
                                backStack.removeAt(backStack.lastIndex)
                            }
                        }
                    )
                }
            }
        )
    }
}