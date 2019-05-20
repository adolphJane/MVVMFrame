package com.magicalrice.project.library_base.advanced.receiver

/**
 * 辅助判断
 *
 * @author mty
 * @date 2013-6-10下午5:50:57
 */
object Check {

    fun isEmpty(str: CharSequence): Boolean {
        return isNull(str) || str.isEmpty()
    }

    fun isEmpty(os: Array<Any>): Boolean {
        return isNull(os) || os.isEmpty()
    }

    fun isEmpty(l: Collection<*>): Boolean {
        return isNull(l) || l.isEmpty()
    }

    fun isEmpty(m: Map<*, *>): Boolean {
        return isNull(m) || m.isEmpty()
    }

    fun isNull(o: Any?): Boolean {
        return o == null
    }
}
