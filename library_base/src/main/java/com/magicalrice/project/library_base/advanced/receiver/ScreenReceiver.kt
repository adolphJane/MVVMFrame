package com.magicalrice.project.library_base.advanced.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.magicalrice.project.library_base.base.log.LogUtils

class ScreenReceiver : BroadcastReceiver() {
    private val TAG = "ScreenActionReceiver"
    private var screenListener: ScreenListener? = null

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == Intent.ACTION_SCREEN_ON) {
            LogUtils.d(TAG, "屏幕解锁广播...")
            if (screenListener != null) {
                screenListener!!.screenOn()
            }
        } else if (action == Intent.ACTION_SCREEN_OFF) {
            LogUtils.d(TAG, "屏幕加锁广播...")
            if (screenListener != null) {
                screenListener!!.screenOff()
            }
        }
    }

    fun registerScreenReceiver(context: Context, screenListener: ScreenListener) {
        try {
            this.screenListener = screenListener
            val filter = IntentFilter()
            filter.addAction(Intent.ACTION_SCREEN_OFF)
            filter.addAction(Intent.ACTION_SCREEN_ON)
            LogUtils.d(TAG, "注册屏幕解锁、加锁广播接收者...")
            context.registerReceiver(this, filter)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun unRegisterScreenReceiver(context: Context) {
        try {
            context.unregisterReceiver(this)
            LogUtils.d(TAG, "注销屏幕解锁、加锁广播接收者...")
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    interface ScreenListener {
        fun screenOn()
        fun screenOff()
    }

}