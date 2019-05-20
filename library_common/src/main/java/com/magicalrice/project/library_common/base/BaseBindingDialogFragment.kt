package com.magicalrice.project.library_common.base

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseBindingDialogFragment<V: ViewDataBinding,VM: BaseViewModel> : BaseDialogFragment<VM>() {
    protected lateinit var binding: V

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(activity!!, getDialogStyle())
        dialog.setContentView(initView())
        return dialog
    }

    override fun initView(): View {
        binding = DataBindingUtil.inflate(layoutInflater,layoutId,null,false)
        initView(binding.root)
        return binding.root
    }
}