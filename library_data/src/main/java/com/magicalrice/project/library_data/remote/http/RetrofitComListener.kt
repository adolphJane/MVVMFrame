package com.magicalrice.project.library_data.remote.http

interface RetrofitComListener<T> :
    RetrofitNextListener<T> {
    fun onComplete()
}