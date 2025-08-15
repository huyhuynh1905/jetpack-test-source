package vn.huyhuynh.appcheckfeature.presenter.contentprovider

import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri

class SharedFolderRepository(private val context: Context) {



    fun getSharedInfo(): Cursor? {
        val cursor = context.contentResolver.query(
            SHARED_INFO_URI,
            null, // projection = tất cả cột
            null, // selection
            null, // selectionArgs
            null  // sortOrder
                                                  )

        cursor?.use { c ->
            val columnCount = c.columnCount
            while (c.moveToNext()) {
                val rowMap = mutableMapOf<String, Any?>()
                for (i in 0 until columnCount) {
                    val columnName = c.getColumnName(i)
                    val value = when (c.getType(i)) {
                        Cursor.FIELD_TYPE_INTEGER -> c.getLong(i)
                        Cursor.FIELD_TYPE_FLOAT -> c.getDouble(i)
                        Cursor.FIELD_TYPE_STRING -> c.getString(i)
                        Cursor.FIELD_TYPE_BLOB -> c.getBlob(i)
                        Cursor.FIELD_TYPE_NULL -> null
                        else -> null
                    }
                    rowMap[columnName] = value
                }
                Log.d("CursorRow", rowMap.toString())
            }
            return c
        }
        return null
    }

    fun insertPaidTicket(ticketCode: String, type: String) {
        val values = ContentValues().apply {
            put("ticket_code", ticketCode)
            put("ticket_type", type)
            put("payment_type", "VISA")
        }

        context.contentResolver.insert(
            PAID_CARD_URI,
            values
        )
    }



    companion object {
        const val AUTHORITY = "com.vinbus.provider"

        //end name
        const val SHARED_FOLDER_STR = "shared_info"
        const val PAID_CARD_INFO_STR = "paid_card_info"

        // URI cho tickets
        val SHARED_INFO_URI: Uri = "content://$AUTHORITY/$SHARED_FOLDER_STR".toUri()
        val PAID_CARD_URI: Uri = "content://$AUTHORITY/$PAID_CARD_INFO_STR".toUri()

        private const val SHARED_FOLDER = 1
        private const val PAID_CARD_INFO = 2

        val matcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, SHARED_FOLDER_STR, SHARED_FOLDER)
            addURI(AUTHORITY, PAID_CARD_INFO_STR, PAID_CARD_INFO)
        }

        private var INSTANCE: SharedFolderRepository? = null

        fun getInstance(ctx: Context): SharedFolderRepository {
            if (INSTANCE == null)  // NOT thread safe!
                INSTANCE = SharedFolderRepository(ctx)
            return INSTANCE as SharedFolderRepository
        }
    }
}