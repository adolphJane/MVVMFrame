package com.magicalrice.project.library_base.advanced.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.magicalrice.project.library_base.base.log.LogUtils

/**
 * 时间广播
 *
 * @author MaTianyu
 * @date 2015-03-09
 */
class TimeReceiver : BroadcastReceiver() {

    private var timeListener: TimeListener? = null

    override fun onReceive(context: Context, intent: Intent) {
        if (LogUtils.getConfig().getLogSwitch()) {
            LogUtils.i(TAG, "action: " + intent.action!!)
            LogUtils.d(TAG, "intent : ")
            val bundle = intent.extras
            for (key in bundle!!.keySet()) {
                LogUtils.d(TAG, key + " : " + bundle.get(key))
            }
        }
        if (Intent.ACTION_TIME_TICK == intent.action) {
            if (timeListener != null) {
                timeListener!!.onTimeTick()
            }
        } else if (Intent.ACTION_TIME_CHANGED == intent.action) {
            if (timeListener != null) {
                timeListener!!.onTimeChanged()
            }
        } else if (Intent.ACTION_TIMEZONE_CHANGED == intent.action) {
            if (timeListener != null) {
                timeListener!!.onTimeZoneChanged()
            }
        }
    }

    fun registerReceiver(context: Context, timeListener: TimeListener) {
        try {
            val filter = IntentFilter()
            filter.addAction(Intent.ACTION_TIME_CHANGED)
            filter.addAction(Intent.ACTION_TIME_TICK)
            filter.addAction(Intent.ACTION_TIMEZONE_CHANGED)
            filter.priority = Integer.MAX_VALUE
            context.registerReceiver(this, filter)
            this.timeListener = timeListener
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun unRegisterReceiver(context: Context) {
        try {
            context.unregisterReceiver(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    interface TimeListener {
        /**
         * 时区改变
         */
        fun onTimeZoneChanged()

        /**
         * 设置时间
         */
        fun onTimeChanged()

        /**
         * 每分钟调用
         */
        fun onTimeTick()
    }

    companion object {

        private val TAG = "TimeReceiver"
    }
}
