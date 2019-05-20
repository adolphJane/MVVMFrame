package com.magicalrice.project.library_widget.base.clickspan

import android.graphics.Color
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View

/**
 * @package com.magicalrice.project.library_widget.base.clickspan
 * @author Adolph
 * @date 2019-04-20 Sat
 * @description 去除点击文本下划线
 */

class NoLineClickSpan : ClickableSpan {
    private var listener: OnClickSkipListener? = null

    constructor(listener: OnClickSkipListener?) : super() {
        this.listener = listener
    }

    override fun updateDrawState(ds: TextPaint) {
        ds.color = Color.parseColor("#2D8BFB")
        ds.isUnderlineText = false
        ds.clearShadowLayer()
    }

    override fun onClick(widget: View) {
        listener?.onClick()
    }

    interface OnClickSkipListener {
        fun onClick()
    }
}