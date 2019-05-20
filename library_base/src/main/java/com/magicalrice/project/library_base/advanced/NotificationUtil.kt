package com.magicalrice.project.library_base.advanced

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.magicalrice.project.library_base.base.log.LogUtils
import java.util.*

/**
 * @author MaTianyu
 * @date 2014-11-19
 */
object NotificationUtil {
    private var LedID = 0
    private val TAG = NotificationUtil::class.java.simpleName

    fun notification(
        context: Context, uri: Uri,
        icon: Int, ticker: String, title: String, msg: String
    ) {
        LogUtils.i(TAG, "notiry uri :$uri")
        // 设置通知的事件消息
        val intent = Intent()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
            intent.setPackage(context.packageName)
        }
        intent.data = uri
        notification(context, intent, 0, icon, ticker, title, msg)
    }

    fun notification(
        context: Context, activityClass: String, bundle: Bundle,
        icon: Int, ticker: String, title: String, msg: String
    ) {
        // 设置通知的事件消息
        val intent = Intent()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
            intent.setPackage(context.packageName)
        }
        intent.putExtras(bundle)
        intent.component = ComponentName(context.packageName, activityClass)
        notification(context, intent, 0, icon, ticker, title, msg)
    }

    fun notification(
        context: Context, intent: Intent, id: Int,
        icon: Int, ticker: String, title: String, msg: String
    ) {
        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        notification(context, pendingIntent, id, icon, ticker, title, msg)
    }

    fun notification(
        context: Context, pendingIntent: PendingIntent, id: Int,
        icon: Int, ticker: String, title: String, msg: String
    ) {
        val builder = Notification.Builder(context)
        builder.setSmallIcon(icon)

        builder.setContentTitle(title)
        builder.setTicker(ticker)
        builder.setContentText(msg)

        builder.setDefaults(Notification.DEFAULT_SOUND)
        builder.setLights(-0x100, 0, 2000)
        builder.setVibrate(longArrayOf(0, 100, 300))
        builder.setAutoCancel(true)
        builder.setContentIntent(pendingIntent)
        val baseNF: Notification
        baseNF = builder.build()
        //发出状态栏通知
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(id, baseNF)
    }

    fun lightLed(context: Context, colorOx: Int, durationMS: Int) {
        lightLed(context, colorOx, 0, durationMS)
    }

    fun lightLed(context: Context, colorOx: Int, startOffMS: Int, durationMS: Int) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = Notification()
        notification.ledARGB = colorOx
        notification.ledOffMS = startOffMS
        notification.ledOnMS = durationMS
        notification.flags = Notification.FLAG_SHOW_LIGHTS
        LedID++
        nm.notify(LedID, notification)
        nm.cancel(LedID)
    }

    fun lightLed(
        context: Context, colorOx: Int, startOffMS: Int, durationMS: Int,
        repeat: Int
    ) {
        var repeat = repeat
        if (repeat < 1) {
            repeat = 1
        }
        val handler = Handler(Looper.getMainLooper())
        for (i in 0 until repeat) {
            handler.postDelayed(
                { lightLed(context, colorOx, startOffMS, durationMS) },
                ((startOffMS + durationMS) * i).toLong()
            )
        }
    }

    fun lightLed(context: Context, patterns: ArrayList<LightPattern>?) {
        if (patterns == null) {
            return
        }
        for (lp in patterns) {
            lightLed(context, lp.argb, lp.startOffMS, lp.durationMS)
        }
    }

    class LightPattern(argb: Int, startOffMS: Int, durationMS: Int) {
        var argb = 0
        var startOffMS = 0
        var durationMS = 0

        init {
            this.argb = argb
            this.startOffMS = startOffMS
            this.durationMS = durationMS
        }
    }

}
