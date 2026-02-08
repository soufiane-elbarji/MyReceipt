package com.myreceipt.domain.analyzer

import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * Real-time receipt analyzer using ML Kit Text Recognition. Processes camera frames and extracts
 * text for parsing.
 */
class ReceiptAnalyzer(private val onTextRecognized: (String) -> Unit) : ImageAnalysis.Analyzer {

    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    private val _recognizedText = Channel<String>(Channel.CONFLATED)
    val recognizedText: Flow<String> = _recognizedText.receiveAsFlow()

    private var lastAnalysisTime = 0L
    private val analysisInterval = 500L // Analyze every 500ms

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val currentTime = System.currentTimeMillis()

        // Throttle analysis to reduce CPU usage
        if (currentTime - lastAnalysisTime < analysisInterval) {
            imageProxy.close()
            return
        }

        lastAnalysisTime = currentTime

        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val inputImage =
                    InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            textRecognizer
                    .process(inputImage)
                    .addOnSuccessListener { visionText ->
                        val text = visionText.text
                        if (text.isNotEmpty()) {
                            onTextRecognized(text)
                            _recognizedText.trySend(text)
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("ReceiptAnalyzer", "Text recognition failed", e)
                    }
                    .addOnCompleteListener { imageProxy.close() }
        } else {
            imageProxy.close()
        }
    }

    fun close() {
        textRecognizer.close()
    }
}

/** Data class for scan results. */
data class ScanResult(
        val storeName: String?,
        val date: String?,
        val amount: Double?,
        val rawText: String
)
