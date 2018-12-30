package com.adolph.project.advancedutils;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import com.adolph.project.baseutils.AppManageUtils;
import com.adolph.project.baseutils.constant.CacheConstants;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

import static android.Manifest.permission.*;

/**
 * 二级缓存相关
 *
 * getInstance                 : 获取缓存实例
 * Instance.put                : 缓存中写入数据
 * Instance.getBytes           : 缓存中读取字节数组
 * Instance.getString          : 缓存中读取 String
 * Instance.getJSONObject      : 缓存中读取 JSONObject
 * Instance.getJSONArray       : 缓存中读取 JSONArray
 * Instance.getBitmap          : 缓存中读取 Bitmap
 * Instance.getDrawable        : 缓存中读取 Drawable
 * Instance.getParcelable      : 缓存中读取 Parcelable
 * Instance.getSerializable    : 缓存中读取 Serializable
 * Instance.getCacheDiskSize   : 获取磁盘缓存大小
 * Instance.getCacheDiskCount  : 获取磁盘缓存个数
 * Instance.getCacheMemoryCount: 获取内存缓存个数
 * Instance.remove             : 根据键值移除缓存
 * Instance.clear              : 清除所有缓存
 */
public final class CacheDoubleUtils implements CacheConstants {

    private static final Map<String, CacheDoubleUtils> CACHE_MAP = new ConcurrentHashMap<>();

    private final CacheMemoryUtils mCacheMemoryUtils;
    private final CacheDiskUtils   mCacheDiskUtils;

    /**
     * 获取缓存实例
     *
     * @return the single {@link CacheDoubleUtils} instance
     */
    public static CacheDoubleUtils getInstance() {
        return getInstance(CacheMemoryUtils.getInstance(), CacheDiskUtils.getInstance());
    }

    /**
     * 获取缓存实例
     *
     * @param cacheMemoryUtils The instance of {@link CacheMemoryUtils}.
     * @param cacheDiskUtils   The instance of {@link CacheDiskUtils}.
     * @return the single {@link CacheDoubleUtils} instance
     */
    public static CacheDoubleUtils getInstance(@NonNull final CacheMemoryUtils cacheMemoryUtils,
                                               @NonNull final CacheDiskUtils cacheDiskUtils) {
        final String cacheKey = cacheDiskUtils.toString() + "_" + cacheMemoryUtils.toString();
        CacheDoubleUtils cache = CACHE_MAP.get(cacheKey);
        if (cache == null) {
            cache = new CacheDoubleUtils(cacheMemoryUtils, cacheDiskUtils);
            CACHE_MAP.put(cacheKey, cache);
        }
        return cache;
    }

    private CacheDoubleUtils(CacheMemoryUtils cacheMemoryUtils, CacheDiskUtils cacheUtils) {
        mCacheMemoryUtils = cacheMemoryUtils;
        mCacheDiskUtils = cacheUtils;
    }


    ///////////////////////////////////////////////////////////////////////////
    // about bytes
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 缓存中写入数据
     *
     * @param key   The key of cache.
     * @param value The value of cache.
     */
    public void put(@NonNull final String key, final byte[] value) {
        put(key, value, -1);
    }

    /**
     * 缓存中写入字节数据
     *
     * @param key      The key of cache.
     * @param value    The value of cache.
     * @param saveTime The save time of cache, in seconds.
     */
    public void put(@NonNull final String key, byte[] value, final int saveTime) {
        mCacheMemoryUtils.put(key, value, saveTime);
        mCacheDiskUtils.put(key, value, saveTime);
    }

    /**
     * 缓存中读取字节数组
     *
     * @param key The key of cache.
     * @return the bytes if cache exists or null otherwise
     */
    public byte[] getBytes(@NonNull final String key) {
        return getBytes(key, null);
    }

    /**
     * 缓存中读取字节数组
     *
     * @param key          The key of cache.
     * @param defaultValue The default value if the cache doesn't exist.
     * @return the bytes if cache exists or defaultValue otherwise
     */
    public byte[] getBytes(@NonNull final String key, final byte[] defaultValue) {
        byte[] obj = mCacheMemoryUtils.get(key);
        if (obj != null) return obj;
        return mCacheDiskUtils.getBytes(key, defaultValue);
    }

    ///////////////////////////////////////////////////////////////////////////
    // about String
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 缓存中写入string数据
     *
     * @param key   The key of cache.
     * @param value The value of cache.
     */
    public void put(@NonNull final String key, final String value) {
        put(key, value, -1);
    }

    /**
     * 缓存中写入string数据
     *
     * @param key      The key of cache.
     * @param value    The value of cache.
     * @param saveTime The save time of cache, in seconds.
     */
    public void put(@NonNull final String key, final String value, final int saveTime) {
        mCacheMemoryUtils.put(key, value, saveTime);
        mCacheDiskUtils.put(key, value, saveTime);
    }

    /**
     * 缓存中读取 String
     *
     * @param key The key of cache.
     * @return the string value if cache exists or null otherwise
     */
    public String getString(@NonNull final String key) {
        return getString(key, null);
    }

    /**
     * 缓存中读取 String
     *
     * @param key          The key of cache.
     * @param defaultValue The default value if the cache doesn't exist.
     * @return the string value if cache exists or defaultValue otherwise
     */
    public String getString(@NonNull final String key, final String defaultValue) {
        String obj = mCacheMemoryUtils.get(key);
        if (obj != null) return obj;
        return mCacheDiskUtils.getString(key, defaultValue);
    }

    ///////////////////////////////////////////////////////////////////////////
    // about JSONObject
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 缓存中写入json数据
     *
     * @param key   The key of cache.
     * @param value The value of cache.
     */
    public void put(@NonNull final String key, final JSONObject value) {
        put(key, value, -1);
    }

    /**
     * 缓存中写入json数据
     *
     * @param key      The key of cache.
     * @param value    The value of cache.
     * @param saveTime The save time of cache, in seconds.
     */
    public void put(@NonNull final String key,
                    final JSONObject value,
                    final int saveTime) {
        mCacheMemoryUtils.put(key, value, saveTime);
        mCacheDiskUtils.put(key, value, saveTime);
    }

    /**
     * 缓存中读取 JSONObject
     *
     * @param key The key of cache.
     * @return the JSONObject if cache exists or null otherwise
     */
    public JSONObject getJSONObject(@NonNull final String key) {
        return getJSONObject(key, null);
    }

    /**
     * 缓存中读取 JSONObject
     *
     * @param key          The key of cache.
     * @param defaultValue The default value if the cache doesn't exist.
     * @return the JSONObject if cache exists or defaultValue otherwise
     */
    public JSONObject getJSONObject(@NonNull final String key, final JSONObject defaultValue) {
        JSONObject obj = mCacheMemoryUtils.get(key);
        if (obj != null) return obj;
        return mCacheDiskUtils.getJSONObject(key, defaultValue);
    }


    ///////////////////////////////////////////////////////////////////////////
    // about JSONArray
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 缓存中写入jsonarray数据
     *
     * @param key   The key of cache.
     * @param value The value of cache.
     */
    public void put(@NonNull final String key, final JSONArray value) {
        put(key, value, -1);
    }

    /**
     * 缓存中写入jsonarray数据
     *
     * @param key      The key of cache.
     * @param value    The value of cache.
     * @param saveTime The save time of cache, in seconds.
     */
    public void put(@NonNull final String key, final JSONArray value, final int saveTime) {
        mCacheMemoryUtils.put(key, value, saveTime);
        mCacheDiskUtils.put(key, value, saveTime);
    }

    /**
     * 缓存中读取 JSONArray
     *
     * @param key The key of cache.
     * @return the JSONArray if cache exists or null otherwise
     */
    public JSONArray getJSONArray(@NonNull final String key) {
        return getJSONArray(key, null);
    }

    /**
     * 缓存中读取 JSONArray
     *
     * @param key          The key of cache.
     * @param defaultValue The default value if the cache doesn't exist.
     * @return the JSONArray if cache exists or defaultValue otherwise
     */
    public JSONArray getJSONArray(@NonNull final String key, final JSONArray defaultValue) {
        JSONArray obj = mCacheMemoryUtils.get(key);
        if (obj != null) return obj;
        return mCacheDiskUtils.getJSONArray(key, defaultValue);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Bitmap cache
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 缓存中写入bitmap数据
     *
     * @param key   The key of cache.
     * @param value The value of cache.
     */
    public void put(@NonNull final String key, final Bitmap value) {
        put(key, value, -1);
    }

    /**
     * 缓存中写入bitmap数据
     *
     * @param key      The key of cache.
     * @param value    The value of cache.
     * @param saveTime The save time of cache, in seconds.
     */
    public void put(@NonNull final String key, final Bitmap value, final int saveTime) {
        mCacheMemoryUtils.put(key, value, saveTime);
        mCacheDiskUtils.put(key, value, saveTime);
    }

    /**
     * 缓存中读取 Bitmap
     *
     * @param key The key of cache.
     * @return the bitmap if cache exists or null otherwise
     */
    public Bitmap getBitmap(@NonNull final String key) {
        return getBitmap(key, null);
    }

    /**
     * 缓存中读取 Bitmap
     *
     * @param key          The key of cache.
     * @param defaultValue The default value if the cache doesn't exist.
     * @return the bitmap if cache exists or defaultValue otherwise
     */
    public Bitmap getBitmap(@NonNull final String key, final Bitmap defaultValue) {
        Bitmap obj = mCacheMemoryUtils.get(key);
        if (obj != null) return obj;
        return mCacheDiskUtils.getBitmap(key, defaultValue);
    }

    ///////////////////////////////////////////////////////////////////////////
    // about Drawable
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 缓存中写入drawable数据
     *
     * @param key   The key of cache.
     * @param value The value of cache.
     */
    public void put(@NonNull final String key, final Drawable value) {
        put(key, value, -1);
    }

    /**
     * 缓存中写入drawable数据
     *
     * @param key      The key of cache.
     * @param value    The value of cache.
     * @param saveTime The save time of cache, in seconds.
     */
    public void put(@NonNull final String key, final Drawable value, final int saveTime) {
        mCacheMemoryUtils.put(key, value, saveTime);
        mCacheDiskUtils.put(key, value, saveTime);
    }

    /**
     * 缓存中读取 Drawable
     *
     * @param key The key of cache.
     * @return the drawable if cache exists or null otherwise
     */
    public Drawable getDrawable(@NonNull final String key) {
        return getDrawable(key, null);
    }

    /**
     * 缓存中读取 Drawable
     *
     * @param key          The key of cache.
     * @param defaultValue The default value if the cache doesn't exist.
     * @return the drawable if cache exists or defaultValue otherwise
     */
    public Drawable getDrawable(@NonNull final String key, final Drawable defaultValue) {
        Drawable obj = mCacheMemoryUtils.get(key);
        if (obj != null) return obj;
        return mCacheDiskUtils.getDrawable(key, defaultValue);
    }

    ///////////////////////////////////////////////////////////////////////////
    // about Parcelable
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 缓存中写入parcelable数据
     *
     * @param key   The key of cache.
     * @param value The value of cache.
     */
    public void put(@NonNull final String key, final Parcelable value) {
        put(key, value, -1);
    }

    /**
     * 缓存中写入parcelable数据
     *
     * @param key      The key of cache.
     * @param value    The value of cache.
     * @param saveTime The save time of cache, in seconds.
     */
    public void put(@NonNull final String key, final Parcelable value, final int saveTime) {
        mCacheMemoryUtils.put(key, value, saveTime);
        mCacheDiskUtils.put(key, value, saveTime);
    }

    /**
     * 缓存中读取 Parcelable
     *
     * @param key     The key of cache.
     * @param creator The creator.
     * @param <T>     The value type.
     * @return the parcelable if cache exists or null otherwise
     */
    public <T> T getParcelable(@NonNull final String key,
                               @NonNull final Parcelable.Creator<T> creator) {
        return getParcelable(key, creator, null);
    }

    /**
     * 缓存中读取 Parcelable
     *
     * @param key          The key of cache.
     * @param creator      The creator.
     * @param defaultValue The default value if the cache doesn't exist.
     * @param <T>          The value type.
     * @return the parcelable if cache exists or defaultValue otherwise
     */
    public <T> T getParcelable(@NonNull final String key,
                               @NonNull final Parcelable.Creator<T> creator,
                               final T defaultValue) {
        T value = mCacheMemoryUtils.get(key);
        if (value != null) return value;
        return mCacheDiskUtils.getParcelable(key, creator, defaultValue);
    }

    ///////////////////////////////////////////////////////////////////////////
    // about Serializable
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 缓存中写入serializable数据
     *
     * @param key   The key of cache.
     * @param value The value of cache.
     */
    public void put(@NonNull final String key, final Serializable value) {
        put(key, value, -1);
    }

    /**
     * 缓存中写入serializable数据
     *
     * @param key      The key of cache.
     * @param value    The value of cache.
     * @param saveTime The save time of cache, in seconds.
     */
    public void put(@NonNull final String key, final Serializable value, final int saveTime) {
        mCacheMemoryUtils.put(key, value, saveTime);
        mCacheDiskUtils.put(key, value, saveTime);
    }

    /**
     * 缓存中读取 Serializable
     *
     * @param key The key of cache.
     * @return the bitmap if cache exists or null otherwise
     */
    public Object getSerializable(@NonNull final String key) {
        return getSerializable(key, null);
    }

    /**
     * 缓存中读取 Serializable
     *
     * @param key          The key of cache.
     * @param defaultValue The default value if the cache doesn't exist.
     * @return the bitmap if cache exists or defaultValue otherwise
     */
    public Object getSerializable(@NonNull final String key, final Object defaultValue) {
        Object obj = mCacheMemoryUtils.get(key);
        if (obj != null) return obj;
        return mCacheDiskUtils.getSerializable(key, defaultValue);
    }

    /**
     * 获取磁盘缓存大小
     *
     * @return the size of cache in disk
     */
    public long getCacheDiskSize() {
        return mCacheDiskUtils.getCacheSize();
    }

    /**
     * 获取磁盘缓存个数
     *
     * @return the count of cache in disk
     */
    public int getCacheDiskCount() {
        return mCacheDiskUtils.getCacheCount();
    }

    /**
     * 获取内存缓存个数
     *
     * @return the count of cache in memory.
     */
    public int getCacheMemoryCount() {
        return mCacheMemoryUtils.getCacheCount();
    }

    /**
     * 根据键值移除缓存
     *
     * @param key The key of cache.
     */
    public void remove(@NonNull String key) {
        mCacheMemoryUtils.remove(key);
        mCacheDiskUtils.remove(key);
    }

    /**
     * 清除所有缓存
     */
    public void clear() {
        mCacheMemoryUtils.clear();
        mCacheDiskUtils.clear();
    }

    /**
     * <pre>
     *     author: Blankj
     *     blog  : http://blankj.com
     *     time  : 2016/09/27
     *     desc  : utils about crash
     * </pre>
     */
    public static final class CrashUtils {

        private static String defaultDir;
        private static String dir;
        private static String versionName;
        private static int    versionCode;

        private static final String FILE_SEP = System.getProperty("file.separator");
        @SuppressLint("SimpleDateFormat")
        private static final Format FORMAT   = new SimpleDateFormat("MM-dd HH-mm-ss");

        private static final Thread.UncaughtExceptionHandler DEFAULT_UNCAUGHT_EXCEPTION_HANDLER;
        private static final Thread.UncaughtExceptionHandler UNCAUGHT_EXCEPTION_HANDLER;

        private static OnCrashListener sOnCrashListener;

        static {
            try {
                PackageInfo pi = AppManageUtils.getApp()
                        .getPackageManager()
                        .getPackageInfo(AppManageUtils.getApp().getPackageName(), 0);
                if (pi != null) {
                    versionName = pi.versionName;
                    versionCode = pi.versionCode;
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            DEFAULT_UNCAUGHT_EXCEPTION_HANDLER = Thread.getDefaultUncaughtExceptionHandler();

            UNCAUGHT_EXCEPTION_HANDLER = new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(final Thread t, final Throwable e) {
                    if (e == null) {
                        if (DEFAULT_UNCAUGHT_EXCEPTION_HANDLER != null) {
                            DEFAULT_UNCAUGHT_EXCEPTION_HANDLER.uncaughtException(t, null);
                        } else {
                            android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(1);
                        }
                        return;
                    }

                    final String time = FORMAT.format(new Date(System.currentTimeMillis()));
                    final StringBuilder sb = new StringBuilder();
                    final String head = "************* Log Head ****************" +
                            "\nTime Of Crash      : " + time +
                            "\nDevice Manufacturer: " + Build.MANUFACTURER +
                            "\nDevice Model       : " + Build.MODEL +
                            "\nAndroid Version    : " + Build.VERSION.RELEASE +
                            "\nAndroid SDK        : " + Build.VERSION.SDK_INT +
                            "\nApp VersionName    : " + versionName +
                            "\nApp VersionCode    : " + versionCode +
                            "\n************* Log Head ****************\n\n";
                    sb.append(head);
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);
                    Throwable cause = e.getCause();
                    while (cause != null) {
                        cause.printStackTrace(pw);
                        cause = cause.getCause();
                    }
                    pw.flush();
                    sb.append(sw.toString());
                    final String crashInfo = sb.toString();
                    final String fullPath = (dir == null ? defaultDir : dir) + time + ".txt";
                    if (createOrExistsFile(fullPath)) {
                        input2File(crashInfo, fullPath);
                    } else {
                        Log.e("CrashUtils", "create " + fullPath + " failed!");
                    }

                    if (sOnCrashListener != null) {
                        sOnCrashListener.onCrash(crashInfo, e);
                    }

                    if (DEFAULT_UNCAUGHT_EXCEPTION_HANDLER != null) {
                        DEFAULT_UNCAUGHT_EXCEPTION_HANDLER.uncaughtException(t, e);
                    }
                }
            };
        }

        private CrashUtils() {
            throw new UnsupportedOperationException("u can't instantiate me...");
        }

        /**
         * Initialization.
         * <p>Must hold
         * {@code <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />}</p>
         */
        @RequiresPermission(WRITE_EXTERNAL_STORAGE)
        public static void init() {
            init("");
        }

        /**
         * Initialization
         * <p>Must hold
         * {@code <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />}</p>
         *
         * @param crashDir The directory of saving crash information.
         */
        @RequiresPermission(WRITE_EXTERNAL_STORAGE)
        public static void init(@NonNull final File crashDir) {
            init(crashDir.getAbsolutePath(), null);
        }

        /**
         * Initialization
         * <p>Must hold
         * {@code <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />}</p>
         *
         * @param crashDirPath The directory's path of saving crash information.
         */
        @RequiresPermission(WRITE_EXTERNAL_STORAGE)
        public static void init(final String crashDirPath) {
            init(crashDirPath, null);
        }

        /**
         * Initialization
         * <p>Must hold
         * {@code <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />}</p>
         *
         * @param onCrashListener The crash listener.
         */
        @RequiresPermission(WRITE_EXTERNAL_STORAGE)
        public static void init(final OnCrashListener onCrashListener) {
            init("", onCrashListener);
        }

        /**
         * Initialization
         * <p>Must hold
         * {@code <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />}</p>
         *
         * @param crashDir        The directory of saving crash information.
         * @param onCrashListener The crash listener.
         */
        @RequiresPermission(WRITE_EXTERNAL_STORAGE)
        public static void init(@NonNull final File crashDir, final OnCrashListener onCrashListener) {
            init(crashDir.getAbsolutePath(), onCrashListener);
        }

        /**
         * Initialization
         * <p>Must hold
         * {@code <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />}</p>
         *
         * @param crashDirPath    The directory's path of saving crash information.
         * @param onCrashListener The crash listener.
         */
        @RequiresPermission(WRITE_EXTERNAL_STORAGE)
        public static void init(final String crashDirPath, final OnCrashListener onCrashListener) {
            if (isSpace(crashDirPath)) {
                dir = null;
            } else {
                dir = crashDirPath.endsWith(FILE_SEP) ? crashDirPath : crashDirPath + FILE_SEP;
            }
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                    && AppManageUtils.getApp().getExternalCacheDir() != null)
                defaultDir = AppManageUtils.getApp().getExternalCacheDir() + FILE_SEP + "crash" + FILE_SEP;
            else {
                defaultDir = AppManageUtils.getApp().getCacheDir() + FILE_SEP + "crash" + FILE_SEP;
            }
            sOnCrashListener = onCrashListener;
            Thread.setDefaultUncaughtExceptionHandler(UNCAUGHT_EXCEPTION_HANDLER);
        }

        ///////////////////////////////////////////////////////////////////////////
        // interface
        ///////////////////////////////////////////////////////////////////////////

        public interface OnCrashListener {
            void onCrash(String crashInfo, Throwable e);
        }

        ///////////////////////////////////////////////////////////////////////////
        // other utils methods
        ///////////////////////////////////////////////////////////////////////////

        private static void input2File(final String input, final String filePath) {
            Future<Boolean> submit = Executors.newSingleThreadExecutor().submit(new Callable<Boolean>() {
                @Override
                public Boolean call() {
                    BufferedWriter bw = null;
                    try {
                        bw = new BufferedWriter(new FileWriter(filePath, true));
                        bw.write(input);
                        return true;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    } finally {
                        try {
                            if (bw != null) {
                                bw.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            try {
                if (submit.get()) return;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            Log.e("CrashUtils", "write crash info to " + filePath + " failed!");
        }

        private static boolean createOrExistsFile(final String filePath) {
            File file = new File(filePath);
            if (file.exists()) return file.isFile();
            if (!createOrExistsDir(file.getParentFile())) return false;
            try {
                return file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        private static boolean createOrExistsDir(final File file) {
            return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
        }

        private static boolean isSpace(final String s) {
            if (s == null) return true;
            for (int i = 0, len = s.length(); i < len; ++i) {
                if (!Character.isWhitespace(s.charAt(i))) {
                    return false;
                }
            }
            return true;
        }
    }
}
