package com.magicalrice.project.library_widget.base

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.magicalrice.project.library_base.base.CountDownUtils
import com.magicalrice.project.library_widget.R

class AuthCodeButton : AppCompatTextView {
    private var timeUtils = CountDownUtils()

    constructor(ctx: Context) : this(ctx, null)
    constructor(ctx: Context, attrs: AttributeSet?) : this(ctx, attrs, 0)
    constructor(ctx: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        ctx,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        setTextColor(ContextCompat.getColor(context, R.color.white1))
        background = ContextCompat.getDrawable(context, R.drawable.selector_blue_gray_enable)
        text = "获取验证码"
        gravity = Gravity.CENTER
        isEnabled = true
    }

    fun startCountDown(time: Long) {
        isEnabled = false
        setTextColor(ContextCompat.getColor(context, R.color.gray1))
        timeUtils.interval(time, object : CountDownUtils.IRxNext {
            override fun onNext(num: Long) {
                if (num == 0L) {
                    endTime()
                } else {
                    text = "${num}秒"
                }
            }
        })
    }

    private fun endTime() {
        setTextColor(ContextCompat.getColor(context, R.color.red1))
        text = "重新获取"
        isEnabled = true
        clear()
    }

    override fun onWindowVisibilityChanged(visibility: Int) {
        super.onWindowVisibilityChanged(visibility)
        if (visibility != View.VISIBLE) {
            clear()
        }
    }

    fun clear() {
        timeUtils.cancelTime()
    }
}