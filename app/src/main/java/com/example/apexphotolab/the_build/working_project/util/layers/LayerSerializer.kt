package com.example.apexphotolab.the_build.working_project.util.layers

import android.net.Uri
import org.json.JSONObject

/**
 * Job: Data Translator (The "Passport Clerk").
 * Responsibility: Maps Layer objects to JSON and vice-versa for persistence.
 */
object LayerSerializer {
    fun toJson(layer: Layer): JSONObject {
        return JSONObject().apply {
            put(val_util.KEY_ID, layer.id)
            put(val_util.KEY_TITLE, layer.title)
            put(val_util.KEY_URI, layer.imageUri.toString())
            put(val_util.KEY_VISIBLE, layer.isVisible)
            put(val_util.KEY_ZORDER, layer.zOrder)
            put(val_util.KEY_WIDTH, layer.width)
            put(val_util.KEY_HEIGHT, layer.height)
            put(val_util.KEY_X, layer.xPosition)
            put(val_util.KEY_Y, layer.yPosition)
            put(val_util.KEY_SCALE, layer.scale)
            put(val_util.KEY_ROTATION, layer.rotation)
            put(val_util.KEY_LOCKED, layer.isLocked)
        }
    }

    fun fromJson(json: JSONObject): Layer {
        return Layer(
            id = json.getString(val_util.KEY_ID),
            title = json.getString(val_util.KEY_TITLE),
            imageUri = Uri.parse(json.getString(val_util.KEY_URI)),
            isVisible = json.getBoolean(val_util.KEY_VISIBLE),
            zOrder = json.getInt(val_util.KEY_ZORDER),
            width = json.optInt(val_util.KEY_WIDTH, 0),
            height = json.optInt(val_util.KEY_HEIGHT, 0),
            xPosition = json.getDouble(val_util.KEY_X).toFloat(),
            yPosition = json.getDouble(val_util.KEY_Y).toFloat(),
            scale = json.optDouble(val_util.KEY_SCALE, 1.0).toFloat(),
            rotation = json.optDouble(val_util.KEY_ROTATION, 0.0).toFloat(),
            isLocked = json.optBoolean(val_util.KEY_LOCKED, false)
        )
    }
}
