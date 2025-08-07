package vn.huyhuynh.appcheckfeature.presenter.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import vn.huyhuynh.appcheckfeature.navigation.LocalNavController
import vn.huyhuynh.appcheckfeature.navigation.Screen

@Composable
fun MainScreen(){
    val navController = LocalNavController.current
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                navController.navigate(Screen.OcrScreen.route)
            },
            enabled = true
        ) {
            Text("Camera Detect CCCD")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                navController.navigate(Screen.EmvCardReader.route)
            },
            enabled = true
        ) {
            Text("Read EMV Card Info")
        }
    }
}