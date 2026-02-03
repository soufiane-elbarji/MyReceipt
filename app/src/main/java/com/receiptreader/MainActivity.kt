package com.receiptreader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.receiptreader.presentation.navigation.ReceiptVaultNavGraph
import com.receiptreader.presentation.ui.theme.ReceiptVaultTheme
import com.receiptreader.presentation.viewmodel.ReceiptViewModel

/**
 * MainActivity - Entry point for ReceiptVault.
 * 
 * =============================================================================
 * PRIVACY BY DESIGN - APP ARCHITECTURE SUMMARY:
 * =============================================================================
 * 
 * This application was architected with privacy as the primary concern:
 * 
 * 1. NO INTERNET PERMISSION:
 *    The AndroidManifest.xml deliberately omits android.permission.INTERNET.
 *    This provides a technical guarantee that no data can be transmitted.
 * 
 * 2. BUNDLED ML KIT MODEL:
 *    We use Google ML Kit Text Recognition V2 with the bundled model.
 *    The TensorFlow Lite model is embedded in the APK (~20MB), ensuring
 *    all OCR processing happens 100% on-device.
 * 
 * 3. LOCAL DATABASE:
 *    All receipt data is stored in Room (SQLite) in the app's private
 *    internal storage. Cloud backup is disabled via android:allowBackup="false".
 * 
 * 4. NO ANALYTICS OR TRACKING:
 *    Zero third-party SDKs for analytics, crash reporting, or advertising.
 *    User activity is never logged or transmitted.
 * 
 * 5. DATA SOVEREIGNTY:
 *    Users have full control: view, edit, delete all their data.
 *    Compliant with GDPR and Moroccan Law 09-08.
 * 
 * 6. GREEN IT PRINCIPLES:
 *    - On-demand processing only (no background services)
 *    - Efficient resource utilization
 *    - Minimal battery consumption
 * 
 * =============================================================================
 */
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable edge-to-edge display
        enableEdgeToEdge()
        
        setContent {
            val viewModel: ReceiptViewModel = viewModel()
            val isDarkMode by viewModel.isDarkMode.collectAsState(initial = true)
            
            ReceiptVaultTheme(darkTheme = isDarkMode) {
                ReceiptVaultNavGraph(viewModel = viewModel)
            }
        }
    }
}
