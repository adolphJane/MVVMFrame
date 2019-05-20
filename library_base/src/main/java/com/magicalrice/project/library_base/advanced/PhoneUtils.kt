package com.magicalrice.project.library_base.advanced

import android.Manifest
import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.bluetooth.BluetoothAdapter
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.provider.Settings
import android.telephony.CellLocation
import android.telephony.SmsManager
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.view.WindowManager
import androidx.annotation.RequiresPermission
import com.magicalrice.project.library_base.base.AppManager
import com.magicalrice.project.library_base.base.ToastUtils
import com.magicalrice.project.library_base.base.log.LogUtils
import java.io.File
import java.util.*

/**
 * @package com.magicalrice.project.library_base.advanced
 * @author Adolph
 * @date 2019-04-22 Mon
 * @description TODO
 */

object PhoneUtils {
    /**
     * 判断设备是否是手机
     *
     * @return `true`: yes<br></br>`false`: no
     */
    fun isPhone(): Boolean {
        val tm =
            AppManager.getInstance().getApp().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        return tm.phoneType != TelephonyManager.PHONE_TYPE_NONE
    }

    /**
     * 获取设备码
     *
     * Must hold
     * `<uses-permission android:name="android.permission.READ_PHONE_STATE" />`
     *
     * @return the unique device id
     */
    @SuppressLint("HardwareIds")
    @RequiresPermission(READ_PHONE_STATE)
    fun getDeviceId(): String {
        val tm =
            AppManager.getInstance().getApp().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val imei = tm.imei
            if (!TextUtils.isEmpty(imei)) return imei
            val meid = tm.meid
            return if (TextUtils.isEmpty(meid)) "" else meid

        }

        return tm.deviceId
    }

    /**
     * 获取序列号
     *
     * @return the serial of device
     */
    @SuppressLint("HardwareIds")
    @RequiresPermission(READ_PHONE_STATE)
    fun getSerial(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Build.getSerial() else Build.SERIAL
    }

    /**
     * 获取 IMEI 码
     *
     * Must hold
     * `<uses-permission android:name="android.permission.READ_PHONE_STATE" />`
     *
     * @return the IMEI
     */
    @SuppressLint("HardwareIds")
    @RequiresPermission(READ_PHONE_STATE)
    fun getIMEI(): String {
        val tm =
            AppManager.getInstance().getApp().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            tm.imei
        } else tm.deviceId

    }

    /**
     * 获取 MEID 码
     *
     * Must hold
     * `<uses-permission android:name="android.permission.READ_PHONE_STATE" />`
     *
     * @return the MEID
     */
    @SuppressLint("HardwareIds")
    @RequiresPermission(READ_PHONE_STATE)
    fun getMEID(): String {
        val tm =
            AppManager.getInstance().getApp().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            tm.meid
        } else tm.deviceId

    }

    /**
     * 获取 IMSI 码
     *
     * Must hold
     * `<uses-permission android:name="android.permission.READ_PHONE_STATE" />`
     *
     * @return the IMSI
     */
    @SuppressLint("HardwareIds")
    @RequiresPermission(READ_PHONE_STATE)
    fun getIMSI(): String {
        val tm =
            AppManager.getInstance().getApp().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        return tm.subscriberId
    }

    /**
     * 获取移动终端类型
     *
     * @return the current phone type
     *
     *  * [TelephonyManager.PHONE_TYPE_NONE]
     *  * [TelephonyManager.PHONE_TYPE_GSM]
     *  * [TelephonyManager.PHONE_TYPE_CDMA]
     *  * [TelephonyManager.PHONE_TYPE_SIP]
     *
     */
    fun getPhoneType(): Int {
        val tm =
            AppManager.getInstance().getApp().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        return tm.phoneType
    }

    /**
     * 判断 sim 卡是否准备好
     *
     * @return `true`: yes<br></br>`false`: no
     */
    fun isSimCardReady(): Boolean {
        val tm =
            AppManager.getInstance().getApp().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        return tm.simState == TelephonyManager.SIM_STATE_READY
    }

    /**
     * 获取 Sim 卡运营商名称
     *
     * @return the sim operator name
     */
    fun getSimOperatorName(): String {
        val tm =
            AppManager.getInstance().getApp().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        return tm.simOperatorName
    }

    /**
     * 获取 Sim 卡运营商名称
     *
     * @return the sim operator
     */
    fun getSimOperatorByMnc(): String {
        val tm =
            AppManager.getInstance().getApp().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        val operator = tm.simOperator ?: return ""
        when (operator) {
            "46000", "46002", "46007", "46020" -> return "中国移动"
            "46001", "46006", "46009" -> return "中国联通"
            "46003", "46005", "46011" -> return "中国电信"
            else -> return operator
        }
    }

    /**
     * 获取手机状态信息
     *
     * Must hold
     * `<uses-permission android:name="android.permission.READ_PHONE_STATE" />`
     *
     * @return DeviceId = 99000311726612<br></br>
     * DeviceSoftwareVersion = 00<br></br>
     * Line1Number =<br></br>
     * NetworkCountryIso = cn<br></br>
     * NetworkOperator = 46003<br></br>
     * NetworkOperatorName = 中国电信<br></br>
     * NetworkType = 6<br></br>
     * PhoneType = 2<br></br>
     * SimCountryIso = cn<br></br>
     * SimOperator = 46003<br></br>
     * SimOperatorName = 中国电信<br></br>
     * SimSerialNumber = 89860315045710604022<br></br>
     * SimState = 5<br></br>
     * SubscriberId(IMSI) = 460030419724900<br></br>
     * VoiceMailNumber = *86<br></br>
     */
    @SuppressLint("HardwareIds")
    @RequiresPermission(READ_PHONE_STATE)
    fun getPhoneStatus(): String {
        val tm =
            AppManager.getInstance().getApp().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        var str = ""

        str += "DeviceId(IMEI) = " + tm.deviceId + "\n"
        str += "DeviceSoftwareVersion = " + tm.deviceSoftwareVersion + "\n"
        str += "Line1Number = " + tm.line1Number + "\n"
        str += "NetworkCountryIso = " + tm.networkCountryIso + "\n"
        str += "NetworkOperator = " + tm.networkOperator + "\n"
        str += "NetworkOperatorName = " + tm.networkOperatorName + "\n"
        str += "NetworkType = " + tm.networkType + "\n"
        str += "PhoneType = " + tm.phoneType + "\n"
        str += "SimCountryIso = " + tm.simCountryIso + "\n"
        str += "SimOperator = " + tm.simOperator + "\n"
        str += "SimOperatorName = " + tm.simOperatorName + "\n"
        str += "SimSerialNumber = " + tm.simSerialNumber + "\n"
        str += "SimState = " + tm.simState + "\n"
        str += "SubscriberId(IMSI) = " + tm.subscriberId + "\n"
        str += "VoiceMailNumber = " + tm.voiceMailNumber + "\n"
        return str
    }

    /**
     * 跳至拨号界面
     *
     * @param phoneNumber The phone number.
     * @return `true`: operate successfully<br></br>`false`: otherwise
     */
    fun dial(phoneNumber: String): Boolean {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
        if (isIntentAvailable(intent)) {
            AppManager.getInstance().getApp()
                .startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            return true
        }
        return false
    }

    /**
     * 拨打 phoneNumber
     *
     * Must hold `<uses-permission android:name="android.permission.CALL_PHONE" />`
     *
     * @param phoneNumber The phone number.
     * @return `true`: operate successfully<br></br>`false`: otherwise
     */
    @RequiresPermission(CALL_PHONE)
    fun call(phoneNumber: String): Boolean {
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber"))
        if (isIntentAvailable(intent)) {
            AppManager.getInstance().getApp()
                .startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            return true
        }
        return false
    }

    /**
     * 跳至发送短信界面
     *
     * @param phoneNumber The phone number.
     * @param content     The content.
     * @return `true`: operate successfully<br></br>`false`: otherwise
     */
    fun sendSms(phoneNumber: String, content: String): Boolean {
        val uri = Uri.parse("smsto:$phoneNumber")
        val intent = Intent(Intent.ACTION_SENDTO, uri)
        if (isIntentAvailable(intent)) {
            intent.putExtra("sms_body", content)
            AppManager.getInstance().getApp()
                .startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            return true
        }
        return false
    }

    /**
     * 发送短信
     *
     * Must hold `<uses-permission android:name="android.permission.SEND_SMS" />`
     *
     * @param phoneNumber The phone number.
     * @param content     The content.
     */
    @RequiresPermission(SEND_SMS)
    fun sendSmsSilent(phoneNumber: String, content: String) {
        if (TextUtils.isEmpty(content)) return
        val sentIntent =
            PendingIntent.getBroadcast(AppManager.getInstance().getApp(), 0, Intent("send"), 0)
        val smsManager = SmsManager.getDefault()
        if (content.length >= 70) {
            val ms = smsManager.divideMessage(content)
            for (str in ms) {
                smsManager.sendTextMessage(phoneNumber, null, str, sentIntent, null)
            }
        } else {
            smsManager.sendTextMessage(phoneNumber, null, content, sentIntent, null)
        }
    }

    private fun isIntentAvailable(intent: Intent): Boolean {
        return AppManager.getInstance().getApp()
            .packageManager
            .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            .size > 0
    }

    /**
     * 获取手机型号
     *
     * @param context  上下文
     * @return   String
     */
    fun getMobileModel(context: Context): String {
        try {
            return Build.MODEL
        } catch (e: Exception) {
            return "未知"
        }

    }

    /**
     * 获取手机品牌
     *
     * @param context  上下文
     * @return  String
     */
    fun getMobileBrand(context: Context): String {
        try {
            return Build.BRAND
        } catch (e: Exception) {
            return "未知"
        }

    }

    /**
     * 获取sd卡剩余空间的大小
     */
    fun getSDFreeSize(): Long {
        val path = Environment.getExternalStorageDirectory() // 取得SD卡文件路径
        val sf = StatFs(path.path)
        val blockSize = sf.blockSize.toLong() // 获取单个数据块的大小(Byte)
        val freeBlocks = sf.availableBlocks.toLong()// 空闲的数据块的数量
        // 返回SD卡空闲大小
        return freeBlocks * blockSize / 1024 / 1024 // 单位MB
    }

    /**
     * 获取sd卡空间的总大小
     */
    fun getSDAllSize(): Long {
        val path = Environment.getExternalStorageDirectory() // 取得SD卡文件路径
        val sf = StatFs(path.path)
        val blockSize = sf.blockSize.toLong() // 获取单个数据块的大小(Byte)
        val allBlocks = sf.blockCount.toLong() // 获取所有数据块数
        // 返回SD卡大小
        return allBlocks * blockSize / 1024 / 1024 // 单位MB
    }

    /**
     * 获取手机内安装的应用
     */
    fun getInstalledApp(context: Context): MutableList<PackageInfo> {
        val pm = context.packageManager
        return pm.getInstalledPackages(0)
    }

    /**
     * 获取手机安装非系统应用
     */
    fun getUserInstalledApp(context: Context): List<PackageInfo> {
        var infos: MutableList<PackageInfo> = getInstalledApp(context)
        val apps = ArrayList<PackageInfo>()
        for (info in infos) {
            if (info.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM <= 0) {
                apps.add(info)
            }
        }
        infos.clear()
        return apps
    }

    /**
     * 获取安装应用的信息
     */
    fun getInstalledAppInfo(
        context: Context,
        info: PackageInfo
    ): Map<String, Any> {
        val appInfos = HashMap<String, Any>()
        val pm = context.packageManager
        val aif = info.applicationInfo
        appInfos["icon"] = pm.getApplicationIcon(aif)
        appInfos["lable"] = pm.getApplicationLabel(aif)
        appInfos["packageName"] = aif.packageName
        return appInfos
    }

    /**
     * 打开指定包名的应用
     */
    fun startAppPkg(context: Context, pkg: String) {
        val startIntent = context.packageManager
            .getLaunchIntentForPackage(pkg)
        startIntent!!.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(startIntent)
    }

    /**
     * 跳转至系统设置界面
     *
     * @param mContext
     * 上下文
     */
    fun toSettingActivity(mContext: Context) {
        val settingsIntent = Intent(Settings.ACTION_SETTINGS)
        mContext.startActivity(settingsIntent)
    }

    /**
     * 跳转至WIFI设置界面
     *
     * @param mContext
     * 上下文
     */
    fun toWIFISettingActivity(mContext: Context) {
        val wifiSettingsIntent = Intent(Settings.ACTION_WIFI_SETTINGS)
        mContext.startActivity(wifiSettingsIntent)
    }

    /**
     * 启动本地应用打开PDF
     *
     * @param mContext
     * 上下文
     * @param filePath
     * 文件路径
     */
    fun openPDFFile(mContext: Context, filePath: String) {
        try {
            val file = File(filePath)
            if (file.exists()) {
                val path = Uri.fromFile(file)
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(path, "application/pdf")
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                mContext.startActivity(intent)
            }
        } catch (e: Exception) {
            ToastUtils.showShort("未检测到可打开PDF相关软件")
        }

    }

    /**
     * 启动本地应用打开PDF
     *
     * @param mContext
     * 上下文
     * @param filePath
     * 文件路径
     */
    fun openWordFile(mContext: Context, filePath: String) {
        try {
            val file = File(filePath)
            if (file.exists()) {
                val path = Uri.fromFile(file)
                val intent = Intent("android.intent.action.VIEW")
                intent.addCategory("android.intent.category.DEFAULT")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.setDataAndType(path, "application/msword")
                mContext.startActivity(intent)
            }
        } catch (e: Exception) {
            ToastUtils.showShort("未检测到可打开Word文档相关软件")
        }

    }

    /**
     * 调用WPS打开office文档 http://bbs.wps.cn/thread-22349340-1-1.html
     *
     * @param mContext
     * 上下文
     * @param filePath
     * 文件路径
     */
    fun openOfficeByWPS(mContext: Context, filePath: String) {

        try {

            // 文件存在性检查
            val file = File(filePath)
            if (!file.exists()) {
                ToastUtils.showShort(filePath + "文件路径不存在")
                return
            }

            // 检查是否安装WPS
            val wpsPackageEng = "cn.wps.moffice_eng"// 普通版与英文版一样
            // String wpsActivity =
            // "cn.wps.moffice.documentmanager.PreStartActivity";
            val wpsActivity2 = "cn.wps.moffice.documentmanager.PreStartActivity2"// 默认第三方程序启动

            val intent = Intent()
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.setClassName(wpsPackageEng, wpsActivity2)

            val uri = Uri.fromFile(File(filePath))
            intent.data = uri
            mContext.startActivity(intent)

        } catch (e: ActivityNotFoundException) {
            ToastUtils.showShort("本地未安装WPS")
        } catch (e: Exception) {
            ToastUtils.showShort("打开文档失败")
        }

    }

    /**
     * 获得电话管理实例对象
     * @param content  上下文
     * @return TelephonyManager 电话管理实例对象
     */
    private fun getSysTelephonyManager(content: Context): TelephonyManager? {
        var telephonyManager: TelephonyManager? = null
        telephonyManager = content.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        LogUtils.i("AppSysMgr-->>getSysTelephonyManager", telephonyManager.toString() + "")
        return telephonyManager
    }

    /**
     * 获取手机状态(0：无活动 1：响铃 2：待机)
     * @param  context 上下文
     * @return Integer 手机状态
     */
    fun getSysPhoneState(context: Context): Int? {
        val callState = getSysTelephonyManager(context)!!.callState
        LogUtils.i("AppSysMgr-->>getSysPhoneState", callState.toString() + "")
        return callState
    }


    /**
     * 获得手机方位
     * @param context 上下文
     * @return CellLocation 手机方位
     */
    @RequiresPermission(ACCESS_FINE_LOCATION)
    fun getSysPhoneLoaction(context: Context): CellLocation {
        val cellLocation = getSysTelephonyManager(context)!!.cellLocation
        LogUtils.i("AppSysMgr-->>getSysPhoneLoaction", cellLocation.toString() + "")
        return cellLocation
    }

    /**
     * 获取SIM序列号
     * @param ctx
     * @return
     */
    @RequiresPermission(READ_PHONE_STATE)
    fun getSIMSerial(ctx: Context): String {
        val tm = ctx.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return tm.simSerialNumber
    }

    /**
     * 获取网络运营商 46000,46002,46007 中国移动,46001 中国联通,46003 中国电信
     * @param ctx
     * @return
     */
    fun getMNC(ctx: Context): String {
        var providersName = ""
        val telephonyManager = ctx.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (telephonyManager.simState == TelephonyManager.SIM_STATE_READY) {
            providersName = telephonyManager.simOperator
        }
        return providersName
    }

    /**
     * 获取网络运营商：中国电信,中国移动,中国联通
     * @param ctx
     * @return
     */
    fun getCarrier(ctx: Context): String {
        val tm = ctx.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return tm.networkOperatorName.toLowerCase(Locale.getDefault())
    }

    /**
     * 获取硬件型号
     * @return
     */
    fun getModel(): String {
        return Build.MODEL
    }

    /**
     * 获取编译厂商
     * @return
     */
    fun getBuildBrand(): String {
        return Build.BRAND
    }

    /**
     * 获取编译服务器主机
     * @return
     */
    fun getBuildHost(): String {
        return Build.HOST
    }

    /**
     * 获取描述Build的标签
     * @return
     */
    fun getBuildTags(): String {
        return Build.TAGS
    }

    /**
     * 获取系统编译时间
     * @return
     */
    fun getBuildTime(): Long {
        return Build.TIME
    }

    /**
     * 获取系统编译作者
     * @return
     */
    fun getBuildUser(): String {
        return Build.USER
    }

    /**
     * 获取编译系统版本(5.1)
     * @return
     */
    fun getBuildVersionRelease(): String {
        return Build.VERSION.RELEASE
    }

    /**
     * 获取开发代号
     * @return
     */
    fun getBuildVersionCodename(): String {
        return Build.VERSION.CODENAME
    }

    /**
     * 获取源码控制版本号
     * @return
     */
    fun getBuildVersionIncremental(): String {
        return Build.VERSION.INCREMENTAL
    }

    /**
     * 获取编译的SDK
     * @return
     */
    fun getBuildVersionSDK(): Int {
        return Build.VERSION.SDK_INT
    }

    /**
     * 获取修订版本列表(LMY47D)
     * @return
     */
    fun getBuildID(): String {
        return Build.ID
    }

    /**
     * CPU指令集
     * @return
     */
    fun getSupportedABIS(): Array<String> {
        var result: Array<String>? = arrayOf("-")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            result = Build.SUPPORTED_ABIS
        }
        if (result == null || result.size == 0) {
            result = arrayOf("-")
        }
        return result
    }

    /**
     * 获取硬件制造厂商
     * @return
     */
    fun getManufacturer(): String {
        return Build.MANUFACTURER
    }

    /**
     * 获取系统启动程序版本号
     * @return
     */
    fun getBootloader(): String {
        return Build.BOOTLOADER
    }

    /**
     *
     * @param ctx
     * @return
     */
    fun getScreenDisplayID(ctx: Context): String {
        val wm = ctx.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return wm.defaultDisplay.displayId.toString()
    }

    /**
     * 获取系统版本号
     * @return
     */
    fun getDisplayVersion(): String {
        return Build.DISPLAY
    }

    /**
     * 获取语言
     * @return
     */
    fun getLanguage(): String {
        return Locale.getDefault().language
    }

    /**
     * 获取国家
     * @param ctx
     * @return
     */
    fun getCountry(ctx: Context): String {
        val tm = ctx.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val locale = Locale.getDefault()
        return if (tm.simState == TelephonyManager.SIM_STATE_READY) tm.simCountryIso.toLowerCase(
            Locale.getDefault()
        ) else locale.country.toLowerCase(locale)
    }

    /**
     * 获取系统版本:5.1.1
     * @return
     */
    fun getOSVersion(): String {
        return Build.VERSION.RELEASE
    }

    /**
     * 获取GSF序列号
     * @param context
     * @return
     */
    //<uses-permission android:name="com.google.android.providers.gsf.permission.READ_GSERVICES"/>
    fun getGSFID(context: Context): String? {
        val result: String
        val URI = Uri.parse("content://com.google.android.gsf.gservices")
        val ID_KEY = "android_id"
        val params = arrayOf(ID_KEY)
        val c = context.contentResolver.query(URI, null, null, params, null)
        if (c == null || !c.moveToFirst() || c.columnCount < 2) {
            return null
        } else {
            result = java.lang.Long.toHexString(java.lang.Long.parseLong(c.getString(1)))
        }
        c.close()
        return result
    }

    /**
     * 获取蓝牙地址
     * @param context
     * <uses-permission android:name="android.permission.BLUETOOTH"/>
     * @return
     */
    @SuppressLint("MissingPermission")
    fun getBluetoothMAC(context: Context): String? {
        var result: String? = null
        try {
            if (context.checkCallingOrSelfPermission(Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED) {
                val bta = BluetoothAdapter.getDefaultAdapter()
                result = bta.address
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result
    }

    /**
     * Android设备物理唯一标识符
     * @return
     */
    fun getPsuedoUniqueID(): String {
        var devIDShort = "35" + Build.BOARD.length % 10 + Build.BRAND.length % 10
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            devIDShort += Build.SUPPORTED_ABIS[0].length % 10
        } else {
            devIDShort += Build.CPU_ABI.length % 10
        }
        devIDShort += Build.DEVICE.length % 10 + Build.MANUFACTURER.length % 10 + Build.MODEL.length % 10 + Build.PRODUCT.length % 10
        var serial: String
        try {
            serial = Build::class.java.getField("SERIAL").get(null).toString()
            return UUID(devIDShort.hashCode().toLong(), serial.hashCode().toLong()).toString()
        } catch (e: Exception) {
            serial = "ESYDV000"
        }

        return UUID(devIDShort.hashCode().toLong(), serial.hashCode().toLong()).toString()
    }

    /**
     * 构建标识,包括brand,name,device,version.release,id,version.incremental,type,tags这些信息
     * @return
     */
    fun getFingerprint(): String {
        return Build.FINGERPRINT
    }

    /**
     * 获取硬件信息
     * @return
     */
    fun getHardware(): String {
        return Build.HARDWARE
    }

    /**
     * 获取产品信息
     * @return
     */
    fun getProduct(): String {
        return Build.PRODUCT
    }

    /**
     * 获取设备信息
     * @return
     */
    fun getDevice(): String {
        return Build.DEVICE
    }

    /**
     * 获取主板信息
     * @return
     */
    fun getBoard(): String {
        return Build.BOARD
    }

    /**
     * 获取基带版本(无线电固件版本 Api14以上)
     * @return
     */
    fun getRadioVersion(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) Build.getRadioVersion() else ""
    }
}