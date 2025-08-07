package vn.huyhuynh.appcheckfeature.presenter.emv

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EmvCardReaderScreen() {
    val context = LocalContext.current
    val emvInfoList = remember { mutableStateListOf<String>() }
    val scope = rememberCoroutineScope()

    // Collect NFC intent
    LaunchedEffect(Unit) {
        EmvNfcIntentHandler.nfcIntentFlow.collect { intent ->
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            if (tag != null) {
                val result = EmvCardReaderUtils.readEmvCard(tag) // Implement logic
                emvInfoList.clear()
                emvInfoList.addAll(result)
            }
        }
    }

    // UI hiển thị thẻ
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Tap thẻ EMV để đọc dữ liệu", fontSize = 18.sp)
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(emvInfoList) { line ->
                Text("• $line", fontSize = 14.sp)
                Divider()
            }
        }
    }
}

