package com.skd.pgmanagement.utils

import android.app.Activity
import android.content.Context
import android.widget.Toast

/**
 * Utility extension for showing Toast messages safely from any context.
 *
 * Usage:
 *    showToast("Message here")
 *
 * It will works for:
 *    - Activity
 *    - Fragment (requireContext().showToast(...))
 *    - Service or Application context
 */


fun Context.showToast(message: String) {
    if (this is Activity) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    } else {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
