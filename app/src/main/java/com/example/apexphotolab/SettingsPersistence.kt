package com.example.apexphotolab

import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * Job: Disk Persistence (Storage).
 * Handles reading and writing values to SharedPreferences.
 */
object SettingsPersistence {
    private const val PREFS_NAME = "SettingsPrefs"
    private const val KEY_THEME = "theme_option"
    private const val KEY_PROJECT_DIR_URI = "project_dir_uri"
    private const val KEY_SHOW_SAVE_CONFIRMATION = "show_save_confirmation"

    fun getTheme(context: Context): Boolean? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return if (prefs.contains(KEY_THEME)) {
            when (prefs.getInt(KEY_THEME, -1)) {
                0 -> false // Light
                1 -> true  // Dark
                else -> null // System
            }
        } else {
            null // Default to system
        }
    }

    fun setTheme(context: Context, useDarkTheme: Boolean?) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
        val themeValue = when (useDarkTheme) {
            false -> 0 // Light
            true -> 1  // Dark
            null -> -1 // System
        }
        prefs.putInt(KEY_THEME, themeValue)
        prefs.apply()
    }

    fun getProjectDir(context: Context): Uri? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val uriString = prefs.getString(KEY_PROJECT_DIR_URI, null)
        return uriString?.let { Uri.parse(it) }
    }

    fun setProjectDir(context: Context, uri: Uri) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
        val contentResolver = context.contentResolver
        contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        prefs.putString(KEY_PROJECT_DIR_URI, uri.toString())
        prefs.apply()
    }

    fun clearProjectDir(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
        prefs.remove(KEY_PROJECT_DIR_URI)
        prefs.apply()
    }

    fun getShouldShowSaveConfirmation(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_SHOW_SAVE_CONFIRMATION, true)
    }

    fun setShouldShowSaveConfirmation(context: Context, shouldShow: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
        prefs.putBoolean(KEY_SHOW_SAVE_CONFIRMATION, shouldShow)
        prefs.apply()
    }
}
