package com.magicalrice.project.library_base.advanced

import android.app.Activity
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.pm.PackageManager
import com.magicalrice.project.library_base.base.AppManager

/**
 * @package com.magicalrice.project.library_base.advanced
 * @author Adolph
 * @date 2019-04-22 Mon
 * @description MetaData 相关
 *
 * getMetaDataInApp     : 获取 application 的 meta-data 值
 * getMetaDataInActivity: 获取 activity 的 meta-data 值
 * getMetaDataInService : 获取 service 的 meta-data 值
 * getMetaDataInReceiver: 获取 receiver 的 meta-data 值
 */

object MetaDataUtils {
    /**
     * 获取 application 的 meta-data 值
     *
     * @param key The key of meta-data.
     * @return the value of meta-data in application
     */
    fun getMetaDataInApp(key: String): String {
        var value = ""
        val pm = AppManager.getInstance().getApp().getPackageManager()
        val packageName = AppManager.getInstance().getApp().getPackageName()
        try {
            val ai = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            value = ai.metaData.get(key).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return value
    }

    /**
     * 获取 activity 的 meta-data 值
     *
     * @param activity The activity.
     * @param key      The key of meta-data.
     * @return the value of meta-data in activity
     */
    fun getMetaDataInActivity(
        activity: Activity,
        key: String
    ): String {
        return getMetaDataInActivity(activity.javaClass, key)
    }

    /**
     * 获取 activity 的 meta-data 值
     *
     * @param clz The activity class.
     * @param key The key of meta-data.
     * @return the value of meta-data in activity
     */
    fun getMetaDataInActivity(
        clz: Class<out Activity>,
        key: String
    ): String {
        var value = ""
        val pm = AppManager.getInstance().getApp().getPackageManager()
        val componentName = ComponentName(AppManager.getInstance().getApp(), clz)
        try {
            val ai = pm.getActivityInfo(componentName, PackageManager.GET_META_DATA)
            value = ai.metaData.get(key).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return value
    }

    /**
     * 获取 service 的 meta-data 值
     *
     * @param service The service.
     * @param key     The key of meta-data.
     * @return the value of meta-data in service
     */
    fun getMetaDataInService(
        service: Service,
        key: String
    ): String {
        return getMetaDataInService(service.javaClass, key)
    }

    /**
     * 获取 service 的 meta-data 值
     *
     * @param clz The service class.
     * @param key The key of meta-data.
     * @return the value of meta-data in service
     */
    fun getMetaDataInService(
        clz: Class<out Service>,
        key: String
    ): String {
        var value = ""
        val pm = AppManager.getInstance().getApp().getPackageManager()
        val componentName = ComponentName(AppManager.getInstance().getApp(), clz)
        try {
            val info = pm.getServiceInfo(componentName, PackageManager.GET_META_DATA)
            value = info.metaData.get(key).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return value
    }

    /**
     * 获取 receiver 的 meta-data 值
     *
     * @param receiver The receiver.
     * @param key      The key of meta-data.
     * @return the value of meta-data in receiver
     */
    fun getMetaDataInReceiver(
        receiver: BroadcastReceiver,
        key: String
    ): String {
        return getMetaDataInReceiver(receiver, key)
    }

    /**
     * 获取 receiver 的 meta-data 值
     *
     * @param clz The receiver class.
     * @param key The key of meta-data.
     * @return the value of meta-data in receiver
     */
    fun getMetaDataInReceiver(
        clz: Class<out BroadcastReceiver>,
        key: String
    ): String {
        var value = ""
        val pm = AppManager.getInstance().getApp().getPackageManager()
        val componentName = ComponentName(AppManager.getInstance().getApp(), clz)
        try {
            val info = pm.getReceiverInfo(componentName, PackageManager.GET_META_DATA)
            value = info.metaData.get(key).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return value
    }
}