package com.magicalrice.project.library_data.remote.utils

import com.google.gson.GsonBuilder
import com.magicalrice.project.library_base.base.AppManager
import com.magicalrice.project.library_base.base.SPUtils
import com.magicalrice.project.library_base.base.log.LogUtils
import com.magicalrice.project.library_data.remote.http.cookie.CookieJarImpl
import com.magicalrice.project.library_data.remote.http.cookie.store.PersistentCookieStore
import com.magicalrice.project.library_data.remote.http.interceptor.CacheInterceptor
import com.magicalrice.project.library_data.remote.http.interceptor.logging.Level
import com.magicalrice.project.library_data.remote.http.interceptor.logging.LoggingInterceptor
import com.magicalrice.project.library_third_internal.BuildConfig
import okhttp3.Cache
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.internal.platform.Platform
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.UnsupportedEncodingException
import java.util.concurrent.TimeUnit

object RetrofitUtils {
    //超时时间
    private val DEFAULT_TIMEOUT = 20L
    //缓存时间
    private val CACHE_TIMEOUT = 10 * 1024 * 1024L
    private val mContext = AppManager.getInstance().getApp()
    private var cache: Cache? = null
    private var httpCacheDirectory: File? = null

    fun getOkHttpClientBuilder(): OkHttpClient {
        val logIntercepter = HttpLoggingInterceptor {
            try {
                LogUtils.e("OkHttp----", it)
            } catch (e: UnsupportedEncodingException) {
                LogUtils.e("OkHttp----", it)
            }
        }

        if (httpCacheDirectory == null) {
            httpCacheDirectory = File(
                mContext.cacheDir, "goldze_cache"
            )
        }

        try {
            if (cache == null) {
                cache = Cache(
                    httpCacheDirectory, CACHE_TIMEOUT.toLong()
                )
            }
        } catch (e: Exception) {
            LogUtils.e("Could not create http cache", e)
        }

        val sslParams =
            HttpsUtils.getSslSocketFactory()


        logIntercepter.level = HttpLoggingInterceptor.Level.BODY

//            val cacheFile = File(DaiHuoApplication.instance.cacheDir,"cache")
//            val cache = Cache(cacheFile, 1024 * 1024 * 100)

        return OkHttpClient.Builder()
            .cookieJar(
                CookieJarImpl(
                    PersistentCookieStore(
                        mContext
                    )
                )
            )
            .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
            .addInterceptor(
                CacheInterceptor(
                    mContext
                )
            )
            .addInterceptor {
                val request = it.request().newBuilder()
                val token = SPUtils.getInstance().get("token", "") as String
                request.addHeader("token", token)
                return@addInterceptor it.proceed(request.build())
            }
            .addInterceptor(
                LoggingInterceptor.Builder()
                    .loggable(BuildConfig.DEBUG) //是否开启日志打印
                    .setLevel(Level.BASIC) //打印的等级
                    .log(Platform.INFO) // 打印类型
                    .request("Request") // request的Tag
                    .response("Response")// Response的Tag
                    .addHeader(
                        "log-header",
                        "I am the log request header."
                    ) // 添加打印头, 注意 key 和 value 都不能是中文
                    .build()
            )
            .connectionPool(
                ConnectionPool(
                    8,
                    15,
                    TimeUnit.SECONDS
                )
            )                 // 这里你可以根据自己的机型设置同时连接的个数和时间，我这里8个，和每个保持时间为10s
            .build()
    }

    fun getRetrofitBuilder(baseUrl: String): Retrofit.Builder {
        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").serializeNulls().create()
        return Retrofit.Builder().client(getOkHttpClientBuilder())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(baseUrl)
    }

    fun getRetrofit(baseUrl: String): Retrofit {
        return getRetrofitBuilder(
            baseUrl
        ).build()
    }
}