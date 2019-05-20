package com.magicalrice.project.library_base.base

import android.os.Build
import android.os.Environment

/**
 * @package com.magicalrice.project.library_base.base
 * @author Adolph
 * @date 2019-04-22 Mon
 * @description 路径相关
 *
 * getRootPath                    : 获取根路径
 * getDataPath                    : 获取数据路径
 * getDownloadCachePath           : 获取下载缓存路径
 * getInternalAppDataPath         : 获取内存应用数据路径
 * getInternalAppCodeCacheDir     : 获取内存应用代码缓存路径
 * getInternalAppCachePath        : 获取内存应用缓存路径
 * getInternalAppDbsPath          : 获取内存应用数据库路径
 * getInternalAppDbPath           : 获取内存应用数据库路径
 * getInternalAppFilesPath        : 获取内存应用文件路径
 * getInternalAppSpPath           : 获取内存应用 SP 路径
 * getInternalAppNoBackupFilesPath: 获取内存应用未备份文件路径
 * getExternalStoragePath         : 获取外存路径
 * getExternalMusicPath           : 获取外存音乐路径
 * getExternalPodcastsPath        : 获取外存播客路径
 * getExternalRingtonesPath       : 获取外存铃声路径
 * getExternalAlarmsPath          : 获取外存闹铃路径
 * getExternalNotificationsPath   : 获取外存通知路径
 * getExternalPicturesPath        : 获取外存图片路径
 * getExternalMoviesPath          : 获取外存影片路径
 * getExternalDownloadsPath       : 获取外存下载路径
 * getExternalDcimPath            : 获取外存数码相机图片路径
 * getExternalDocumentsPath       : 获取外存文档路径
 * getExternalAppDataPath         : 获取外存应用数据路径
 * getExternalAppCachePath        : 获取外存应用缓存路径
 * getExternalAppFilesPath        : 获取外存应用文件路径
 * getExternalAppMusicPath        : 获取外存应用音乐路径
 * getExternalAppPodcastsPath     : 获取外存应用播客路径
 * getExternalAppRingtonesPath    : 获取外存应用铃声路径
 * getExternalAppAlarmsPath       : 获取外存应用闹铃路径
 * getExternalAppNotificationsPath: 获取外存应用通知路径
 * getExternalAppPicturesPath     : 获取外存应用图片路径
 * getExternalAppMoviesPath       : 获取外存应用影片路径
 * getExternalAppDownloadPath     : 获取外存应用下载路径
 * getExternalAppDcimPath         : 获取外存应用数码相机图片路径
 * getExternalAppDocumentsPath    : 获取外存应用文档路径
 * getExternalAppObbPath          : 获取外存应用 OBB 路径
 */

object PathUtils {
    /**
     * 获取根路径
     *
     * @return the path of /system
     */
    fun getRootPath(): String {
        return Environment.getRootDirectory().absolutePath
    }

    /**
     * 获取数据路径
     *
     * @return the path of /data
     */
    fun getDataPath(): String {
        return Environment.getDataDirectory().absolutePath
    }

    /**
     * 获取下载缓存路径
     *
     * @return the path of /cache
     */
    fun getDownloadCachePath(): String {
        return Environment.getDownloadCacheDirectory().absolutePath
    }

    /**
     * 获取内存应用数据路径
     *
     * @return the path of /data/data/package
     */
    fun getInternalAppDataPath(): String {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            AppManager.getInstance().getApp().applicationInfo.dataDir
        } else AppManager.getInstance().getApp().dataDir.absolutePath
    }

    /**
     * 获取内存应用代码缓存路径
     *
     * @return the path of /data/data/package/code_cache
     */
    fun getInternalAppCodeCacheDir(): String {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            AppManager.getInstance().getApp().applicationInfo.dataDir + "/code_cache"
        } else AppManager.getInstance().getApp().codeCacheDir.absolutePath
    }

    /**
     * 获取内存应用缓存路径
     *
     * @return the path of /data/data/package/cache
     */
    fun getInternalAppCachePath(): String {
        return AppManager.getInstance().getApp().cacheDir.absolutePath
    }

    /**
     * 获取内存应用数据库路径
     *
     * @return the path of /data/data/package/databases
     */
    fun getInternalAppDbsPath(): String {
        return AppManager.getInstance().getApp().applicationInfo.dataDir + "/databases"
    }

    /**
     * 获取内存应用数据库路径
     *
     * @param name The name of database.
     * @return the path of /data/data/package/databases/name
     */
    fun getInternalAppDbPath(name: String): String {
        return AppManager.getInstance().getApp().getDatabasePath(name).absolutePath
    }

    /**
     * 获取内存应用文件路径
     *
     * @return the path of /data/data/package/files
     */
    fun getInternalAppFilesPath(): String {
        return AppManager.getInstance().getApp().filesDir.absolutePath
    }

    /**
     * 获取内存应用 SP 路径
     *
     * @return the path of /data/data/package/shared_prefs
     */
    fun getInternalAppSpPath(): String {
        return AppManager.getInstance().getApp().applicationInfo.dataDir + "shared_prefs"
    }

    /**
     * 获取内存应用未备份文件路径
     *
     * @return the path of /data/data/package/no_backup
     */
    fun getInternalAppNoBackupFilesPath(): String {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            AppManager.getInstance().getApp().applicationInfo.dataDir + "no_backup"
        } else AppManager.getInstance().getApp().noBackupFilesDir.absolutePath
    }

    /**
     * 获取外存路径
     *
     * @return the path of /storage/emulated/0
     */
    fun getExternalStoragePath(): String {
        return if (isExternalStorageDisable()) "" else Environment.getExternalStorageDirectory().absolutePath
    }

    /**
     * 获取外存音乐路径
     *
     * @return the path of /storage/emulated/0/Music
     */
    fun getExternalMusicPath(): String {
        return if (isExternalStorageDisable()) "" else Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_MUSIC
        ).absolutePath
    }

    /**
     * 获取外存播客路径
     *
     * @return the path of /storage/emulated/0/Podcasts
     */
    fun getExternalPodcastsPath(): String {
        return if (isExternalStorageDisable()) "" else Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PODCASTS
        ).absolutePath
    }

    /**
     * 获取外存铃声路径
     *
     * @return the path of /storage/emulated/0/Ringtones
     */
    fun getExternalRingtonesPath(): String {
        return if (isExternalStorageDisable()) "" else Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_RINGTONES
        ).absolutePath
    }

    /**
     * 获取外存闹铃路径
     *
     * @return the path of /storage/emulated/0/Alarms
     */
    fun getExternalAlarmsPath(): String {
        return if (isExternalStorageDisable()) "" else Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_ALARMS
        ).absolutePath
    }

    /**
     * 获取外存通知路径
     *
     * @return the path of /storage/emulated/0/Notifications
     */
    fun getExternalNotificationsPath(): String {
        return if (isExternalStorageDisable()) "" else Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_NOTIFICATIONS
        ).absolutePath
    }

    /**
     * 获取外存图片路径
     *
     * @return the path of /storage/emulated/0/Pictures
     */
    fun getExternalPicturesPath(): String {
        return if (isExternalStorageDisable()) "" else Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES
        ).absolutePath
    }

    /**
     * 获取外存影片路径
     *
     * @return the path of /storage/emulated/0/Movies
     */
    fun getExternalMoviesPath(): String {
        return if (isExternalStorageDisable()) "" else Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_MOVIES
        ).absolutePath
    }

    /**
     * 获取外存下载路径
     *
     * @return the path of /storage/emulated/0/Download
     */
    fun getExternalDownloadsPath(): String {
        return if (isExternalStorageDisable()) "" else Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS
        ).absolutePath
    }

    /**
     * 获取外存数码相机图片路径
     *
     * @return the path of /storage/emulated/0/DCIM
     */
    fun getExternalDcimPath(): String {
        return if (isExternalStorageDisable()) "" else Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DCIM
        ).absolutePath
    }

    /**
     * 获取外存文档路径
     *
     * @return the path of /storage/emulated/0/Documents
     */
    fun getExternalDocumentsPath(): String {
        if (isExternalStorageDisable()) return ""
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            Environment.getExternalStorageDirectory().absolutePath + "/Documents"
        } else Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath
    }

    /**
     * 获取外存应用数据路径
     *
     * @return the path of /storage/emulated/0/Android/data/package
     */
    fun getExternalAppDataPath(): String {
        return if (isExternalStorageDisable()) "" else AppManager.getInstance().getApp().externalCacheDir?.parentFile?.absolutePath
            ?: ""

    }

    /**
     * 获取外存应用缓存路径
     *
     * @return the path of /storage/emulated/0/Android/data/package/cache
     */
    fun getExternalAppCachePath(): String {
        return if (isExternalStorageDisable()) "" else AppManager.getInstance().getApp().externalCacheDir?.absolutePath
            ?: ""

    }

    /**
     * 获取外存应用文件路径
     *
     * @return the path of /storage/emulated/0/Android/data/package/files
     */
    fun getExternalAppFilesPath(): String {
        return if (isExternalStorageDisable()) "" else AppManager.getInstance().getApp().getExternalFilesDir(
            null
        )?.absolutePath ?: ""

    }

    /**
     * 获取外存应用音乐路径
     *
     * @return the path of /storage/emulated/0/Android/data/package/files/Music
     */
    fun getExternalAppMusicPath(): String {
        return if (isExternalStorageDisable()) "" else AppManager.getInstance().getApp().getExternalFilesDir(
            Environment.DIRECTORY_MUSIC
        )?.absolutePath ?: ""

    }

    /**
     * 获取外存应用播客路径
     *
     * @return the path of /storage/emulated/0/Android/data/package/files/Podcasts
     */
    fun getExternalAppPodcastsPath(): String {
        return if (isExternalStorageDisable()) "" else AppManager.getInstance().getApp().getExternalFilesDir(
            Environment.DIRECTORY_PODCASTS
        )?.absolutePath ?: ""

    }

    /**
     * 获取外存应用铃声路径
     *
     * @return the path of /storage/emulated/0/Android/data/package/files/Ringtones
     */
    fun getExternalAppRingtonesPath(): String {
        return if (isExternalStorageDisable())
            "" else
            AppManager.getInstance().getApp().getExternalFilesDir(Environment.DIRECTORY_RINGTONES)?.absolutePath
                ?: ""

    }

    /**
     * 获取外存应用闹铃路径
     *
     * @return the path of /storage/emulated/0/Android/data/package/files/Alarms
     */
    fun getExternalAppAlarmsPath(): String {
        return if (isExternalStorageDisable()) "" else AppManager.getInstance().getApp().getExternalFilesDir(
            Environment.DIRECTORY_ALARMS
        )?.absolutePath ?: ""

    }

    /**
     * 获取外存应用通知路径
     *
     * @return the path of /storage/emulated/0/Android/data/package/files/Notifications
     */
    fun getExternalAppNotificationsPath(): String {
        return if (isExternalStorageDisable()) "" else AppManager.getInstance().getApp().getExternalFilesDir(
            Environment.DIRECTORY_NOTIFICATIONS
        )?.absolutePath ?: ""

    }

    /**
     * 获取外存应用图片路径
     *
     * @return path of /storage/emulated/0/Android/data/package/files/Pictures
     */
    fun getExternalAppPicturesPath(): String {
        return if (isExternalStorageDisable()) "" else AppManager.getInstance().getApp().getExternalFilesDir(
            Environment.DIRECTORY_PICTURES
        )?.absolutePath ?: ""

    }

    /**
     * 获取外存应用影片路径
     *
     * @return the path of /storage/emulated/0/Android/data/package/files/Movies
     */
    fun getExternalAppMoviesPath(): String {
        return if (isExternalStorageDisable()) "" else AppManager.getInstance().getApp().getExternalFilesDir(
            Environment.DIRECTORY_MOVIES
        )?.absolutePath ?: ""

    }

    /**
     * 获取外存应用下载路径
     *
     * @return the path of /storage/emulated/0/Android/data/package/files/Download
     */
    fun getExternalAppDownloadPath(): String {
        return if (isExternalStorageDisable()) "" else AppManager.getInstance().getApp().getExternalFilesDir(
            Environment.DIRECTORY_DOWNLOADS
        )?.absolutePath ?: ""

    }

    /**
     * 获取外存应用数码相机图片路径
     *
     * @return the path of /storage/emulated/0/Android/data/package/files/DCIM
     */
    fun getExternalAppDcimPath(): String {
        return if (isExternalStorageDisable()) "" else AppManager.getInstance().getApp().getExternalFilesDir(
            Environment.DIRECTORY_DCIM
        )?.absolutePath ?: ""

    }

    /**
     * 获取外存应用文档路径
     *
     * @return the path of /storage/emulated/0/Android/data/package/files/Documents
     */
    fun getExternalAppDocumentsPath(): String {
        if (isExternalStorageDisable()) return ""
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            AppManager.getInstance().getApp().getExternalFilesDir(null)?.absolutePath ?: ""
            + "/Documents"
        } else {
            AppManager.getInstance().getApp().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                ?.absolutePath ?: ""
        }

    }

    /**
     * 获取外存应用 OBB 路径
     *
     * @return the path of /storage/emulated/0/Android/obb/package
     */
    fun getExternalAppObbPath(): String {
        return if (isExternalStorageDisable()) "" else AppManager.getInstance().getApp().obbDir.absolutePath
    }

    private fun isExternalStorageDisable(): Boolean {
        return Environment.MEDIA_MOUNTED != Environment.getExternalStorageState()
    }
}