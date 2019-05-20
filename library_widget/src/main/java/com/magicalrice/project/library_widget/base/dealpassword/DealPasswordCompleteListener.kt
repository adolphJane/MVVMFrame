package com.magicalrice.project.library_widget.base.dealpassword

/**
 * @package com.magicalrice.project.library_widget.base.dealpassword
 * @author Adolph
 * @date 2019-04-20 Sat
 * @description 验证码输入框监听事件
 */
interface DealPasswordCompleteListener {
    fun passwordComplete(pwd: String, isComplete: Boolean)
    fun enter()
}