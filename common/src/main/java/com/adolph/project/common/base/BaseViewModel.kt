package com.adolph.project.common.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.adolph.project.baseutils.ToastUtils
import com.trello.rxlifecycle3.LifecycleProvider

open class BaseViewModel(application: Application) : AndroidViewModel(application), IBaseViewModel {
    private lateinit var lifecycle: LifecycleProvider<*>
    private var uc: UIChangeLiveData

    init {
        uc = UIChangeLiveData()
    }
    /**
     * 注入RxLifecycle生命周期
     *
     * @param lifecycle
     */
    fun injectLifecycleProvider(lifecycle: LifecycleProvider<*>) {
        this.lifecycle = lifecycle
    }

    fun getLifecycleProvider(): LifecycleProvider<*> {
        return lifecycle
    }

    fun getUc() : UIChangeLiveData {
        return uc
    }

    fun showDialog() {
        uc.showDialogEvent.postValue(Unit)
    }

    fun dismissDialog() {
        uc.dismissDialogEvent.postValue(Unit)
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

    override fun showToast(content: String) {
        ToastUtils.showShort(content)
    }

    class UIChangeLiveData {
        val showDialogEvent = MutableLiveData<Unit>()
        val dismissDialogEvent = MutableLiveData<Unit>()
    }
}