package com.magicalrice.project.library_data.remote.http

interface RetrofitNextListener<T> {
    fun onNext(t: T)
}