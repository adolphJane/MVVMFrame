package com.adolph.project.advancedutils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import com.adolph.project.baseutils.AppManageUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 服务相关
 *
 * getAllRunningServices: 获取所有运行的服务
 * startService         : 启动服务
 * stopService          : 停止服务
 * bindService          : 绑定服务
 * unbindService        : 解绑服务
 * isServiceRunning     : 判断服务是否运行
 */
public final class ServiceUtils {

    private ServiceUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 获取所有运行的服务
     *
     * @return all of the services are running
     */
    public static Set getAllRunningServices() {
        ActivityManager am =
                (ActivityManager) AppManageUtils.getApp().getSystemService(Context.ACTIVITY_SERVICE);
        //noinspection ConstantConditions
        List<ActivityManager.RunningServiceInfo> info = am.getRunningServices(0x7FFFFFFF);
        Set<String> names = new HashSet<>();
        if (info == null || info.size() == 0) return null;
        for (ActivityManager.RunningServiceInfo aInfo : info) {
            names.add(aInfo.service.getClassName());
        }
        return names;
    }

    /**
     * 启动服务
     *
     * @param className The name of class.
     */
    public static void startService(final String className) {
        try {
            startService(Class.forName(className));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动服务
     *
     * @param cls The service class.
     */
    public static void startService(final Class<?> cls) {
        Intent intent = new Intent(AppManageUtils.getApp(), cls);
        AppManageUtils.getApp().startService(intent);
    }

    /**
     * 停止服务
     *
     * @param className The name of class.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean stopService(final String className) {
        try {
            return stopService(Class.forName(className));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 停止服务
     *
     * @param cls The name of class.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean stopService(final Class<?> cls) {
        Intent intent = new Intent(AppManageUtils.getApp(), cls);
        return AppManageUtils.getApp().stopService(intent);
    }

    /**
     * Bind the service.
     *
     * @param className The name of class.
     * @param conn      The ServiceConnection object.
     * @param flags     Operation options for the binding.
     *                  <ul>
     *                  <li>0</li>
     *                  <li>{@link Context#BIND_AUTO_CREATE}</li>
     *                  <li>{@link Context#BIND_DEBUG_UNBIND}</li>
     *                  <li>{@link Context#BIND_NOT_FOREGROUND}</li>
     *                  <li>{@link Context#BIND_ABOVE_CLIENT}</li>
     *                  <li>{@link Context#BIND_ALLOW_OOM_MANAGEMENT}</li>
     *                  <li>{@link Context#BIND_WAIVE_PRIORITY}</li>
     *                  </ul>
     */
    public static void bindService(final String className,
                                   final ServiceConnection conn,
                                   final int flags) {
        try {
            bindService(Class.forName(className), conn, flags);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 绑定服务
     *
     * @param cls   The service class.
     * @param conn  The ServiceConnection object.
     * @param flags Operation options for the binding.
     *              <ul>
     *              <li>0</li>
     *              <li>{@link Context#BIND_AUTO_CREATE}</li>
     *              <li>{@link Context#BIND_DEBUG_UNBIND}</li>
     *              <li>{@link Context#BIND_NOT_FOREGROUND}</li>
     *              <li>{@link Context#BIND_ABOVE_CLIENT}</li>
     *              <li>{@link Context#BIND_ALLOW_OOM_MANAGEMENT}</li>
     *              <li>{@link Context#BIND_WAIVE_PRIORITY}</li>
     *              </ul>
     */
    public static void bindService(final Class<?> cls,
                                   final ServiceConnection conn,
                                   final int flags) {
        Intent intent = new Intent(AppManageUtils.getApp(), cls);
        AppManageUtils.getApp().bindService(intent, conn, flags);
    }

    /**
     * 解绑服务
     *
     * @param conn The ServiceConnection object.
     */
    public static void unbindService(final ServiceConnection conn) {
        AppManageUtils.getApp().unbindService(conn);
    }

    /**
     * 判断服务是否运行
     *
     * @param cls The service class.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isServiceRunning(final Class<?> cls) {
        return isServiceRunning(cls.getName());
    }

    /**
     * 判断服务是否运行
     *
     * @param className The name of class.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isServiceRunning(final String className) {
        ActivityManager am =
                (ActivityManager) AppManageUtils.getApp().getSystemService(Context.ACTIVITY_SERVICE);
        //noinspection ConstantConditions
        List<ActivityManager.RunningServiceInfo> info = am.getRunningServices(0x7FFFFFFF);
        if (info == null || info.size() == 0) return false;
        for (ActivityManager.RunningServiceInfo aInfo : info) {
            if (className.equals(aInfo.service.getClassName())) return true;
        }
        return false;
    }
}