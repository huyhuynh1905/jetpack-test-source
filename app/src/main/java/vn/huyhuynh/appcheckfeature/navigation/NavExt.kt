package vn.huyhuynh.appcheckfeature.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.filterNotNull

/**
 * Lắng nghe dữ liệu trả về từ SavedStateHandle khi quay lại màn hình trước.
 */
@Composable
fun <T> NavHostController.HandleResult(
    key: String,
    onResult: (T) -> Unit
) {
    val savedStateHandle = currentBackStackEntry?.savedStateHandle
    val resultFlow = remember(key) {
        savedStateHandle?.getStateFlow<T?>(key, null)
    }

    val result = resultFlow?.collectAsState(initial = null)?.value

    LaunchedEffect(result) {
        result?.let {
            onResult(it)
            savedStateHandle?.let { value -> value[key] = null } // reset sau khi dùng
        }
    }
}

fun <T> NavController.setResultAndPop(resultKey: String, result: T) {
    this.previousBackStackEntry?.savedStateHandle?.set(resultKey, result)
    Log.d("navigateForResult","setResultAndPop -> $resultKey:  $result")
    this.popBackStack()
}
