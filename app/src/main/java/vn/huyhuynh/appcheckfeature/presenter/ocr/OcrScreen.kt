package vn.huyhuynh.appcheckfeature.presenter.ocr

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import vn.huyhuynh.appcheckfeature.base.RequestCameraPermissionScreen
import vn.huyhuynh.appcheckfeature.navigation.HandleResult
import vn.huyhuynh.appcheckfeature.navigation.LocalNavController
import vn.huyhuynh.appcheckfeature.navigation.Screen
import vn.huyhuynh.appcheckfeature.ui.theme.AppCheckFeatureTheme
import vn.huyhuynh.appcheckfeature.utils.ArgsKey
import vn.huyhuynh.appcheckfeature.utils.image.processCccdImageFromUri

@Composable
fun OcrScreen(){
    MainView()
}

@Composable
fun MainView() {
    val navController = LocalNavController.current
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var requestPermission by remember { mutableStateOf(false) }
    var navigateWithPermission by remember { mutableStateOf(false) }
    var capturedUri by remember { mutableStateOf<Uri?>(null) }
    var potraiBm by remember { mutableStateOf<Bitmap?>(null) }
    var dataOcr by remember { mutableStateOf<String>("") }


    if (requestPermission) {
        RequestCameraPermissionScreen(
            onPermissionGranted = {
                requestPermission = false
                navigateWithPermission = true

            },
            onPermissionDenied = {
                requestPermission = false
            }
        )
    }

    navController.HandleResult<Uri>(
        key = ArgsKey.keyResultCameraOcr,
        onResult = { uri ->
            capturedUri = uri
            processCccdImageFromUri(context, capturedUri) { text, portrait ->
                Log.d("CCCD", "Thông tin OCR: $text")
                portrait?.let {
                    potraiBm = it // Hiển thị ảnh chân dung
                }
                dataOcr = text ?: ""
            }
        }
    )

    if (navigateWithPermission) {
        navigateWithPermission = false
        navController.navigate(Screen.CameraOcrPreview.route)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.safeDrawing.asPaddingValues())
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Log.d("MainView", "navigateForResult Column: $capturedUri")

        if(capturedUri!=null){
            PreviewCapturedImage(imageUri = capturedUri!!)
        }

        if(potraiBm!=null){
            PortraitPreview(bitmap = potraiBm!!)
        }

        if(dataOcr.isNotBlank()){
            Text(
                text = dataOcr,
                color = Color.Black
            )
        }

        Button(
            onClick = {
                requestPermission = true
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1464BD)  // đổi màu nền
            )
        ) {
            Text(
                text = "Open Camera",
                color = Color.White
            )
        }
    }
}

@Composable
fun PreviewCapturedImage(imageUri: Uri) {
    AsyncImage(
        model = imageUri,
        contentDescription = "Ảnh CCCD",
        contentScale = ContentScale.Inside,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}

@Composable
fun PortraitPreview(bitmap: Bitmap) {
    Image(
        painter = remember(bitmap) { BitmapPainter(bitmap.asImageBitmap()) },
        contentDescription = "Ảnh chân dung",
        modifier = Modifier
            .width(LocalConfiguration.current.screenWidthDp.dp/3)
            .clip(RoundedCornerShape(8.dp))
            .padding(15.dp),
        contentScale = ContentScale.Inside
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppCheckFeatureTheme {
        MainView()
    }
}