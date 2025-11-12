package com.skd.pgmanagement.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64

/**
 * Utility function to decode a Base64 string into a Bitmap.
 *
 * Usage:
 *     val bitmap = imageBase64.decodeBase64ToBitmap()
 */
fun String.decodeBase64ToBitmap(): Bitmap? {
    return try {
        val decodedBytes = Base64.decode(this, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
