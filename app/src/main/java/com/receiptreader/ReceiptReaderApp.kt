package com.receiptreader

import android.app.Application

/**
 * Application class for Receipt Reader.
 * 
 * PRIVACY BY DESIGN:
 * This application is designed with privacy as a core principle:
 * - No crash reporting SDKs (Firebase Crashlytics, Sentry, etc.)
 * - No analytics SDKs (Google Analytics, Mixpanel, etc.)
 * - No advertising SDKs
 * - No network libraries (Retrofit, OkHttp, etc.)
 * 
 * The app operates in complete isolation from the network.
 */
class ReceiptReaderApp : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // No initialization of tracking, analytics, or network services
        // This is intentional for privacy compliance
    }
}
