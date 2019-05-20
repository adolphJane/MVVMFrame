package com.magicalrice.project.library_base.advanced

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Build
import android.os.SystemClock
import android.provider.Settings
import com.magicalrice.project.library_base.base.FileUtils
import com.magicalrice.project.library_base.base.log.LogUtils
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * 手机信息 & MAC地址 & 开机时间
 *
 * @author MaTianyu
 * @date 2014-09-25
 */
object AndroidUtil {
    private val TAG = AndroidUtil::class.java.simpleName
    //Ethernet Mac Address
    private val ETH0_MAC_ADDRESS = "/sys/class/net/eth0/address"


    /**
     * 获取 以太网 MAC 地址
     */
    val ethernetMacAddress: String
        get() {
            try {
                val mac = FileUtils.readFileToString(File(ETH0_MAC_ADDRESS))
                if (LogUtils.getConfig().getLogSwitch()) {
                    LogUtils.i(TAG, "Ethernet MAC：$mac")
                }
                return mac
            } catch (e: IOException) {
                LogUtils.e(TAG, "IO Exception when getting eth0 mac address", e)
                e.printStackTrace()
                return "unknown"
            }

        }

    /**
     * 获取 开机时间
     */
    val bootTimeString: String
        get() {
            val ut = SystemClock.elapsedRealtime() / 1000
            val h = (ut / 3600).toInt()
            val m = (ut / 60 % 60).toInt()
            if (LogUtils.getConfig().getLogSwitch()) {
                LogUtils.i(TAG, "$h:$m")
            }
            return "$h:$m"
        }

    /**
     * 获取 Wifi MAC 地址
     * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"></uses-permission>
     */
    @Deprecated("")
    fun getMacAddress(context: Context): String {
        return getWifiMacAddress(context)
    }

    /**
     * 获取 Wifi MAC 地址
     * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"></uses-permission>
     */
    fun getWifiMacAddress(context: Context): String {
        //wifi mac地址
        val wifi = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val info = wifi.connectionInfo
        val mac = info.macAddress
        if (LogUtils.getConfig().getLogSwitch()) {
            LogUtils.i(TAG, "WIFI MAC：$mac")
        }
        return mac
    }

    /**
     * 获取 ANDROID_ID
     */
    fun getAndroidId(context: Context): String {
        val androidId =
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        if (LogUtils.getConfig().getLogSwitch()) {
            LogUtils.i(TAG, "ANDROID_ID ：$androidId")
        }
        return androidId
    }

    fun printSystemInfo(): String {
        val date = Date(System.currentTimeMillis())
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val time = dateFormat.format(date)
        val sb = StringBuilder()
        sb.append("_______  系统信息  ").append(time).append(" ______________")
        sb.append("\nID                 :").append(Build.ID)
        sb.append("\nBRAND              :").append(Build.BRAND)
        sb.append("\nMODEL              :").append(Build.MODEL)
        sb.append("\nRELEASE            :").append(Build.VERSION.RELEASE)
        sb.append("\nSDK                :").append(Build.VERSION.SDK)

        sb.append("\n_______ OTHER _______")
        sb.append("\nBOARD              :").append(Build.BOARD)
        sb.append("\nPRODUCT            :").append(Build.PRODUCT)
        sb.append("\nDEVICE             :").append(Build.DEVICE)
        sb.append("\nFINGERPRINT        :").append(Build.FINGERPRINT)
        sb.append("\nHOST               :").append(Build.HOST)
        sb.append("\nTAGS               :").append(Build.TAGS)
        sb.append("\nTYPE               :").append(Build.TYPE)
        sb.append("\nTIME               :").append(Build.TIME)
        sb.append("\nINCREMENTAL        :").append(Build.VERSION.INCREMENTAL)

        sb.append("\n_______ CUPCAKE-3 _______")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            sb.append("\nDISPLAY            :").append(Build.DISPLAY)
        }

        sb.append("\n_______ DONUT-4 _______")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
            sb.append("\nSDK_INT            :").append(Build.VERSION.SDK_INT)
            sb.append("\nMANUFACTURER       :").append(Build.MANUFACTURER)
            sb.append("\nBOOTLOADER         :").append(Build.BOOTLOADER)
            sb.append("\nCPU_ABI            :").append(Build.CPU_ABI)
            sb.append("\nCPU_ABI2           :").append(Build.CPU_ABI2)
            sb.append("\nHARDWARE           :").append(Build.HARDWARE)
            sb.append("\nUNKNOWN            :").append(Build.UNKNOWN)
            sb.append("\nCODENAME           :").append(Build.VERSION.CODENAME)
        }

        sb.append("\n_______ GINGERBREAD-9 _______")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            sb.append("\nSERIAL             :").append(Build.SERIAL)
        }
        LogUtils.i(TAG, sb.toString())
        return sb.toString()
    }
}
