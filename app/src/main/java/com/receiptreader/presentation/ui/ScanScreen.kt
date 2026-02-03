package com.receiptreader.presentation.ui

import android.Manifest
import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.receiptreader.domain.analyzer.ReceiptParser
import kotlinx.coroutines.delay
import java.util.concurrent.Executors

/**
 * Scan Screen - Premium camera with manual photo capture.
 * 
 * Fixed camera initialization using DisposableEffect for proper lifecycle management.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScanScreen(
    onReceiptCaptured: (
        storeName: String?,
        date: String?,
        amount: Double?,
        rawText: String
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (cameraPermissionState.status.isGranted) {
            CameraContent(onReceiptCaptured = onReceiptCaptured)
        } else {
            PermissionRequest(
                onRequestPermission = { cameraPermissionState.launchPermissionRequest() }
            )
        }
    }
}

@Composable
private fun CameraContent(
    onReceiptCaptured: (String?, String?, Double?, String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    var isProcessing by remember { mutableStateOf(false) }
    var flashEnabled by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Camera components - stored in remember to persist across recompositions
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var camera by remember { mutableStateOf<Camera?>(null) }
    var previewView by remember { mutableStateOf<PreviewView?>(null) }
    
    val textRecognizer = remember {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }
    
    // Initialize camera once using DisposableEffect
    DisposableEffect(lifecycleOwner) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        
        cameraProviderFuture.addListener({
            try {
                val provider = cameraProviderFuture.get()
                cameraProvider = provider
                
                // Set up preview
                val preview = Preview.Builder()
                    .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                    .build()
                
                // Set up image capture
                val capture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                    .build()
                imageCapture = capture
                
                // Unbind all before rebinding
                provider.unbindAll()
                
                // Bind use cases
                camera = provider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    capture
                )
                
                // Connect preview to surface provider
                previewView?.let { view ->
                    preview.setSurfaceProvider(view.surfaceProvider)
                }
                
                Log.d("ScanScreen", "Camera initialized successfully")
            } catch (e: Exception) {
                Log.e("ScanScreen", "Camera initialization failed", e)
                errorMessage = "Camera initialization failed: ${e.message}"
            }
        }, ContextCompat.getMainExecutor(context))
        
        onDispose {
            cameraProvider?.unbindAll()
            textRecognizer.close()
        }
    }
    
    // Success animation effect
    LaunchedEffect(showSuccess) {
        if (showSuccess) {
            delay(1500)
            showSuccess = false
        }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Camera Preview
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    previewView = this
                    
                    // If camera provider already exists, connect preview
                    cameraProvider?.let { provider ->
                        try {
                            val preview = Preview.Builder()
                                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                                .build()
                            preview.setSurfaceProvider(surfaceProvider)
                            
                            provider.unbindAll()
                            
                            val capture = ImageCapture.Builder()
                                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                                .build()
                            imageCapture = capture
                            
                            camera = provider.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_BACK_CAMERA,
                                preview,
                                capture
                            )
                        } catch (e: Exception) {
                            Log.e("ScanScreen", "Camera rebind failed", e)
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        
        // Scanning overlay
        ScanningOverlay(isProcessing = isProcessing)
        
        // Top bar with controls
        TopControls(
            flashEnabled = flashEnabled,
            onFlashToggle = {
                flashEnabled = !flashEnabled
                camera?.cameraControl?.enableTorch(flashEnabled)
            }
        )
        
        // Bottom capture area
        BottomCaptureControls(
            isProcessing = isProcessing,
            onCapture = {
                if (!isProcessing && imageCapture != null) {
                    isProcessing = true
                    errorMessage = null
                    captureAndProcess(
                        context = context,
                        imageCapture = imageCapture!!,
                        textRecognizer = textRecognizer,
                        onResult = { storeName, date, amount, rawText ->
                            isProcessing = false
                            if (rawText.isNotEmpty()) {
                                showSuccess = true
                                onReceiptCaptured(storeName, date, amount, rawText)
                            } else {
                                errorMessage = "No text detected. Try again."
                            }
                        },
                        onError = { error ->
                            isProcessing = false
                            errorMessage = error
                        }
                    )
                }
            }
        )
        
        // Success indicator
        AnimatedVisibility(
            visible = showSuccess,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(Color(0xFF34C759).copy(alpha = 0.9f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Success",
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )
            }
        }
        
        // Error message
        errorMessage?.let { error ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .padding(32.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFF3B30).copy(alpha = 0.9f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Text(
                            text = error,
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ScanningOverlay(isProcessing: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "scan")
    
    val scanLineOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scanLine"
    )
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Semi-transparent vignette
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.4f)
                        ),
                        radius = 800f
                    )
                )
        )
        
        // Frame guide
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 120.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.65f)
                    .border(
                        width = 2.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF007AFF),
                                Color(0xFF5AC8FA)
                            )
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                // Animated scan line when processing
                if (isProcessing) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .offset(y = (300.dp * scanLineOffset))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color(0xFF007AFF),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }
                
                // Corner brackets
                CornerBrackets()
            }
        }
        
        // Instruction text
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 80.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isProcessing) "Scanning..." else "Position receipt in frame",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun BoxScope.CornerBrackets() {
    val size = 32.dp
    val thickness = 3.dp
    val color = Color(0xFF007AFF)
    
    // Top-left
    Box(modifier = Modifier.align(Alignment.TopStart).padding(8.dp)) {
        Box(
            modifier = Modifier
                .width(size)
                .height(thickness)
                .background(color, RoundedCornerShape(thickness))
        )
        Box(
            modifier = Modifier
                .width(thickness)
                .height(size)
                .background(color, RoundedCornerShape(thickness))
        )
    }
    
    // Top-right
    Box(modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)) {
        Box(
            modifier = Modifier
                .width(size)
                .height(thickness)
                .align(Alignment.TopEnd)
                .background(color, RoundedCornerShape(thickness))
        )
        Box(
            modifier = Modifier
                .width(thickness)
                .height(size)
                .align(Alignment.TopEnd)
                .background(color, RoundedCornerShape(thickness))
        )
    }
    
    // Bottom-left
    Box(modifier = Modifier.align(Alignment.BottomStart).padding(8.dp)) {
        Box(
            modifier = Modifier
                .width(size)
                .height(thickness)
                .align(Alignment.BottomStart)
                .background(color, RoundedCornerShape(thickness))
        )
        Box(
            modifier = Modifier
                .width(thickness)
                .height(size)
                .align(Alignment.BottomStart)
                .background(color, RoundedCornerShape(thickness))
        )
    }
    
    // Bottom-right
    Box(modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp)) {
        Box(
            modifier = Modifier
                .width(size)
                .height(thickness)
                .align(Alignment.BottomEnd)
                .background(color, RoundedCornerShape(thickness))
        )
        Box(
            modifier = Modifier
                .width(thickness)
                .height(size)
                .align(Alignment.BottomEnd)
                .background(color, RoundedCornerShape(thickness))
        )
    }
}

@Composable
private fun TopControls(
    flashEnabled: Boolean,
    onFlashToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(16.dp),
        horizontalArrangement = Arrangement.End
    ) {
        // Flash toggle
        IconButton(
            onClick = onFlashToggle,
            modifier = Modifier
                .size(48.dp)
                .background(
                    Color.Black.copy(alpha = 0.3f),
                    CircleShape
                )
        ) {
            Icon(
                imageVector = if (flashEnabled) Icons.Filled.FlashOn else Icons.Outlined.FlashOff,
                contentDescription = "Flash",
                tint = if (flashEnabled) Color(0xFFFFCC00) else Color.White
            )
        }
    }
}

@Composable
private fun BoxScope.BottomCaptureControls(
    isProcessing: Boolean,
    onCapture: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.7f)
                    )
                )
            )
            .padding(bottom = 100.dp, top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Capture button
        Box(
            modifier = Modifier
                .size(80.dp)
                .shadow(8.dp, CircleShape)
                .clip(CircleShape)
                .background(Color.White)
                .border(4.dp, Color(0xFF007AFF), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (isProcessing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(40.dp),
                    color = Color(0xFF007AFF),
                    strokeWidth = 3.dp
                )
            } else {
                IconButton(
                    onClick = onCapture,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color(0xFF007AFF), CircleShape)
                    )
                }
            }
        }
        
        Text(
            text = if (isProcessing) "Processing..." else "Tap to scan",
            style = MaterialTheme.typography.labelLarge,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun PermissionRequest(
    onRequestPermission: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Camera icon with gradient background
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF007AFF),
                                Color(0xFF5AC8FA)
                            )
                        ),
                        RoundedCornerShape(32.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.CameraAlt,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = Color.White
                )
            }
            
            Text(
                text = "Camera Access",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Text(
                text = "ReceiptVault needs camera access to scan your receipts. All processing happens on your device - your photos never leave your phone.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onRequestPermission,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF007AFF)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Enable Camera",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            // Privacy note
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Shield,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFF34C759)
                )
                Text(
                    text = "100% Private • No data leaves your device",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF34C759)
                )
            }
        }
    }
}

private fun captureAndProcess(
    context: Context,
    imageCapture: ImageCapture,
    textRecognizer: TextRecognizer,
    onResult: (String?, String?, Double?, String) -> Unit,
    onError: (String) -> Unit
) {
    val executor = Executors.newSingleThreadExecutor()
    
    imageCapture.takePicture(
        executor,
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(imageProxy: ImageProxy) {
                val mediaImage = imageProxy.image
                if (mediaImage != null) {
                    val inputImage = InputImage.fromMediaImage(
                        mediaImage,
                        imageProxy.imageInfo.rotationDegrees
                    )
                    
                    textRecognizer.process(inputImage)
                        .addOnSuccessListener { visionText ->
                            val rawText = visionText.text
                            val parsed = ReceiptParser.parse(rawText)
                            
                            // Convert amount string to Double
                            val amountValue = parsed.totalAmount
                                ?.replace(Regex("[^0-9.,]"), "")
                                ?.replace(",", ".")
                                ?.toDoubleOrNull()
                            
                            ContextCompat.getMainExecutor(context).execute {
                                onResult(
                                    parsed.merchantName,
                                    parsed.date,
                                    amountValue,
                                    rawText
                                )
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("ScanScreen", "OCR failed", e)
                            ContextCompat.getMainExecutor(context).execute {
                                onError("Text recognition failed: ${e.message}")
                            }
                        }
                        .addOnCompleteListener {
                            imageProxy.close()
                        }
                } else {
                    imageProxy.close()
                    ContextCompat.getMainExecutor(context).execute {
                        onError("Failed to capture image")
                    }
                }
            }
            
            override fun onError(exception: ImageCaptureException) {
                Log.e("ScanScreen", "Capture failed", exception)
                ContextCompat.getMainExecutor(context).execute {
                    onError("Capture failed: ${exception.message}")
                }
            }
        }
    )
}
