package vn.huyhuynh.appcheckfeature.presenter.contentprovider

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
fun ContentProviderScreen(){
    MainView()
}

@Composable
fun MainView() {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                val data = SharedFolderRepository.getInstance(context).getSharedInfo()
                Log.d("ContentProviderScreen","getData data: $data")
            },
            enabled = true
        ) {
            Text("Get Data")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                val timestamp = System.currentTimeMillis()
                println("Timestamp: $timestamp")

                // Lấy ngẫu nhiên "HSSV" hoặc "LUOT"
                val options = listOf("HSSV", "LUOT")
                val randomText = options.random()
                SharedFolderRepository.getInstance(context).insertPaidTicket("$timestamp",randomText)
            },
            enabled = true
        ) {
            Text("Save Data")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppCheckFeatureTheme {
        MainView()
    }
}