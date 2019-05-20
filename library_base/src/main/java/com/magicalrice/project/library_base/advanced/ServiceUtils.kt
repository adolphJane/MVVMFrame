package com.magicalrice.project.library_base.advanced

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import com.magicalrice.project.library_base.base.AppManager
import java.util.*

/**
 * @package com.magicalrice.project.library_base.advanced
 * @author Adolph
 * @date 2019-04-22 Mon
 * @description 服务相关
 *
 * getAllRunningServices: 获取所有运行的服务
 * startService         : 启动服务
 * stopService          : 停止服务
 * bindService          : 绑定服务
 * unbindService        : 解绑服务
 * isServiceRunning     : 判断服务是否运行
 */

object ServiceUtils {
    /**
     * 获取所有运行的服务
     *
     * @return all of the services are running
     */
    fun getAllRunningServices(): Set<*>? {
        val am =
            AppManager.getInstance().getApp().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        val info = am.getRunningServices(0x7FFFFFFF)
        val names = HashSet<String>()
        if (info == null || info.size == 0) return null
        for (aInfo in info) {
            names.add(aInfo.service.className)
        }
        return names
    }

    /**
     * 启动服务
     *
     * @param className The name of class.
     */
    fun startService(className: String) {
        try {
            startService(Class.forName(className))
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 启动服务
     *
     * @param cls The service class.
     */
    fun startService(cls: Class<*>) {
        val intent = Intent(AppManager.getInstance().getApp(), cls)
        AppManager.getInstance().getApp().startService(intent)
    }

    /**
     * 停止服务
     *
     * @param className The name of class.
     * @return `true`: success<br></br>`false`: fail
     */
    fun stopService(className: String): Boolean {
        try {
            return stopService(Class.forName(className))
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }

    /**
     * 停止服务
     *
     * @param cls The name of class.
     * @return `true`: success<br></br>`false`: fail
     */
    fun stopService(cls: Class<*>): Boolean {
        val intent = Intent(AppManager.getInstance().getApp(), cls)
        return AppManager.getInstance().getApp().stopService(intent)
    }

    /**
     * Bind the service.
     *
     * @param className The name of class.
     * @param conn      The ServiceConnection object.
     * @param flags     Operation options for the binding.
     *
     *  * 0
     *  * [Context.BIND_AUTO_CREATE]
     *  * [Context.BIND_DEBUG_UNBIND]
     *  * [Context.BIND_NOT_FOREGROUND]
     *  * [Context.BIND_ABOVE_CLIENT]
     *  * [Context.BIND_ALLOW_OOM_MANAGEMENT]
     *  * [Context.BIND_WAIVE_PRIORITY]
     *
     */
    fun bindService(
        className: String,
        conn: ServiceConnection,
        flags: Int
    ) {
        try {
            bindService(Class.forName(className), conn, flags)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 绑定服务
     *
     * @param cls   The service class.
     * @param conn  The ServiceConnection object.
     * @param flags Operation options for the binding.
     *
     *  * 0
     *  * [Context.BIND_AUTO_CREATE]
     *  * [Context.BIND_DEBUG_UNBIND]
     *  * [Context.BIND_NOT_FOREGROUND]
     *  * [Context.BIND_ABOVE_CLIENT]
     *  * [Context.BIND_ALLOW_OOM_MANAGEMENT]
     *  * [Context.BIND_WAIVE_PRIORITY]
     *
     */
    fun bindService(
        cls: Class<*>,
        conn: ServiceConnection,
        flags: Int
    ) {
        val intent = Intent(AppManager.getInstance().getApp(), cls)
        AppManager.getInstance().getApp().bindService(intent, conn, flags)
    }

    /**
     * 解绑服务
     *
     * @param conn The ServiceConnection object.
     */
    fun unbindService(conn: ServiceConnection) {
        AppManager.getInstance().getApp().unbindService(conn)
    }

    /**
     * 判断服务是否运行
     *
     * @param cls The service class.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isServiceRunning(cls: Class<*>): Boolean {
        return isServiceRunning(cls.name)
    }

    /**
     * 判断服务是否运行
     *
     * @param className The name of class.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isServiceRunning(className: String): Boolean {
        val am =
            AppManager.getInstance().getApp().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        val info = am.getRunningServices(0x7FFFFFFF)
        if (info == null || info.size == 0) return false
        for (aInfo in info) {
            if (className == aInfo.service.className) return true
        }
        return false
    }
}