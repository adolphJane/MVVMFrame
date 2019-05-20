package com.magicalrice.project.library_widget.base

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.BounceInterpolator
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.magicalrice.project.library_widget.R


class TabIconLayout : RelativeLayout {
    private lateinit var imageView: ImageView
    private lateinit var textView: TextView
    private var animatorSet: AnimatorSet? = null

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
    }

    private fun initView() {
        LayoutInflater.from(context).inflate(R.layout.layout_tab, this, true)
        imageView = findViewById(R.id.img_tab)
        textView = findViewById(R.id.tv_tab)
    }

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
        if (selected) {
            imageView.isSelected = true
            textView.setTextColor(ContextCompat.getColor(context, R.color.red1))
            if (animatorSet == null) {
                animatorSet = AnimatorSet()
                val animatorX = ObjectAnimator.ofFloat(imageView, "scaleX", 1f, 1.4f, 1f)
                val animatorY = ObjectAnimator.ofFloat(imageView, "scaleY", 1f, 1.4f, 1f)
                animatorSet!!.playTogether(animatorX, animatorY)
                animatorSet!!.interpolator = BounceInterpolator()
                animatorSet!!.duration = 500
            }
            if (animatorSet != null && !animatorSet!!.isRunning) {
                animatorSet!!.start()
            }
        } else {
            imageView.isSelected = false
            textView.setTextColor(ContextCompat.getColor(context, R.color.gray1))
            if (animatorSet != null && animatorSet!!.isRunning) {
                animatorSet!!.cancel()
                imageView.scaleX = 1f
                imageView.scaleY = 1f
            }
        }
    }

    fun setTabTitle(title: String) {
        textView.text = title
    }

    fun setTabIcon(@DrawableRes imgSrc: Int) {
        imageView.setImageResource(imgSrc)
    }
}