package com.adolph.project.baseutils

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class CountDownUtils {
    private var mCountDownDisposable: Disposable? = null
    private var mPollingDisposable: Disposable? = null
    private var mtimerDisposable: Disposable? = null
    private var totalDisposable: CompositeDisposable = CompositeDisposable()

    /**
     * 倒计时
     */
    fun interval(time: Long,next: IRxNext) {
        if (time > 0) {
            mCountDownDisposable = Observable.interval(0,1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .take(time + 1)
                .map {
                    return@map time - it
                }.observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                }.subscribe ({
                    next.onNext(it)
                },{
                    cancelCountDown()
                },{
                    cancelCountDown()
                })
            mCountDownDisposable?.let {
                totalDisposable.add(it)
            }
        }
    }

    /**
     * 轮询
     */
    fun intervalTime(delayTime: Long,time: Long,next: IRxNext) {
        mPollingDisposable = Observable.interval(delayTime,time, TimeUnit.SECONDS)
            .compose(RxUtils.io_main())
            .doOnSubscribe {
            }.subscribe ({
                next.onNext(it)
            },{
                cancelPolling()
            },{
                cancelPolling()
            })

        mPollingDisposable?.let {
            totalDisposable.add(it)
        }
    }

    /**
     * 延时
     */
    fun timer(time: Long,next: IRxNext) {
        if (time > 0) {
            mtimerDisposable = Observable.timer(time,TimeUnit.SECONDS)
                .compose(RxUtils.io_main())
                .doOnSubscribe {
                }.subscribe ({
                    next.onNext(it)
                },{
                    cancelTimer()
                },{
                    cancelTimer()
                })

            mtimerDisposable?.let {
                totalDisposable.add(it)
            }
        }
    }

    /**
     * 延时
     */
    fun timerIO(time: Long,next: IRxNext) {
        if (time > 0) {
            mtimerDisposable = Observable.timer(time,TimeUnit.SECONDS)
                .compose(RxUtils.io_io())
                .doOnSubscribe {
                }.subscribe ({
                    next.onNext(it)
                },{
                    cancelTimer()
                },{
                    cancelTimer()
                })

            mtimerDisposable?.let {
                totalDisposable.add(it)
            }
        }
    }

    fun cancelPolling() {
//        if (mPollingDisposable != null && mPollingDisposable?.isDisposed == false) {
            mPollingDisposable?.dispose()
//        }
    }

    fun cancelCountDown() {
        if (mCountDownDisposable != null && mCountDownDisposable?.isDisposed == false) {
            mCountDownDisposable?.dispose()
        }
    }

    fun cancelTimer() {
        if (mtimerDisposable != null && mtimerDisposable?.isDisposed == false) {
            mtimerDisposable?.dispose()
        }
    }

    fun cancelTime() {
        if (!totalDisposable.isDisposed) {
            totalDisposable.clear()
        }
    }

    interface IRxNext {
        fun onNext(num: Long)
    }
}