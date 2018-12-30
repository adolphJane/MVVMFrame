package com.ixiye.project.daihuo.utils

import android.content.Context
import android.content.SharedPreferences
import com.adolph.project.baseutils.AppManageUtils

class SPUtils {
    private var sp : SharedPreferences = AppManageUtils.getApp().applicationContext.getSharedPreferences("daihuo",Context.MODE_PRIVATE)

    companion object {
        fun getInstance() = Holder.INSTANCE
    }

    private object Holder {
        val INSTANCE = SPUtils()
    }

    fun put(key: String,objects: Any) : Boolean{
        val editor = sp.edit()
        when (objects) {
            is String -> editor.putString(key,objects)
            is Int -> editor.putInt(key,objects)
            is Float -> editor.putFloat(key,objects)
            is Long -> editor.putLong(key,objects)
            is Boolean -> editor.putBoolean(key,objects)
        }
        return editor.commit()
    }

    fun putApply(key: String,objects: Any) {
        val editor = sp.edit()
        when (objects) {
            is String -> editor.putString(key,objects)
            is Int -> editor.putInt(key,objects)
            is Float -> editor.putFloat(key,objects)
            is Long -> editor.putLong(key,objects)
            is Boolean -> editor.putBoolean(key,objects)
        }
        editor.apply()
    }

    fun get(key: String,default: Any) : Any {
        when (default) {
            is String -> return sp.getString(key,default)
            is Int -> return sp.getInt(key,default)
            is Float -> return sp.getFloat(key,default)
            is Long -> return sp.getLong(key,default)
            is Boolean -> return sp.getBoolean(key,default)
        }
        return ""
    }

    fun contains(key: String) : Boolean {
        return sp.contains(key)
    }

    fun remove(key: String) : Boolean{
        return sp.edit().remove(key).commit()
    }

    fun removeApply(key: String) {
        sp.edit().remove(key).apply()
    }

    fun clear() : Boolean{
        return sp.edit().clear().commit()
    }

    fun clearApply() {
        sp.edit().clear().apply()
    }
}