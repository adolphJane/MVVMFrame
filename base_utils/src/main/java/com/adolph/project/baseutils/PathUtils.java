package com.adolph.project.baseutils;

import android.os.Build;
import android.os.Environment;

/**
 * 路径相关
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
public class PathUtils {

    private PathUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 获取根路径
     *
     * @return the path of /system
     */
    public static String getRootPath() {
        return Environment.getRootDirectory().getAbsolutePath();
    }

    /**
     * 获取数据路径
     *
     * @return the path of /data
     */
    public static String getDataPath() {
        return Environment.getDataDirectory().getAbsolutePath();
    }

    /**
     * 获取下载缓存路径
     *
     * @return the path of /cache
     */
    public static String getDownloadCachePath() {
        return Environment.getDownloadCacheDirectory().getAbsolutePath();
    }

    /**
     * 获取内存应用数据路径
     *
     * @return the path of /data/data/package
     */
    public static String getInternalAppDataPath() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return AppManageUtils.getApp().getApplicationInfo().dataDir;
        }
        return AppManageUtils.getApp().getDataDir().getAbsolutePath();
    }

    /**
     * 获取内存应用代码缓存路径
     *
     * @return the path of /data/data/package/code_cache
     */
    public static String getInternalAppCodeCacheDir() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return AppManageUtils.getApp().getApplicationInfo().dataDir + "/code_cache";
        }
        return AppManageUtils.getApp().getCodeCacheDir().getAbsolutePath();
    }

    /**
     * 获取内存应用缓存路径
     *
     * @return the path of /data/data/package/cache
     */
    public static String getInternalAppCachePath() {
        return AppManageUtils.getApp().getCacheDir().getAbsolutePath();
    }

    /**
     * 获取内存应用数据库路径
     *
     * @return the path of /data/data/package/databases
     */
    public static String getInternalAppDbsPath() {
        return AppManageUtils.getApp().getApplicationInfo().dataDir + "/databases";
    }

    /**
     * 获取内存应用数据库路径
     *
     * @param name The name of database.
     * @return the path of /data/data/package/databases/name
     */
    public static String getInternalAppDbPath(String name) {
        return AppManageUtils.getApp().getDatabasePath(name).getAbsolutePath();
    }

    /**
     * 获取内存应用文件路径
     *
     * @return the path of /data/data/package/files
     */
    public static String getInternalAppFilesPath() {
        return AppManageUtils.getApp().getFilesDir().getAbsolutePath();
    }

    /**
     * 获取内存应用 SP 路径
     *
     * @return the path of /data/data/package/shared_prefs
     */
    public static String getInternalAppSpPath() {
        return AppManageUtils.getApp().getApplicationInfo().dataDir + "shared_prefs";
    }

    /**
     * 获取内存应用未备份文件路径
     *
     * @return the path of /data/data/package/no_backup
     */
    public static String getInternalAppNoBackupFilesPath() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return AppManageUtils.getApp().getApplicationInfo().dataDir + "no_backup";
        }
        return AppManageUtils.getApp().getNoBackupFilesDir().getAbsolutePath();
    }

    /**
     * 获取外存路径
     *
     * @return the path of /storage/emulated/0
     */
    public static String getExternalStoragePath() {
        if (isExternalStorageDisable()) return "";
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    /**
     * 获取外存音乐路径
     *
     * @return the path of /storage/emulated/0/Music
     */
    public static String getExternalMusicPath() {
        if (isExternalStorageDisable()) return "";
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath();
    }

    /**
     * 获取外存播客路径
     *
     * @return the path of /storage/emulated/0/Podcasts
     */
    public static String getExternalPodcastsPath() {
        if (isExternalStorageDisable()) return "";
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS).getAbsolutePath();
    }

    /**
     * 获取外存铃声路径
     *
     * @return the path of /storage/emulated/0/Ringtones
     */
    public static String getExternalRingtonesPath() {
        if (isExternalStorageDisable()) return "";
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES).getAbsolutePath();
    }

    /**
     * 获取外存闹铃路径
     *
     * @return the path of /storage/emulated/0/Alarms
     */
    public static String getExternalAlarmsPath() {
        if (isExternalStorageDisable()) return "";
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_ALARMS).getAbsolutePath();
    }

    /**
     * 获取外存通知路径
     *
     * @return the path of /storage/emulated/0/Notifications
     */
    public static String getExternalNotificationsPath() {
        if (isExternalStorageDisable()) return "";
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_NOTIFICATIONS).getAbsolutePath();
    }

    /**
     * 获取外存图片路径
     *
     * @return the path of /storage/emulated/0/Pictures
     */
    public static String getExternalPicturesPath() {
        if (isExternalStorageDisable()) return "";
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
    }

    /**
     * 获取外存影片路径
     *
     * @return the path of /storage/emulated/0/Movies
     */
    public static String getExternalMoviesPath() {
        if (isExternalStorageDisable()) return "";
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath();
    }

    /**
     * 获取外存下载路径
     *
     * @return the path of /storage/emulated/0/Download
     */
    public static String getExternalDownloadsPath() {
        if (isExternalStorageDisable()) return "";
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
    }

    /**
     * 获取外存数码相机图片路径
     *
     * @return the path of /storage/emulated/0/DCIM
     */
    public static String getExternalDcimPath() {
        if (isExternalStorageDisable()) return "";
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
    }

    /**
     * 获取外存文档路径
     *
     * @return the path of /storage/emulated/0/Documents
     */
    public static String getExternalDocumentsPath() {
        if (isExternalStorageDisable()) return "";
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return Environment.getExternalStorageDirectory().getAbsolutePath() + "/Documents";
        }
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
    }

    /**
     * 获取外存应用数据路径
     *
     * @return the path of /storage/emulated/0/Android/data/package
     */
    public static String getExternalAppDataPath() {
        if (isExternalStorageDisable()) return "";
        //noinspection ConstantConditions
        return AppManageUtils.getApp().getExternalCacheDir().getParentFile().getAbsolutePath();
    }

    /**
     * 获取外存应用缓存路径
     *
     * @return the path of /storage/emulated/0/Android/data/package/cache
     */
    public static String getExternalAppCachePath() {
        if (isExternalStorageDisable()) return "";
        //noinspection ConstantConditions
        return AppManageUtils.getApp().getExternalCacheDir().getAbsolutePath();
    }

    /**
     * 获取外存应用文件路径
     *
     * @return the path of /storage/emulated/0/Android/data/package/files
     */
    public static String getExternalAppFilesPath() {
        if (isExternalStorageDisable()) return "";
        //noinspection ConstantConditions
        return AppManageUtils.getApp().getExternalFilesDir(null).getAbsolutePath();
    }

    /**
     * 获取外存应用音乐路径
     *
     * @return the path of /storage/emulated/0/Android/data/package/files/Music
     */
    public static String getExternalAppMusicPath() {
        if (isExternalStorageDisable()) return "";
        //noinspection ConstantConditions
        return AppManageUtils.getApp().getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath();
    }

    /**
     * 获取外存应用播客路径
     *
     * @return the path of /storage/emulated/0/Android/data/package/files/Podcasts
     */
    public static String getExternalAppPodcastsPath() {
        if (isExternalStorageDisable()) return "";
        //noinspection ConstantConditions
        return AppManageUtils.getApp().getExternalFilesDir(Environment.DIRECTORY_PODCASTS).getAbsolutePath();
    }

    /**
     * 获取外存应用铃声路径
     *
     * @return the path of /storage/emulated/0/Android/data/package/files/Ringtones
     */
    public static String getExternalAppRingtonesPath() {
        if (isExternalStorageDisable()) return "";
        //noinspection ConstantConditions
        return AppManageUtils.getApp().getExternalFilesDir(Environment.DIRECTORY_RINGTONES).getAbsolutePath();
    }

    /**
     * 获取外存应用闹铃路径
     *
     * @return the path of /storage/emulated/0/Android/data/package/files/Alarms
     */
    public static String getExternalAppAlarmsPath() {
        if (isExternalStorageDisable()) return "";
        //noinspection ConstantConditions
        return AppManageUtils.getApp().getExternalFilesDir(Environment.DIRECTORY_ALARMS).getAbsolutePath();
    }

    /**
     * 获取外存应用通知路径
     *
     * @return the path of /storage/emulated/0/Android/data/package/files/Notifications
     */
    public static String getExternalAppNotificationsPath() {
        if (isExternalStorageDisable()) return "";
        //noinspection ConstantConditions
        return AppManageUtils.getApp().getExternalFilesDir(Environment.DIRECTORY_NOTIFICATIONS).getAbsolutePath();
    }

    /**
     * 获取外存应用图片路径
     *
     * @return path of /storage/emulated/0/Android/data/package/files/Pictures
     */
    public static String getExternalAppPicturesPath() {
        if (isExternalStorageDisable()) return "";
        //noinspection ConstantConditions
        return AppManageUtils.getApp().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
    }

    /**
     * 获取外存应用影片路径
     *
     * @return the path of /storage/emulated/0/Android/data/package/files/Movies
     */
    public static String getExternalAppMoviesPath() {
        if (isExternalStorageDisable()) return "";
        //noinspection ConstantConditions
        return AppManageUtils.getApp().getExternalFilesDir(Environment.DIRECTORY_MOVIES).getAbsolutePath();
    }

    /**
     * 获取外存应用下载路径
     *
     * @return the path of /storage/emulated/0/Android/data/package/files/Download
     */
    public static String getExternalAppDownloadPath() {
        if (isExternalStorageDisable()) return "";
        //noinspection ConstantConditions
        return AppManageUtils.getApp().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
    }

    /**
     * 获取外存应用数码相机图片路径
     *
     * @return the path of /storage/emulated/0/Android/data/package/files/DCIM
     */
    public static String getExternalAppDcimPath() {
        if (isExternalStorageDisable()) return "";
        //noinspection ConstantConditions
        return AppManageUtils.getApp().getExternalFilesDir(Environment.DIRECTORY_DCIM).getAbsolutePath();
    }

    /**
     * 获取外存应用文档路径
     *
     * @return the path of /storage/emulated/0/Android/data/package/files/Documents
     */
    public static String getExternalAppDocumentsPath() {
        if (isExternalStorageDisable()) return "";
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            //noinspection ConstantConditions
            return AppManageUtils.getApp().getExternalFilesDir(null).getAbsolutePath() + "/Documents";
        }
        //noinspection ConstantConditions
        return AppManageUtils.getApp().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
    }

    /**
     * 获取外存应用 OBB 路径
     *
     * @return the path of /storage/emulated/0/Android/obb/package
     */
    public static String getExternalAppObbPath() {
        if (isExternalStorageDisable()) return "";
        return AppManageUtils.getApp().getObbDir().getAbsolutePath();
    }

    private static boolean isExternalStorageDisable() {
        return !Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }
}
