package vn.huyhuynh.appcheckfeature.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import vn.huyhuynh.appcheckfeature.presenter.ocr.CameraOcrPreview
import vn.huyhuynh.appcheckfeature.presenter.ocr.OcrScreen

val LocalNavController = staticCompositionLocalOf<NavHostController> {
    error("No NavController provided")
}

@Composable
fun ComposeNavigationApp() {
    val navController = rememberNavController()
    CompositionLocalProvider(LocalNavController provides navController) {
        NavHost(navController = navController, startDestination = Screen.OcrScreen.route) {
            composable(Screen.OcrScreen.route) {
                OcrScreen()
            }
            composable(Screen.CameraOcrPreview.route) {
                CameraOcrPreview()
            }
        }
    }

}