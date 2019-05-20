package com.magicalrice.project.library_widget.base

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.magicalrice.project.library_widget.R

class ErrorView : RelativeLayout {
    private lateinit var tvTitle: TextView
    private lateinit var tvButton: TextView

    constructor(ctx: Context?) : this(ctx, null)
    constructor(ctx: Context?, attrs: AttributeSet?) : super(ctx, attrs) {
        initView(ctx)
    }

    private fun initView(ctx: Context?) {
        LayoutInflater.from(ctx).inflate(R.layout.layout_error_common_view, this, true)
        tvTitle = findViewById(R.id.tv_error_title)
        tvButton = findViewById(R.id.tv_error_button)
    }

    fun setTitle(content: String) {
        tvTitle.text = content
    }

    fun setImage(@DrawableRes drawableId: Int) {
        val drawable = ContextCompat.getDrawable(context, drawableId)
        drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        tvTitle.setCompoundDrawables(null, drawable, null, null)
    }

    fun onRetry(listener: RetryListener) {
        tvButton.setOnClickListener {
            listener.onRetry()
        }
    }

    interface RetryListener {
        fun onRetry()
    }
}