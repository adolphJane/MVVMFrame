package com.magicalrice.project.library_base.base

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.format.Formatter
import android.util.Log
import androidx.annotation.RequiresPermission
import com.magicalrice.project.library_base.advanced.ShellUtils
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.net.UnknownHostException
import java.util.*

/**
 * @package com.magicalrice.project.library_base.base
 * @author Adolph
 * @date 2019-04-22 Mon
 * @description TODO
 */

class NetworkUtils {
    enum class NetworkType {
        NETWORK_ETHERNET,
        NETWORK_WIFI,
        NETWORK_4G,
        NETWORK_3G,
        NETWORK_2G,
        NETWORK_UNKNOWN,
        NETWORK_NO
    }

    interface Callback {
        fun call(isSuccess: Boolean)
    }

    companion object {
        /**
         * 打开网络设置界面
         */
        fun openWirelessSettings() {
            AppManager.getInstance().getApp().startActivity(
                Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }

        /**
         * 判断网络是否连接
         *
         */
        @RequiresPermission(ACCESS_NETWORK_STATE)
        fun isConnected(): Boolean {
            val info = getActiveNetworkInfo()
            return info != null && info.isConnected
        }

        /**
         * 判断网络是否可用
         */
        @RequiresPermission(INTERNET)
        fun isAvailableByPing(): Boolean {
            return isAvailableByPing(null)
        }

        /**
         * 判断网络是否可用
         */
        @RequiresPermission(INTERNET)
        fun isAvailableByPing(ip: String?): Boolean {
            var ip = ip
            if (ip == null || ip.isEmpty()) {
                ip = "223.5.5.5"// default ping ip
            }
            val result = ShellUtils.execCmd(String.format("ping -c 1 %s", ip), false)
            val ret = result.result == 0
            if (result.errorMsg != null) {
                Log.d("NetworkUtils", "isAvailableByPing() called" + result.errorMsg)
            }
            if (result.successMsg != null) {
                Log.d("NetworkUtils", "isAvailableByPing() called" + result.successMsg)
            }
            return ret
        }


        @RequiresPermission(INTERNET)
        fun isAvailableByDns(ip: String) {

        }

        /**
         * 判断移动数据是否打开
         */
        fun getMobileDataEnabled(): Boolean {
            try {
                val tm =
                    AppManager.getInstance().getApp().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                        ?: return false
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    return tm.isDataEnabled
                }
                @SuppressLint("PrivateApi")
                val getMobileDataEnabledMethod = tm.javaClass.getDeclaredMethod("getDataEnabled")
                if (null != getMobileDataEnabledMethod) {
                    return getMobileDataEnabledMethod.invoke(tm) as Boolean
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

            return false
        }

        /**
         * 打开或关闭移动数据
         */
        @RequiresPermission(MODIFY_PHONE_STATE)
        fun setMobileDataEnabled(enabled: Boolean) {
            try {
                val tm =
                    AppManager.getInstance().getApp().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                        ?: return
                val setMobileDataEnabledMethod = tm.javaClass.getDeclaredMethod(
                    "setDataEnabled",
                    Boolean::class.javaPrimitiveType!!
                )
                setMobileDataEnabledMethod?.invoke(tm, enabled)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        /**
         * 判断网络是否是移动数据
         */
        @RequiresPermission(ACCESS_NETWORK_STATE)
        fun isMobileData(): Boolean {
            val info = getActiveNetworkInfo()
            return (null != info
                    && info.isAvailable
                    && info.type == ConnectivityManager.TYPE_MOBILE)
        }

        /**
         * 判断网络是否是 4G
         */
        @RequiresPermission(ACCESS_NETWORK_STATE)
        fun is4G(): Boolean {
            val info = getActiveNetworkInfo()
            return (info != null
                    && info.isAvailable
                    && info.subtype == TelephonyManager.NETWORK_TYPE_LTE)
        }

        /**
         * 判断 wifi 是否打开
         */
        @RequiresPermission(ACCESS_WIFI_STATE)
        fun getWifiEnabled(): Boolean {
            @SuppressLint("WifiManagerLeak")
            val manager =
                AppManager.getInstance().getApp().getSystemService(Context.WIFI_SERVICE) as WifiManager
            return manager.isWifiEnabled
        }

        /**
         * 打开或关闭 wifi
         */
        @RequiresPermission(CHANGE_WIFI_STATE)
        fun setWifiEnabled(enabled: Boolean) {
            @SuppressLint("WifiManagerLeak")
            val manager =
                AppManager.getInstance().getApp().getSystemService(Context.WIFI_SERVICE) as WifiManager
                    ?: return
            if (enabled == manager.isWifiEnabled) return
            manager.isWifiEnabled = enabled
        }

        /**
         * 判断 wifi 是否连接状态
         */
        @RequiresPermission(ACCESS_NETWORK_STATE)
        fun isWifiConnected(): Boolean {
            val cm =
                AppManager.getInstance().getApp().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val ni = cm.activeNetworkInfo
            return ni != null && ni.type == ConnectivityManager.TYPE_WIFI
        }

        /**
         * 判断 wifi 数据是否可用
         */
        @RequiresPermission(allOf = [ACCESS_WIFI_STATE, INTERNET])
        fun isWifiAvailable(): Boolean {
            return getWifiEnabled() && isAvailableByPing()
        }

        /**
         * 获取移动网络运营商名称
         */
        fun getNetworkOperatorName(): String {
            val tm =
                AppManager.getInstance().getApp().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            return tm.networkOperatorName
        }

        /**
         * 获取当前网络类型
         *
         *  * [NetworkType.NETWORK_ETHERNET]
         *  * [NetworkType.NETWORK_WIFI]
         *  * [NetworkType.NETWORK_4G]
         *  * [NetworkType.NETWORK_3G]
         *  * [NetworkType.NETWORK_2G]
         *  * [NetworkType.NETWORK_UNKNOWN]
         *  * [NetworkType.NETWORK_NO]
         *
         */
        @RequiresPermission(ACCESS_NETWORK_STATE)
        fun getNetworkType(): NetworkType {
            var netType = NetworkType.NETWORK_NO
            val info = getActiveNetworkInfo()
            if (info != null && info.isAvailable) {
                if (info.type == ConnectivityManager.TYPE_ETHERNET) {
                    netType = NetworkType.NETWORK_ETHERNET
                } else if (info.type == ConnectivityManager.TYPE_WIFI) {
                    netType = NetworkType.NETWORK_WIFI
                } else if (info.type == ConnectivityManager.TYPE_MOBILE) {
                    when (info.subtype) {

                        TelephonyManager.NETWORK_TYPE_GSM, TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_IDEN -> netType =
                            NetworkType.NETWORK_2G

                        TelephonyManager.NETWORK_TYPE_TD_SCDMA, TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_EVDO_B, TelephonyManager.NETWORK_TYPE_EHRPD, TelephonyManager.NETWORK_TYPE_HSPAP -> netType =
                            NetworkType.NETWORK_3G

                        TelephonyManager.NETWORK_TYPE_IWLAN, TelephonyManager.NETWORK_TYPE_LTE -> netType =
                            NetworkType.NETWORK_4G
                        else -> {

                            val subtypeName = info.subtypeName
                            if (subtypeName.equals("TD-SCDMA", ignoreCase = true)
                                || subtypeName.equals("WCDMA", ignoreCase = true)
                                || subtypeName.equals("CDMA2000", ignoreCase = true)
                            ) {
                                netType = NetworkType.NETWORK_3G
                            } else {
                                netType = NetworkType.NETWORK_UNKNOWN
                            }
                        }
                    }
                } else {
                    netType = NetworkType.NETWORK_UNKNOWN
                }
            }
            return netType
        }

        @RequiresPermission(ACCESS_NETWORK_STATE)
        private fun getActiveNetworkInfo(): NetworkInfo? {
            val cm =
                AppManager.getInstance().getApp().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return cm.activeNetworkInfo
        }

        /**
         * 获取 IP 地址
         * @param useIPv4 True to use ipv4, false otherwise.
         * @return the ip address
         */
        @RequiresPermission(INTERNET)
        fun getIPAddress(useIPv4: Boolean): String {
            try {
                val nis = NetworkInterface.getNetworkInterfaces()
                val adds = LinkedList<InetAddress>()
                while (nis.hasMoreElements()) {
                    val ni = nis.nextElement()
                    // To prevent phone of xiaomi return "10.0.2.15"
                    if (!ni.isUp || ni.isLoopback) continue
                    val addresses = ni.inetAddresses
                    while (addresses.hasMoreElements()) {
                        adds.addFirst(addresses.nextElement())
                    }
                }
                for (add in adds) {
                    if (!add.isLoopbackAddress) {
                        val hostAddress = add.hostAddress
                        val isIPv4 = hostAddress.indexOf(':') < 0
                        if (useIPv4) {
                            if (isIPv4) return hostAddress
                        } else {
                            if (!isIPv4) {
                                val index = hostAddress.indexOf('%')
                                return if (index < 0)
                                    hostAddress.toUpperCase()
                                else
                                    hostAddress.substring(0, index).toUpperCase()
                            }
                        }
                    }
                }
            } catch (e: SocketException) {
                e.printStackTrace()
            }

            return ""
        }

        /**
         * 返回广播IP地址
         *
         * @return the ip address of broadcast
         */
        fun getBroadcastIpAddress(): String {
            try {
                val nis = NetworkInterface.getNetworkInterfaces()
                val adds = LinkedList<InetAddress>()
                while (nis.hasMoreElements()) {
                    val ni = nis.nextElement()
                    if (!ni.isUp || ni.isLoopback) continue
                    val ias = ni.interfaceAddresses
                    var i = 0
                    val size = ias.size
                    while (i < size) {
                        val ia = ias[i]
                        val broadcast = ia.broadcast
                        if (broadcast != null) {
                            return broadcast.hostAddress
                        }
                        i++
                    }
                }
            } catch (e: SocketException) {
                e.printStackTrace()
            }

            return ""
        }

        /**
         * 获取域名 IP 地址
         */
        @RequiresPermission(INTERNET)
        fun getDomainAddress(domain: String): String {
            val inetAddress: InetAddress
            return try {
                inetAddress = InetAddress.getByName(domain)
                inetAddress.hostAddress
            } catch (e: UnknownHostException) {
                e.printStackTrace()
                ""
            }

        }

        /**
         * 根据 WiFi 获取网络 IP 地址
         */
        @RequiresPermission(ACCESS_WIFI_STATE)
        fun getIpAddressByWifi(): String {
            @SuppressLint("WifiManagerLeak")
            val wm =
                AppManager.getInstance().getApp().getSystemService(Context.WIFI_SERVICE) as WifiManager
                    ?: return ""
            return Formatter.formatIpAddress(wm.dhcpInfo.ipAddress)
        }

        /**
         * 根据 WiFi 获取网关 IP 地址
         */
        @RequiresPermission(ACCESS_WIFI_STATE)
        fun getGatewayByWifi(): String {
            @SuppressLint("WifiManagerLeak")
            val wm =
                AppManager.getInstance().getApp().getSystemService(Context.WIFI_SERVICE) as WifiManager
            return Formatter.formatIpAddress(wm.dhcpInfo.gateway)
        }

        /**
         * 根据 WiFi 获取子网掩码 IP 地址
         */
        @RequiresPermission(ACCESS_WIFI_STATE)
        fun getNetMaskByWifi(): String {
            @SuppressLint("WifiManagerLeak")
            val wm =
                AppManager.getInstance().getApp().getSystemService(Context.WIFI_SERVICE) as WifiManager
            return Formatter.formatIpAddress(wm.dhcpInfo.netmask)
        }

        /**
         * 根据 WiFi 获取服务端 IP 地址
         */
        @RequiresPermission(ACCESS_WIFI_STATE)
        fun getServerAddressByWifi(): String {
            @SuppressLint("WifiManagerLeak")
            val wm =
                AppManager.getInstance().getApp().getSystemService(Context.WIFI_SERVICE) as WifiManager
            return Formatter.formatIpAddress(wm.dhcpInfo.serverAddress)
        }
    }
}