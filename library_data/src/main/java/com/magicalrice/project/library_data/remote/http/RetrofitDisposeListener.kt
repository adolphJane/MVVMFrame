package com.magicalrice.project.library_data.remote.http

import io.reactivex.disposables.Disposable

interface RetrofitDisposeListener<T> :
    RetrofitNextListener<T> {
    fun onDisposable(dispose: Disposable)
}