package com.magicalrice.project.library_base.advanced

import android.annotation.SuppressLint
import android.os.Build
import android.os.Environment
import android.text.TextUtils
import java.io.*
import java.util.*

/**
 */
object RomUtils {

    const val SYS_EMUI = "emui"
    const val SYS_MIUI = "miui"
    const val SYS_FLYME = "flyme"
    const val SYS_COLOROS = "colorOs"
    const val SYS_FUNTOUCH = "Funtouch"
    const val SYS_SAMSUNG = "samsung"

    ///////////////////////////////////////////////////////////////////////////
    // MIUI
    ///////////////////////////////////////////////////////////////////////////
    private val KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code"
    private val KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name"
    private val KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage"
    private val KEY_MIUI_VERSION_INCREMENTAL = "ro.build.version.incremental"

    ///////////////////////////////////////////////////////////////////////////
    // EMUI
    ///////////////////////////////////////////////////////////////////////////
    private val KEY_EMUI_API_LEVEL = "ro.build.hw_emui_api_level"
    private val KEY_EMUI_VERSION = "ro.build.version.emui"
    private val KEY_EMUI_CONFIG_HW_SYS_VERSION = "ro.confg.hw_systemversion"

    ///////////////////////////////////////////////////////////////////////////
    // OPPO
    ///////////////////////////////////////////////////////////////////////////
    private val KEY_OPPO_NAME = "ro.rom.different.version"
    private val KEY_OPPO_VERSION = "ro.build.version.opporom"

    ///////////////////////////////////////////////////////////////////////////
    // VIVO
    ///////////////////////////////////////////////////////////////////////////
    private val KEY_VIVO_NAME = "ro.vivo.os.name"
    private val KEY_VIVO_VERSION = "ro.vivo.os.version"

    private var bean: RomBean? = null

    /**
     * Return the name of rom.
     *
     * @return the name of rom
     */
    // 小米
    // 华为
    // EmotionUI_2.0
    // 魅族
    // OPPO
    // VIVO
    // 其他手机
    val rom: RomBean
        get() {
            if (bean != null) return bean!!
            bean = RomBean()
            if (!TextUtils.isEmpty(getSystemProperty(KEY_MIUI_VERSION_CODE))
                || !TextUtils.isEmpty(getSystemProperty(KEY_MIUI_VERSION_NAME))
                || !TextUtils.isEmpty(getSystemProperty(KEY_MIUI_INTERNAL_STORAGE))
            ) {
                bean?.setRomName(SYS_MIUI)
                bean?.setRomVersion(getSystemProperty(KEY_MIUI_VERSION_INCREMENTAL))
            } else if (!TextUtils.isEmpty(getSystemProperty(KEY_EMUI_API_LEVEL))
                || !TextUtils.isEmpty(getSystemProperty(KEY_EMUI_VERSION))
                || !TextUtils.isEmpty(getSystemProperty(KEY_EMUI_CONFIG_HW_SYS_VERSION))
            ) {
                bean?.setRomName(SYS_EMUI)
                val version = getSystemProperty(KEY_EMUI_VERSION)
                val temp =
                    version.split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (temp.size > 1) {
                    bean?.setRomVersion(temp[1])
                } else {
                    bean?.setRomVersion(version)
                }
            } else if (Build.DISPLAY.toLowerCase().contains("flyme")) {
                bean?.setRomName(SYS_FLYME)
                bean?.setRomVersion(Build.DISPLAY)
                return bean!!
            } else if (!TextUtils.isEmpty(getSystemProperty(KEY_OPPO_NAME)) && getSystemProperty(
                    KEY_OPPO_NAME
                ).toLowerCase().contains("coloros")
            ) {
                bean?.setRomName(SYS_COLOROS)
                bean?.setRomVersion(getSystemProperty(KEY_OPPO_VERSION))
            } else if (!TextUtils.isEmpty(getSystemProperty(KEY_VIVO_NAME))) {
                bean?.setRomName(SYS_FUNTOUCH)
                bean?.setRomVersion(getSystemProperty(KEY_VIVO_VERSION))
            } else {
                val brand = Build.BRAND
                bean?.setRomName(Build.BRAND)
                if (SYS_SAMSUNG.equals(brand, ignoreCase = true)) {
                    bean?.setRomVersion(getSystemProperty("ro.build.changelist"))
                }
            }
            return bean!!
        }

    private fun getSystemProperty(name: String): String {
        var prop = getSystemPropertyByShell(name)
        if (!TextUtils.isEmpty(prop)) return prop
        prop = getSystemPropertyByStream(name)
        if (!TextUtils.isEmpty(prop)) return prop
        return if (Build.VERSION.SDK_INT < 28) {
            getSystemPropertyByReflect(name)
        } else prop
    }

    private fun getSystemPropertyByShell(propName: String): String {
        val line: String
        var input: BufferedReader? = null
        try {
            val p = Runtime.getRuntime().exec("getprop $propName")
            input = BufferedReader(InputStreamReader(p.inputStream), 1024)
            return input.readLine()
        } catch (e: IOException) {
            return ""
        } finally {
            if (input != null) {
                try {
                    input.close()
                } catch (ignore: IOException) {

                }

            }
        }
    }

    private fun getSystemPropertyByStream(key: String): String {
        try {
            val prop = Properties()
            val `is` = FileInputStream(
                File(Environment.getRootDirectory(), "build.prop")
            )
            prop.load(`is`)
            return prop.getProperty(key, "")
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }

    }

    private fun getSystemPropertyByReflect(key: String): String {
        try {
            @SuppressLint("PrivateApi")
            val clz = Class.forName("android.os.SystemProperties")
            val get = clz.getMethod("get", String::class.java, String::class.java)
            return get.invoke(clz, key, "") as String
        } catch (e: Exception) {
            return ""
        }

    }

    class RomBean {
        private var romName: String? = null
        private var romVersion: String? = null

        fun getRomName(): String {
            return if (romName == null) "" else romName ?: ""
        }

        fun setRomName(romName: String) {
            this.romName = romName
        }

        fun getRomVersion(): String {
            return if (romVersion == null) "" else romVersion ?: ""
        }

        fun setRomVersion(romVersion: String) {
            this.romVersion = romVersion
        }

        override fun toString(): String {
            return "romName: " + romName +
                    "\nromVersion: " + romVersion
        }
    }
}
