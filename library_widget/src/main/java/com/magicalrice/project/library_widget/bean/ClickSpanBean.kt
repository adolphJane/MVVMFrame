package com.magicalrice.project.library_widget.bean

import com.magicalrice.project.library_widget.base.clickspan.NoLineClickSpan

/**
 * @package com.magicalrice.project.library_widget.bean
 * @author Adolph
 * @date 2019-04-20 Sat
 * @description 点击文本Bean
 */

data class ClickSpanBean(
    var content: String,
    var isClickable: Boolean,
    var listener: NoLineClickSpan.OnClickSkipListener?
)