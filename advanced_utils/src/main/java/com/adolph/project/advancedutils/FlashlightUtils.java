package com.adolph.project.advancedutils;

import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import com.adolph.project.baseutils.AppManageUtils;

import java.io.IOException;

import static android.hardware.Camera.Parameters.FLASH_MODE_OFF;
import static android.hardware.Camera.Parameters.FLASH_MODE_TORCH;

/**
 * 闪光灯相关
 *
 * getInstance              : 获取闪光灯实例
 * Instance.register        : 注册
 * Instance.unregister      : 注销
 * Instance.setFlashlightOn : 打开闪光灯
 * Instance.setFlashlightOff: 关闭闪光灯
 * Instance.isFlashlightOn  : 判断闪光灯是否打开
 * isFlashlightEnable       : 判断设备是否支持闪光灯
 */
public final class FlashlightUtils {

    private static final String TAG = "FlashlightUtils";

    private Camera mCamera;

    private FlashlightUtils() {
    }

    /**
     * 获取闪光灯实例
     *
     * @return the single {@link FlashlightUtils} instance
     */
    public static FlashlightUtils getInstance() {
        return LazyHolder.INSTANCE;
    }

    /**
     * 注册
     *
     * @return {@code true}: success<br>{@code false}: fail
     */
    public boolean register() {
        try {
            mCamera = Camera.open(0);
        } catch (Throwable t) {
            Log.e(TAG, "register: ", t);
            return false;
        }
        if (mCamera == null) {
            Log.e(TAG, "register: open camera failed!");
            return false;
        }
        try {
            mCamera.setPreviewTexture(new SurfaceTexture(0));
            mCamera.startPreview();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 注销
     */
    public void unregister() {
        if (mCamera == null) return;
        mCamera.stopPreview();
        mCamera.release();
    }

    /**
     * 打开闪光灯
     */
    public void setFlashlightOn() {
        if (mCamera == null) {
            Log.e(TAG, "setFlashlightOn: the utils of flashlight register failed!");
            return;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        if (!FLASH_MODE_TORCH.equals(parameters.getFlashMode())) {
            parameters.setFlashMode(FLASH_MODE_TORCH);
            mCamera.setParameters(parameters);
        }
    }

    /**
     * 关闭闪光灯
     */
    public void setFlashlightOff() {
        if (mCamera == null) {
            Log.e(TAG, "setFlashlightOn: the utils of flashlight register failed!");
            return;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        if (FLASH_MODE_TORCH.equals(parameters.getFlashMode())) {
            parameters.setFlashMode(FLASH_MODE_OFF);
            mCamera.setParameters(parameters);
        }
    }

    /**
     * 判断闪光灯是否打开
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public boolean isFlashlightOn() {
        if (mCamera == null) {
            Log.e(TAG, "setFlashlightOn: the utils of flashlight register failed!");
            return false;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        return FLASH_MODE_TORCH.equals(parameters.getFlashMode());
    }

    /**
     * 判断设备是否支持闪光灯
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isFlashlightEnable() {
        return AppManageUtils.getApp()
                .getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    private static final class LazyHolder {
        private static final FlashlightUtils INSTANCE = new FlashlightUtils();
    }
}
