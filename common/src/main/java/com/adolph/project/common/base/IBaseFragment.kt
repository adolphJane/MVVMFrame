package com.adolph.project.common.base

interface IBaseFragment {
    /**
     * 初始化界面传递参数
     */
    fun initParam()

    /**
     * 初始化数据
     */
    fun initData()

    /**
     * 懒加载数据
     */
    fun lazyInitData()

    /**
     * 初始化界面观察者的监听
     */
    fun initViewObservable()

    /**
     * 回退监听
     */
    fun onBackPressed()
}