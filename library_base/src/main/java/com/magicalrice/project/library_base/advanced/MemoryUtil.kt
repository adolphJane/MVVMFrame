package com.magicalrice.project.library_base.advanced

import android.annotation.TargetApi
import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.text.format.Formatter
import com.magicalrice.project.library_base.base.FileIOUtils
import com.magicalrice.project.library_base.base.log.LogUtils

/**
 * @package com.magicalrice.project.library_base.advanced
 * @author Adolph
 * @date 2019-04-22 Mon
 * @description 获取内存信息
 */
object MemoryUtil {
    private val TAG = MemoryUtil::class.java.simpleName
    private val MEM_INFO_PATH = "/proc/meminfo"

    /**
     * Print memory info. such as:
     *
     * MemTotal:        1864292 kB
     * MemFree:          779064 kB
     * Buffers:            4540 kB
     * Cached:           185656 kB
     * SwapCached:        13160 kB
     * Active:           435588 kB
     * Inactive:         269312 kB
     * Active(anon):     386188 kB
     * Inactive(anon):   132576 kB
     * Active(file):      49400 kB
     * Inactive(file):   136736 kB
     * Unevictable:        2420 kB
     * Mlocked:               0 kB
     * HighTotal:       1437692 kB
     * HighFree:         520212 kB
     * LowTotal:         426600 kB
     * LowFree:          258852 kB
     * SwapTotal:        511996 kB
     * SwapFree:         171876 kB
     * Dirty:               412 kB
     * Writeback:             0 kB
     * AnonPages:        511924 kB
     * Mapped:           152368 kB
     * Shmem:              1636 kB
     * Slab:             109224 kB
     * SReclaimable:      75932 kB
     * SUnreclaim:        33292 kB
     * KernelStack:       13056 kB
     * PageTables:        28032 kB
     * NFS_Unstable:          0 kB
     * Bounce:                0 kB
     * WritebackTmp:          0 kB
     * CommitLimit:     1444140 kB
     * Committed_AS:   25977748 kB
     * VmallocTotal:     458752 kB
     * VmallocUsed:      123448 kB
     * VmallocChunk:     205828 kB
     */
    fun printMemInfo(): String {
        val info = FileIOUtils.getFileOutputString(MEM_INFO_PATH)
        if (LogUtils.getConfig().getLogSwitch()) {
            LogUtils.i(TAG, "_______  内存信息:   \n$info")
        }
        return info
    }

    /**
     * Get memory info of device.
     */
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    fun getMemoryInfo(context: Context): ActivityManager.MemoryInfo {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val mi = ActivityManager.MemoryInfo()
        am.getMemoryInfo(mi)
        return mi
    }

    /**
     * Print Memory info.
     */
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    fun printMemoryInfo(context: Context): ActivityManager.MemoryInfo {
        val mi = getMemoryInfo(context)
        if (LogUtils.getConfig().getLogSwitch()) {
            val sb = StringBuilder()
            sb.append("_______  Memory :   ")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                sb.append("\ntotalMem        :").append(mi.totalMem)
            }
            sb.append("\navailMem        :").append(mi.availMem)
            sb.append("\nlowMemory       :").append(mi.lowMemory)
            sb.append("\nthreshold       :").append(mi.threshold)
            LogUtils.i(TAG, sb.toString())
        }
        return mi
    }

    /**
     * Get available memory info.
     */
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    fun getAvailMemory(context: Context): String {// 获取android当前可用内存大小
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val mi = ActivityManager.MemoryInfo()
        am.getMemoryInfo(mi)
        // mi.availMem; 当前系统的可用内存
        return Formatter.formatFileSize(context, mi.availMem)// 将获取的内存大小规格化
    }

}
