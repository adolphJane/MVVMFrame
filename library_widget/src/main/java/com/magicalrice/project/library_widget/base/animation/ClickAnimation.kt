package com.magicalrice.project.library_widget.base.animation

import android.graphics.ColorMatrixColorFilter
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.ImageView
import com.magicalrice.project.library_base.base.log.LogUtils

/**
 * @Description:主要功能:控件点击效果动画工具类
 */

object ClickAnimation {

    /**
     * 让控件点击时，颜色变深
     */
    val VIEW_TOUCH_DARK: OnTouchListener = object : OnTouchListener {

        val BT_SELECTED = floatArrayOf(
            1f,
            0f,
            0f,
            0f,
            -50f,
            0f,
            1f,
            0f,
            0f,
            -50f,
            0f,
            0f,
            1f,
            0f,
            -50f,
            0f,
            0f,
            0f,
            1f,
            0f
        )
        val BT_NOT_SELECTED = floatArrayOf(
            1f,
            0f,
            0f,
            0f,
            0f,
            0f,
            1f,
            0f,
            0f,
            0f,
            0f,
            0f,
            1f,
            0f,
            0f,
            0f,
            0f,
            0f,
            1f,
            0f
        )

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (v is ImageView) {
                    v.colorFilter = ColorMatrixColorFilter(BT_SELECTED)
                } else {
                    v.background.colorFilter = ColorMatrixColorFilter(BT_SELECTED)
                    v.setBackgroundDrawable(v.background)
                }
            } else if (event.action == MotionEvent.ACTION_UP) {
                if (v is ImageView) {
                    v.colorFilter = ColorMatrixColorFilter(
                        BT_NOT_SELECTED
                    )
                } else {
                    v.background.colorFilter = ColorMatrixColorFilter(BT_NOT_SELECTED)
                    v.setBackgroundDrawable(v.background)
                }
            }
            return false
        }
    }

    /**
     * 让控件点击时，颜色变暗
     */
    val VIEW_TOUCH_LIGHT: OnTouchListener = object : OnTouchListener {

        val BT_SELECTED = floatArrayOf(
            1f,
            0f,
            0f,
            0f,
            50f,
            0f,
            1f,
            0f,
            0f,
            50f,
            0f,
            0f,
            1f,
            0f,
            50f,
            0f,
            0f,
            0f,
            1f,
            0f
        )
        val BT_NOT_SELECTED = floatArrayOf(
            1f,
            0f,
            0f,
            0f,
            0f,
            0f,
            1f,
            0f,
            0f,
            0f,
            0f,
            0f,
            1f,
            0f,
            0f,
            0f,
            0f,
            0f,
            1f,
            0f
        )

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (v is ImageView) {
                    v.isDrawingCacheEnabled = true

                    v.colorFilter = ColorMatrixColorFilter(BT_SELECTED)
                } else {
                    v.background.colorFilter = ColorMatrixColorFilter(BT_SELECTED)
                    v.background = v.background
                }
            } else if (event.action == MotionEvent.ACTION_UP) {
                if (v is ImageView) {
                    v.colorFilter = ColorMatrixColorFilter(
                        BT_NOT_SELECTED
                    )
                    LogUtils.e("变回来")
                } else {
                    v.background.colorFilter = ColorMatrixColorFilter(BT_NOT_SELECTED)
                    v.background = v.background
                }
            }
            return false
        }
    }

    /**
     * 给视图添加点击效果,让背景变深
     */
    fun addTouchDrak(view: View, isClick: Boolean) {
        view.setOnTouchListener(VIEW_TOUCH_DARK)

        if (!isClick) {
            view.setOnClickListener { }
        }
    }

    /**
     * 给视图添加点击效果,让背景变暗
     */
    fun addTouchLight(view: View, isClick: Boolean) {
        view.setOnTouchListener(VIEW_TOUCH_LIGHT)

        if (!isClick) {
            view.setOnClickListener { }
        }
    }
}
