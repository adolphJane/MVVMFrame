package com.magicalrice.project.library_base.advanced.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.magicalrice.project.library_base.base.log.LogUtils

/**
 * <uses-permission android:name="android.permission.READ_PHONE_STATE"></uses-permission>
 * <uses-permission android:name="android.permission.PROCESS_OUTGOING_CALLS"></uses-permission>
 *
 *
 * action: android.intent.action.PHONE_STATE;  android.intent.action.NEW_OUTGOING_CALL;
 *
 *
 * 去电时：
 * 未接：phone_state=OFFHOOK;
 * 挂断：phone_state=IDLE
 * 来电时:
 * 未接：phone_state=RINGING
 * 已接：phone_state=OFFHOOK;
 * 挂断：phone_state=IDLE
 *
 * @author MaTianyu
 * @date 2015-03-09
 */
class PhoneReceiver : BroadcastReceiver() {
    private var phoneListener: PhoneListener? = null
    private var isDialOut: Boolean = false
    private var number: String? = null

    override fun onReceive(context: Context, intent: Intent) {
        if (LogUtils.getConfig().getLogSwitch()) {
            LogUtils.i(TAG, "action: " + intent.action!!)
            LogUtils.d(TAG, "intent : ")
            val bundle = intent.extras
            for (key in bundle!!.keySet()) {
                LogUtils.d(TAG, key + " : " + bundle.get(key))
            }
        }
        if (NEW_OUTGOING_CALL == intent.action) {
            isDialOut = true
            val outNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER)
            if (!Check.isEmpty(outNumber)) {
                this.number = outNumber
            }
            phoneListener?.onPhoneStateChanged(CallState.Outgoing, number)
        } else if (PHONE_STATE == intent.action) {
            val state = intent.getStringExtra(INTENT_STATE)
            val inNumber = intent.getStringExtra(INTENT_INCOMING_NUMBER)
            if (!Check.isEmpty(inNumber)) {
                this.number = inNumber
            }
            if (RINGING == state) {
                isDialOut = false
                phoneListener?.onPhoneStateChanged(CallState.IncomingRing, number)
            } else if (OFFHOOK == state) {
                if (!isDialOut && phoneListener != null) {
                    phoneListener!!.onPhoneStateChanged(CallState.Incoming, number)
                }
            } else if (IDLE == state) {
                if (isDialOut) {
                    phoneListener?.onPhoneStateChanged(CallState.OutgoingEnd, number)
                } else {
                    phoneListener?.onPhoneStateChanged(CallState.IncomingEnd, number)
                }
            }
        }
    }

    /**
     * 去电时：
     * 未接：phone_state=OFFHOOK;
     * 挂断：phone_state=IDLE
     * 来电时:
     * 未接：phone_state=RINGING
     * 已接：phone_state=OFFHOOK;
     * 挂断：phone_state=IDLE
     */
    //public void registerCallStateListener(Context context, PhoneStateListener listener) {
    //    try {
    //        //获取电话通讯服务
    //        TelephonyManager tpm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    //        tpm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
    //    } catch (Exception e) {
    //        e.printStackTrace();
    //    }
    //}
    fun registerReceiver(context: Context, phoneListener: PhoneListener) {
        try {
            val filter = IntentFilter()
            filter.addAction("android.intent.action.PHONE_STATE")
            filter.addAction("android.intent.action.NEW_OUTGOING_CALL")
            filter.priority = Integer.MAX_VALUE
            context.registerReceiver(this, filter)
            this.phoneListener = phoneListener
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

    interface PhoneListener {
        fun onPhoneStateChanged(state: CallState, number: String?)
    }

    /**
     * 分别是：
     *
     *
     * 播出电话
     * 播出电话结束
     * 接入电话铃响
     * 接入通话中
     * 接入通话完毕
     */
    enum class CallState {
        Outgoing,
        OutgoingEnd,
        IncomingRing,
        Incoming,
        IncomingEnd
    }

    companion object {

        private val TAG = "PhoneReceiver"

        private val RINGING = "RINGING"
        private val OFFHOOK = "OFFHOOK"
        private val IDLE = "IDLE"

        private val PHONE_STATE = "android.intent.action.PHONE_STATE"
        private val NEW_OUTGOING_CALL = "android.intent.action.NEW_OUTGOING_CALL"
        private val INTENT_STATE = "state"
        private val INTENT_INCOMING_NUMBER = "incoming_number"
    }

}
