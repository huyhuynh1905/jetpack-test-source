package vn.huyhuynh.appcheckfeature

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import vn.huyhuynh.appcheckfeature.navigation.ComposeNavigationApp
import vn.huyhuynh.appcheckfeature.ui.theme.AppCheckFeatureTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppCheckFeatureTheme {
                ComposeNavigationApp()
            }
        }
    }
}

