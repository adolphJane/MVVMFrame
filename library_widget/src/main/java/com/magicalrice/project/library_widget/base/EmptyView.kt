package com.magicalrice.project.library_widget.base

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.magicalrice.project.library_widget.R

class EmptyView : RelativeLayout {
    private lateinit var tv: TextView

    constructor(ctx: Context?) : this(ctx, null)
    constructor(ctx: Context?, attrs: AttributeSet?) : super(ctx, attrs) {
        initView(ctx)
    }

    private fun initView(ctx: Context?) {
        LayoutInflater.from(ctx).inflate(R.layout.layout_empty_common_view, this, true)
        tv = findViewById(R.id.tv_common_empty)
    }

    fun setTitle(content: String) {
        tv.text = content
    }

    fun setImage(@DrawableRes drawableId: Int) {
        val drawable = ContextCompat.getDrawable(context, drawableId)
        drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        tv.setCompoundDrawables(null, drawable, null, null)
    }
}