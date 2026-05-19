package com.example.apexphotolab.android.android_keys

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile

/**
 * Job: Logic/Validation.
 * Verifies the integrity of a project directory URI.
 */
object ProjectDirValidator {
    fun isValid(context: Context, uri: Uri?): Boolean {
        if (uri == null) return false
        return try {
            val docFile = DocumentFile.fromTreeUri(context, uri)
            docFile != null && docFile.canRead()
        } catch (e: Exception) {
            false
        }
    }
}