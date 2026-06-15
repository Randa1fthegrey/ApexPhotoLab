package com.example.apexphotolab.the_build.welcome_screen

import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.example.apexphotolab.android.android_keys.SettingsPersistence

/**
 * Job: Project Folder Handler.
 * Responsibility: Persists the custom project root folder URI.
 */
class ProjectFolderHandler(
    private val context: Context,
    private val onFolderSet: () -> Unit
) {
    fun execute(uri: Uri?) {
        uri?.let {
            SettingsPersistence.setProjectDir(context, it)
            onFolderSet()
            Toast.makeText(context, val_util.TOAST_FOLDER_SET, Toast.LENGTH_SHORT).show()
        }
    }
}
