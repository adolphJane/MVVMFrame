package com.magicalrice.project.library_common.base

interface IBaseActivity {
    /**
     * 初始化界面传递参数
     */
    fun initParam()

    /**
     * 初始化视图
     */
    fun initView()

    /**
     * 初始化数据
     */
    fun initData()

    /**
     * 界面前台展示时初始化数据
     */
    fun initResumeData()

    /**
     * 初始化界面观察者的监听
     */
    fun initViewObservable()
}