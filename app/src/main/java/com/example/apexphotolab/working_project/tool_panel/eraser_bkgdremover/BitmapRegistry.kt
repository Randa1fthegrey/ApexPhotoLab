package com.example.apexphotolab.working_project.tool_panel.eraser_bkgdremover

import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateMapOf

/**
 * The Central Registry for Live/Mutable Bitmaps.
 * When a layer is being edited (Eraser/BG Remover), its bitmap is copied into a mutable
 * format here. This allows for destructive, real-time pixel modification without 
 * constantly re-decoding from URI.
 */
object BitmapRegistry {
    // Stores mutable working copies of bitmaps by Layer ID
    private val activeBitmaps = mutableStateMapOf<String, Bitmap>()
    
    // A secondary map to track "revisions". Incrementing this forces Compose to re-draw.
    private val revisions = mutableStateMapOf<String, Int>()

    fun getWorkingBitmap(layerId: String): Bitmap? = activeBitmaps[layerId]
    
    fun getRevision(layerId: String): Int = revisions[layerId] ?: 0

    fun register(layerId: String, bitmap: Bitmap) {
        if (!bitmap.isMutable) {
            val mutableCopy = bitmap.copy(Bitmap.Config.ARGB_8888, true)
            activeBitmaps[layerId] = mutableCopy
        } else {
            activeBitmaps[layerId] = bitmap
        }
        revisions[layerId] = (revisions[layerId] ?: 0) + 1
    }

    /**
     * Call this whenever you modify the pixels of a registered bitmap manually.
     */
    fun notifyModification(layerId: String) {
        revisions[layerId] = (revisions[layerId] ?: 0) + 1
    }

    fun unregister(layerId: String) {
        activeBitmaps.remove(layerId)?.recycle()
        revisions.remove(layerId)
    }
    
    fun clearAll() {
        activeBitmaps.values.forEach { it.recycle() }
        activeBitmaps.clear()
        revisions.clear()
    }
}
