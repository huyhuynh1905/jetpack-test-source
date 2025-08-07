package vn.huyhuynh.appcheckfeature.presenter.emv

import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.util.Log

object EmvCardReaderUtils {

    fun readEmvCard(tag: Tag): List<String> {
        val result = mutableListOf<String>()
        val isoDep = IsoDep.get(tag)

        if (isoDep == null) {
            result.add("❌ Không hỗ trợ IsoDep (thẻ không phải EMV)")
            return result
        }

        try {
            isoDep.connect()
            result.add("✅ Đã kết nối tới thẻ EMV")

            // Gửi APDU SELECT AID để chọn ứng dụng
            val selectPpse = byteArrayOf(
                0x00.toByte(), 0xA4.toByte(), 0x04.toByte(), 0x00.toByte(),
                0x0E.toByte(),
                *"2PAY.SYS.DDF01".toByteArray(Charsets.US_ASCII),
                0x00.toByte()
            )
            val rsp = isoDep.transceive(selectPpse)
            result.add("→ SELECT PPSE Response: ${rsp.toHexString()}")

            // Gửi tiếp SELECT AID cụ thể (ví dụ Visa A0000000031010)
            val selectVisa = byteArrayOf(
                0x00.toByte(), 0xA4.toByte(), 0x04.toByte(), 0x00.toByte(),
                0x07.toByte(),
                0xA0.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
                0x03.toByte(), 0x10.toByte(), 0x10.toByte(),
                0x00.toByte()
            )
            val visaRsp = isoDep.transceive(selectVisa)
            result.add("→ SELECT Visa: ${visaRsp.toHexString()}")

            // Gửi GET PROCESSING OPTIONS
            val gpo = byteArrayOf(
                0x80.toByte(), 0xA8.toByte(), 0x00.toByte(), 0x00.toByte(),
                0x02.toByte(), 0x83.toByte(), 0x00.toByte(), 0x00.toByte()
            )
            val gpoRsp = isoDep.transceive(gpo)
            result.add("→ GPO: ${gpoRsp.toHexString()}")

            // Bạn có thể gửi READ RECORD để lấy chi tiết tag
            // Gửi READ RECORD: SFI 1 Record 1
            val readRecord = byteArrayOf(
                0x00.toByte(), 0xB2.toByte(), 0x01.toByte(), 0x0C.toByte(), 0x00.toByte()
            )
            val readRsp = isoDep.transceive(readRecord)
            result.add("→ READ RECORD 1: ${readRsp.toHexString()}")

            isoDep.close()
        } catch (e: Exception) {
            result.add("❌ Lỗi: ${e.message}")
            Log.e("EmvCardReaderUtils", "Error reading EMV card", e)
        }

        return result
    }

    private fun ByteArray.toHexString(): String {
        return joinToString(" ") { String.format("%02X", it) }
    }
}
