package com.adolph.project.baseutils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;
import androidx.core.app.NotificationManagerCompat;

import java.lang.reflect.Field;

/**
 * 吐司相关
 *
 * setGravity     : 设置吐司位置
 * setBgColor     : 设置背景颜色
 * setBgResource  : 设置背景资源
 * setMsgColor    : 设置消息颜色
 * setMsgTextSize : 设置消息字体大小
 * showShort      : 显示短时吐司
 * showLong       : 显示长时吐司
 * showCustomShort: 显示短时自定义吐司
 * showCustomLong : 显示长时自定义吐司
 * cancel         : 取消吐司显示
 */
public final class ToastUtils {

    private static final int     COLOR_DEFAULT = 0xFEFFFFFF;
    private static final Handler HANDLER       = new Handler(Looper.getMainLooper());
    private static final String  NULL          = "null";

    private static IToast iToast;
    private static int    sGravity     = -1;
    private static int    sXOffset     = -1;
    private static int    sYOffset     = -1;
    private static int    sBgColor     = COLOR_DEFAULT;
    private static int    sBgResource  = -1;
    private static int    sMsgColor    = COLOR_DEFAULT;
    private static int    sMsgTextSize = -1;

    private ToastUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 设置吐司位置
     *
     * @param gravity The gravity.
     * @param xOffset X-axis offset, in pixel.
     * @param yOffset Y-axis offset, in pixel.
     */
    public static void setGravity(final int gravity, final int xOffset, final int yOffset) {
        sGravity = gravity;
        sXOffset = xOffset;
        sYOffset = yOffset;
    }

    /**
     * 设置背景颜色
     *
     * @param backgroundColor The color of background.
     */
    public static void setBgColor(@ColorInt final int backgroundColor) {
        sBgColor = backgroundColor;
    }

    /**
     * 设置背景资源
     *
     * @param bgResource The resource of background.
     */
    public static void setBgResource(@DrawableRes final int bgResource) {
        sBgResource = bgResource;
    }

    /**
     * 设置消息颜色
     *
     * @param msgColor The color of message.
     */
    public static void setMsgColor(@ColorInt final int msgColor) {
        sMsgColor = msgColor;
    }

    /**
     * 设置消息字体大小
     *
     * @param textSize The text size of message.
     */
    public static void setMsgTextSize(final int textSize) {
        sMsgTextSize = textSize;
    }

    /**
     * 显示短时吐司
     *
     * @param text The text.
     */
    public static void showShort(final CharSequence text) {
        show(text == null ? NULL : text, Toast.LENGTH_SHORT);
    }

    /**
     * 显示短时吐司
     *
     * @param resId The resource id for text.
     */
    public static void showShort(@StringRes final int resId) {
        show(resId, Toast.LENGTH_SHORT);
    }

    /**
     * 显示短时吐司
     *
     * @param resId The resource id for text.
     * @param args  The args.
     */
    public static void showShort(@StringRes final int resId, final Object... args) {
        show(resId, Toast.LENGTH_SHORT, args);
    }

    /**
     * 显示短时吐司
     *
     * @param format The format.
     * @param args   The args.
     */
    public static void showShort(final String format, final Object... args) {
        show(format, Toast.LENGTH_SHORT, args);
    }

    /**
     * 显示长时吐司
     *
     * @param text The text.
     */
    public static void showLong(final CharSequence text) {
        show(text == null ? NULL : text, Toast.LENGTH_LONG);
    }

    /**
     * 显示长时吐司
     *
     * @param resId The resource id for text.
     */
    public static void showLong(@StringRes final int resId) {
        show(resId, Toast.LENGTH_LONG);
    }

    /**
     * 显示长时吐司
     *
     * @param resId The resource id for text.
     * @param args  The args.
     */
    public static void showLong(@StringRes final int resId, final Object... args) {
        show(resId, Toast.LENGTH_LONG, args);
    }

    /**
     * 显示长时吐司
     *
     * @param format The format.
     * @param args   The args.
     */
    public static void showLong(final String format, final Object... args) {
        show(format, Toast.LENGTH_LONG, args);
    }

    /**
     * 显示短时自定义吐司
     *
     * @param layoutId ID for an XML layout resource to load.
     */
    public static View showCustomShort(@LayoutRes final int layoutId) {
        final View view = getView(layoutId);
        show(view, Toast.LENGTH_SHORT);
        return view;
    }

    /**
     * 显示长时自定义吐司
     *
     * @param layoutId ID for an XML layout resource to load.
     */
    public static View showCustomLong(@LayoutRes final int layoutId) {
        final View view = getView(layoutId);
        show(view, Toast.LENGTH_LONG);
        return view;
    }

    /**
     * 取消吐司显示
     */
    public static void cancel() {
        if (iToast != null) {
            iToast.cancel();
        }
    }

    private static void show(final int resId, final int duration) {
        try {
            CharSequence text = AppManageUtils.getApp().getResources().getText(resId);
            show(text, duration);
        } catch (Exception ignore) {
            show(String.valueOf(resId), duration);
        }
    }

    private static void show(final int resId, final int duration, final Object... args) {
        try {
            CharSequence text = AppManageUtils.getApp().getResources().getText(resId);
            String format = String.format(text.toString(), args);
            show(format, duration);
        } catch (Exception ignore) {
            show(String.valueOf(resId), duration);
        }
    }

    private static void show(final String format, final int duration, final Object... args) {
        String text;
        if (format == null) {
            text = NULL;
        } else {
            text = String.format(format, args);
            if (text == null) {
                text = NULL;
            }
        }
        show(text, duration);
    }

    private static void show(final CharSequence text, final int duration) {
        HANDLER.post(new Runnable() {
            @SuppressLint("ShowToast")
            @Override
            public void run() {
                cancel();
                iToast = ToastFactory.makeToast(AppManageUtils.getApp(), text, duration);
                final TextView tvMessage = iToast.getView().findViewById(android.R.id.message);
                if (sMsgColor != COLOR_DEFAULT) {
                    tvMessage.setTextColor(sMsgColor);
                }
                if (sMsgTextSize != -1) {
                    tvMessage.setTextSize(sMsgTextSize);
                }
                if (sGravity != -1 || sXOffset != -1 || sYOffset != -1) {
                    iToast.setGravity(sGravity, sXOffset, sYOffset);
                }
                setBg(tvMessage);
                iToast.show();
            }
        });
    }

    private static void show(final View view, final int duration) {
        HANDLER.post(new Runnable() {
            @Override
            public void run() {
                cancel();
                iToast = ToastFactory.newToast(AppManageUtils.getApp());
                iToast.setView(view);
                iToast.setDuration(duration);
                if (sGravity != -1 || sXOffset != -1 || sYOffset != -1) {
                    iToast.setGravity(sGravity, sXOffset, sYOffset);
                }
                setBg();
                iToast.show();
            }
        });
    }

    private static void setBg() {
        if (sBgResource != -1) {
            final View toastView = iToast.getView();
            toastView.setBackgroundResource(sBgResource);
        } else if (sBgColor != COLOR_DEFAULT) {
            final View toastView = iToast.getView();
            Drawable background = toastView.getBackground();
            if (background != null) {
                background.setColorFilter(
                        new PorterDuffColorFilter(sBgColor, PorterDuff.Mode.SRC_IN)
                );
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    toastView.setBackground(new ColorDrawable(sBgColor));
                } else {
                    toastView.setBackgroundDrawable(new ColorDrawable(sBgColor));
                }
            }
        }
    }

    private static void setBg(final TextView tvMsg) {
        if (sBgResource != -1) {
            final View toastView = iToast.getView();
            toastView.setBackgroundResource(sBgResource);
            tvMsg.setBackgroundColor(Color.TRANSPARENT);
        } else if (sBgColor != COLOR_DEFAULT) {
            final View toastView = iToast.getView();
            Drawable tvBg = toastView.getBackground();
            Drawable msgBg = tvMsg.getBackground();
            if (tvBg != null && msgBg != null) {
                tvBg.setColorFilter(new PorterDuffColorFilter(sBgColor, PorterDuff.Mode.SRC_IN));
                tvMsg.setBackgroundColor(Color.TRANSPARENT);
            } else if (tvBg != null) {
                tvBg.setColorFilter(new PorterDuffColorFilter(sBgColor, PorterDuff.Mode.SRC_IN));
            } else if (msgBg != null) {
                msgBg.setColorFilter(new PorterDuffColorFilter(sBgColor, PorterDuff.Mode.SRC_IN));
            } else {
                toastView.setBackgroundColor(sBgColor);
            }
        }
    }

    private static View getView(@LayoutRes final int layoutId) {
        LayoutInflater inflate =
                (LayoutInflater) AppManageUtils.getApp().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //noinspection ConstantConditions
        return inflate.inflate(layoutId, null);
    }

    static class ToastFactory {

        static IToast makeToast(Context context, CharSequence text, int duration) {
            if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
                return new SystemToast(makeNormalToast(context, text, duration));
            }
            return new ToastWithoutNotification(makeNormalToast(context, text, duration));
        }

        static IToast newToast(Context context) {
            if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
                return new SystemToast(new Toast(context));
            }
            return new ToastWithoutNotification(new Toast(context));
        }

        private static Toast makeNormalToast(Context context, CharSequence text, int duration) {
            @SuppressLint("ShowToast")
            Toast toast = Toast.makeText(context, "", duration);
            toast.setText(text);
            return toast;
        }
    }

    static class SystemToast extends AbsToast {

        private static Field sField_mTN;
        private static Field sField_TN_Handler;

        SystemToast(Toast toast) {
            super(toast);
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
                try {
                    //noinspection JavaReflectionMemberAccess
                    sField_mTN = Toast.class.getDeclaredField("mTN");
                    sField_mTN.setAccessible(true);
                    Object mTN = sField_mTN.get(toast);
                    sField_TN_Handler = sField_mTN.getType().getDeclaredField("mHandler");
                    sField_TN_Handler.setAccessible(true);
                    Handler tnHandler = (Handler) sField_TN_Handler.get(mTN);
                    sField_TN_Handler.set(mTN, new SafeHandler(tnHandler));
                } catch (Exception ignored) { /**/ }
            }
        }

        @Override
        public void show() {
            mToast.show();
        }

        @Override
        public void cancel() {
            mToast.cancel();
        }

        static class SafeHandler extends Handler {
            private Handler impl;

            SafeHandler(Handler impl) {
                this.impl = impl;
            }

            @Override
            public void handleMessage(Message msg) {
                impl.handleMessage(msg);
            }

            @Override
            public void dispatchMessage(Message msg) {
                try {
                    impl.dispatchMessage(msg);
                } catch (Exception e) {
                    Log.e("ToastUtils", e.toString());
                }
            }
        }
    }

    static class ToastWithoutNotification extends AbsToast {

        private WindowManager mWM;
        private View          mView;

        private WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();

        private AppManageUtils.OnActivityDestroyedListener listener =
                new AppManageUtils.OnActivityDestroyedListener() {
                    @Override
                    public void onActivityDestroyed(Activity activity) {
                        cancel();
                    }
                };

        ToastWithoutNotification(Toast toast) {
            super(toast);
        }

        @Override
        public void show() {
            mView = mToast.getView();
            if (mView == null) return;
            Context context = mToast.getView().getContext();
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) {
                mWM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                mParams.type = WindowManager.LayoutParams.TYPE_TOAST;
                mParams.y = mToast.getYOffset();
            } else {
                Context topActivityOrApp = AppManageUtils.getTopActivityOrApp();
                if (topActivityOrApp instanceof Activity) {
                    Activity topActivity = (Activity) topActivityOrApp;
                    mWM = topActivity.getWindowManager();
                    AppManageUtils.getActivityLifecycle().addOnActivityDestroyedListener(topActivity, listener);
                }
                mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
                mParams.y = mToast.getYOffset() + getNavBarHeight();
            }

            final Configuration config = context.getResources().getConfiguration();
            final int gravity;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                gravity = Gravity.getAbsoluteGravity(mToast.getGravity(), config.getLayoutDirection());
            } else {
                gravity = mToast.getGravity();
            }

            mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            mParams.format = PixelFormat.TRANSLUCENT;
            mParams.windowAnimations = android.R.style.Animation_Toast;

            mParams.setTitle("ToastWithoutNotification");
            mParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
            mParams.gravity = gravity;
            if ((gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.FILL_HORIZONTAL) {
                mParams.horizontalWeight = 1.0f;
            }
            if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.FILL_VERTICAL) {
                mParams.verticalWeight = 1.0f;
            }
            mParams.x = mToast.getXOffset();
            mParams.packageName = AppManageUtils.getApp().getPackageName();

            try {
                mWM.addView(mView, mParams);
            } catch (Exception ignored) { /**/ }

            HANDLER.postDelayed(new Runnable() {
                @Override
                public void run() {
                    cancel();
                }
            }, mToast.getDuration() == Toast.LENGTH_SHORT ? 2000 : 3500);
        }

        @Override
        public void cancel() {
            try {
                if (mWM != null) {
                    mWM.removeViewImmediate(mView);
                }
            } catch (Exception ignored) { /**/ }
            mView = null;
            mWM = null;
            mToast = null;
        }

        private int getNavBarHeight() {
            Resources res = Resources.getSystem();
            int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId != 0) {
                return res.getDimensionPixelSize(resourceId);
            } else {
                return 0;
            }
        }
    }

    static abstract class AbsToast implements IToast {

        Toast mToast;

        AbsToast(Toast toast) {
            mToast = toast;
        }

        @Override
        public void setView(View view) {
            mToast.setView(view);
        }

        @Override
        public View getView() {
            return mToast.getView();
        }

        @Override
        public void setDuration(int duration) {
            mToast.setDuration(duration);
        }

        @Override
        public void setGravity(int gravity, int xOffset, int yOffset) {
            mToast.setGravity(gravity, xOffset, yOffset);
        }

        @Override
        public void setText(int resId) {
            mToast.setText(resId);
        }

        @Override
        public void setText(CharSequence s) {
            mToast.setText(s);
        }
    }

    interface IToast {

        void show();

        void cancel();

        void setView(View view);

        View getView();

        void setDuration(int duration);

        void setGravity(int gravity, int xOffset, int yOffset);

        void setText(@StringRes int resId);

        void setText(CharSequence s);
    }
}