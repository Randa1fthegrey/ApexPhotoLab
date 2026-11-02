package com.example.apexphotolab.the_build.welcome_screen.rgb_remote.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.graphics.SweepGradient
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

class ColorWheelView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private var colorListener: ((Int) -> Unit)? = null

    private val huePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val saturationPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val selectorPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var radius = 0f
    private var centerX = 0f
    private var centerY = 0f

    private var currentHue = 0f
    private var currentSaturation = 0f
    private var currentColor = Color.WHITE

    init {
        selectorPaint.color = Color.BLACK
        selectorPaint.strokeWidth = 4f
        selectorPaint.style = Paint.Style.STROKE
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = w / 2f
        centerY = h / 2f
        radius = min(centerX, centerY) * 0.8f

        // The corrected color order to match the math
        val hueColors = intArrayOf(Color.RED, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA, Color.RED)
        val hueShader = SweepGradient(centerX, centerY, hueColors, null)
        huePaint.shader = hueShader

        val satShader = RadialGradient(
            centerX,
            centerY,
            radius,
            Color.WHITE,
            Color.TRANSPARENT,
            Shader.TileMode.CLAMP
        )
        saturationPaint.shader = satShader
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle(centerX, centerY, radius, huePaint)
        canvas.drawCircle(centerX, centerY, radius, saturationPaint)
        val selectorX = centerX + (radius * currentSaturation * cos(Math.toRadians(currentHue.toDouble()))).toFloat()
        val selectorY = centerY + (radius * currentSaturation * sin(Math.toRadians(currentHue.toDouble()))).toFloat()
        canvas.drawCircle(selectorX, selectorY, radius / 20, selectorPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_MOVE -> {
                val dx = x - centerX
                val dy = y - centerY
                val distance = sqrt(dx * dx + dy * dy)
                currentSaturation = min(1f, distance / radius)

                currentHue = (Math.toDegrees(atan2(dy.toDouble(), dx.toDouble()))).toFloat()
                if (currentHue < 0) {
                    currentHue += 360f
                }

                updateColor()
                invalidate()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun updateColor() {
        val hsv = floatArrayOf(currentHue, currentSaturation, 1f)
        currentColor = Color.HSVToColor(hsv)
        colorListener?.invoke(currentColor)
    }

    fun setOnColorSelectedListener(listener: (Int) -> Unit) {
        this.colorListener = listener
    }
}