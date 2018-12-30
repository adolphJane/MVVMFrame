package com.adolph.project.common.base

import android.app.Application
import com.adolph.project.baseutils.AppManageUtils

class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AppManageUtils.init(this)
    }
}