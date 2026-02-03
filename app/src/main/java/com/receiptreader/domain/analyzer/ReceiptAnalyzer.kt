package com.receiptreader.domain.analyzer

import android.graphics.Rect
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * CameraX Image Analyzer that performs real-time OCR on camera frames.
 * 
 * ============================================================================
 * PRIVACY BY DESIGN - THE CORE OF OFFLINE OCR:
 * ============================================================================
 * 
 * This class uses Google ML Kit Text Recognition V2 with the BUNDLED model.
 * 
 * KEY PRIVACY GUARANTEES:
 * 
 * 1. BUNDLED MODEL (TextRecognizerOptions.DEFAULT_OPTIONS):
 *    The ML model is embedded directly in the APK (~20MB).
 *    This is different from the "thin" client that downloads models.
 *    
 * 2. 100% ON-DEVICE PROCESSING:
 *    All text recognition happens locally on the device's CPU/GPU.
 *    No images or text are ever sent to Google's servers.
 *    
 * 3. NO INTERNET REQUIRED:
 *    Since the model is bundled, the app works completely offline.
 *    You can verify this by enabling airplane mode before scanning.
 *    
 * 4. COMBINED WITH NO INTERNET PERMISSION:
 *    Even if ML Kit wanted to phone home (it doesn't with bundled),
 *    the app cannot make network requests because we didn't declare
 *    the INTERNET permission in AndroidManifest.xml.
 *    
 * This dual protection (bundled model + no permission) provides a
 * TECHNICAL GUARANTEE that receipt images never leave the device.
 * 
 * Reference: https://developers.google.com/ml-kit/vision/text-recognition/v2/android
 * ============================================================================
 */
class ReceiptAnalyzer(
    private val onTextRecognized: (ScanResult) -> Unit
) : ImageAnalysis.Analyzer {
    
    /**
     * ML Kit Text Recognizer instance.
     * 
     * PRIVACY NOTE: TextRecognizerOptions.DEFAULT_OPTIONS uses the bundled model.
     * This is the key to ensuring offline-only operation.
     */
    private val textRecognizer: TextRecognizer = TextRecognition.getClient(
        TextRecognizerOptions.DEFAULT_OPTIONS
    )
    
    // State for tracking analysis status
    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()
    
    // Prevent processing too many frames
    private var lastAnalyzedTimestamp = 0L
    private val analysisIntervalMs = 500L // Analyze max 2 frames per second
    
    /**
     * Result of a scan operation.
     */
    data class ScanResult(
        val rawText: String,
        val parsedData: ReceiptParser.ParseResult,
        val textBlocks: List<TextBlock>,
        val imageWidth: Int,
        val imageHeight: Int
    )
    
    /**
     * Represents a block of text with its bounding box.
     * Used for drawing overlays on the camera preview.
     */
    data class TextBlock(
        val text: String,
        val boundingBox: Rect?
    )
    
    /**
     * Analyze a camera frame for text.
     * 
     * This method is called for every frame from CameraX.
     * We throttle processing to avoid overwhelming the device.
     */
    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val currentTime = System.currentTimeMillis()
        
        // Throttle analysis to prevent overload
        if (currentTime - lastAnalyzedTimestamp < analysisIntervalMs) {
            imageProxy.close()
            return
        }
        
        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }
        
        lastAnalyzedTimestamp = currentTime
        _isAnalyzing.value = true
        
        // Create InputImage from camera frame
        // PRIVACY: This image stays in memory, never transmitted
        val inputImage = InputImage.fromMediaImage(
            mediaImage,
            imageProxy.imageInfo.rotationDegrees
        )
        
        val imageWidth = imageProxy.width
        val imageHeight = imageProxy.height
        
        // Process with ML Kit (100% on-device)
        textRecognizer.process(inputImage)
            .addOnSuccessListener { visionText ->
                processRecognizedText(visionText, imageWidth, imageHeight)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Text recognition failed", exception)
            }
            .addOnCompleteListener {
                _isAnalyzing.value = false
                imageProxy.close()
            }
    }
    
    /**
     * Process the recognized text and extract receipt data.
     */
    private fun processRecognizedText(visionText: Text, imageWidth: Int, imageHeight: Int) {
        val rawText = visionText.text
        
        if (rawText.isBlank()) {
            return
        }
        
        // Extract text blocks with bounding boxes for overlay
        val textBlocks = visionText.textBlocks.map { block ->
            TextBlock(
                text = block.text,
                boundingBox = block.boundingBox
            )
        }
        
        // Parse the text to extract structured data
        // PRIVACY: All parsing is done locally using ReceiptParser
        val parsedData = ReceiptParser.parse(rawText)
        
        // Callback with results
        onTextRecognized(
            ScanResult(
                rawText = rawText,
                parsedData = parsedData,
                textBlocks = textBlocks,
                imageWidth = imageWidth,
                imageHeight = imageHeight
            )
        )
    }
    
    /**
     * Release resources when done.
     */
    fun close() {
        textRecognizer.close()
    }
    
    companion object {
        private const val TAG = "ReceiptAnalyzer"
    }
}
