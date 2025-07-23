package vn.huyhuynh.appcheckfeature.utils.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun processCccdImageFromUriWithFaceDetect(
    context: Context,
    uri: Uri?,
    onResult: (cccdText: String?, portraitBitmap: Bitmap?) -> Unit
) {
    if(uri==null){
        return
    }
    val bitmap = uriToBitmap(context, uri) ?: run {
        onResult(null, null)
        return
    }

    // 1. OCR
    val image = InputImage.fromBitmap(bitmap, 0)
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    recognizer.process(image)
        .addOnSuccessListener { visionText ->
            val extractedText = visionText.text  // Đây là toàn bộ text trên CCCD

            // 2. Face detection
            val faceDetector = FaceDetection.getClient(
                FaceDetectorOptions.Builder()
                    .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                    .build()
            )

            faceDetector.process(image)
                .addOnSuccessListener { faces ->
                    val faceBitmap = faces.firstOrNull()?.boundingBox?.let { box ->
                        Bitmap.createBitmap(
                            bitmap,
                            box.left.coerceAtLeast(0),
                            box.top.coerceAtLeast(0),
                            box.width().coerceAtMost(bitmap.width - box.left),
                            box.height().coerceAtMost(bitmap.height - box.top)
                        )
                    }
                    onResult(extractedText, faceBitmap)
                }
                .addOnFailureListener {
                    onResult(extractedText, null)  // Text ok, ảnh fail
                }
        }
        .addOnFailureListener {
            onResult(null, null)
        }
}


fun processCccdImageFromUri(
    context: Context,
    uri: Uri?,
    onResult: (cccdText: String?, portraitBitmap: Bitmap?) -> Unit
) {
    if (uri == null) {
        onResult(null, null)
        return
    }

    val bitmap = uriToBitmap(context, uri) ?: run {
        onResult(null, null)
        return
    }

    // 1. OCR toàn ảnh
    val image = InputImage.fromBitmap(bitmap, 0)
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    recognizer.process(image)
        .addOnSuccessListener { visionText ->
            val extractedText = visionText.text

            // 2. Crop ảnh chân dung theo layout chuẩn
            val portraitBitmap = cropPortraitFromCccd(bitmap)

            onResult(extractedText, portraitBitmap)
        }
        .addOnFailureListener {
            onResult(null, null)
        }
}

fun cropPortraitFromCccd(bitmap: Bitmap): Bitmap {
    val width = bitmap.width
    val height = bitmap.height

    val paddingLeft = 20 // pixel
    val paddingBottom = 30 // pixel

    val cropWidth = (width * 0.33f).toInt()
    val cropHeight = (height * 0.66f).toInt()

    val cropLeft = paddingLeft
    val cropTop = height - cropHeight - paddingBottom

    val safeLeft = cropLeft.coerceAtLeast(0)
    val safeTop = cropTop.coerceAtLeast(0)
    val safeWidth = cropWidth.coerceAtMost(width - safeLeft)
    val safeHeight = cropHeight.coerceAtMost(height - safeTop)

    return Bitmap.createBitmap(bitmap, safeLeft, safeTop, safeWidth, safeHeight)
}



