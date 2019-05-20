package com.magicalrice.project.library_base.base.log

/**
 * @package com.magicalrice.project.library_base.base.log
 * @author Adolph
 * @date 2019-04-22 Mon
 * @description TODO
 */

abstract class IFormatter<T> {
    abstract fun format(t: T): String
}