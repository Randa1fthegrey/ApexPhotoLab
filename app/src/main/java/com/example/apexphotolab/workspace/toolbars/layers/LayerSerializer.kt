package com.example.apexphotolab.workspace.toolbars.layers

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
            put("xPosition", layer.xPosition)
            put("yPosition", layer.yPosition)
            put("scale", layer.scale)
            put("rotation", layer.rotation)
        }
    }

    fun fromJson(json: JSONObject): Layer {
        return Layer(
            id = json.getString("id"),
            title = json.getString("title"),
            imageUri = Uri.parse(json.getString("imageUri")),
            isVisible = json.getBoolean("isVisible"),
            zOrder = json.getInt("zOrder"),
            xPosition = json.getDouble("xPosition").toFloat(),
            yPosition = json.getDouble("yPosition").toFloat(),
            scale = json.getDouble("scale").toFloat(),
            rotation = json.getDouble("rotation").toFloat()
        )
    }
}
