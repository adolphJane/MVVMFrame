package com.magicalrice.project.library_base.base.constant

import androidx.annotation.IntDef

/**
 * @package com.magicalrice.project.library_base.base.constant
 * @author Adolph
 * @date 2019-04-22 Mon
 * @description TODO
 */

const val MSEC = 1
const val SEC = 1000
const val MIN = 60000
const val HOUR = 3600000
const val DAY = 86400000

@IntDef(MSEC, SEC, MIN, HOUR, DAY)
@Retention(AnnotationRetention.SOURCE)
annotation class TimeConstants
