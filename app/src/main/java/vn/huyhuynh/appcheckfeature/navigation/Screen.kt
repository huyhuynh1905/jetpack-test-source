package vn.huyhuynh.appcheckfeature.navigation

sealed class Screen(val route: String) {
    object MainScreen : Screen("MainScreen")
    object OcrScreen : Screen("OcrScreen")
    object CameraOcrPreview : Screen("CameraOcrPreview")
}
