package com.magicalrice.project.library_base.advanced

import com.magicalrice.project.library_base.advanced.receiver.Check
import com.magicalrice.project.library_base.base.FileIOUtils
import com.magicalrice.project.library_base.base.log.LogUtils
import java.io.*
import java.util.regex.Pattern

/**
 * @package com.magicalrice.project.library_base.advanced
 * @author Adolph
 * @date 2019-04-22 Mon
 * @description 获取Cpu信息.
 */
object CpuUtil {
    private val TAG = CpuUtil::class.java.simpleName
    private val CPU_INFO_PATH = "/proc/cpuinfo"
    private val CPU_FREQ_NULL = "N/A"
    private val CMD_CAT = "/system/bin/cat"
    private val CPU_FREQ_CUR_PATH = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq"
    private val CPU_FREQ_MAX_PATH = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq"
    private val CPU_FREQ_MIN_PATH = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq"

    private var CPU_NAME: String? = null
    private var CPU_CORES = 0
    private var CPU_MAX_FREQENCY: Long = 0
    private var CPU_MIN_FREQENCY: Long = 0

    /**
     * Get available processors.
     */
    val processorsCount: Int
        get() = Runtime.getRuntime().availableProcessors()

    /**
     * Gets the number of cores available in this device, across all processors.
     * Requires: Ability to peruse the filesystem at "/sys/devices/system/cpu"
     *
     * @return The number of cores, or available processors if failed to get result
     */
    //Private Class to display only CPU devices in the directory listing
    //Check if filename is "cpu", followed by a single digit number
    //Get directory containing CPU info
    //Filter to only list the devices we care about
    //Return the number of cores (virtual CPU devices)
    val coresNumbers: Int
        get() {
            if (CPU_CORES != 0) {
                return CPU_CORES
            }
            class CpuFilter : FileFilter {
                override fun accept(pathname: File): Boolean {
                    return Pattern.matches("cpu[0-9]+", pathname.name)
                }
            }

            try {
                val dir = File("/sys/devices/system/cpu/")
                val files = dir.listFiles(CpuFilter())
                CPU_CORES = files.size
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (CPU_CORES < 1) {
                CPU_CORES = Runtime.getRuntime().availableProcessors()
            }
            if (CPU_CORES < 1) {
                CPU_CORES = 1
            }
            return CPU_CORES
        }

    /**
     * Get CPU name.
     */
    val cpuName: String?
        get() {
            if (!Check.isEmpty(CPU_NAME.toString())) {
                return CPU_NAME
            }
            try {
                val bufferedReader = BufferedReader(FileReader(CPU_INFO_PATH), 8192)
                val line = bufferedReader.readLine()
                bufferedReader.close()
                val array = line.split(":\\s+".toRegex(), 2).toTypedArray()
                if (array.size > 1) {
                    if (LogUtils.getConfig().getLogSwitch()) {
                        LogUtils.i(TAG, array[1])
                    }
                    CPU_NAME = array[1]
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return CPU_NAME
        }

    /**
     * Get current CPU freqency.
     */
    val currentFreqency: Long
        get() {
            try {
                return java.lang.Long.parseLong(FileIOUtils.getFileOutputString(CPU_FREQ_CUR_PATH).trim())
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return 0
        }

    /**
     * Get maximum CPU freqency
     */
    val maxFreqency: Long
        get() {
            if (CPU_MAX_FREQENCY > 0) {
                return CPU_MAX_FREQENCY
            }
            try {
                CPU_MAX_FREQENCY = java.lang.Long.parseLong(
                    getCMDOutputString(
                        arrayOf(
                            CMD_CAT,
                            CPU_FREQ_MAX_PATH
                        )
                    )!!.trim { it <= ' ' })
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return CPU_MAX_FREQENCY
        }

    /**
     * Get minimum frenqency.
     */
    val minFreqency: Long
        get() {
            if (CPU_MIN_FREQENCY > 0) {
                return CPU_MIN_FREQENCY
            }
            try {
                CPU_MIN_FREQENCY = java.lang.Long.parseLong(
                    getCMDOutputString(
                        arrayOf(
                            CMD_CAT,
                            CPU_FREQ_MIN_PATH
                        )
                    )!!.trim { it <= ' ' })
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return CPU_MIN_FREQENCY
        }

    /**
     * Print cpu info.
     */
    fun printCpuInfo(): String {
        val info = FileIOUtils.getFileOutputString(CPU_INFO_PATH)
        if (LogUtils.getConfig().getLogSwitch()) {
            LogUtils.i(TAG, "_______  CPU :   \n$info")
        }
        return info
    }

    /**
     * Get command output string.
     */
    fun getCMDOutputString(args: Array<String>): String? {
        try {
            val cmd = ProcessBuilder(*args)
            val process = cmd.start()
            val inputStream = process.inputStream
            val sb = StringBuilder()
            val re = ByteArray(64)
            var len: Int = inputStream.read(re)
            while (len != -1) {
                sb.append(String(re, 0, len))
                len = inputStream.read(re)
            }
            inputStream.close()
            process.destroy()
            if (LogUtils.getConfig().getLogSwitch()) {
                LogUtils.i(TAG, "CMD: $sb")
            }
            return sb.toString()
        } catch (ex: IOException) {
            ex.printStackTrace()
        }

        return null
    }
}
