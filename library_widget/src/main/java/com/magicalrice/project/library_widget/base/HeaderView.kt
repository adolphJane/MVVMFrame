package com.magicalrice.project.library_widget.base

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.magicalrice.project.library_widget.R

class HeaderView : RelativeLayout {
    private lateinit var back: Button
    private lateinit var tvTitle: TextView
    private lateinit var rightBtn: Button
    private lateinit var separator: View

    constructor(ctx: Context) : this(ctx, null)
    constructor(ctx: Context, attrs: AttributeSet?) : super(ctx, attrs) {
        initView(ctx, attrs)
    }

    private fun initView(ctx: Context, attrs: AttributeSet?) {
        LayoutInflater.from(context).inflate(R.layout.layout_header, this, true)
        back = findViewById(R.id.btn_back)
        tvTitle = findViewById(R.id.tv_header_title)
        rightBtn = findViewById(R.id.btn_right)
        separator = findViewById(R.id.separator)

        val a = ctx.obtainStyledAttributes(attrs, R.styleable.HeaderView)
        this.setBackgroundColor(a.getColor(R.styleable.HeaderView_title_background_color, Color.WHITE))
        back.visibility = if (a.getBoolean(R.styleable.HeaderView_left_button_visible, true)) View.VISIBLE else View.GONE
        rightBtn.visibility = if (a.getBoolean(R.styleable.HeaderView_right_button_visible, false)) View.VISIBLE else View.GONE
        rightBtn.text = a.getText(R.styleable.HeaderView_right_button_text)
        val resId = a.getResourceId(R.styleable.HeaderView_right_button_drawable, -1)
        if (resId != -1) {
            val drawable = ContextCompat.getDrawable(context, resId)
            drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
            rightBtn.setCompoundDrawables(null, null, drawable, null)
        }
        rightBtn.setTextColor(
            a.getColor(
                R.styleable.HeaderView_right_button_text_color,
                ContextCompat.getColor(context, R.color.black1)
            )
        )
        rightBtn.textSize = a.getFloat(R.styleable.HeaderView_right_button_text_size, 15f)
        tvTitle.text = a.getText(R.styleable.HeaderView_title_text)
        tvTitle.setTextColor(a.getColor(R.styleable.HeaderView_title_text_color, Color.WHITE))
        separator.visibility = if (a.getBoolean(R.styleable.HeaderView_separator_show, true)) View.VISIBLE else View.GONE
        a.recycle()
    }

    fun setTitle(title: String?) {
        tvTitle.text = title
    }

    fun setBackListener(listener: OnClickListener): HeaderView {
        back.setOnClickListener(listener)
        return this
    }

    fun setRightListener(listener: OnClickListener): HeaderView {
        rightBtn.setOnClickListener(listener)
        return this
    }

    fun setRightButtonVisible(isVisible: Boolean) {
        if (isVisible) {
            rightBtn.visibility = View.VISIBLE
        } else {
            rightBtn.visibility = View.GONE
        }
    }

    fun setRightButtonText(title: String?) {
        rightBtn.text = title
    }
}