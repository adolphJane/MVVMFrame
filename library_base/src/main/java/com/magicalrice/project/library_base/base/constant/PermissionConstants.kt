package com.magicalrice.project.library_base.base.constant

import android.Manifest
import android.os.Build
import androidx.annotation.StringDef
import java.util.*

/**
 * @package com.magicalrice.project.library_base.base.constant
 * @author Adolph
 * @date 2019-04-22 Mon
 * @description TODO
 */

object PermissionConstants {
    const val CALENDAR = Manifest.permission_group.CALENDAR
    const val CAMERA = Manifest.permission_group.CAMERA
    const val CONTACTS = Manifest.permission_group.CONTACTS
    const val LOCATION = Manifest.permission_group.LOCATION
    const val MICROPHONE = Manifest.permission_group.MICROPHONE
    const val PHONE = Manifest.permission_group.PHONE
    const val SENSORS = Manifest.permission_group.SENSORS
    const val SMS = Manifest.permission_group.SMS
    const val STORAGE = Manifest.permission_group.STORAGE

    private val GROUP_CALENDAR =
        arrayOf(Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR)
    private val GROUP_CAMERA = arrayOf(Manifest.permission.CAMERA)
    private val GROUP_CONTACTS = arrayOf(
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.WRITE_CONTACTS,
        Manifest.permission.GET_ACCOUNTS
    )
    private val GROUP_LOCATION = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    private val GROUP_MICROPHONE = arrayOf(Manifest.permission.RECORD_AUDIO)
    private val GROUP_PHONE = arrayOf(
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.READ_PHONE_NUMBERS,
        Manifest.permission.CALL_PHONE,
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.WRITE_CALL_LOG,
        Manifest.permission.ADD_VOICEMAIL,
        Manifest.permission.USE_SIP,
        Manifest.permission.PROCESS_OUTGOING_CALLS,
        Manifest.permission.ANSWER_PHONE_CALLS
    )
    private val GROUP_PHONE_BELOW_O = Arrays.copyOf(
        GROUP_PHONE, GROUP_PHONE.size - 1
    )
    private val GROUP_SENSORS = arrayOf(Manifest.permission.BODY_SENSORS)
    private val GROUP_SMS = arrayOf(
        Manifest.permission.SEND_SMS,
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.READ_SMS,
        Manifest.permission.RECEIVE_WAP_PUSH,
        Manifest.permission.RECEIVE_MMS
    )
    private val GROUP_STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    @StringDef(CALENDAR, CAMERA, CONTACTS, LOCATION, MICROPHONE, PHONE, SENSORS, SMS, STORAGE)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Permission

    fun getPermissions(@Permission permission: String): Array<String> {
        when (permission) {
            CALENDAR -> return GROUP_CALENDAR
            CAMERA -> return GROUP_CAMERA
            CONTACTS -> return GROUP_CONTACTS
            LOCATION -> return GROUP_LOCATION
            MICROPHONE -> return GROUP_MICROPHONE
            PHONE -> return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                GROUP_PHONE_BELOW_O
            } else {
                GROUP_PHONE
            }
            SENSORS -> return GROUP_SENSORS
            SMS -> return GROUP_SMS
            STORAGE -> return GROUP_STORAGE
        }
        return arrayOf(permission)
    }
}