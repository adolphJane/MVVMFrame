package com.magicalrice.project.library_base.advanced

import android.Manifest.permission.VIBRATE
import android.content.Context
import android.os.Vibrator
import androidx.annotation.RequiresPermission

/**
 * @package com.magicalrice.project.library_base.advanced
 * @author Adolph
 * @date 2019-04-22 Mon
 * @description 震动相关工具类
 */

object VibrationUtils {
    /**
     * 震动
     *
     * 需添加权限 `<uses-permission android:name="android.permission.VIBRATE" />`
     *
     * @param context      上下文
     * @param milliseconds 振动时长
     */
    @RequiresPermission(VIBRATE)
    fun vibrate(context: Context, milliseconds: Long) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(milliseconds)
    }

    /**
     * 指定手机以pattern模式振动
     *
     * @param context
     * @param pattern new long[]{400,800,1200,1600}，就是指定在400ms、800ms、1200ms、1600ms这些时间点交替启动、关闭手机振动器
     * @param repeat  指定pattern数组的索引，指定pattern数组中从repeat索引开始的振动进行循环。-1表示只振动一次，非-1表示从 pattern的指定下标开始重复振动。
     */
    @RequiresPermission(VIBRATE)
    fun vibrate(context: Context, pattern: LongArray, repeat: Int) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(pattern, repeat)
    }

    /**
     * 取消振动
     *
     * @param context 上下文
     */
    @RequiresPermission(VIBRATE)
    fun cancel(context: Context) {
        (context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).cancel()
    }
}