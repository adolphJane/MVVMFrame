package com.magicalrice.project.library_base.advanced.receiver

import android.content.*
import android.net.Uri
import android.os.Build
import android.telephony.SmsManager
import android.telephony.SmsMessage
import com.magicalrice.project.library_base.base.log.LogUtils

/**
 * Call requires API level 4
 * <uses-permission android:name="android.permission.RECEIVE_SMS"></uses-permission>
 *
 *
 * action: android.provider.Telephony.SMS_RECEIVED
 *
 * @author MaTianyu
 * @date 14-7-23
 */
class SmsReceiver : BroadcastReceiver() {
    private var smsListener: SmsListener? = null

    override fun onReceive(context: Context, intent: Intent) {
        try {
            if (LogUtils.getConfig().getLogSwitch()) {
                LogUtils.i(TAG, "收到广播：" + intent.action!!)
                val bundle = intent.extras
                for (key in bundle!!.keySet()) {
                    LogUtils.i(TAG, key + " : " + bundle.get(key))
                }
            }
            val pdus = intent.extras?.get("pdus") as Array<*>?
            var fromAddress: String? = null
            var serviceCenterAddress: String? = null
            if (pdus != null) {
                var msgBody = ""
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
                    for (obj in pdus) {
                        val sms = SmsMessage.createFromPdu(obj as ByteArray)
                        msgBody += sms.messageBody
                        fromAddress = sms.originatingAddress
                        serviceCenterAddress = sms.serviceCenterAddress

                        if (smsListener != null) {
                            smsListener!!.onMessage(sms)
                        }
                        //Log.i(TAG, "getDisplayMessageBody：" + sms.getDisplayMessageBody());
                        //Log.i(TAG, "getDisplayOriginatingAddress：" + sms.getDisplayOriginatingAddress());
                        //Log.i(TAG, "getEmailBody：" + sms.getEmailBody());
                        //Log.i(TAG, "getEmailFrom：" + sms.getEmailFrom());
                        //Log.i(TAG, "getMessageBody：" + sms.getMessageBody());
                        //Log.i(TAG, "getOriginatingAddress：" + sms.getOriginatingAddress());
                        //Log.i(TAG, "getPseudoSubject：" + sms.getPseudoSubject());
                        //Log.i(TAG, "getServiceCenterAddress：" + sms.getServiceCenterAddress());
                        //Log.i(TAG, "getIndexOnIcc：" + sms.getIndexOnIcc());
                        //Log.i(TAG, "getMessageClass：" + sms.getMessageClass());
                        //Log.i(TAG, "getUserData：" + new String(sms.getUserData()));
                    }
                }
                if (smsListener != null) {
                    smsListener!!.onMessage(msgBody, fromAddress, serviceCenterAddress)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun registerSmsReceiver(context: Context, smsListener: SmsListener) {
        try {
            this.smsListener = smsListener
            val filter = IntentFilter()
            filter.addAction("android.provider.Telephony.SMS_RECEIVED")
            filter.priority = Integer.MAX_VALUE
            context.registerReceiver(this, filter)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun unRegisterSmsReceiver(context: Context) {
        try {
            context.unregisterReceiver(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    abstract class SmsListener {
        abstract fun onMessage(msg: String, fromAddress: String?, serviceCenterAddress: String?)

        fun onMessage(msg: SmsMessage) {}
    }

    companion object {
        private val TAG = SmsReceiver::class.java.simpleName

        /**
         * Call requires API level 4
         * <uses-permission android:name="android.permission.SEND_SMS"></uses-permission>
         *
         * @param phone
         * @param msg
         */
        fun sendMsgToPhone(phone: String, msg: String) {
            LogUtils.i(TAG, "发送手机：$phone ,内容： $msg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
                val manager = SmsManager.getDefault()
                val texts = manager.divideMessage(msg)
                for (txt in texts) {
                    manager.sendTextMessage(phone, null, txt, null, null)
                }
            } else {
                LogUtils.e(TAG, "发送失败，系统版本低于DONUT，$phone ,内容： $msg")
            }

        }

        /**
         * Call requires API level 4
         * <uses-permission android:name="android.permission.WRITE_SMS"></uses-permission>
         *
         * @param context
         * @param phone
         * @param msg
         */
        fun saveMsgToSystem(context: Context, phone: String, msg: String) {
            val values = ContentValues()
            values.put("date", System.currentTimeMillis())
            //阅读状态 
            values.put("read", 0)
            //1为收 2为发  
            values.put("type", 2)
            values.put("address", phone)
            values.put("body", msg)
            context.contentResolver.insert(Uri.parse("content://sms/inbox"), values)
        }
    }

}
