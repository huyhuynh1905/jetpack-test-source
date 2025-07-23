package vn.huyhuynh.appcheckfeature.utils

import androidx.camera.lifecycle.ProcessCameraProvider
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun ListenableFuture<ProcessCameraProvider>.await(): ProcessCameraProvider {
    return suspendCancellableCoroutine { cont ->
        addListener({
            try {
                cont.resume(get())
            } catch (e: Exception) {
                cont.resumeWithException(e)
            }
        }, Executors.newSingleThreadExecutor())
    }
}
