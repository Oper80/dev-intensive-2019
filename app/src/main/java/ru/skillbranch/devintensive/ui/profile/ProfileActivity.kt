package ru.skillbranch.devintensive.ui.profile

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextPaint
import android.text.TextWatcher
import android.util.TypedValue
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_profile.*
import androidx.lifecycle.ViewModelProviders
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.models.Profile
import ru.skillbranch.devintensive.utils.Utils
import ru.skillbranch.devintensive.viewmodels.ProfileViewModel
import android.view.KeyEvent.KEYCODE_ENTER
import android.view.inputmethod.EditorInfo
import android.view.KeyEvent
import android.view.View.OnFocusChangeListener
import kotlin.math.roundToInt


class ProfileActivity : AppCompatActivity() {

    companion object {
        const val IS_EDIT_MODE = "IS_EDIT_MODE"
    }

    private lateinit var viewModel: ProfileViewModel
    var isEditMode = false
    lateinit var viewFields: Map<String, TextView>
    lateinit var initials: String

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        initViews(savedInstanceState)
        initViewModel()

    }

    private fun showCurrentMode(isEdit: Boolean) {
        val info = viewFields.filter { setOf("firstName", "lastName", "about", "repository").contains(it.key) }
        for ((_, v) in info) {
            v as EditText
            v.isEnabled = isEdit
            v.isFocusable = isEdit
            v.isFocusableInTouchMode = isEdit
            v.background.alpha = if (isEdit) 255 else 0
        }
        ic_eye.visibility = if (isEdit) View.GONE else View.VISIBLE
        wr_about.isCounterEnabled = isEdit
        with(btn_edit) {
            val filter: ColorFilter? = if (isEdit) {
                PorterDuffColorFilter(
                        fetchAccentColor(),
                        PorterDuff.Mode.SRC_IN
                )
            } else {
                null
            }

            val icon = if (isEdit) {
                resources.getDrawable(R.drawable.ic_save_black_24dp, theme)
            } else {
                resources.getDrawable(R.drawable.ic_edit_black_24dp, theme)
            }
            background.colorFilter = filter
            setImageDrawable(icon)
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        viewModel.getProfileData().observe(this, Observer { updateUi(it) })
        viewModel.getTheme().observe(this, Observer { updateTheme(it) })
    }

    private fun updateTheme(mode: Int) {
        delegate.setLocalNightMode(mode)
    }

    private fun updateUi(profile: Profile) {
        profile.toMap().also {
            for ((k, v) in viewFields) {
                val x = it[k].toString()
                v.text = it[k].toString()

            }
        }
        val fn = et_first_name.text?.toString()
        val ln = et_last_name.text?.toString()
        initials = Utils.toInitials(fn, ln) ?: ""
        if (initials != ""){
            drawDefaultAvatar(initials)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(IS_EDIT_MODE, isEditMode)
    }

    private fun initViews(savedInstanceState: Bundle?) {

        viewFields = mapOf(
                "nickName" to tv_nick_name,
                "rank" to tv_rank,
                "firstName" to et_first_name,
                "lastName" to et_last_name,
                "about" to et_about,
                "repository" to et_repository,
                "rating" to tv_rating,
                "respect" to tv_respect
        )
        isEditMode = savedInstanceState?.getBoolean(IS_EDIT_MODE, false) ?: false
        showCurrentMode(isEditMode)
        btn_edit.setOnClickListener {
            if (isEditMode) {
                if (Utils.isInvalidGithub(et_repository.text.toString())) {
                    et_repository.text.clear()
                    wr_repository.isErrorEnabled = false
                }
                saveProfileInfo()
            }
            isEditMode = !isEditMode
            showCurrentMode(isEditMode)
        }
        btn_switch_theme.setOnClickListener {
            viewModel.switchTheme()
        }

        et_repository.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
            }

            override fun onTextChanged(
                    s: CharSequence, start: Int,
                    before: Int, count: Int
            ) {
                if (Utils.isInvalidGithub(et_repository.text.toString())) {
                    wr_repository.isErrorEnabled = true
                    wr_repository.error = "Невалидный адрес репозитория"
                } else {
                    wr_repository.isErrorEnabled = false
                }
            }
        })
    }



    private fun saveProfileInfo() {
        Profile(
                firstName = et_first_name.text.toString(),
                lastName = et_last_name.text.toString(),
                about = et_about.text.toString(),
                repository = et_repository.text.toString()

        ).apply {
            viewModel.saveProfileData(this)
        }
    }

    private fun drawDefaultAvatar(initials: String, textSize: Float = 48f, color: Int = Color.WHITE) {
        val bitmap = textAsBitmap(initials, textSize, color)
        val drawable = BitmapDrawable(resources, bitmap)
        iv_avatar.setImageDrawable(drawable)
    }

    private fun textAsBitmap(text:String, textSize:Float, textColor:Int): Bitmap {
        val dp = resources.displayMetrics.density.roundToInt()
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.textSize = textSize*dp
        paint.color = textColor
        paint.textAlign = Paint.Align.CENTER

        val avatarSize = iv_avatar.layoutParams.height
        val image = Bitmap.createBitmap(avatarSize, avatarSize, Bitmap.Config.ARGB_8888)

        image.eraseColor(fetchAccentColor())
        val canvas = Canvas(image)

        canvas.drawText(text, 48f*dp, 48f*dp + paint.textSize/3, paint)
        return image
    }

    private fun fetchAccentColor(): Int {
        val typedValue = TypedValue()

        val a = this.obtainStyledAttributes(typedValue.data, intArrayOf(R.attr.colorAccent))
        val color = a.getColor(0, 0)

        a.recycle()

        return color
    }
}

