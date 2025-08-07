package vn.huyhuynh.appcheckfeature

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import vn.huyhuynh.appcheckfeature.navigation.ComposeNavigationApp
import vn.huyhuynh.appcheckfeature.presenter.emv.EmvCardReaderScreen
import vn.huyhuynh.appcheckfeature.presenter.emv.EmvNfcIntentHandler
import vn.huyhuynh.appcheckfeature.ui.theme.AppCheckFeatureTheme

class MainActivity : ComponentActivity() {
    private lateinit var nfcAdapter: NfcAdapter
    private lateinit var pendingIntent: PendingIntent


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        val isNfcEnabled = nfcAdapter.isEnabled == true
        Log.d("MainActivity"," nfcAdapter isNfcEnabled: $isNfcEnabled")
        pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_MUTABLE
        )

        setContent {
            AppCheckFeatureTheme {
                ComposeNavigationApp()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d("MainActivity", "onNewIntent CALLED: ${intent.action}")
        lifecycleScope.launch {
            EmvNfcIntentHandler.nfcIntentFlow.emit(intent)
        }
    }
}

