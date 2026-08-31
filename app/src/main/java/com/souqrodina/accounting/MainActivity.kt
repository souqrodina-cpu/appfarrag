package com.souqrodina.accounting

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.souqrodina.accounting.data.model.Language
import com.souqrodina.accounting.ui.screens.MainShellScreen
import com.souqrodina.accounting.ui.theme.SouqRodinaTheme
import com.souqrodina.accounting.ui.viewmodel.AccountingViewModel
import com.souqrodina.accounting.ui.viewmodel.AccountingViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: AccountingViewModel by viewModels {
        val app = application as AccountingApplication
        AccountingViewModelFactory(app.repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val uiState by viewModel.uiState.collectAsState()
            
            val layoutDirection = if (uiState.language == Language.AR) {
                LayoutDirection.Rtl
            } else {
                LayoutDirection.Ltr
            }

            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                SouqRodinaTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = SouqRodinaTheme.colors.background
                    ) {
                        MainShellScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}