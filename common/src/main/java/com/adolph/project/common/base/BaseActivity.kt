package com.adolph.project.common.base

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.adolph.project.baseutils.AppSystemBarMgr
import com.adolph.project.baseutils.KeyboardUtils
import com.adolph.project.baseutils.ToastUtils
import com.adolph.project.basewidgets.HeaderView
import com.adolph.project.common.R
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity
import java.lang.reflect.ParameterizedType

abstract class BaseActivity<V : ViewDataBinding, VM : BaseViewModel> : RxAppCompatActivity(),IBaseActivity {
    protected lateinit var binding: V
    protected var viewModel: VM? = null
    protected var dialog: AlertDialog? = null
    private var mLastClickTime: Long = 0
    private val CLICK_TIME = 1500
    @get:LayoutRes
    protected abstract val layoutId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //页面接受的参数方法
        initParam()
        //私有的初始化Databinding和ViewModel方法
        initViewDataBinding(savedInstanceState)
        //私有的ViewModel与View的契约事件回调逻辑
        registorUIChangeLiveDataCallBack()
        //页面视图初始化方法
        initView()
        //页面数据初始化方法
        initData()
        //页面事件监听的方法，一般用于ViewModel层转到View层的事件注册
        initViewObservable()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel?.let {
            lifecycle.removeObserver(it)
        }
        viewModel = null
        binding.unbind()
    }

    /**
     * 注入绑定
     */
    @Suppress("UNCHECKED_CAST")
    private fun initViewDataBinding(savedInstanceState: Bundle?) {
        //DataBindingUtil类需要在project的build中配置 dataBinding {enabled true }, 同步后会自动关联android.databinding包
        binding = DataBindingUtil.setContentView(this, layoutId)
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
        binding.setLifecycleOwner(this)
        viewModel?.let {
            //让ViewModel拥有View的生命周期感应
            lifecycle.addObserver(it)
            //注入RxLifecycle生命周期
            it.injectLifecycleProvider(this)
        }
        setBackButton()
    }

    /**
     * =====================================================================
     */
    //注册ViewModel与View的契约UI回调事件
    private fun registorUIChangeLiveDataCallBack() {
        //加载对话框显示
        viewModel?.getUc()?.showDialogEvent?.observe(this, Observer {
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
//            val builder = MaterialDialogUtils.showIndeterminateProgressDialog(this, title, true)
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
        startActivity(Intent(this, clz))
    }

    /**
     * 跳转页面
     *
     * @param clz    所跳转的目的Activity类
     * @param bundle 跳转所携带的信息
     */
    fun startActivity(clz: Class<*>, bundle: Bundle?) {
        val intent = Intent(this, clz)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        startActivity(intent)
    }

    /**
     * =====================================================================
     */
    override fun initParam() {

    }

    /**
     * 初始化ViewModel
     *
     * @return 继承BaseViewModel的ViewModel
     */
    fun initViewModel(): VM? {
        return null
    }

    override fun initView() {

    }

    override fun initData() {

    }

    override fun initViewObservable() {

    }

    /**
     * 创建ViewModel
     *
     * @param cls
     * @param <T>
     * @return
    </T> */
    fun <T : ViewModel> createViewModel(activity: FragmentActivity, cls: Class<T>): T {
        return ViewModelProviders.of(activity).get(cls)
    }

    /**
     * 检测双击
     */
    protected fun vertifyClickTime(): Boolean {
        if (System.currentTimeMillis() - mLastClickTime > CLICK_TIME) {
            mLastClickTime = System.currentTimeMillis()
            return true
        }
        return false
    }

    fun setStatusBar() {
        if (this is BaseActivity) {
            //注意替换BaseActivity为需要显示沉浸式图片的Activity
            AppSystemBarMgr.setTranslucentForImageView(this,null)
        } else {
            AppSystemBarMgr.setColorNoTranslucent(this, ContextCompat.getColor(this, R.color.white1))
        }
        AppSystemBarMgr.setAndroidNativeLightStatusBar(this,true)
    }

    fun hideInputMethod() {
        KeyboardUtils.hideSoftInput(this)
    }

    private fun setBackButton() {
        val header = findViewById<HeaderView?>(R.id.header_view)
        header?.let {
            it.setBackListener(View.OnClickListener {
                finish()
            })
        }
    }

    fun showToast(content: String) {
        ToastUtils.showShort(content)
    }
}