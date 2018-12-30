package com.adolph.project.advancedutils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.CellLocation;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.WindowManager;
import androidx.annotation.RequiresPermission;
import com.adolph.project.baseutils.AppManageUtils;
import com.adolph.project.baseutils.LogUtils;
import com.adolph.project.baseutils.ToastUtils;

import java.io.File;
import java.util.*;

import static android.Manifest.permission.*;

/**
 * 手机相关
 *
 * isPhone            : 判断设备是否是手机
 * getDeviceId        : 获取设备码
 * getSerial          : 获取序列号
 * getIMEI            : 获取 IMEI 码
 * getMEID            : 获取 MEID 码
 * getIMSI            : 获取 IMSI 码
 * getPhoneType       : 获取移动终端类型
 * isSimCardReady     : 判断 sim 卡是否准备好
 * getSimOperatorName : 获取 Sim 卡运营商名称
 * getSimOperatorByMnc: 获取 Sim 卡运营商名称
 * getPhoneStatus     : 获取手机状态信息
 * dial               : 跳至拨号界面
 * call               : 拨打 phoneNumber
 * sendSms            : 跳至发送短信界面
 * sendSmsSilent      : 发送短信
 */
public final class PhoneUtils {

    private PhoneUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 判断设备是否是手机
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isPhone() {
        TelephonyManager tm =
                (TelephonyManager) AppManageUtils.getApp().getSystemService(Context.TELEPHONY_SERVICE);
        //noinspection ConstantConditions
        return tm.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE;
    }

    /**
     * 获取设备码
     * <p>Must hold
     * {@code <uses-permission android:name="android.permission.READ_PHONE_STATE" />}</p>
     *
     * @return the unique device id
     */
    @SuppressLint("HardwareIds")
    @RequiresPermission(READ_PHONE_STATE)
    public static String getDeviceId() {
        TelephonyManager tm =
                (TelephonyManager) AppManageUtils.getApp().getSystemService(Context.TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //noinspection ConstantConditions
            String imei = tm.getImei();
            if (!TextUtils.isEmpty(imei)) return imei;
            String meid = tm.getMeid();
            return TextUtils.isEmpty(meid) ? "" : meid;

        }
        //noinspection ConstantConditions
        return tm.getDeviceId();
    }

    /**
     * 获取序列号
     *
     * @return the serial of device
     */
    @SuppressLint("HardwareIds")
    @RequiresPermission(READ_PHONE_STATE)
    public static String getSerial() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? Build.getSerial() : Build.SERIAL;
    }

    /**
     * 获取 IMEI 码
     * <p>Must hold
     * {@code <uses-permission android:name="android.permission.READ_PHONE_STATE" />}</p>
     *
     * @return the IMEI
     */
    @SuppressLint("HardwareIds")
    @RequiresPermission(READ_PHONE_STATE)
    public static String getIMEI() {
        TelephonyManager tm =
                (TelephonyManager) AppManageUtils.getApp().getSystemService(Context.TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //noinspection ConstantConditions
            return tm.getImei();
        }
        //noinspection ConstantConditions
        return tm.getDeviceId();
    }

    /**
     * 获取 MEID 码
     * <p>Must hold
     * {@code <uses-permission android:name="android.permission.READ_PHONE_STATE" />}</p>
     *
     * @return the MEID
     */
    @SuppressLint("HardwareIds")
    @RequiresPermission(READ_PHONE_STATE)
    public static String getMEID() {
        TelephonyManager tm =
                (TelephonyManager) AppManageUtils.getApp().getSystemService(Context.TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //noinspection ConstantConditions
            return tm.getMeid();
        }
        //noinspection ConstantConditions
        return tm.getDeviceId();
    }

    /**
     * 获取 IMSI 码
     * <p>Must hold
     * {@code <uses-permission android:name="android.permission.READ_PHONE_STATE" />}</p>
     *
     * @return the IMSI
     */
    @SuppressLint("HardwareIds")
    @RequiresPermission(READ_PHONE_STATE)
    public static String getIMSI() {
        TelephonyManager tm =
                (TelephonyManager) AppManageUtils.getApp().getSystemService(Context.TELEPHONY_SERVICE);
        //noinspection ConstantConditions
        return tm.getSubscriberId();
    }

    /**
     * 获取移动终端类型
     *
     * @return the current phone type
     * <ul>
     * <li>{@link TelephonyManager#PHONE_TYPE_NONE}</li>
     * <li>{@link TelephonyManager#PHONE_TYPE_GSM }</li>
     * <li>{@link TelephonyManager#PHONE_TYPE_CDMA}</li>
     * <li>{@link TelephonyManager#PHONE_TYPE_SIP }</li>
     * </ul>
     */
    public static int getPhoneType() {
        TelephonyManager tm =
                (TelephonyManager) AppManageUtils.getApp().getSystemService(Context.TELEPHONY_SERVICE);
        //noinspection ConstantConditions
        return tm.getPhoneType();
    }

    /**
     * 判断 sim 卡是否准备好
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isSimCardReady() {
        TelephonyManager tm =
                (TelephonyManager) AppManageUtils.getApp().getSystemService(Context.TELEPHONY_SERVICE);
        //noinspection ConstantConditions
        return tm.getSimState() == TelephonyManager.SIM_STATE_READY;
    }

    /**
     * 获取 Sim 卡运营商名称
     *
     * @return the sim operator name
     */
    public static String getSimOperatorName() {
        TelephonyManager tm =
                (TelephonyManager) AppManageUtils.getApp().getSystemService(Context.TELEPHONY_SERVICE);
        //noinspection ConstantConditions
        return tm.getSimOperatorName();
    }

    /**
     * 获取 Sim 卡运营商名称
     *
     * @return the sim operator
     */
    public static String getSimOperatorByMnc() {
        TelephonyManager tm =
                (TelephonyManager) AppManageUtils.getApp().getSystemService(Context.TELEPHONY_SERVICE);
        //noinspection ConstantConditions
        String operator = tm.getSimOperator();
        if (operator == null) return "";
        switch (operator) {
            case "46000":
            case "46002":
            case "46007":
            case "46020":
                return "中国移动";
            case "46001":
            case "46006":
            case "46009":
                return "中国联通";
            case "46003":
            case "46005":
            case "46011":
                return "中国电信";
            default:
                return operator;
        }
    }

    /**
     * 获取手机状态信息
     * <p>Must hold
     * {@code <uses-permission android:name="android.permission.READ_PHONE_STATE" />}</p>
     *
     * @return DeviceId = 99000311726612<br>
     * DeviceSoftwareVersion = 00<br>
     * Line1Number =<br>
     * NetworkCountryIso = cn<br>
     * NetworkOperator = 46003<br>
     * NetworkOperatorName = 中国电信<br>
     * NetworkType = 6<br>
     * PhoneType = 2<br>
     * SimCountryIso = cn<br>
     * SimOperator = 46003<br>
     * SimOperatorName = 中国电信<br>
     * SimSerialNumber = 89860315045710604022<br>
     * SimState = 5<br>
     * SubscriberId(IMSI) = 460030419724900<br>
     * VoiceMailNumber = *86<br>
     */
    @SuppressLint("HardwareIds")
    @RequiresPermission(READ_PHONE_STATE)
    public static String getPhoneStatus() {
        TelephonyManager tm =
                (TelephonyManager) AppManageUtils.getApp().getSystemService(Context.TELEPHONY_SERVICE);
        String str = "";
        //noinspection ConstantConditions
        str += "DeviceId(IMEI) = " + tm.getDeviceId() + "\n";
        str += "DeviceSoftwareVersion = " + tm.getDeviceSoftwareVersion() + "\n";
        str += "Line1Number = " + tm.getLine1Number() + "\n";
        str += "NetworkCountryIso = " + tm.getNetworkCountryIso() + "\n";
        str += "NetworkOperator = " + tm.getNetworkOperator() + "\n";
        str += "NetworkOperatorName = " + tm.getNetworkOperatorName() + "\n";
        str += "NetworkType = " + tm.getNetworkType() + "\n";
        str += "PhoneType = " + tm.getPhoneType() + "\n";
        str += "SimCountryIso = " + tm.getSimCountryIso() + "\n";
        str += "SimOperator = " + tm.getSimOperator() + "\n";
        str += "SimOperatorName = " + tm.getSimOperatorName() + "\n";
        str += "SimSerialNumber = " + tm.getSimSerialNumber() + "\n";
        str += "SimState = " + tm.getSimState() + "\n";
        str += "SubscriberId(IMSI) = " + tm.getSubscriberId() + "\n";
        str += "VoiceMailNumber = " + tm.getVoiceMailNumber() + "\n";
        return str;
    }

    /**
     * 跳至拨号界面
     *
     * @param phoneNumber The phone number.
     * @return {@code true}: operate successfully<br>{@code false}: otherwise
     */
    public static boolean dial(final String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
        if (isIntentAvailable(intent)) {
            AppManageUtils.getApp().startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            return true;
        }
        return false;
    }

    /**
     * 拨打 phoneNumber
     * <p>Must hold {@code <uses-permission android:name="android.permission.CALL_PHONE" />}</p>
     *
     * @param phoneNumber The phone number.
     * @return {@code true}: operate successfully<br>{@code false}: otherwise
     */
    @RequiresPermission(CALL_PHONE)
    public static boolean call(final String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
        if (isIntentAvailable(intent)) {
            AppManageUtils.getApp().startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            return true;
        }
        return false;
    }

    /**
     * 跳至发送短信界面
     *
     * @param phoneNumber The phone number.
     * @param content     The content.
     * @return {@code true}: operate successfully<br>{@code false}: otherwise
     */
    public static boolean sendSms(final String phoneNumber, final String content) {
        Uri uri = Uri.parse("smsto:" + phoneNumber);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        if (isIntentAvailable(intent)) {
            intent.putExtra("sms_body", content);
            AppManageUtils.getApp().startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            return true;
        }
        return false;
    }

    /**
     * 发送短信
     * <p>Must hold {@code <uses-permission android:name="android.permission.SEND_SMS" />}</p>
     *
     * @param phoneNumber The phone number.
     * @param content     The content.
     */
    @RequiresPermission(SEND_SMS)
    public static void sendSmsSilent(final String phoneNumber, final String content) {
        if (TextUtils.isEmpty(content)) return;
        PendingIntent sentIntent = PendingIntent.getBroadcast(AppManageUtils.getApp(), 0, new Intent("send"), 0);
        SmsManager smsManager = SmsManager.getDefault();
        if (content.length() >= 70) {
            List<String> ms = smsManager.divideMessage(content);
            for (String str : ms) {
                smsManager.sendTextMessage(phoneNumber, null, str, sentIntent, null);
            }
        } else {
            smsManager.sendTextMessage(phoneNumber, null, content, sentIntent, null);
        }
    }

    private static boolean isIntentAvailable(final Intent intent) {
        return AppManageUtils.getApp()
                .getPackageManager()
                .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
                .size() > 0;
    }

    /**
     * 获取手机型号
     *
     * @param context  上下文
     * @return   String
     */
    public static String getMobileModel(Context context) {
        try {
            String model = android.os.Build.MODEL; // 手机型号
            return model;
        } catch (Exception e) {
            return "未知";
        }
    }

    /**
     * 获取手机品牌
     *
     * @param context  上下文
     * @return  String
     */
    public static String getMobileBrand(Context context) {
        try {
            String brand = android.os.Build.BRAND; // android系统版本号
            return brand;
        } catch (Exception e) {
            return "未知";
        }
    }

    /**
     * 获取sd卡剩余空间的大小
     */
    @SuppressWarnings("deprecation")
    public long getSDFreeSize() {
        File path = Environment.getExternalStorageDirectory(); // 取得SD卡文件路径
        StatFs sf = new StatFs(path.getPath());
        long blockSize = sf.getBlockSize(); // 获取单个数据块的大小(Byte)
        long freeBlocks = sf.getAvailableBlocks();// 空闲的数据块的数量
        // 返回SD卡空闲大小
        return (freeBlocks * blockSize) / 1024 / 1024; // 单位MB
    }

    /**
     * 获取sd卡空间的总大小
     */
    @SuppressWarnings("deprecation")
    public long getSDAllSize() {
        File path = Environment.getExternalStorageDirectory(); // 取得SD卡文件路径
        StatFs sf = new StatFs(path.getPath());
        long blockSize = sf.getBlockSize(); // 获取单个数据块的大小(Byte)
        long allBlocks = sf.getBlockCount(); // 获取所有数据块数
        // 返回SD卡大小
        return (allBlocks * blockSize) / 1024 / 1024; // 单位MB
    }

    /**
     * 获取手机内安装的应用
     */
    public List<PackageInfo> getInstalledApp(Context context) {
        PackageManager pm = context.getPackageManager();
        return pm.getInstalledPackages(0);
    }

    /**
     * 获取手机安装非系统应用
     */
    @SuppressWarnings("static-access")
    public List<PackageInfo> getUserInstalledApp(Context context) {
        List<PackageInfo> infos = getInstalledApp(context);
        List<PackageInfo> apps = new ArrayList<PackageInfo>();
        for (PackageInfo info : infos) {
            if ((info.applicationInfo.flags & info.applicationInfo.FLAG_SYSTEM) <= 0) {
                apps.add(info);
            }
        }
        infos.clear();
        infos = null;
        return apps;
    }

    /**
     * 获取安装应用的信息
     */
    public Map<String, Object> getInstalledAppInfo(Context context,
                                                   PackageInfo info) {
        Map<String, Object> appInfos = new HashMap<String, Object>();
        PackageManager pm = context.getPackageManager();
        ApplicationInfo aif = info.applicationInfo;
        appInfos.put("icon", pm.getApplicationIcon(aif));
        appInfos.put("lable", pm.getApplicationLabel(aif));
        appInfos.put("packageName", aif.packageName);
        return appInfos;
    }

    /**
     * 打开指定包名的应用
     */
    public void startAppPkg(Context context, String pkg) {
        Intent startIntent = context.getPackageManager()
                .getLaunchIntentForPackage(pkg);
        startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(startIntent);
    }

    /**
     * 跳转至系统设置界面
     *
     * @param mContext
     *            上下文
     */
    public static void toSettingActivity(Context mContext) {
        Intent settingsIntent = new Intent(Settings.ACTION_SETTINGS);
        mContext.startActivity(settingsIntent);
    }

    /**
     * 跳转至WIFI设置界面
     *
     * @param mContext
     *            上下文
     */
    public static void toWIFISettingActivity(Context mContext) {
        Intent wifiSettingsIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        mContext.startActivity(wifiSettingsIntent);
    }

    /**
     * 启动本地应用打开PDF
     *
     * @param mContext
     *            上下文
     * @param filePath
     *            文件路径
     */
    public static void openPDFFile(Context mContext, String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                Uri path = Uri.fromFile(file);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(path, "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mContext.startActivity(intent);
            }
        } catch (Exception e) {
            ToastUtils.showShort("未检测到可打开PDF相关软件");
        }
    }

    /**
     * 启动本地应用打开PDF
     *
     * @param mContext
     *            上下文
     * @param filePath
     *            文件路径
     */
    public static void openWordFile(Context mContext, String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                Uri path = Uri.fromFile(file);
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(path, "application/msword");
                mContext.startActivity(intent);
            }
        } catch (Exception e) {
            ToastUtils.showShort("未检测到可打开Word文档相关软件");
        }
    }

    /**
     * 调用WPS打开office文档 http://bbs.wps.cn/thread-22349340-1-1.html
     *
     * @param mContext
     *            上下文
     * @param filePath
     *            文件路径
     */
    public static void openOfficeByWPS(Context mContext, String filePath) {

        try {

            // 文件存在性检查
            File file = new File(filePath);
            if (!file.exists()) {
                ToastUtils.showShort(filePath + "文件路径不存在");
                return;
            }

            // 检查是否安装WPS
            String wpsPackageEng = "cn.wps.moffice_eng";// 普通版与英文版一样
            // String wpsActivity =
            // "cn.wps.moffice.documentmanager.PreStartActivity";
            String wpsActivity2 = "cn.wps.moffice.documentmanager.PreStartActivity2";// 默认第三方程序启动

            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setClassName(wpsPackageEng, wpsActivity2);

            Uri uri = Uri.fromFile(new File(filePath));
            intent.setData(uri);
            mContext.startActivity(intent);

        } catch (ActivityNotFoundException e) {
            ToastUtils.showShort("本地未安装WPS");
        } catch (Exception e) {
            ToastUtils.showShort("打开文档失败");
        }
    }

    /**
     * 获得电话管理实例对象
     * @param content  上下文
     * @return TelephonyManager 电话管理实例对象
     */
    private static TelephonyManager getSysTelephonyManager(Context content) {
        TelephonyManager telephonyManager = null;
        telephonyManager = (TelephonyManager) content.getSystemService(Context.TELEPHONY_SERVICE);
        LogUtils.i("AppSysMgr-->>getSysTelephonyManager",  telephonyManager + "");
        return telephonyManager;
    }

    /**
     * 获取手机状态(0：无活动 1：响铃 2：待机)
     * @param  context 上下文
     * @return Integer 手机状态
     */
    public static Integer getSysPhoneState(Context context) {
        Integer callState = getSysTelephonyManager(context).getCallState();
        LogUtils.i("AppSysMgr-->>getSysPhoneState",  callState + "");
        return callState;
    }


    /**
     * 获得手机方位
     * @param context 上下文
     * @return CellLocation 手机方位
     */
    @RequiresPermission(ACCESS_FINE_LOCATION)
    public static CellLocation getSysPhoneLoaction(Context context) {
        CellLocation cellLocation = getSysTelephonyManager(context).getCellLocation();
        LogUtils.i("AppSysMgr-->>getSysPhoneLoaction",  cellLocation + "");
        return cellLocation;
    }

    /**
     * 获取SIM序列号
     * @param ctx
     * @return
     */
    @RequiresPermission(READ_PHONE_STATE)
    public static String getSIMSerial(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getSimSerialNumber();
    }

    /**
     * 获取网络运营商 46000,46002,46007 中国移动,46001 中国联通,46003 中国电信
     * @param ctx
     * @return
     */
    public static String getMNC(Context ctx) {
        String providersName = "";
        TelephonyManager telephonyManager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY) {
            providersName = telephonyManager.getSimOperator();
            providersName = providersName == null ? "" : providersName;
        }
        return providersName;
    }

    /**
     * 获取网络运营商：中国电信,中国移动,中国联通
     * @param ctx
     * @return
     */
    public static String getCarrier(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getNetworkOperatorName().toLowerCase(Locale.getDefault());
    }

    /**
     * 获取硬件型号
     * @return
     */
    public static String getModel() {
        return Build.MODEL;
    }

    /**
     * 获取编译厂商
     * @return
     */
    public static String getBuildBrand() {
        return Build.BRAND;
    }

    /**
     *获取编译服务器主机
     * @return
     */
    public static String getBuildHost() {
        return Build.HOST;
    }

    /**
     *获取描述Build的标签
     * @return
     */
    public static String getBuildTags() {
        return Build.TAGS;
    }

    /**
     *获取系统编译时间
     * @return
     */
    public static long getBuildTime() {
        return Build.TIME;
    }

    /**
     *获取系统编译作者
     * @return
     */
    public static String getBuildUser() {
        return Build.USER;
    }

    /**
     *获取编译系统版本(5.1)
     * @return
     */
    public static String getBuildVersionRelease() {
        return Build.VERSION.RELEASE;
    }

    /**
     *获取开发代号
     * @return
     */
    public static String getBuildVersionCodename() {
        return Build.VERSION.CODENAME;
    }

    /**
     * 获取源码控制版本号
     * @return
     */
    public static String getBuildVersionIncremental() {
        return Build.VERSION.INCREMENTAL;
    }

    /**
     *获取编译的SDK
     * @return
     */
    public static int getBuildVersionSDK() {
        return Build.VERSION.SDK_INT;
    }

    /**
     *获取修订版本列表(LMY47D)
     * @return
     */
    public static String getBuildID() {
        return Build.ID;
    }

    /**
     *CPU指令集
     * @return
     */
    public static String[] getSupportedABIS() {
        String[] result = new String[]{"-"};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            result = Build.SUPPORTED_ABIS;
        }
        if (result == null || result.length == 0) {
            result = new String[]{"-"};
        }
        return result;
    }

    /**
     *获取硬件制造厂商
     * @return
     */
    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }

    /**
     *获取系统启动程序版本号
     * @return
     */
    public static String getBootloader() {
        return Build.BOOTLOADER;
    }

    /**
     *
     * @param ctx
     * @return
     */
    public static String getScreenDisplayID(Context ctx) {
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        return String.valueOf(wm.getDefaultDisplay().getDisplayId());
    }

    /**
     * 获取系统版本号
     * @return
     */
    public static String getDisplayVersion() {
        return Build.DISPLAY;
    }

    /**
     *获取语言
     * @return
     */
    public static String getLanguage() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * 获取国家
     * @param ctx
     * @return
     */
    public static String getCountry(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        Locale locale = Locale.getDefault();
        return tm.getSimState() == TelephonyManager.SIM_STATE_READY ? tm.getSimCountryIso().toLowerCase(Locale.getDefault()) : locale.getCountry().toLowerCase(locale);
    }

    /**
     *获取系统版本:5.1.1
     * @return
     */
    public static String getOSVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     *获取GSF序列号
     * @param context
     * @return
     */
    //<uses-permission android:name="com.google.android.providers.gsf.permission.READ_GSERVICES"/>
    public static String getGSFID(Context context) {
        String result;
        final Uri URI = Uri.parse("content://com.google.android.gsf.gservices");
        final String ID_KEY = "android_id";
        String[] params = {ID_KEY};
        Cursor c = context.getContentResolver().query(URI, null, null, params, null);
        if (c == null || !c.moveToFirst() || c.getColumnCount() < 2) {
            return null;
        } else {
            result = Long.toHexString(Long.parseLong(c.getString(1)));
        }
        c.close();
        return result;
    }

    /**
     * 获取蓝牙地址
     * @param context
     * @return
     */
    //<uses-permission android:name="android.permission.BLUETOOTH"/>
    @SuppressWarnings("MissingPermission")
    public static String getBluetoothMAC(Context context) {
        String result = null;
        try {
            if (context.checkCallingOrSelfPermission(Manifest.permission.BLUETOOTH)
                    == PackageManager.PERMISSION_GRANTED) {
                BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();
                result = bta.getAddress();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Android设备物理唯一标识符
     * @return
     */
    public static String getPsuedoUniqueID() {
        String devIDShort = "35" + (Build.BOARD.length() % 10) + (Build.BRAND.length() % 10);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            devIDShort += (Build.SUPPORTED_ABIS[0].length() % 10);
        } else {
            devIDShort += (Build.CPU_ABI.length() % 10);
        }
        devIDShort += (Build.DEVICE.length() % 10) + (Build.MANUFACTURER.length() % 10) + (Build.MODEL.length() % 10) + (Build.PRODUCT.length() % 10);
        String serial;
        try {
            serial = Build.class.getField("SERIAL").get(null).toString();
            return new UUID(devIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception e) {
            serial = "ESYDV000";
        }
        return new UUID(devIDShort.hashCode(), serial.hashCode()).toString();
    }

    /**
     * 构建标识,包括brand,name,device,version.release,id,version.incremental,type,tags这些信息
     * @return
     */
    public static String getFingerprint() {
        return Build.FINGERPRINT;
    }

    /**
     * 获取硬件信息
     * @return
     */
    public static String getHardware() {
        return Build.HARDWARE;
    }

    /**
     * 获取产品信息
     * @return
     */
    public static String getProduct() {
        return Build.PRODUCT;
    }

    /**
     *  获取设备信息
     * @return
     */
    public static String getDevice() {
        return Build.DEVICE;
    }

    /**
     * 获取主板信息
     * @return
     */
    public static String getBoard() {
        return Build.BOARD;
    }

    /**
     *  获取基带版本(无线电固件版本 Api14以上)
     * @return
     */
    public static String getRadioVersion() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH ? Build.getRadioVersion() : "";
    }
}