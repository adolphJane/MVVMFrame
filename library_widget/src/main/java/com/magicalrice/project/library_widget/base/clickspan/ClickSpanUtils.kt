package com.magicalrice.project.library_widget.base.clickspan

import android.text.SpannableStringBuilder
import android.text.Spanned
import com.magicalrice.project.library_widget.bean.ClickSpanBean

/**
 * @package com.magicalrice.project.library_widget.base.clickspan
 * @author Adolph
 * @date 2019-04-20 Sat
 * @description 管理点击文本工具
 */

object ClickSpanUtils {
    fun setClick(vararg args: ClickSpanBean): SpannableStringBuilder {
        var start = 0
        var end = 0
        val spannableStringBuilder = SpannableStringBuilder()
        args.forEach {
            spannableStringBuilder.append(it.content)
            end += it.content.length
            if (it.isClickable) {
                val clickSpan = NoLineClickSpan(it.listener)
                spannableStringBuilder.setSpan(
                    clickSpan,
                    start,
                    end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            start += it.content.length
        }

        return spannableStringBuilder
    }
}