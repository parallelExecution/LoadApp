package com.example.android.loadapp

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.withStyledAttributes
import com.google.android.material.color.MaterialColors.getColor
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var xPos = 0f
    private var yPos = 0f

    private var startAngle = 0f

    private var sweepAngle = 0f
    private var rightSweep = 0f
    private var textColor = 0
    private var backgroundColorCustom = 0
    private var arcColor = 0
    private var loadingColor = 0

    private var rectArc = RectF(0f, 0f, 50f, 50f)
    private var rectText = Rect()
    private var rectBackground = Rect()

    private var drawRightSweepBoolean = false
    private var drawArcBoolean = false
    private var textToDraw = ""

    private val paint = Paint().apply {
        isAntiAlias = true
        strokeWidth = resources.getDimension(R.dimen.strokeWidth)
        textSize = resources.getDimension(R.dimen.default_text_size)
    }

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Unclicked) { p, old, new ->
        if (new == ButtonState.Loading) {
            textToDraw = context.getString(R.string.button_loading)
            drawRightSweepBoolean = true
            drawArcBoolean = true

        } else if (new == ButtonState.Completed) {
            textToDraw = context.getString(R.string.download)
            drawRightSweepBoolean = false
            drawArcBoolean = false

        }
    }

    init {
        isClickable = true
        buttonState = ButtonState.Completed

        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            textColor = getColor(R.styleable.LoadingButton_textColor, 0)
            backgroundColorCustom = getColor(R.styleable.LoadingButton_backgroundColorCustom, 0)
            arcColor = getColor(R.styleable.LoadingButton_arcColor, 0)
            loadingColor = getColor(R.styleable.LoadingButton_loadingColor, 0)
        }
    }

    fun setRightSweep(sweep: Float) {
        rightSweep = sweep
    }

    fun setSweepAngle(degree: Float) {
        sweepAngle = degree
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawColor(backgroundColorCustom)

        if (drawRightSweepBoolean) {
            paint.color = loadingColor
            canvas.getClipBounds(rectBackground)
            rectBackground.set(
                rectBackground.left, rectBackground.top,
                rightSweep.toInt(), rectBackground.bottom
            )
            canvas.drawRect(rectBackground, paint)
        }

        paint.color = textColor
        paint.textAlign = Paint.Align.CENTER
        //((paint.descent() + paint.ascent()) / 2) is the distance from the baseline to the center.
        xPos = widthSize.toFloat() / 2f
        yPos = (heightSize.toFloat() / 2f - (paint.descent() + paint.ascent()) / 2)
        canvas.drawText(textToDraw, xPos, yPos, paint)

        if (drawArcBoolean) {
            paint.getTextBounds(textToDraw, 0, textToDraw.length, rectText)
            canvas.translate(
                xPos + rectText.exactCenterX(),
                heightSize.toFloat() / 2f - rectText.height() / 2f
            )
            paint.color = arcColor
            canvas.drawArc(rectArc, startAngle, sweepAngle, true, paint)
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(MeasureSpec.getSize(w), heightMeasureSpec, 0)
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}