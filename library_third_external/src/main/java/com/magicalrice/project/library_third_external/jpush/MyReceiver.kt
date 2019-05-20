package com.magicalrice.project.library_third_external.jpush

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import cn.jpush.android.api.JPushInterface
import com.magicalrice.project.library_base.base.AppManager
import com.magicalrice.project.library_base.base.SPUtils
import com.magicalrice.project.library_base.base.log.LogUtils

class MyReceiver : BroadcastReceiver() {
    private val TAG = "JIGUANG-Example"

    override fun onReceive(context: Context?, intent: Intent?) {
        try {
            val bundle = intent?.extras
            when (intent?.action) {
                JPushInterface.ACTION_REGISTRATION_ID -> {
                    val regId = bundle?.getString(JPushInterface.EXTRA_REGISTRATION_ID)
                    LogUtils.e(TAG, "[MyReceiver] 接收Registration Id : $regId")
                    val registId = bundle?.getString(JPushInterface.EXTRA_REGISTRATION_ID)
                    registId?.let {
                        SPUtils.getInstance().put("JpushRegistId", registId)
                    }
                }
                JPushInterface.ACTION_MESSAGE_RECEIVED -> {
                    LogUtils.e(
                        TAG,
                        "[MyReceiver] 接收到推送下来的自定义消息: " + bundle?.getString(JPushInterface.EXTRA_MESSAGE)
                    )
                    processCustomMessage(context, bundle)
                }
                JPushInterface.ACTION_NOTIFICATION_RECEIVED -> {
                    LogUtils.e(TAG, "[MyReceiver] 接收到推送下来的通知")

                }
                JPushInterface.ACTION_NOTIFICATION_OPENED -> {
                    LogUtils.e(TAG, "[MyReceiver] 用户点击打开了通知")
                    val extras = bundle?.getString(JPushInterface.EXTRA_EXTRA)
                    LogUtils.e(TAG, "[MyReceiver] $extras")

                    extras?.let { extra ->
                        val msgIntent = Intent(context, PushReceiverActivity::class.java)
//                        val msgBean = Gson().fromJson(extra, PushMsgBean::class.java)
                        val bundle = Bundle()
//                        bundle.putInt("noticeType",msgBean.noticeType)
//                        bundle.putLong("id",msgBean.id)
                        msgIntent.putExtra("extras", bundle)
                        //打开自定义的Activity
                        val activty = AppManager.getInstance().getTopActivity()
                        if (activty == null) {
                            msgIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            PushReceiverActivity.start(context, msgIntent)
                        } else {
//                            if (activty is BaseActivity<*,*>) {
//                                msgIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                                PushReceiverActivity.start(context,msgIntent)
//                            } else {
//                            }
                        }
                    }
//                    i.putExtras(bundle)
//                    i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
//                    context?.startActivity(i)
                }
                JPushInterface.ACTION_RICHPUSH_CALLBACK -> {
                    LogUtils.e(
                        TAG,
                        "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle?.getString(JPushInterface.EXTRA_EXTRA)
                    );
                    //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..
                }
                JPushInterface.ACTION_CONNECTION_CHANGE -> {
                    val connected =
                        intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false)
                    LogUtils.e(
                        TAG,
                        "[MyReceiver]" + intent.action + " connected state change to " + connected
                    )
                    val registId = bundle?.getString(JPushInterface.EXTRA_REGISTRATION_ID)
                    registId?.let {
                        SPUtils.getInstance().put("JpushRegistId", registId)
                    }
                }
                else -> {
                    LogUtils.e(TAG, "[MyReceiver] Unhandled intent - " + intent?.action)
                }
            }
        } catch (e: Exception) {

        }
    }

    //send msg to MainActivity
    private fun processCustomMessage(context: Context?, bundle: Bundle?) {
//        if (MainActivity.isForeground) {
        val message = bundle?.getString(JPushInterface.EXTRA_MESSAGE)
        val extras = bundle?.getString(JPushInterface.EXTRA_EXTRA)
//            val msgIntent = Intent(MainActivity.MESSAGE_RECEIVED_ACTION)
//            msgIntent.putExtra(MainActivity.KEY_MESSAGE, message)
//            if (!ExampleUtil.isEmpty(extras)) {
//                try {
//                    val extraJson = JSONObject(extras)
//                    if (extraJson.length() > 0) {
//                        msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras)
//                    }
//                } catch (e: JSONException) {
//
//                }
//
//            }
//            LocalBroadcastManager.getInstance(context).sendBroadcast(msgIntent)
//        }
    }
}