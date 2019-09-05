package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.Dimension
import androidx.annotation.Dimension.DP
import androidx.core.content.ContextCompat
import ru.skillbranch.devintensive.R
import kotlin.math.min


class CircleImageView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {
    companion object {
        private const val DEFAULT_BORDER_COLOR = Color.WHITE
        private const val DEFAULT_BORDER_WIDTH = 2.0f
        private const val DEFAULT_TEXT_COLOR = Color.WHITE
    }


    private val maskPaint: Paint = Paint().apply {
        isAntiAlias = true
        isFilterBitmap = true
        isDither = true
//        xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC)
    }
    private val imagePaint: Paint = Paint().apply {
        isAntiAlias = true
        isFilterBitmap = true
        isDither = true
        xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    }
    private val borderPaint: Paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
    }
    private val paint = Paint().apply {
        isAntiAlias = true
        textSize = 40f
        color = DEFAULT_TEXT_COLOR
        textAlign = Paint.Align.CENTER
    }
    private var avatarInitials: String? = null

    private var borderWidth: Float = DEFAULT_BORDER_WIDTH
    private var borderColor: Int = DEFAULT_BORDER_COLOR


    fun setInitials(initials: String) {
        avatarInitials = initials
    }

    @Dimension(unit = DP)
    fun getBorderWidth(): Int {
        return borderWidth.toInt()
    }

    @Dimension(unit = DP)
    fun setBorderWidth(dp: Int) {
        borderWidth = dp.toFloat()
        this.invalidate()
    }

    fun getBorderColor(): Int {
        return borderColor
    }

    fun setBorderColor(@ColorRes colorId: Int) {
        borderColor = ContextCompat.getColor(context, colorId)
        this.invalidate()
    }

    fun setBorderColor(hex: String) {
        borderColor = Color.parseColor(hex)
        this.invalidate()
    }

    init {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView)
            borderColor = a.getColor(R.styleable.CircleImageView_cv_borderColor, DEFAULT_BORDER_COLOR)
            borderWidth = a.getDimension(R.styleable.CircleImageView_cv_borderWidth, DEFAULT_BORDER_WIDTH)
            a.recycle()
        }
        borderPaint.style = Paint.Style.STROKE
        setLayerType(LAYER_TYPE_HARDWARE, null)
    }


    override fun onDraw(canvas: Canvas) {

        if (width == 0 || height == 0) return
        var bitmap = drawableToBitmap(drawable)
        val isDefault = bitmap == null
        if (isDefault) {
            bitmap = emptyBitmap()
        }

        val halfWidth = width / 2F
        val halfHeight = height / 2F
        val radius = min(halfWidth, halfHeight)

        canvas.drawCircle(halfWidth, halfHeight, radius, maskPaint)
        canvas.drawBitmap(bitmap!!, 0F, 0F, imagePaint)

        if (isDefault) {
            canvas.drawText(avatarInitials
                    ?: "??", halfWidth, halfHeight + paint.textSize / 3, paint)
        }

        if (borderWidth > 0) {
            borderPaint.color = borderColor
            borderPaint.strokeWidth = borderWidth
            canvas.drawCircle(halfWidth, halfHeight, radius - borderWidth / 2, borderPaint)
        }

    }

    private fun emptyBitmap(): Bitmap {

        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bmp.eraseColor(R.attr.colorAccent)
        return bmp
    }


    private fun drawableToBitmap(drawable: Drawable?): Bitmap? =
            when (drawable) {
                null -> null
                is BitmapDrawable -> drawable.bitmap
                else -> {
                    val bmp = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(bmp)
                    drawable.setBounds(0, 0, canvas.width, canvas.height)
                    drawable.draw(canvas)
                    bmp
                }
            }
}