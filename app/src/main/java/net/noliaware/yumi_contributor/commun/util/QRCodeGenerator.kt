package net.noliaware.yumi_contributor.commun.util

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.yield

object QRCodeGenerator {

    suspend fun encodeAsBitmap(content: String, size: Int): Bitmap? {

        val qrCodeWriter = QRCodeWriter()
        try {
            val bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, size, size)
            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
            for (x in 0 until size) {
                for (y in 0 until size) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
                yield()
            }

            return bitmap

        } catch (e: Exception) {
            e.recordNonFatal()
        }

        return null
    }
}