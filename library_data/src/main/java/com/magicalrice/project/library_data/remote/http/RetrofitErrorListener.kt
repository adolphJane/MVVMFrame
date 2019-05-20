package com.magicalrice.project.library_data.remote.http

interface RetrofitErrorListener<T> :
    RetrofitNextListener<T> {
    fun onError(t: Throwable)
}