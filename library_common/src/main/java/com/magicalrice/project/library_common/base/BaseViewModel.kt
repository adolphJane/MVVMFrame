package com.magicalrice.project.library_common.base

import android.app.Application
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.magicalrice.project.library_base.base.ToastUtils

open class BaseViewModel(application: Application) : AndroidViewModel(application), IBaseViewModel {
    private lateinit var lifecycle: Lifecycle

    /**
     * 注入RxLifecycle生命周期
     *
     * @param lifecycle
     */
    fun setLifecycle(lifecycle: Lifecycle) {
        this.lifecycle = lifecycle
        this.lifecycle.addObserver(this)
    }

    fun removeLifecycle() {
        this.lifecycle.removeObserver(this)
    }

    override fun onAny(owner: LifecycleOwner, event: Lifecycle.Event) {

    }

    override fun onCreate() {
    }

    override fun onDestroy() {
    }

    override fun onStart() {
    }

    override fun onStop() {
    }

    override fun onResume() {
    }

    override fun onPause() {
    }

    override fun answerReceiver(param: String, activity: FragmentActivity) {

    }

    override fun showToast(content: String) {
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            ToastUtils.showShort(content)
        }
    }
}