package com.magicalrice.project.library_base.base

import android.graphics.Paint
import android.graphics.Rect

/**
 * @package com.magicalrice.project.library_base.base
 * @author Adolph
 * @date 2019-04-22 Mon
 * @description Paint文本工具
 */

object PaintUtil {
    /**
     * 计算文本宽度
     *
     * @param paint
     * @param demoText
     * @return
     */
    fun calcTextWidth(paint: Paint, demoText: String): Int {
        return paint.measureText(demoText).toInt()
    }

    /**
     * 计算文本高度
     *
     * @param paint
     * @param demoText
     * @return
     */
    fun calcTextHeight(paint: Paint, demoText: String): Int {
        val r = Rect()
        paint.getTextBounds(demoText, 0, demoText.length, r)
        return r.height()
    }

    /**
     * 计算单行文本高度
     * @param paint
     * @return
     */
    fun getLineHeight(paint: Paint): Float {
        val metrics = paint.fontMetrics
        return metrics.descent - metrics.ascent
    }

    /**
     * 计算行间距
     * @param paint
     * @return
     */
    fun getLineSpacing(paint: Paint): Float {
        val metrics = paint.fontMetrics
        return metrics.ascent - metrics.top + metrics.bottom
    }
}