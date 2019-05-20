package com.magicalrice.project.library_base.base

import android.Manifest.permission.WRITE_SETTINGS
import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Environment
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.Surface
import android.view.WindowManager
import android.webkit.WebView
import androidx.annotation.RequiresPermission
import com.magicalrice.project.library_base.base.log.LogUtils
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object ScreenUtils {
    fun dp2pxInt(context: Context?, dpValue: Float) : Int {
        val scale = context?.resources?.displayMetrics?.density ?: 0f
        return (dpValue * scale + 0.5f).toInt()
    }

    fun dp2pxInt(context: Context?, dpValue: Int) : Int {
        val scale = context?.resources?.displayMetrics?.density ?: 0f
        return (dpValue * scale + 0.5f).toInt()
    }

    fun dp2pxFloat(context: Context?, dpValue: Float) : Float {
        val scale = context?.resources?.displayMetrics?.density ?: 0f
        return dpValue * scale + 0.5f
    }

    fun dp2pxFloat(context: Context?, dpValue: Int) : Float {
        val scale = context?.resources?.displayMetrics?.density ?: 0f
        return dpValue * scale + 0.5f
    }

    fun px2dp(context: Context,pxValue: Float) : Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    fun sp2pxInt(context: Context, spValue: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }

    fun sp2pxFloat(context: Context, spValue: Float): Float {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return spValue * fontScale + 0.5f
    }

    fun px2sp(context: Context,pxValue: Float) : Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (pxValue / fontScale + 0.5f).toInt()
    }

    /**
     * 获取屏幕的宽度（单位：px）
     *
     * @return the width of screen, in pixel
     */
    fun getScreenWidth(context: Context?) : Int{
        return context?.resources?.displayMetrics?.widthPixels ?: 0
    }

    /**
     * 获取屏幕的高度（单位：px）
     *
     * @return the height of screen, in pixel
     */
    fun getScreenHeight(context: Context) : Int{
        return context.resources.displayMetrics.heightPixels
    }

    fun getStatusBarHeight(context: Context) : Int{
        var statusBarHeight = -1
        val resourceId = context.resources.getIdentifier("status_bar_height","dimen","android")
        if (resourceId > 0) {
            statusBarHeight = context.resources.getDimensionPixelSize(resourceId)
        }
        if (statusBarHeight == -1) {
            statusBarHeight = dp2pxInt(context,20)
        }
        return statusBarHeight
    }

    /**
     * 获取屏幕密度
     *
     * @return the density of screen
     */
    fun getScreenDensity(): Float {
        return Resources.getSystem().displayMetrics.density
    }

    /**
     * 获取屏幕密度 DPI
     *
     * @return the screen density expressed as dots-per-inch
     */
    fun getScreenDensityDpi(): Int {
        return Resources.getSystem().displayMetrics.densityDpi
    }

    /**
     * 设置屏幕为全屏
     *
     * @param activity The activity.
     */
    fun setFullScreen(activity: Activity) {
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    /**
     * 设置屏幕为非全屏
     *
     * @param activity The activity.
     */
    fun setNonFullScreen(activity: Activity) {
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    /**
     * 切换屏幕为全屏与否状态
     *
     * @param activity The activity.
     */
    fun toggleFullScreen(activity: Activity) {
        val fullScreenFlag = WindowManager.LayoutParams.FLAG_FULLSCREEN
        val window = activity.window
        if (window.attributes.flags and fullScreenFlag == fullScreenFlag) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
    }

    /**
     * 判断屏幕是否为全屏
     *
     * @param activity The activity.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isFullScreen(activity: Activity): Boolean {
        val fullScreenFlag = WindowManager.LayoutParams.FLAG_FULLSCREEN
        return activity.window.attributes.flags and fullScreenFlag == fullScreenFlag
    }

    /**
     * 设置屏幕为横屏
     *
     * @param activity The activity.
     */
    fun setLandscape(activity: Activity) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    /**
     * 设置屏幕为竖屏
     *
     * @param activity The activity.
     */
    fun setPortrait(activity: Activity) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    /**
     * 判断是否横屏
     *
     * @return `true`: yes<br></br>`false`: no
     */
    fun isLandscape(): Boolean {
        return AppManager.getInstance().getApp().resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

    /**
     * 判断是否竖屏
     *
     * @return `true`: yes<br></br>`false`: no
     */
    fun isPortrait(): Boolean {
        return AppManager.getInstance().getApp().resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    }

    /**
     * 获取屏幕旋转角度
     *
     * @param activity The activity.
     * @return the rotation of screen
     */
    fun getScreenRotation(activity: Activity): Int {
        return when (activity.windowManager.defaultDisplay.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> 0
        }
    }

    /**
     * 获取当前屏幕截图，包含状态栏
     */
    fun snapShotWithStatusBar(activity: Activity): Bitmap? {
        val view = activity.window.decorView
        view.isDrawingCacheEnabled = true
        view.buildDrawingCache()
        val bmp = view.drawingCache
        val width = getScreenWidth(activity)
        val height = getScreenHeight(activity)
        var bp: Bitmap? = null
        bp = Bitmap.createBitmap(bmp, 0, 0, width, height)
        view.destroyDrawingCache()
        return bp

    }

    /**
     * 获取当前屏幕截图，不包含状态栏
     */
    fun snapShotWithoutStatusBar(activity: Activity): Bitmap? {
        val view = activity.window.decorView
        view.isDrawingCacheEnabled = true
        view.buildDrawingCache()
        val bmp = view.drawingCache
        val frame = Rect()
        activity.window.decorView.getWindowVisibleDisplayFrame(frame)
        val statusBarHeight = frame.top

        val width = getScreenWidth(activity)
        val height = getScreenHeight(activity)
        var bp: Bitmap? = null
        bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height - statusBarHeight)
        view.destroyDrawingCache()
        return bp
    }

    /**
     * 获取指定Activity的截屏，保存到png文件
     *
     * @param activity activity
     * @return 截屏Bitmap
     */
    private fun takeScreenShot(activity: Activity): Bitmap {
        // View是你需要截图的View
        val view = activity.window.decorView
        view.isDrawingCacheEnabled = true
        view.buildDrawingCache()
        val b1 = view.drawingCache

        // 获取状态栏高度
        val frame = Rect()
        activity.window.decorView.getWindowVisibleDisplayFrame(frame)
        val statusBarHeight = frame.top
        LogUtils.i("TAG", "" + statusBarHeight)

        // 获取屏幕长和高
        val width = activity.windowManager.defaultDisplay.width
        val height = activity.windowManager.defaultDisplay
            .height
        val b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height - statusBarHeight)
        view.destroyDrawingCache()
        return b
    }

    /**
     * 截取webView快照(webView加载的整个内容的大小)
     *
     * @param webView webview
     * @return 截屏bitmap
     */
    private fun captureWebView(webView: WebView): Bitmap {
        val snapShot = webView.capturePicture()

        val bmp = Bitmap.createBitmap(snapShot.width, snapShot.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        snapShot.draw(canvas)
        return bmp
    }

    /**
     * 保存bitmap
     *
     * @param b           bitmap
     * @param strFileName 文件名
     * @return 是否保存成功
     */
    private fun savePic(b: Bitmap, strFileName: String): Boolean {
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(strFileName)
            fos?.let {
                b.compress(Bitmap.CompressFormat.PNG, 90, fos)
                it.flush()
                it.close()
                return true
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return false
    }

    /**
     * 根据毫秒获得格式化日期
     *
     * @param time   毫秒数
     * @param format 格式化字符串
     * @return 格式化后的字符串
     */
    fun getDate(time: Long, format: String) : String{
        val date = Date(time)
        val formatter =  SimpleDateFormat(format, Locale.getDefault())
        return formatter.format(date)
    }

    /**
     * 是否存在sd卡
     *
     * @return 是否存在sd卡
     */
    fun isExistsSD() : Boolean{
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED)
            return true
        return false
    }

    /**
     * 获得文件名
     *
     * @param context 上下文
     * @return 文件名
     */
    private fun getFileName(context: Context): String {
        val fileName = getDate(System.currentTimeMillis(), "yyyyMMddHHmmss") + ".png"
        val localPath: String
        if (isExistsSD()) {
            localPath = context.externalCacheDir.absolutePath + File.separator + fileName
        } else {
            localPath = context.filesDir.absolutePath + fileName
        }

        return localPath
    }

    /**
     * 截屏并保存
     *
     * @param a activity
     * @return 保存的路径
     */
    fun shoot(a: Activity): String {
        val localPath = getFileName(a)
        val ret = savePic(takeScreenShot(a), localPath)
        return if (ret) {
            localPath
        } else {
            ""
        }
    }

    /**
     * 截屏并保存
     *
     * @param context 上下文
     * @param webView webview
     * @return 保存的路径
     */
    fun shootWebView(context: Context, webView: WebView): String {
        val localPath = getFileName(context)
        val ret = savePic(captureWebView(webView), localPath)
        return if (ret) {
            localPath
        } else {
            ""
        }
    }

    /**
     * 判断是否锁屏
     *
     * @return `true`: yes<br></br>`false`: no
     */
    fun isScreenLock(): Boolean {
        val km =
            AppManager.getInstance().getApp().getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        return km.isKeyguardLocked
    }

    /**
     * 设置进入休眠时长
     *
     * Must hold `<uses-permission android:name="android.permission.WRITE_SETTINGS" />`
     *
     * @param duration The duration.
     */
    @RequiresPermission(WRITE_SETTINGS)
    fun setSleepDuration(duration: Int) {
        Settings.System.putInt(
            AppManager.getInstance().getApp().contentResolver,
            Settings.System.SCREEN_OFF_TIMEOUT,
            duration
        )
    }

    /**
     * 获取进入休眠时长
     *
     * @return the duration of sleep.
     */
    fun getSleepDuration(): Int {
        return try {
            Settings.System.getInt(
                AppManager.getInstance().getApp().contentResolver,
                Settings.System.SCREEN_OFF_TIMEOUT
            )
        } catch (e: Settings.SettingNotFoundException) {
            e.printStackTrace()
            -123
        }

    }

    /**
     * 判断是否是平板
     *
     * @return `true`: yes<br></br>`false`: no
     */
    fun isTablet(): Boolean {
        return (AppManager.getInstance().getApp().resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }

    /**
     * 功能描述：获取整块屏幕的高度
     *
     * @param context
     * @return int
     */
    fun getRealScreenHeight(context: Context): Int {
        var dpi = 0
        val display = (context as Activity).windowManager
            .defaultDisplay
        val dm = DisplayMetrics()
        val c: Class<*>
        try {
            c = Class.forName("android.view.Display")
            val method = c.getMethod("getRealMetrics", DisplayMetrics::class.java)
            method.invoke(display, dm)
            dpi = dm.heightPixels
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return dpi
    }

    /**
     * 功能描述：获取虚拟按键区域的高度
     *
     * @param context
     * @return int 如果有虚拟按键则返回其高度否则返回0；
     */
    fun getNavigationAreaHeight(context: Context): Int {
        val realScreenHeight = getRealScreenHeight(context)
        val screenHeight = getScreenHeight(context)

        return realScreenHeight - screenHeight
    }

    /**
     * 获取导航栏高度
     * @param c
     * @return
     */
    fun getNavigationBarrH(c: Context): Int {
        val resources = c.resources
        val identifier = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return resources.getDimensionPixelOffset(identifier)
    }
}