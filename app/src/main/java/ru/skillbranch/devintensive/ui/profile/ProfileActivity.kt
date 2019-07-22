package ru.skillbranch.devintensive.ui.profile

import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_profile.*
import ru.skillbranch.devintensive.models.Bender
import android.view.inputmethod.EditorInfo
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.extensions.hideKeyboard


class ProfileActivity : AppCompatActivity() {

    companion object {
        const val IS_EDIT_MODE = "IS_EDIT_MODE"
    }

    var isEditMode = false
    lateinit var viewFields: Map<String, TextView>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        initViews(savedInstanceState)
    }

    private fun initViews(savedInstanceState: Bundle?) {
        viewFields = mapOf(
            "nickname" to tv_nick_name,
            "rank" to tv_rank,
            "firstName" to et_first_name,
            "lastName" to et_last_name,
            "about" to et_about,
            "repository" to et_repository,
            "rating" to tv_rating,
            "respect" to tv_respect
        )
        btn_edit.setOnClickListener {
            isEditMode = !isEditMode
            showCurrentMode(isEditMode)
        }
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
        ic_eye.visibility = if(isEdit) View.GONE else View.VISIBLE
        wr_about.isCounterEnabled = isEdit
        with(btn_edit){
            val filter: ColorFilter? = if(isEdit){
                PorterDuffColorFilter(
                    resources.getColor(R.color.color_accent,theme),
                    PorterDuff.Mode.SRC_IN
                )
            }else{
                null
            }

            val icon = if(isEdit){
                resources.getDrawable(R.drawable.ic_save_black_24dp)
            }else{
                resources.getDrawable(R.drawable.ic_edit_black_24dp)
            }
            background.colorFilter = filter
            setImageDrawable(icon)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }


}