package com.magicalrice.project.library_base.base

import android.widget.EditText
import java.math.BigDecimal
import java.text.DecimalFormat

/**
 * @package com.magicalrice.project.library_base.base
 * @author Adolph
 * @date 2019-04-22 Mon
 * @description 数字工具类
 */

object NumberUtil {
    private val amountFormat = DecimalFormat("###,###,###,##0.00")

    /**
     * 四舍五入
     *
     * @param value 数值
     * @param digit 保留小数位
     * @return
     */
    fun getRoundUp(value: BigDecimal, digit: Int): String {
        return value.setScale(digit, BigDecimal.ROUND_HALF_UP).toString()
    }

    /**
     * 四舍五入
     *
     * @param value 数值
     * @param digit 保留小数位
     * @return
     */
    fun getRoundUp(value: Double, digit: Int): String {
        val result = BigDecimal(value)
        return result.setScale(digit, BigDecimal.ROUND_HALF_UP).toString()
    }

    /**
     * 四舍五入
     *
     * @param value 数值
     * @param digit 保留小数位
     * @return
     */
    fun getRoundUp(value: String, digit: Int): String {
        val result = BigDecimal(java.lang.Double.parseDouble(value))
        return result.setScale(digit, BigDecimal.ROUND_HALF_UP).toString()
    }

    /**
     * 获取百分比（乘100）
     *
     * @param value 数值
     * @param digit 保留小数位
     * @return
     */
    fun getPercentValue(value: BigDecimal, digit: Int): String {
        val result = value.multiply(BigDecimal(100))
        return getRoundUp(result, digit)
    }

    /**
     * 获取百分比（乘100）
     *
     * @param value 数值
     * @param digit 保留小数位
     * @return
     */
    fun getPercentValue(value: Double, digit: Int): String {
        val result = BigDecimal(value)
        return getPercentValue(result, digit)
    }

    /**
     * 获取百分比（乘100,保留两位小数）
     *
     * @param value 数值
     * @return
     */
    fun getPercentValue(value: Double): String {
        val result = BigDecimal(value)
        return getPercentValue(result, 2)
    }

    /**
     * 金额格式化
     *
     * @param value 数值
     * @return
     */
    fun getAmountValue(value: Double): String {
        return amountFormat.format(value)
    }

    /**
     * 金额格式化
     *
     * @param value 数值
     * @return
     */
    fun getAmountValue(value: String): String {
        return amountFormat.format(java.lang.Double.parseDouble(value))
    }

    /**
     * int -tostring
     *
     * @param value 数值
     * @return
     */
    fun getIntegerValue(value: Int): String {
        return Integer.valueOf(value).toString()
    }

    /**
     * onTextChanged
     * @param sequence  (CharSequenc s
     * @param editText
     */
    fun formatDot(sequence: CharSequence, editText: EditText) {
        var s = sequence.toString()
        if (s.contains(".")) {
            /**
             * 如果小数点位数大于两位 截取后两位
             */
            if (s.length - 1 - s.indexOf(".") > 2) {
                s = s.substring(0, s.indexOf(".") + 3)
                editText.setText(s)
                editText.setSelection(s.length)
            }
        }
        /**
         * 如果第一个输入为小数点 ，自动补零
         */
        if (s.trim { it <= ' ' }.substring(0) == ".") {
            s = "0$s"
            editText.setText(s)
            editText.setSelection(s.length)
        }
        /**
         * 如果第一个第二个均为0
         */
        if (s.startsWith("0") && s.trim { it <= ' ' }.length > 1) {
            if (s.substring(1, 2) != ".") {
                editText.setText(s.substring(0, 1))
                editText.setSelection(1)
                return
            }
        }
    }
}