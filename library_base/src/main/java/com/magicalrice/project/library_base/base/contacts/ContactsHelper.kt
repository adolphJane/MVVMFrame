package com.magicalrice.project.library_base.base.contacts

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.ContactsContract.Contacts.Data
import android.text.TextUtils
import java.util.*

/**
 * 操作手机联系人的帮助类，提供了增删改查单个联系人的方法.
 * @author Jack Tony
 * @date 2015/5/11
 */
class ContactsHelper(private val mContext: Context) {

    /** 联系人显示名称*  */
    private val PHONES_DISPLAY_NAME_INDEX = 0

    /** 电话号码*  */
    private val PHONES_NUMBER_INDEX = 1

    /** 联系人姓名+号码的list*  */
    private val contactsList = ArrayList<ContactModel>()

    /**
     * 得到手机中的联系人列表
     *
     * @return 得到联系人列表
     */
    fun getContactsList(): ArrayList<ContactModel> {
        try {
            //得到手机中的联系人信息
            getPhoneContacts()
            //得到sim卡中联系人信息
            getSIMContacts()
        } catch (e: Exception) {
        }

        return contactsList
    }

    /**
     * 得到手机通讯录联系人信息
     */
    private fun getPhoneContacts() {
        val resolver = mContext.contentResolver
        //query查询，得到结果的游标
        val phoneCursor = resolver.query(Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null)

        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
                //得到手机号码
                val phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX)
                //当手机号码为空的或者为空字段 跳过当前循环
                if (TextUtils.isEmpty(phoneNumber)) {
                    continue
                }
                //得到联系人名称
                val contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX)

                val cb = ContactModel("", "")
                cb.contactName = contactName
                cb.phoneNumber = phoneNumber
                contactsList.add(cb)
            }
            phoneCursor.close()
        }
    }

    /**
     * 得到手机SIM卡联系人人信息
     */
    private fun getSIMContacts() {

        val resolver = mContext.contentResolver
        val uri = Uri.parse("content://icc/adn")
        val phoneCursor = resolver.query(uri, PHONES_PROJECTION, null, null, null)

        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
                // 得到手机号码
                val phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX)
                // 当手机号码为空的或者为空字段 跳过当前循环
                if (TextUtils.isEmpty(phoneNumber)) {
                    continue
                }
                // 得到联系人名称
                val contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX)

                val cb = ContactModel("", "")
                cb.contactName = contactName
                cb.phoneNumber = phoneNumber
                contactsList.add(cb)
            }
            phoneCursor.close()
        }
    }

    /**
     * 添加一个联系人数据
     *
     * @return 返回true表示添加成功，false表示失败
     */
    fun insert(name: String, phoneNumber: String): Boolean {
        //根据号码找数据，如果存在则不添加
        if (getNameByPhoneNumber(phoneNumber) == null) {
            //插入raw_contacts表，并获取_id属性
            var uri = Uri.parse("content://com.android.contacts/raw_contacts")
            val resolver = mContext.contentResolver
            val values = ContentValues()
            val contact_id = ContentUris.parseId(resolver.insert(uri, values))
            //插入data表
            uri = Uri.parse("content://com.android.contacts/data")
            //添加姓名
            values.put("raw_contact_id", contact_id)
            values.put(Data.MIMETYPE, "vnd.android.cursor.item/name")
            values.put("data2", name)
            resolver.insert(uri, values)
            values.clear()
            //添加手机号码
            values.put("raw_contact_id", contact_id)
            values.put(Data.MIMETYPE, "vnd.android.cursor.item/phone_v2")
            values.put("data2", "2")    //2表示手机
            values.put("data1", phoneNumber)
            resolver.insert(uri, values)
            values.clear()
            return true
        } else {
            return false
        }
    }

    //

    /**
     * 删除单个数据，会直接删除是这个名字的人的所有信息
     *
     * @param name 用户的姓名
     * @return 是否删除成功
     */
    fun delete(name: String): Boolean {
        try {
            //根据姓名求id
            var uri = Uri.parse("content://com.android.contacts/raw_contacts")
            val resolver = mContext.contentResolver
            //查询到name=“name”的集合
            val cursor = resolver.query(
                uri, arrayOf(Data._ID),
                "display_name=?", arrayOf(name), null
            )
            if (cursor!!.moveToFirst()) {
                val id = cursor.getInt(0)
                //根据id删除data中的相应数据
                resolver.delete(uri, "display_name=?", arrayOf(name))
                uri = Uri.parse("content://com.android.contacts/data")
                resolver.delete(uri, "raw_contact_id=?", arrayOf(id.toString() + ""))
                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }

    /**
     * 修改联系人数据
     */
    fun update(name: String, phoneNumber: String): Boolean {
        try {
            //根据姓名求id,再根据id删除
            val uri = Uri.parse("content://com.android.contacts/raw_contacts")
            val resolver = mContext.contentResolver
            val cursor =
                resolver.query(uri, arrayOf(Data._ID), "display_name=?", arrayOf(name), null)
            if (cursor!!.moveToFirst()) {
                val id = cursor.getInt(0)
                val mUri = Uri.parse("content://com.android.contacts/data")//对data表的所有数据操作
                val mResolver = mContext.contentResolver
                val values = ContentValues()
                values.put("data1", phoneNumber)
                mResolver.update(
                    mUri,
                    values,
                    "mimetype=? and raw_contact_id=?",
                    arrayOf("vnd.android.cursor.item/phone_v2", id.toString() + "")
                )
                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }

    /**
     * 根据电话号码查询姓名
     *
     * @return 返回这个电话的主人名，如果没有则返回null
     */
    fun getNameByPhoneNumber(phoneNumber: String): String? {
        var name: String? = null
        //uri=  content://com.android.contacts/data/phones/filter/#
        val uri = Uri.parse("content://com.android.contacts/data/phones/filter/$phoneNumber")
        val resolver = mContext.contentResolver
        //从raw_contact表中返回display_name
        val cursor = resolver.query(uri, arrayOf("display_name"), null, null, null)
        if (cursor!!.moveToFirst()) {
            name = cursor.getString(0)
        }
        cursor.close()
        return name
    }

    companion object {

        /**
         * 获取库Phone表字段,仅仅获取电话号码联系人等
         * 它所指向的其实是“content:// com.android.contacts/data/phones”。
         * 这个url对应着contacts表 和 raw_contacts表 以及 data表 所以说我们的数据都是从这三个表中获取的。
         */
        private val PHONES_PROJECTION = arrayOf(Phone.DISPLAY_NAME, Phone.NUMBER, Phone.CONTACT_ID)
    }


}