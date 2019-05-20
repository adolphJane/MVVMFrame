package com.magicalrice.project.library_base.base.constant

import androidx.annotation.IntDef

/**
 * @package com.magicalrice.project.library_base.base.constant
 * @author Adolph
 * @date 2019-04-22 Mon
 * @description TODO
 */
const val BYTE = 1
const val KB = 1024
const val MB = 1048576
const val GB = 1073741824

@IntDef(BYTE, KB, MB, GB)
@Retention(AnnotationRetention.SOURCE)
annotation class MemoryConstants
