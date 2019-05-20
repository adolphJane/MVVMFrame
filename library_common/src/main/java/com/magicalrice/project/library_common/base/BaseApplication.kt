package com.magicalrice.project.library_common.base

import android.app.Application
import com.magicalrice.project.library_base.base.AppManager

class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AppManager.getInstance().init(this)
//        initX5Webview()
    }

//    private fun initX5Webview() {
//        // 在调用TBS初始化、创建WebView之前进行如下配置，以开启优化方案(仅Android 5.1+生效)
//        val map = HashMap<String,Any>()
//        map[TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER] = true
//        QbSdk.initTbsSettings(map)
//        // x5内核初始化接口
//        QbSdk.initX5Environment(applicationContext, null)
//        // 非wifi网络条件下是否允许下载内核，默认为false(针对用户没有安装微信/手Q/QQ空间[无内核]的情况下)
//        QbSdk.setDownloadWithoutWifi(true)
//    }

//    private fun initJpush() {
//        JPushInterface.setDebugMode(BuildConfig.DEBUG)
//        JPushInterface.init(this)
//    }
}