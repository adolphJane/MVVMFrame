package com.adolph.project.common.base

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.trello.rxlifecycle3.components.support.RxFragment
import java.lang.reflect.ParameterizedType


abstract class BaseFragment<V : ViewDataBinding,VM : BaseViewModel> : RxFragment(),IBaseFragment {
    //标志位 判断fragment是否可见
    protected var isVisibleToUser = false
    //标志位 判断view已经加载完成 避免空指针操作
    protected var isPrepareView = false
    protected lateinit var binding: V
    protected var viewModel: VM? = null
    //子类必须实现，用于创建 view
    @get:LayoutRes
    protected abstract val layoutId: Int
    private var dialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initParam()
    }

    override fun onDestroy() {
        super.onDestroy()
        //解除ViewModel生命周期感应
        viewModel?.let {
            lifecycle.removeObserver(it)
            viewModel = null
        }
        binding.unbind()
    }

    @Suppress("UNCHECKED_CAST")
    @Nullable
    override fun onCreateView(inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<ViewDataBinding>(
            inflater,
            layoutId,
            container,
            false
        ) as V
        viewModel = initViewModel()
        if (viewModel == null) {
            val modelClass: Class<*>
            val type = javaClass.genericSuperclass
            modelClass = if (type is ParameterizedType) {
                type.actualTypeArguments[1] as Class<BaseViewModel>
            } else {
                //如果没有指定泛型参数，则默认使用BaseViewModel
                BaseViewModel::class.java
            }
            viewModel = createViewModel(this, modelClass) as VM
        }

        viewModel?.let {
            //让ViewModel拥有View的生命周期感应
            lifecycle.addObserver(it)
            //注入RxLifecycle生命周期
            it.injectLifecycleProvider(this)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //私有的ViewModel与View的契约事件回调逻辑
        registorUIChangeLiveDataCallBack()
        //页面数据初始化方法
        initData()
        //页面事件监听的方法，一般用于ViewModel层转到View层的事件注册
        initViewObservable()

        isPrepareView = true
        if (isPrepareView && isVisibleToUser) {
            lazyInitData()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initData()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        this.isVisibleToUser = isVisibleToUser
        if (isVisibleToUser && isPrepareView) {
            lazyInitData()
        }
    }

    override fun lazyInitData() {

    }

    /**
     * =====================================================================
     */
    //注册ViewModel与View的契约UI回调事件
    private fun registorUIChangeLiveDataCallBack() {
        //加载对话框显示
        viewModel?.getUc()?.showDialogEvent?.observe(this, Observer{
                showDialog()
        })
        //加载对话框消失
        viewModel?.getUc()?.dismissDialogEvent?.observe(this, Observer {
                dismissDialog()
        })
    }

    fun showDialog() {
        if (dialog != null) {
            dialog?.show()
        } else {
//            val builder = MaterialDialogUtils.showIndeterminateProgressDialog(activity, title, true)
//            dialog = builder.show()
        }
    }

    fun dismissDialog() {
        if (dialog != null && dialog?.isShowing == true) {
            dialog?.dismiss()
        }
    }

    /**
     * 跳转页面
     *
     * @param clz 所跳转的目的Activity类
     */
    fun startActivity(clz: Class<*>) {
        startActivity(Intent(context, clz))
    }

    /**
     * 跳转页面
     *
     * @param clz    所跳转的目的Activity类
     * @param bundle 跳转所携带的信息
     */
    fun startActivity(clz: Class<*>, bundle: Bundle?) {
        val intent = Intent(context, clz)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        startActivity(intent)
    }

    override fun initParam() {

    }

    /**
     * 初始化根布局
     *
     * @return 布局layout的id
     */
    abstract fun initContentView(inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): Int

    /**
     * 初始化ViewModel的id
     *
     * @return BR的id
     */
    abstract fun initVariableId(): Int

    /**
     * 初始化ViewModel
     *
     * @return 继承BaseViewModel的ViewModel
     */
    fun initViewModel(): VM? {
        return null
    }

    override fun initData() {

    }

    override fun initViewObservable() {

    }

    override fun onBackPressed() {
    }

    /**
     * 创建ViewModel
     *
     * @param cls
     * @param <T>
     * @return
    </T> */
    fun <T : ViewModel> createViewModel(fragment: Fragment, cls: Class<T>): T {
        return ViewModelProviders.of(fragment).get(cls)
    }
}