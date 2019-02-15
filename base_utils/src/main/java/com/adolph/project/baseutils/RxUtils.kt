package com.adolph.project.baseutils

import io.reactivex.FlowableTransformer
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

object RxUtils {
    const val TAG = "RxUtils"
    private var mDisposable: CompositeDisposable? = null

    fun <T> io_main() : ObservableTransformer<T,T>{
        return ObservableTransformer {
            it.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        }
    }

    fun <T> io_io() : ObservableTransformer<T,T>{
        return ObservableTransformer {
            it.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
        }
    }

    fun <T> main_main() : ObservableTransformer<T,T>{
        return ObservableTransformer {
            it.subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
        }
    }

    fun <T> io_main_f() : FlowableTransformer<T,T>{
        return FlowableTransformer {
            it.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        }
    }

    fun doOnUIThread(task: UITask) {
        Observable.just(task)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe{
                it.doOnUI()
            }
    }

    fun cancel() {
        if (mDisposable != null && mDisposable?.isDisposed == false) {
            mDisposable?.clear()
        }
    }

    interface IRxNext {
        fun onNext(num: Long)
    }
}