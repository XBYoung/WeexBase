package com.young.weexbase.utils

import android.os.Build
import android.os.Environment
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.*

/**
 * Created by luke on 2016/10/24.
 */
object DeviceUtils {
    val version: String
        get() {
            return Build.VERSION.RELEASE
        }

    //获取手机型号
    val phoneModel: String
        get() = Build.MODEL

    val product: String
        get() = Build.MANUFACTURER

    val porductAndModel: String
        get() {
            val sb = StringBuffer()
            sb.append(product)
            sb.append("-")
            sb.append(phoneModel)
            return sb.toString()
        }


    val device: String
        get() = Build.MANUFACTURER


    /**
     * 获取当前手机系统版本号
     *
     * @return  系统版本号
     */
    fun getSystemVersion(): String {
        return Build.VERSION.RELEASE
    }


    @JvmStatic
    fun getDeviceId(): String {
        var serial: String
        var m_head = "35" +
                Build.BOARD.length % 10 + Build.BRAND.length % 10 +

                Build.CPU_ABI.length % 10 + Build.DEVICE.length % 10 +

                Build.DISPLAY.length % 10 + Build.HOST.length % 10 +

                Build.ID.length % 10 + Build.MANUFACTURER.length % 10 +

                Build.MODEL.length % 10 + Build.PRODUCT.length % 10 +

                Build.TAGS.length % 10 + Build.TYPE.length % 10 +

                Build.USER.length % 10  //13 位

        try {
            serial = android.os.Build::class.java.getField("SERIAL").get(null).toString()
            return UUID(m_head.hashCode().toLong(), serial.hashCode().toLong()).toString()
        } catch (e: Exception) {
            serial = "serial"
        }
        return UUID(m_head.hashCode().toLong(), serial.hashCode().toLong()).toString()
    }

    val SYS_EMUI = "sys_emui"
    val SYS_MIUI = "sys_miui"
    val SYS_FLYME = "sys_flyme"
    private val KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code"
    private val KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name"
    private val KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage"
    private val KEY_EMUI_API_LEVEL = "ro.build.hw_emui_api_level"
    private val KEY_EMUI_VERSION = "ro.build.version.emui"
    private val KEY_EMUI_CONFIG_HW_SYS_VERSION = "ro.confg.hw_systemversion"

    //小米
    //华为
    //魅族
    val system: String
        get() {
            var SYS = ""
            try {
                val prop = Properties()
                prop.load(FileInputStream(File(Environment.getRootDirectory(), "build.prop")))
                if (prop.getProperty(KEY_MIUI_VERSION_CODE, null) != null
                        || prop.getProperty(KEY_MIUI_VERSION_NAME, null) != null
                        || prop.getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null) {
                    SYS = SYS_MIUI
                } else if (prop.getProperty(KEY_EMUI_API_LEVEL, null) != null
                        || prop.getProperty(KEY_EMUI_VERSION, null) != null
                        || prop.getProperty(KEY_EMUI_CONFIG_HW_SYS_VERSION, null) != null) {
                    SYS = SYS_EMUI
                } else if (meizuFlymeOSFlag.toLowerCase().contains("flyme")) {
                    SYS = SYS_FLYME
                }
            } catch (e: IOException) {
                e.printStackTrace()
                return SYS
            }

            return SYS
        }

    val meizuFlymeOSFlag: String
        get() = getSystemProperty("ro.build.display.id", "")

    private fun getSystemProperty(key: String, defaultValue: String): String {
        try {
            val clz = Class.forName("android.os.SystemProperties")
            val get = clz.getMethod("get", String::class.java, String::class.java)
            return get.invoke(clz, key, defaultValue) as String
        } catch (e: Exception) {
        }

        return defaultValue
    }

    /**
     * 获取当前时区

     * @return
     */
    fun getCurrentTimeZone(includeGmt: Boolean,
                           includeMinuteSeparator: Boolean, isFour: Boolean): String {
        val tz = TimeZone.getDefault()
        return createGmtOffsetString(includeGmt, includeMinuteSeparator, tz.rawOffset, isFour)
    }

    private fun createGmtOffsetString(includeGmt: Boolean,
                                      includeMinuteSeparator: Boolean, offsetMillis: Int, isFour: Boolean): String {
        var offsetMinutes = offsetMillis / 60000
        var sign = '+'
        if (offsetMinutes < 0) {
            sign = '-'
            offsetMinutes = -offsetMinutes
        }
        val builder = StringBuilder(9)
        if (includeGmt) {
            builder.append("GMT")
        }
        builder.append(sign)
        if (isFour) {
            appendNumber(builder, 2, offsetMinutes / 60)
            if (includeMinuteSeparator) {
                builder.append(':')
            }
            appendNumber(builder, 2, offsetMinutes % 60)
        } else {
            builder.append(offsetMinutes / 60)
        }
        return builder.toString()
    }

    /**
     * 返回四位需要

     * @param builder
     * *
     * @param count
     * *
     * @param value
     */

    private fun appendNumber(builder: StringBuilder, count: Int, value: Int) {
        val string = Integer.toString(value)
        for (i in 0..count - string.length - 1) {
            builder.append('0')
        }
        builder.append(string)
    }

    val OS_VERSION = Build.VERSION.RELEASE

}
