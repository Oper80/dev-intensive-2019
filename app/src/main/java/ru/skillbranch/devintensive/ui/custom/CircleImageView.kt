package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import ru.skillbranch.devintensive.R
import kotlin.math.min
import kotlin.math.roundToInt


class CircleImageView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {
    companion object {
        private const val DEFAULT_BORDER_COLOR = Color.WHITE
        private const val DEFAULT_BORDER_WIDTH = 2.0f
    }

    private val paint: Paint = Paint().apply { isAntiAlias = true }
    private val textPaint: TextPaint = TextPaint().apply { isAntiAlias = true }
    private val paintBorder: Paint = Paint().apply { isAntiAlias = true }
    private val paintBackground: Paint = Paint().apply { isAntiAlias = true }
    private var circleCenter = 0
    private var heightCircle: Int = 0
    private var avatarInitials: String? = null

    var cv_borderWidth: Float = DEFAULT_BORDER_WIDTH
    var cv_borderColor: Int = DEFAULT_BORDER_COLOR


    fun setInitials(initials: String) {
        avatarInitials = initials
    }

    fun getBorderWidth(): Int {
        return cv_borderWidth.toInt()
    }

    fun setBorderWidth(dp: Int) {
        cv_borderWidth = dp.toFloat()
        update()
    }

    fun getBorderColor(): Int {
        return cv_borderColor
    }

    fun setBorderColor(@ColorRes colorId: Int) {
        cv_borderColor = ContextCompat.getColor(context, colorId)
        update()
    }

    fun setBorderColor(hex: String) {
        cv_borderColor = Color.parseColor(hex)
        update()
    }


    // Object used to draw
    private var civImage: Bitmap? = null
    private var civDrawable: Drawable? = null

    init {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView)
            cv_borderColor = a.getColor(R.styleable.CircleImageView_cv_borderColor, DEFAULT_BORDER_COLOR)
            cv_borderWidth = a.getDimension(R.styleable.CircleImageView_cv_borderWidth, DEFAULT_BORDER_WIDTH)
            a.recycle()
        }
    }


    override fun onDraw(canvas: Canvas) {
        loadBitmap()

        // Check if civImage isn't null
        if (civImage == null) return

        val circleCenterWithBorder = circleCenter + cv_borderWidth

        // Draw Border
        canvas.drawCircle(circleCenterWithBorder, circleCenterWithBorder, circleCenterWithBorder, paintBorder)

        // Draw CircularImageView
        canvas.drawCircle(circleCenterWithBorder, circleCenterWithBorder, circleCenter.toFloat(), paint)
    }

    private fun update() {

        if (civImage != null)
            updateShader()

        val usableWidth = width - (paddingLeft + paddingRight)
        val usableHeight = height - (paddingTop + paddingBottom)

        heightCircle = min(usableWidth, usableHeight)

        circleCenter = (heightCircle - cv_borderWidth * 2).toInt() / 2
        paintBorder.color = cv_borderColor

        invalidate()
    }


    private fun loadBitmap() {
        if (avatarInitials == null) {
            if (civDrawable == drawable) return
            civDrawable = drawable
            civImage = drawableToBitmap(civDrawable)
        }else{
            civImage = textAsBitmap(avatarInitials.toString(), 48f, Color.WHITE)
            civDrawable = BitmapDrawable(resources, civImage)
        }

        updateShader()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        update()
    }

    private fun updateShader() {
        civImage?.also {
            // Create Shader
            val shader = BitmapShader(it, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

            // Center Image in Shader
            val scale: Float
            val dx: Float
            val dy: Float

            if (it.width * height > width * it.height) {
                scale = height / it.height.toFloat()
                dx = (width - it.width * scale) * 0.5f
                dy = 0f
            } else {
                scale = width / it.width.toFloat()
                dx = 0f
                dy = (height - it.height * scale) * 0.5f
            }

            shader.setLocalMatrix(Matrix().apply {
                setScale(scale, scale)
                postTranslate(dx, dy)
            })

            // Set Shader in Paint
            paint.shader = shader

        }
    }

    private fun textAsBitmap(text: String, textSize: Float, textColor: Int): Bitmap {
        val dp = resources.displayMetrics.density.roundToInt()
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.textSize = textSize * dp
        paint.color = textColor
        paint.textAlign = Paint.Align.CENTER

        val image = Bitmap.createBitmap(R.dimen.avatar_item_size, R.dimen.avatar_item_size, Bitmap.Config.ARGB_8888)

        image.eraseColor(R.attr.colorAccent)
        val canvas = Canvas(image)
        canvas.drawText(text, 48f * dp, 48f * dp + paint.textSize / 3, paint)
        return image
    }

    private fun drawableToBitmap(drawable: Drawable?): Bitmap? =
            when (drawable) {
                null -> null
                is BitmapDrawable -> drawable.bitmap
                else -> try {
                    // Create Bitmap object out of the drawable
                    val bitmap =
                            Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(bitmap)
                    drawable.setBounds(0, 0, canvas.width, canvas.height)
                    drawable.draw(canvas)
                    bitmap
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
}





