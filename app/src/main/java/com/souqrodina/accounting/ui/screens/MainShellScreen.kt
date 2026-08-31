package com.souqrodina.accounting.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.souqrodina.accounting.data.model.Language
import com.souqrodina.accounting.data.model.TabType
import com.souqrodina.accounting.ui.screens.dashboard.DashboardScreen
import com.souqrodina.accounting.ui.screens.directories.DirectoriesScreen
import com.souqrodina.accounting.ui.screens.inventory.InventoryScreen
import com.souqrodina.accounting.ui.screens.sales.SalesScreen
import com.souqrodina.accounting.ui.screens.settings.SettingsScreen
import com.souqrodina.accounting.ui.theme.*
import com.souqrodina.accounting.ui.viewmodel.AccountingViewModel

@Composable
fun MainShellScreen(viewModel: AccountingViewModel) {
    val state by viewModel.uiState.collectAsState()
    val isArabic = state.language == Language.AR

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceWhite,
                tonalElevation = 8.dp
            ) {
                val items = listOf(
                    Triple(TabType.DASHBOARD, if (isArabic) "الرئيسية" else "Home", Icons.Outlined.Dashboard),
                    Triple(TabType.INVENTORY, if (isArabic) "المخزون" else "Stock", Icons.Outlined.Inventory2),
                    Triple(TabType.SALES, if (isArabic) "المبيعات" else "Sales", Icons.Outlined.PointOfSale),
                    Triple(TabType.DIRECTORIES, if (isArabic) "الدليل" else "Directory", Icons.Outlined.Contacts),
                    Triple(TabType.SETTINGS, if (isArabic) "الإعدادات" else "Settings", Icons.Outlined.Settings)
                )

                items.forEach { (tab, label, icon) ->
                    val isSelected = state.currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.setTab(tab) },
                        icon = { Icon(icon, contentDescription = label) },
                        label = {
                            Text(
                                label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryNavy,
                            selectedTextColor = PrimaryNavy,
                            indicatorColor = PrimaryNavyLight,
                            unselectedIconColor = TextSlateMuted,
                            unselectedTextColor = TextSlateMuted
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Crossfade(targetState = state.currentTab, label = "ScreenTransition") { targetTab ->
                when (targetTab) {
                    TabType.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                    TabType.INVENTORY -> InventoryScreen(viewModel = viewModel)
                    TabType.SALES -> SalesScreen(viewModel = viewModel)
                    TabType.DIRECTORIES -> DirectoriesScreen(viewModel = viewModel)
                    TabType.SETTINGS -> SettingsScreen(viewModel = viewModel)
                }
            }
        }
    }
}