package com.magicalrice.project.library_data.remote.http

import io.reactivex.disposables.Disposable

interface RetrofitAllListener<T> :
    RetrofitNextListener<T> {
    fun onComplete()
    fun onError(t: Throwable)
    fun onDisposable(dispose: Disposable)
}