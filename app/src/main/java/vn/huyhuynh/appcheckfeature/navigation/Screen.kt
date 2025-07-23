package vn.huyhuynh.appcheckfeature.navigation

sealed class Screen(val route: String) {
    object OcrScreen : Screen("OcrScreen")
    object CameraOcrPreview : Screen("CameraOcrPreview")
}
