package com.magicalrice.project.library_data.remote.http

import android.Manifest.permission.ACCESS_NETWORK_STATE
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AlertDialog
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.magicalrice.project.library_base.base.NetworkUtils
import com.magicalrice.project.library_base.base.ToastUtils
import com.magicalrice.project.library_data.BaseConstants
import com.magicalrice.project.library_data.bean.NetErrorBean
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException


class NetSubscriber<T> : Observer<T> {
    private val TAG = "NetSubscriber"
    private val REQUESTINVALID = 400
    private val UNAUTHORIZED = 401
    private val FORBIDDEN = 403
    private val NOT_FOUND = 404
    private val REQUEST_TIMEOUT = 408
    private val INTERNAL_SERVER_ERROR = 500
    private val BAD_GATEWAY = 502
    private val SERVICE_UNAVAILABLE = 503
    private val GATEWAY_TIMEOUT = 504
    private var mListener: RetrofitNextListener<T>
    private var mContext: Context
    private var refreshLayout: SwipeRefreshLayout? = null
    private var loadingDialog: AlertDialog? = null

    constructor(
        refreshLayout: SwipeRefreshLayout,
        listener: RetrofitNextListener<T>,
        context: Context
    ) {
        this.refreshLayout = refreshLayout
        this.mListener = listener
        this.mContext = context
    }

    constructor(
        loadingDialog: AlertDialog,
        listener: RetrofitNextListener<T>,
        context: Context
    ) {
        this.loadingDialog = loadingDialog
        this.mListener = listener
        this.mContext = context
    }

    constructor(listener: RetrofitNextListener<T>, context: Context) {
        this.mListener = listener
        this.mContext = context
    }


    override fun onComplete() {
        refreshLayout?.isRefreshing = false
        loadingDialog?.dismiss()

        if (mListener is RetrofitComListener) {
            (mListener as RetrofitComListener<T>).onComplete()
        }
        if (mListener is RetrofitAllListener) {
            (mListener as RetrofitAllListener<T>).onComplete()
        }
    }

    override fun onNext(t: T) {
        refreshLayout?.isRefreshing = false
        mListener.onNext(t)

        loadingDialog?.dismiss()
    }

    override fun onError(p0: Throwable) {
        refreshLayout?.isRefreshing = false

        loadingDialog?.dismiss()
        if (mListener is RetrofitErrorListener) {
            (mListener as RetrofitErrorListener<T>).onError(p0)
        }
        if (mListener is RetrofitAllListener) {
            (mListener as RetrofitAllListener<T>).onError(p0)
        }
        when (p0) {
            is HttpException -> when (p0.response().code()) {
                INTERNAL_SERVER_ERROR -> {
                    val error = p0.response().errorBody()
                    error?.let {
                        val resultBean: NetErrorBean? = Gson().fromJson<NetErrorBean>(
                            String(it.bytes()),
                            NetErrorBean::class.java
                        )
                        showToastError(resultBean?.message)
                    }
                }

                REQUESTINVALID -> {
                    showToastError("请求无效")
                    val error = p0.response().errorBody()
                    error?.let {
                        val resultBean = Gson().fromJson<NetErrorBean>(
                            String(it.bytes()),
                            NetErrorBean::class.java
                        )
                        showToastError(resultBean.message)
                    }
                }

                UNAUTHORIZED -> {
                    // Token过期
                    val intent = Intent()
                    intent.action = BaseConstants.GLOBAL_BROADCAST_ACTION
                    intent.putExtra("type", BaseConstants.TOKEN_UNAUTHORIZED)
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent)
                    showToastError("登录状态已失效，请重新登录！")
                }

                FORBIDDEN -> {
                    //请求被拒绝
                    showToastError("服务器拒绝请求")
                }

                NOT_FOUND -> {
                    //未找到资源
                    showToastError("服务器找不到请求的网页")
                }

                REQUEST_TIMEOUT -> {
                    showToastError("请求超时")
                }

                GATEWAY_TIMEOUT -> {
                    showToastError("网关超时")
                }

                SERVICE_UNAVAILABLE -> {
                    showToastError("服务器维护")
                }

                BAD_GATEWAY -> {
                    showToastError("服务器开小差了哦,请稍后再试")
                }

                else -> {
                    showToastError("服务器开小差了哦，请稍后重试")
                }
            }
            is ConnectException -> showToastError("网络请求失败，请稍后重试")
            is UnknownHostException -> showToastError("网络不可用")
            is SocketTimeoutException -> showToastError("网络请求超时")
            else -> showToastError(p0.message)
        }
    }

    @RequiresPermission(ACCESS_NETWORK_STATE)
    override fun onSubscribe(p0: Disposable) {
        if (!NetworkUtils.isConnected()) {
            ToastUtils.showShort("网络出现异常")
            onComplete()
        }
        if (mListener is RetrofitDisposeListener) {
            (mListener as RetrofitDisposeListener).onDisposable(p0)
        }
        if (mListener is RetrofitAllListener) {
            (mListener as RetrofitAllListener).onDisposable(p0)
        }
    }

    private fun showToastError(content: String?) {
        if (content?.isEmpty() == false) {
            ToastUtils.showShort(content)
        }
    }
}