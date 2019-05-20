package com.magicalrice.project.library_base.advanced

import android.Manifest.permission.ACCESS_WIFI_STATE
import android.Manifest.permission.INTERNET
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.text.TextUtils
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import com.magicalrice.project.library_base.base.AppManager
import java.io.File
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException

/**
 * @package com.magicalrice.project.library_base.advanced
 * @author Adolph
 * @date 2019-04-22 Mon
 * @description TODO
 */

object DeviceUtils {
    /**
     * 判断设备是否 rooted
     *
     * @return `true`: yes<br></br>`false`: no
     */
    fun isDeviceRooted(): Boolean {
        val su = "su"
        val locations = arrayOf(
            "/system/bin/",
            "/system/xbin/",
            "/sbin/",
            "/system/sd/xbin/",
            "/system/bin/failsafe/",
            "/data/local/xbin/",
            "/data/local/bin/",
            "/data/local/"
        )
        for (location in locations) {
            if (File(location + su).exists()) {
                return true
            }
        }
        return false
    }

    /**
     * 判断设备 ADB 是否可用
     *
     * @return `true`: yes<br></br>`false`: no
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun isAdbEnabled(): Boolean {
        return Settings.Secure.getInt(
            AppManager.getInstance().getApp().getContentResolver(),
            Settings.Global.ADB_ENABLED, 0
        ) > 0
    }

    /**
     * 获取设备系统版本号
     *
     * @return the version name of device's system
     */
    fun getSDKVersionName(): String {
        return Build.VERSION.RELEASE
    }

    /**
     * 获取设备系统版本码
     *
     * @return version code of device's system
     */
    fun getSDKVersionCode(): Int {
        return Build.VERSION.SDK_INT
    }

    /**
     * 获取设备 AndroidID
     *
     * @return the android id of device
     */
    @SuppressLint("HardwareIds")
    fun getAndroidID(): String {
        val id = Settings.Secure.getString(
            AppManager.getInstance().getApp().getContentResolver(),
            Settings.Secure.ANDROID_ID
        )
        return id ?: ""
    }

    /**
     * 获取设备 MAC 地址
     *
     * Must hold
     * `<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />`,
     * `<uses-permission android:name="android.permission.INTERNET" />`
     *
     * @return the MAC address
     */
    @RequiresPermission(allOf = [ACCESS_WIFI_STATE, INTERNET])
    fun getMacAddress(): String {
        return getMacAddress("")
    }

    /**
     * 获取设备 MAC 地址
     *
     * Must hold
     * `<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />`,
     * `<uses-permission android:name="android.permission.INTERNET" />`
     *
     * @return the MAC address
     */
    @RequiresPermission(allOf = [ACCESS_WIFI_STATE, INTERNET])
    fun getMacAddress(vararg excepts: String): String {
        var macAddress = getMacAddressByWifiInfo()
        if (isAddressNotInExcepts(macAddress, *excepts)) {
            return macAddress
        }
        macAddress = getMacAddressByNetworkInterface()
        if (isAddressNotInExcepts(macAddress, *excepts)) {
            return macAddress
        }
        macAddress = getMacAddressByInetAddress()
        if (isAddressNotInExcepts(macAddress, *excepts)) {
            return macAddress
        }
        macAddress = getMacAddressByFile()
        return if (isAddressNotInExcepts(macAddress, *excepts)) {
            macAddress
        } else ""
    }

    private fun isAddressNotInExcepts(address: String, vararg excepts: String): Boolean {
        if (excepts == null || excepts.size == 0) {
            return "02:00:00:00:00:00" != address
        }
        for (filter in excepts) {
            if (address == filter) {
                return false
            }
        }
        return true
    }

    @SuppressLint("HardwareIds", "MissingPermission", "WifiManagerLeak")
    private fun getMacAddressByWifiInfo(): String {
        try {
            val wifi =
                AppManager.getInstance().getApp().getSystemService(Context.WIFI_SERVICE) as WifiManager
            if (wifi != null) {
                val info = wifi.connectionInfo
                if (info != null) return info.macAddress
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return "02:00:00:00:00:00"
    }

    private fun getMacAddressByNetworkInterface(): String {
        try {
            val nis = NetworkInterface.getNetworkInterfaces()
            while (nis.hasMoreElements()) {
                val ni = nis.nextElement()
                if (ni == null || !ni.name.equals("wlan0", ignoreCase = true)) continue
                val macBytes = ni.hardwareAddress
                if (macBytes != null && macBytes.size > 0) {
                    val sb = StringBuilder()
                    for (b in macBytes) {
                        sb.append(String.format("%02x:", b))
                    }
                    return sb.substring(0, sb.length - 1)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return "02:00:00:00:00:00"
    }

    private fun getMacAddressByInetAddress(): String {
        try {
            val inetAddress = getInetAddress()
            if (inetAddress != null) {
                val ni = NetworkInterface.getByInetAddress(inetAddress)
                if (ni != null) {
                    val macBytes = ni.hardwareAddress
                    if (macBytes != null && macBytes.size > 0) {
                        val sb = StringBuilder()
                        for (b in macBytes) {
                            sb.append(String.format("%02x:", b))
                        }
                        return sb.substring(0, sb.length - 1)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return "02:00:00:00:00:00"
    }

    private fun getInetAddress(): InetAddress? {
        try {
            val nis = NetworkInterface.getNetworkInterfaces()
            while (nis.hasMoreElements()) {
                val ni = nis.nextElement()
                // To prevent phone of xiaomi return "10.0.2.15"
                if (!ni.isUp) continue
                val addresses = ni.inetAddresses
                while (addresses.hasMoreElements()) {
                    val inetAddress = addresses.nextElement()
                    if (!inetAddress.isLoopbackAddress) {
                        val hostAddress = inetAddress.hostAddress
                        if (hostAddress.indexOf(':') < 0) return inetAddress
                    }
                }
            }
        } catch (e: SocketException) {
            e.printStackTrace()
        }

        return null
    }

    private fun getMacAddressByFile(): String {
        var result: ShellUtils.CommandResult = ShellUtils.execCmd("getprop wifi.interface", false)
        if (result.result == 0) {
            val name = result.successMsg
            if (name != null) {
                result = ShellUtils.execCmd("cat /sys/class/net/$name/address", false)
                if (result.result == 0) {
                    val address = result.successMsg
                    if (address != null && address.length > 0) {
                        return address
                    }
                }
            }
        }
        return "02:00:00:00:00:00"
    }

    /**
     * 获取设备厂商
     *
     * e.g. Xiaomi
     *
     * @return the manufacturer of the product/hardware
     */
    fun getManufacturer(): String {
        return Build.MANUFACTURER
    }

    /**
     * 获取设备型号
     *
     * e.g. MI2SC
     *
     * @return the model of device
     */
    fun getModel(): String {
        var model: String? = Build.MODEL
        if (model != null) {
            model = model.trim { it <= ' ' }.replace("\\s*".toRegex(), "")
        } else {
            model = ""
        }
        return model
    }

    /**
     * 获取设备 ABIs
     *
     * @return an ordered list of ABIs supported by this device
     */
    fun getABIs(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Build.SUPPORTED_ABIS
        } else {
            if (!TextUtils.isEmpty(Build.CPU_ABI2)) {
                arrayOf(Build.CPU_ABI, Build.CPU_ABI2)
            } else arrayOf(Build.CPU_ABI)
        }
    }

    /**
     * 关机
     *
     * Requires root permission
     * or hold `android:sharedUserId="android.uid.system"`,
     * `<uses-permission android:name="android.permission.SHUTDOWN/>`
     * in manifest.
     */
    fun shutdown() {
        ShellUtils.execCmd("reboot -p", true)
        val intent = Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN")
        intent.putExtra("android.intent.extra.KEY_CONFIRM", false)
        AppManager.getInstance().getApp()
            .startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    /**
     * 重启
     *
     * Requires root permission
     * or hold `android:sharedUserId="android.uid.system"` in manifest.
     */
    fun reboot() {
        ShellUtils.execCmd("reboot", true)
        val intent = Intent(Intent.ACTION_REBOOT)
        intent.putExtra("nowait", 1)
        intent.putExtra("interval", 1)
        intent.putExtra("window", 0)
        AppManager.getInstance().getApp().sendBroadcast(intent)
    }

    /**
     * 重启
     *
     * Requires root permission
     * or hold `android:sharedUserId="android.uid.system"`,
     * `<uses-permission android:name="android.permission.REBOOT" />`
     *
     * @param reason code to pass to the kernel (e.g., "recovery") to
     * request special boot modes, or null.
     */
    fun reboot(reason: String) {
        val pm =
            AppManager.getInstance().getApp().getSystemService(Context.POWER_SERVICE) as PowerManager

        pm.reboot(reason)
    }

    /**
     * 重启到 recovery
     *
     * Requires root permission.
     */
    fun reboot2Recovery() {
        ShellUtils.execCmd("reboot recovery", true)
    }

    /**
     * 重启到 bootloader
     *
     * Requires root permission.
     */
    fun reboot2Bootloader() {
        ShellUtils.execCmd("reboot bootloader", true)
    }
}