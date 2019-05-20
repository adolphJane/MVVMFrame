package com.magicalrice.project.library_base.advanced

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.Window
import androidx.annotation.IntRange
import com.magicalrice.project.library_base.base.AppManager

/**
 * 亮度相关
 *
 * isAutoBrightnessEnabled : 判断是否开启自动调节亮度
 * setAutoBrightnessEnabled: 设置是否开启自动调节亮度
 * getBrightness           : 获取屏幕亮度
 * setBrightness           : 设置屏幕亮度
 * setWindowBrightness     : 设置窗口亮度
 * getWindowBrightness     : 获取窗口亮度
 */
object BrightnessUtils {

    /**
     * 判断是否开启自动调节亮度
     *
     * @return `true`: 是<br></br>`false`: 否
     */
    val isAutoBrightnessEnabled: Boolean
        get() {
            try {
                val mode = Settings.System.getInt(
                    AppManager.getInstance().getApp().getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE
                )
                return mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
            } catch (e: Settings.SettingNotFoundException) {
                e.printStackTrace()
                return false
            }

        }

    /**
     * 设置是否开启自动调节亮度
     *
     * 需添加权限 `<uses-permission android:name="android.permission.WRITE_SETTINGS" />`
     * 并得到授权
     *
     * @param enabled `true`: 打开<br></br>`false`: 关闭
     * @return `true`: 成功<br></br>`false`: 失败
     */
    fun setAutoBrightnessEnabled(enabled: Boolean): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(
                AppManager.getInstance().getApp()
            )
        ) {
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            intent.data = Uri.parse("package:" + AppManager.getInstance().getApp().getPackageName())
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            AppManager.getInstance().getApp().startActivity(intent)
            return false
        }
        return Settings.System.putInt(
            AppManager.getInstance().getApp().getContentResolver(),
            Settings.System.SCREEN_BRIGHTNESS_MODE,
            if (enabled)
                Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
            else
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
        )
    }

    /**
     * 获取屏幕亮度
     *
     * @return 屏幕亮度 0-255
     */
    val brightness: Int
        get() {
            try {
                return Settings.System.getInt(
                    AppManager.getInstance().getApp().getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS
                )
            } catch (e: Settings.SettingNotFoundException) {
                e.printStackTrace()
                return 0
            }

        }

    /**
     * 设置屏幕亮度
     *
     * 需添加权限 `<uses-permission android:name="android.permission.WRITE_SETTINGS" />`
     * 并得到授权
     *
     * @param brightness 亮度值
     */
    fun setBrightness(@IntRange(from = 0, to = 255) brightness: Int): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(
                AppManager.getInstance().getApp()
            )
        ) {
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            intent.data = Uri.parse("package:" + AppManager.getInstance().getApp().getPackageName())
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            AppManager.getInstance().getApp().startActivity(intent)
            return false
        }
        val resolver = AppManager.getInstance().getApp().contentResolver
        val b = Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, brightness)
        resolver.notifyChange(Settings.System.getUriFor("screen_brightness"), null)
        return b
    }

    /**
     * 设置窗口亮度
     *
     * @param window     窗口
     * @param brightness 亮度值
     */
    fun setWindowBrightness(
        window: Window,
        @IntRange(from = 0, to = 255) brightness: Int
    ) {
        val lp = window.attributes
        lp.screenBrightness = brightness / 255f
        window.attributes = lp
    }

    /**
     * 获取窗口亮度
     *
     * @param window 窗口
     * @return 屏幕亮度 0-255
     */
    fun getWindowBrightness(window: Window): Int {
        val lp = window.attributes
        val brightness = lp.screenBrightness
        return if (brightness < 0) brightness.toInt() else (brightness * 255).toInt()
    }
}
