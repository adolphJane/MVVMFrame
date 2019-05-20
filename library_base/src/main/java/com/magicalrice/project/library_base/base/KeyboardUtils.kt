package com.magicalrice.project.library_base.base

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout

/**
 * @package com.magicalrice.project.library_base.base
 * @author Adolph
 * @date 2019-04-22 Mon
 * @description 键盘相关
 *
 * showSoftInput                     : 显示软键盘
 * showSoftInputUsingToggle          : 显示软键盘用 toggle
 * hideSoftInput                     : 隐藏软键盘
 * hideSoftInputUsingToggle          : 隐藏软键盘用 toggle
 * toggleSoftInput                   : 切换键盘显示与否状态
 * isSoftInputVisible                : 判断软键盘是否可见
 * registerSoftInputChangedListener  : 注册软键盘改变监听器
 * unregisterSoftInputChangedListener: 注销软键盘改变监听器
 * fixAndroidBug5497                 : 修复安卓 5497 BUG
 * fixSoftInputLeaks                 : 修复软键盘内存泄漏
 * clickBlankArea2HideSoftInput      : 点击屏幕空白区域隐藏软键盘
 */

object KeyboardUtils {
    private var sDecorViewInvisibleHeightPre: Int = 0
    private var onGlobalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null
    private var onSoftInputChangedListener: OnSoftInputChangedListener? = null
    private var sContentViewInvisibleHeightPre5497: Int = 0

    /**
     * 显示软键盘
     *
     * @param activity The activity.
     */
    fun showSoftInput(activity: Activity) {
        val imm =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager ?: return
        var view = activity.currentFocus
        if (view == null) {
            view = View(activity)
            view.isFocusable = true
            view.isFocusableInTouchMode = true
            view.requestFocus()
        }
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED)
    }

    /**
     * 显示软键盘
     *
     * @param view The view.
     */
    fun showSoftInput(view: View) {
        val imm =
            AppManager.getInstance().getApp().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        view.isFocusable = true
        view.isFocusableInTouchMode = true
        view.requestFocus()

        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED)
    }

    /**
     * 显示软键盘用 toggle
     *
     * @param activity The activity.
     */
    fun showSoftInputUsingToggle(activity: Activity) {
        if (isSoftInputVisible(activity)) return
        toggleSoftInput()
    }

    /**
     * 隐藏软键盘
     *
     * @param activity The activity.
     */
    fun hideSoftInput(activity: Activity) {
        val imm =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager ?: return
        var view = activity.currentFocus
        if (view == null) view = View(activity)
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * 隐藏软键盘
     *
     * @param view The view.
     */
    fun hideSoftInput(view: View) {
        val imm =
            AppManager.getInstance().getApp().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * 隐藏软键盘用 toggle
     *
     * @param activity The activity.
     */
    fun hideSoftInputUsingToggle(activity: Activity) {
        if (!isSoftInputVisible(activity)) return
        toggleSoftInput()
    }

    /**
     * 切换键盘显示与否状态
     */
    fun toggleSoftInput() {
        val imm =
            AppManager.getInstance().getApp().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    /**
     * 判断软键盘是否可见
     *
     * @param activity The activity.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isSoftInputVisible(activity: Activity): Boolean {
        return getDecorViewInvisibleHeight(activity) > 0
    }

    private var sDecorViewDelta = 0

    private fun getDecorViewInvisibleHeight(activity: Activity): Int {
        val decorView = activity.window.decorView ?: return sDecorViewInvisibleHeightPre
        val outRect = Rect()
        decorView.getWindowVisibleDisplayFrame(outRect)
        Log.d(
            "KeyboardUtils",
            "getDecorViewInvisibleHeight: " + (decorView.bottom - outRect.bottom)
        )
        val delta = Math.abs(decorView.bottom - outRect.bottom)
        if (delta <= getNavBarHeight()) {
            sDecorViewDelta = delta
            return 0
        }
        return delta - sDecorViewDelta
    }

    /**
     * 注册软键盘改变监听器
     *
     * @param activity The activity.
     * @param listener The soft input changed listener.
     */
    fun registerSoftInputChangedListener(
        activity: Activity,
        listener: OnSoftInputChangedListener
    ) {
        val flags = activity.window.attributes.flags
        if (flags and WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS != 0) {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
        val contentView = activity.findViewById<FrameLayout>(android.R.id.content)
        sDecorViewInvisibleHeightPre = getDecorViewInvisibleHeight(activity)
        onSoftInputChangedListener = listener
        onGlobalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
            if (onSoftInputChangedListener != null) {
                val height = getDecorViewInvisibleHeight(activity)
                if (sDecorViewInvisibleHeightPre != height) {
                    onSoftInputChangedListener?.onSoftInputChanged(height)
                    sDecorViewInvisibleHeightPre = height
                }
            }
        }
        contentView.viewTreeObserver
            .addOnGlobalLayoutListener(onGlobalLayoutListener)
    }

    /**
     * 注销软键盘改变监听器
     *
     * @param activity The activity.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    fun unregisterSoftInputChangedListener(activity: Activity) {
        val contentView = activity.findViewById<View>(android.R.id.content)
        contentView.viewTreeObserver.removeOnGlobalLayoutListener(onGlobalLayoutListener)
        onSoftInputChangedListener = null
        onGlobalLayoutListener = null
    }

    /**
     * 修复安卓 5497 BUG
     *
     * Don't set adjustResize
     *
     * @param activity The activity.
     */
    fun fixAndroidBug5497(activity: Activity) {
        //        Window window = activity.getWindow();
        //        int softInputMode = window.getAttributes().softInputMode;
        //        window.setSoftInputMode(softInputMode & ~WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        val contentView = activity.findViewById<FrameLayout>(android.R.id.content)
        val contentViewChild = contentView.getChildAt(0)
        val paddingBottom = contentViewChild.paddingBottom
        sContentViewInvisibleHeightPre5497 = getContentViewInvisibleHeight(activity)
        contentView.viewTreeObserver
            .addOnGlobalLayoutListener {
                val height = getContentViewInvisibleHeight(activity)
                if (sContentViewInvisibleHeightPre5497 != height) {
                    contentViewChild.setPadding(
                        contentViewChild.paddingLeft,
                        contentViewChild.paddingTop,
                        contentViewChild.paddingRight,
                        paddingBottom + getDecorViewInvisibleHeight(activity)
                    )
                    sContentViewInvisibleHeightPre5497 = height
                }
            }
    }

    private fun getContentViewInvisibleHeight(activity: Activity): Int {
        val contentView = activity.findViewById<View>(android.R.id.content)
            ?: return sContentViewInvisibleHeightPre5497
        val outRect = Rect()
        contentView.getWindowVisibleDisplayFrame(outRect)
        Log.d(
            "KeyboardUtils",
            "getContentViewInvisibleHeight: " + (contentView.bottom - outRect.bottom)
        )
        val delta = Math.abs(contentView.bottom - outRect.bottom)
        return if (delta <= getStatusBarHeight() + getNavBarHeight()) {
            0
        } else delta
    }

    /**
     * 修复软键盘内存泄漏
     *
     * Call the function in [Activity.onDestroy].
     *
     * @param context The context.
     */
    fun fixSoftInputLeaks(context: Context?) {
        if (context == null) return
        val imm =
            AppManager.getInstance().getApp().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val strArr = arrayOf("mCurRootView", "mServedView", "mNextServedView", "mLastSrvView")
        for (i in 0..3) {
            try {

                val declaredField = imm.javaClass.getDeclaredField(strArr[i]) ?: continue
                if (!declaredField.isAccessible) {
                    declaredField.isAccessible = true
                }
                val obj = declaredField.get(imm)
                if (obj == null || obj !is View) continue
                if (obj.context === context) {
                    declaredField.set(imm, null)
                } else {
                    return
                }
            } catch (th: Throwable) {
                th.printStackTrace()
            }

        }
    }

    /**
     * 点击屏幕空白区域隐藏软键盘(复制代码到指定Activity)
     *
     * Copy the following code in ur activity.
     */
    fun clickBlankArea2HideSoftInput() {
        Log.i("KeyboardUtils", "Please refer to the following code.")
//        @Override
//        public boolean dispatchTouchEvent(MotionEvent ev) {
//            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
//                View v = getCurrentFocus();
//                if (isShouldHideKeyboard(v, ev)) {
//                    InputMethodManager imm =
//                            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(v.getWindowToken(),
//                            InputMethodManager.HIDE_NOT_ALWAYS
//                    );
//                }
//            }
//            return super.dispatchTouchEvent(ev);
//        }
//
//        // Return whether touch the view.
//        private boolean isShouldHideKeyboard(View v, MotionEvent event) {
//            if (v != null && (v instanceof EditText)) {
//                int[] l = {0, 0};
//                v.getLocationInWindow(l);
//                int left = l[0],
//                        top = l[1],
//                        bottom = top + v.getHeight(),
//                        right = left + v.getWidth();
//                return !(event.getX() > left && event.getX() < right
//                        && event.getY() > top && event.getY() < bottom);
//            }
//            return false;
//        }
    }

    private fun getStatusBarHeight(): Int {
        val resources = Resources.getSystem()
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return resources.getDimensionPixelSize(resourceId)
    }

    private fun getNavBarHeight(): Int {
        val res = Resources.getSystem()
        val resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId != 0) {
            res.getDimensionPixelSize(resourceId)
        } else {
            0
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // interface
    ///////////////////////////////////////////////////////////////////////////

    interface OnSoftInputChangedListener {
        fun onSoftInputChanged(height: Int)
    }
}