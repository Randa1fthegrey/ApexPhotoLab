package com.example.apexphotolab.working_project.work_space

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange

/**
 * Defines the possible outcomes of a precision mode interaction.
 */
enum class PrecisionAction {
    DROP, LIFT, MOVE
}

/**
 * Job: Interaction Logic.
 * Responsibility: Managing the brush state and translating raw gestures into editor actions.
 * Purified: Logic-only handling of "Precision Mode" and "Freeform" eraser interactions.
 */
class WorkspaceInteractionManager {
    var brushPosition by mutableStateOf<Offset?>(null)
        private set

    fun updateBrushPosition(pos: Offset?) {
        brushPosition = pos
    }

    /**
     * Logic for Freeform Eraser drag start.
     */
    fun onFreeformStart(offset: Offset, onEdit: (Offset, Offset?) -> Unit) {
        brushPosition = offset
        onEdit(offset, null)
    }

    /**
     * Logic for Freeform Eraser dragging.
     */
    fun onFreeformDrag(change: PointerInputChange, onEdit: (Offset, Offset?) -> Unit) {
        val prev = brushPosition
        change.consume()
        brushPosition = change.position
        onEdit(change.position, prev)
    }

    /**
     * Evaluates what type of precision action should be taken based on the touch position.
     */
    fun handlePrecisionDown(startPos: Offset, brushSize: Float): PrecisionAction {
        val currentPos = brushPosition
        if (currentPos == null) {
            brushPosition = startPos
            return PrecisionAction.DROP
        }
        val dist = (startPos - currentPos).getDistance()
        if (dist <= brushSize) {
            brushPosition = null
            return PrecisionAction.LIFT
        }
        return PrecisionAction.MOVE
    }

    /**
     * Updates the brush position and triggers the edit callback during a remote move.
     */
    fun handlePrecisionMove(
        delta: Offset,
        onEdit: (Offset, Offset?) -> Unit
    ) {
        brushPosition?.let { pos ->
            val prev = pos
            val newPos = pos + delta
            brushPosition = newPos
            onEdit(newPos, prev)
        }
    }
}
