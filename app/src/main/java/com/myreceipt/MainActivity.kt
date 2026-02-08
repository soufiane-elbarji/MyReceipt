package com.myreceipt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.myreceipt.presentation.navigation.MyReceiptNavGraph
import com.myreceipt.presentation.ui.theme.MyReceiptTheme
import com.myreceipt.presentation.viewmodel.ReceiptViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            val viewModel: ReceiptViewModel = viewModel()
            val isDarkMode by viewModel.isDarkMode.collectAsState(initial = true)

            MyReceiptTheme(darkTheme = isDarkMode) { MyReceiptNavGraph(viewModel = viewModel) }
        }
    }
}
