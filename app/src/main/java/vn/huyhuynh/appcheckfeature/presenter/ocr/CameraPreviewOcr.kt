package vn.huyhuynh.appcheckfeature.presenter.ocr

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.compose.LocalLifecycleOwner
import vn.huyhuynh.appcheckfeature.navigation.LocalNavController
import vn.huyhuynh.appcheckfeature.navigation.setResultAndPop
import vn.huyhuynh.appcheckfeature.utils.ArgsKey
import vn.huyhuynh.appcheckfeature.utils.await
import java.io.File
import java.io.FileOutputStream

@Composable
fun CameraOcrPreview(){
    val navController = LocalNavController.current


    CccdCameraScreen(
        modifier = Modifier.fillMaxSize(),
        onImageCaptured = { uri ->
            navController.setResultAndPop(
                ArgsKey.keyResultCameraOcr,
                uri
            )
        }
    )
}

@Composable
fun CccdCameraScreen(
    modifier: Modifier = Modifier,
    onImageCaptured: (Uri) -> Unit
) {
    val imageName = "cccd_capture_example.jpg"
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val previewView = remember {
        PreviewView(context).apply {
            layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            scaleType = PreviewView.ScaleType.FIT_CENTER
        }
    }

    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }

    val outputDirectory = remember {
        context.cacheDir
    }

    // Gắn CameraX trong LaunchedEffect, không gọi trong AndroidView
    LaunchedEffect(Unit) {
        val cameraProvider = ProcessCameraProvider.getInstance(context).await()

        val preview = Preview.Builder().build().also {
            it.surfaceProvider = previewView.surfaceProvider
        }

        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        } catch (e: Exception) {
            Log.e("CameraX", "Bind failed", e)
        }
    }

    // UI chính
    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())

        // Overlay khung CCCD
        Canvas(modifier = Modifier.fillMaxSize()) {
            val rectWidth = size.width * 0.9f
            val rectHeight = size.height * 0.25f
            val left = (size.width - rectWidth) / 2f
            val top = (size.height - rectHeight) / 2f

            drawRect(
                color = Color.White,
                topLeft = Offset(left, top),
                size = Size(rectWidth, rectHeight),
                style = Stroke(width = 4f)
            )
        }

        // Nút chụp ảnh
        IconButton(
            onClick = {
                val photoFile = File(
                    outputDirectory,
                    imageName
                )

                val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                imageCapture?.takePicture(
                    outputOptions,
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                            //onImageCaptured(Uri.fromFile(photoFile))
                            val imagePath = photoFile.absolutePath
                            val originalBitmap = BitmapFactory.decodeFile(imagePath)
                            // Xoay đúng trước khi crop
                            val bitmap = rotateBitmapIfRequired(originalBitmap, imagePath)

                            // Crop ảnh
                            val croppedBitmap = cropBitmapToOverlay(bitmap, previewView)

                            // Lưu lại ảnh crop
                            val croppedFile = File(outputDirectory, imageName)
                            FileOutputStream(croppedFile).use {
                                croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                            }

                            // Trả về URI ảnh đã crop
                            onImageCaptured(Uri.fromFile(croppedFile))

                        }

                        override fun onError(exc: ImageCaptureException) {
                            Log.e("CameraX", "Capture failed", exc)
                        }
                    }
                )
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .size(72.dp)
                .background(Color.White, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Place,
                contentDescription = "Chụp ảnh",
                tint = Color.Black,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

fun rotateBitmapIfRequired(bitmap: Bitmap, imagePath: String): Bitmap {
    val exif = ExifInterface(imagePath)
    val orientation = exif.getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_NORMAL
    )

    val matrix = Matrix()
    when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
        ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
        ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
    }

    return if (matrix.isIdentity) bitmap
    else Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

fun cropBitmapToOverlay(
    bitmap: Bitmap,
    previewView: PreviewView,
    overlayPercentWidth: Float = 0.9f,
    overlayPercentHeight: Float = 0.25f
): Bitmap {
    val imageWidth = bitmap.width.toFloat()
    val imageHeight = bitmap.height.toFloat()

    val viewWidth = previewView.width.toFloat()
    val viewHeight = previewView.height.toFloat()

    // 🔍 Tính scale tuỳ theo scaleType
    val scale = when (previewView.scaleType) {
        PreviewView.ScaleType.FIT_CENTER,
        PreviewView.ScaleType.FIT_START,
        PreviewView.ScaleType.FIT_END -> {
            maxOf(imageWidth / viewWidth, imageHeight / viewHeight)
        }
        PreviewView.ScaleType.FILL_CENTER,
        PreviewView.ScaleType.FILL_START,
        PreviewView.ScaleType.FILL_END -> {
            maxOf(imageWidth / viewWidth, imageHeight / viewHeight)
        }
    }

    // Kích thước ảnh hiển thị thực tế trên preview sau scale
    val displayedWidth = viewWidth * scale
    val displayedHeight = viewHeight * scale

    val horizontalPadding = (imageWidth - displayedWidth) / 2f
    val verticalPadding = (imageHeight - displayedHeight) / 2f

    // Khung overlay nằm giữa preview
    val rectWidth = viewWidth * overlayPercentWidth
    val rectHeight = viewHeight * overlayPercentHeight
    val rectLeft = (viewWidth - rectWidth) / 2f
    val rectTop = (viewHeight - rectHeight) / 2f

    // Tọa độ trong ảnh thật
    val cropLeft = (horizontalPadding + rectLeft * scale).toInt().coerceAtLeast(0)
    val cropTop = (verticalPadding + rectTop * scale).toInt().coerceAtLeast(0)
    val cropWidth = (rectWidth * scale).toInt().coerceAtMost(bitmap.width - cropLeft)
    val cropHeight = (rectHeight * scale).toInt().coerceAtMost(bitmap.height - cropTop)

    return Bitmap.createBitmap(bitmap, cropLeft, cropTop, cropWidth, cropHeight)
}





