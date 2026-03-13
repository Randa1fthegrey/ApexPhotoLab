package com.example.apexphotolab

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class Screen : Parcelable {
    @Parcelize
    object Welcome : Screen()
    @Parcelize
    data class ProjectFileExplorer(val isDeleteMode: Boolean = false) : Screen()
    @Parcelize
    data class MainEditor(val projectDirUri: Uri) : Screen()
}
