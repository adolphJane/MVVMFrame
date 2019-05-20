package com.magicalrice.project.library_base.advanced

import android.Manifest.permission.KILL_BACKGROUND_PROCESSES
import android.app.ActivityManager
import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresPermission
import com.magicalrice.project.library_base.base.AppManager
import java.util.*

/**
 * @package com.magicalrice.project.library_base.advanced
 * @author Adolph
 * @date 2019-04-22 Mon
 * @description 进程相关
 *
 * getForegroundProcessName  : 获取前台线程包名
 * killAllBackgroundProcesses: 杀死所有的后台服务进程
 * killBackgroundProcesses   : 杀死后台服务进程
 * isMainProcess             : 判断是否运行在主进程
 * getCurrentProcessName     : 获取当前进程名称
 */

object ProcessUtils {
    /**
     * Return the foreground process name.
     *
     * Target APIs greater than 21 must hold
     * `<uses-permission android:name="android.permission.PACKAGE_USAGE_STATS" />`
     *
     * @return the foreground process name
     */
    fun getForegroundProcessName(): String? {
        val am =
            AppManager.getInstance().getApp().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        val pInfo = am.runningAppProcesses
        if (pInfo != null && pInfo.size > 0) {
            for (aInfo in pInfo) {
                if (aInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return aInfo.processName
                }
            }
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            val pm = AppManager.getInstance().getApp().getPackageManager()
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            val list = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            Log.i("ProcessUtils", list.toString())
            if (list.size <= 0) {
                Log.i(
                    "ProcessUtils",
                    "getForegroundProcessName: noun of access to usage information."
                )
                return ""
            }
            try {// Access to usage information.
                val info =
                    pm.getApplicationInfo(AppManager.getInstance().getApp().getPackageName(), 0)
                val aom =
                    AppManager.getInstance().getApp().getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager

                if (aom.checkOpNoThrow(
                        AppOpsManager.OPSTR_GET_USAGE_STATS,
                        info.uid,
                        info.packageName
                    ) != AppOpsManager.MODE_ALLOWED
                ) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    AppManager.getInstance().getApp().startActivity(intent)
                }
                if (aom.checkOpNoThrow(
                        AppOpsManager.OPSTR_GET_USAGE_STATS,
                        info.uid,
                        info.packageName
                    ) != AppOpsManager.MODE_ALLOWED
                ) {
                    Log.i(
                        "ProcessUtils",
                        "getForegroundProcessName: refuse to device usage stats."
                    )
                    return ""
                }
                val usageStatsManager = AppManager.getInstance().getApp()
                    .getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
                var usageStatsList: List<UsageStats>? = null
                if (usageStatsManager != null) {
                    val endTime = System.currentTimeMillis()
                    val beginTime = endTime - 86400000 * 7
                    usageStatsList = usageStatsManager
                        .queryUsageStats(
                            UsageStatsManager.INTERVAL_BEST,
                            beginTime, endTime
                        )
                }
                if (usageStatsList == null || usageStatsList.isEmpty()) return null
                var recentStats: UsageStats? = null
                for (usageStats in usageStatsList) {
                    if (recentStats == null || usageStats.lastTimeUsed > recentStats.lastTimeUsed) {
                        recentStats = usageStats
                    }
                }
                return recentStats?.packageName
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }

        }
        return ""
    }

    /**
     * Return all background processes.
     *
     * Must hold
     * `<uses-permission android:name="android.permission.KILL_BACKGROUND_PROCESSES" />`
     *
     * @return all background processes
     */
    @RequiresPermission(KILL_BACKGROUND_PROCESSES)
    fun getAllBackgroundProcesses(): Set<String> {
        val am =
            AppManager.getInstance().getApp().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        val info = am.runningAppProcesses
        val set = HashSet<String>()
        if (info != null) {
            for (aInfo in info) {
                Collections.addAll(set, *aInfo.pkgList)
            }
        }
        return set
    }

    /**
     * Kill all background processes.
     *
     * Must hold
     * `<uses-permission android:name="android.permission.KILL_BACKGROUND_PROCESSES" />`
     *
     * @return background processes were killed
     */
    @RequiresPermission(KILL_BACKGROUND_PROCESSES)
    fun killAllBackgroundProcesses(): Set<String> {
        val am =
            AppManager.getInstance().getApp().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        var info: List<ActivityManager.RunningAppProcessInfo> = am.runningAppProcesses
        val set = HashSet<String>()
        for (aInfo in info) {
            for (pkg in aInfo.pkgList) {
                am.killBackgroundProcesses(pkg)
                set.add(pkg)
            }
        }
        info = am.runningAppProcesses
        for (aInfo in info) {
            for (pkg in aInfo.pkgList) {
                set.remove(pkg)
            }
        }
        return set
    }

    /**
     * Kill background processes.
     *
     * Must hold
     * `<uses-permission android:name="android.permission.KILL_BACKGROUND_PROCESSES" />`
     *
     * @param packageName The name of the package.
     * @return `true`: success<br></br>`false`: fail
     */
    @RequiresPermission(KILL_BACKGROUND_PROCESSES)
    fun killBackgroundProcesses(packageName: String): Boolean {
        val am =
            AppManager.getInstance().getApp().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        var info: List<ActivityManager.RunningAppProcessInfo>? = am.runningAppProcesses
        if (info == null || info.size == 0) return true
        for (aInfo in info) {
            if (Arrays.asList(*aInfo.pkgList).contains(packageName)) {
                am.killBackgroundProcesses(packageName)
            }
        }
        info = am.runningAppProcesses
        if (info == null || info.size == 0) return true
        for (aInfo in info) {
            if (Arrays.asList(*aInfo.pkgList).contains(packageName)) {
                return false
            }
        }
        return true
    }

    /**
     * Return whether app running in the main process.
     *
     * @return `true`: yes<br></br>`false`: no
     */
    fun isMainProcess(): Boolean {
        return AppManager.getInstance().getApp().getPackageName().equals(getCurrentProcessName())
    }

    /**
     * Return the name of current process.
     *
     * @return the name of current process
     */
    fun getCurrentProcessName(): String? {
        val am =
            AppManager.getInstance().getApp().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        val info = am.runningAppProcesses
        if (info == null || info.size == 0) return null
        val pid = Process.myPid()
        for (aInfo in info) {
            if (aInfo.pid == pid) {
                if (aInfo.processName != null) {
                    return aInfo.processName
                }
            }
        }
        return ""
    }
}