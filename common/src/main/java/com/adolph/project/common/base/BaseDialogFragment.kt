package com.adolph.project.common.base

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.adolph.project.common.R
import com.trello.rxlifecycle3.components.support.RxDialogFragment
import java.lang.reflect.ParameterizedType


abstract class BaseDialogFragment<VM: BaseViewModel> : RxDialogFragment() {
    private var windowWidth = WindowManager.LayoutParams.MATCH_PARENT
    private var windowHeight = WindowManager.LayoutParams.MATCH_PARENT
    private var windowGravity = Gravity.CENTER
    protected var viewModel: VM? = null
    //子类必须实现，用于创建 view
    @get:LayoutRes
    protected abstract val layoutId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(activity!!, getDialogStyle())
        dialog.setContentView(initView())
        return dialog
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initData()
    }

    abstract fun initData()

    override fun onStart() {
        super.onStart()
        measureDialog()
    }

    fun setWindowSize(withRatio: Double,heightRatio: Double) {
        val window = dialog.window
        val metrics = DisplayMetrics()
        window?.windowManager?.defaultDisplay?.getMetrics(metrics)
        windowWidth = (withRatio * metrics.widthPixels).toInt()
        windowHeight = (withRatio * metrics.heightPixels).toInt()
    }

    fun setGravity(gravity: Int) {
        this.windowGravity = gravity
    }

    private fun measureDialog() {
        val window = dialog.window
        val params = window?.attributes
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        params?.gravity = windowGravity
        params?.width = windowWidth
        params?.height = windowHeight
        window?.attributes = params
    }

    protected open fun initView() : View{
        val view = layoutInflater.inflate(layoutId,null,false)
        initView(view)
        return view
    }

    abstract fun initView(view: View)

    /**
     * 初始化ViewModel
     *
     * @return 继承BaseViewModel的ViewModel
     */
    fun initViewModel(): VM? {
        return null
    }

    fun getDialogStyle() : Int{
        return R.style.BottomDialog
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