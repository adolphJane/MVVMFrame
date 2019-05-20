package com.magicalrice.project.library_base.advanced

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2018/03/21
 * desc  : 位运算工具类
</pre> *
 */
object BitUtils {

    /**
     * 获取运算数指定位置的值<br></br>
     * 例如： 0000 1011 获取其第 0 位的值为 1, 第 2 位 的值为 0<br></br>
     *
     * @param source 需要运算的数
     * @param pos    指定位置 (0...7)
     * @return 指定位置的值(0 or 1)
     */
    fun getBitValue(source: Byte, pos: Int): Byte {
        return (source.toInt() shr pos and 1).toByte()

    }


    /**
     * 将运算数指定位置的值置为指定值<br></br>
     * 例: 0000 1011 需要更新为 0000 1111, 即第 2 位的值需要置为 1<br></br>
     *
     * @param source 需要运算的数
     * @param pos    指定位置 (0<=pos<=7)
     * @param value  只能取值为 0, 或 1, 所有大于0的值作为1处理, 所有小于0的值作为0处理
     * @return 运算后的结果数
     */
    fun setBitValue(source: Byte, pos: Int, value: Byte): Byte {
        var source = source.toInt()

        val mask = 1 shl pos
        if (value > 0) {
            source = source or mask

        } else {
            source = source and mask.inv()

        }
        return source.toByte()
    }


    /**
     * 将运算数指定位置取反值<br></br>
     * 例： 0000 1011 指定第 3 位取反, 结果为 0000 0011; 指定第2位取反, 结果为 0000 1111<br></br>
     *
     * @param source
     * @param pos    指定位置 (0<=pos<=7)
     * @return 运算后的结果数
     */
    fun reverseBitValue(source: Byte, pos: Int): Byte {
        val mask = (1 shl pos)
        return (source.toInt() xor mask).toByte()

    }


    /**
     * 检查运算数的指定位置是否为1<br></br>
     *
     * @param source 需要运算的数
     * @param pos    指定位置 (0<=pos<=7)
     * @return true 表示指定位置值为1, false 表示指定位置值为 0
     */
    fun checkBitValue(source: Byte, pos: Int): Boolean {
        var source = source.toInt()

        source = source.ushr(pos)

        return source and 1 == 1

    }
}
