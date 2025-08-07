package vn.huyhuynh.appcheckfeature.presenter.emv

import android.content.Intent
import kotlinx.coroutines.flow.MutableSharedFlow

object EmvNfcIntentHandler {
    val nfcIntentFlow = MutableSharedFlow<Intent>(replay = 1)
}