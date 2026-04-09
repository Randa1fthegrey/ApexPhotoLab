package com.example.apexphotolab.workspace.tool_panel.layers

import android.net.Uri
import org.json.JSONObject

object LayerSerializer {
    fun toJson(layer: Layer): JSONObject {
        return JSONObject().apply {
            put("id", layer.id)
            put("title", layer.title)
            put("imageUri", layer.imageUri.toString())
            put("isVisible", layer.isVisible)
            put("zOrder", layer.zOrder)
            put("width", layer.width)
            put("height", layer.height)
            put("xPosition", layer.xPosition)
            put("yPosition", layer.yPosition)
            put("scale", layer.scale)
            put("rotation", layer.rotation)
            put("isLocked", layer.isLocked)
        }
    }

    fun fromJson(json: JSONObject): Layer {
        return Layer(
            id = json.getString("id"),
            title = json.getString("title"),
            imageUri = Uri.parse(json.getString("imageUri")),
            isVisible = json.getBoolean("isVisible"),
            zOrder = json.getInt("zOrder"),
            width = json.optInt("width", 0),
            height = json.optInt("height", 0),
            xPosition = json.getDouble("xPosition").toFloat(),
            yPosition = json.getDouble("yPosition").toFloat(),
            scale = json.optDouble("scale", 1.0).toFloat(),
            rotation = json.optDouble("rotation", 0.0).toFloat(),
            isLocked = json.optBoolean("isLocked", false)
        )
    }
}
