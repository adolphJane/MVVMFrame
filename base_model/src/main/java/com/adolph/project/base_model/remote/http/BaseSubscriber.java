package com.adolph.project.base_model.remote.http;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;
import androidx.annotation.RequiresPermission;
import com.adolph.project.baseutils.LogUtils;
import com.adolph.project.baseutils.ToastUtils;
import io.reactivex.observers.DisposableObserver;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
/**
 * Created by goldze on 2017/5/10.
 * 该类仅供参考，实际业务Code, 根据需求来定义，
 */
public abstract class BaseSubscriber<T> extends DisposableObserver<T> {
    public abstract void onResult(T t);

    private Context context;
    private boolean isNeedCahe;

    public BaseSubscriber(Context context) {
        this.context = context;
    }

    @Override
    public void onError(Throwable e) {
        LogUtils.e(e.getMessage());
        // todo error somthing

        if (e instanceof ResponseThrowable) {
            onError((ResponseThrowable) e);
        } else {
            onError(new ResponseThrowable(e, ExceptionHandle.ERROR.UNKNOWN));
        }
    }


    @RequiresPermission(ACCESS_NETWORK_STATE)
    @Override
    public void onStart() {
        super.onStart();

        Toast.makeText(context, "http is start", Toast.LENGTH_SHORT).show();
        // todo some common as show loadding  and check netWork is NetworkAvailable
        // if  NetworkAvailable no !   must to call onCompleted
        if (!NetworkUtil.isNetworkAvailable(context)) {
            Toast.makeText(context, "无网络，读取缓存数据", Toast.LENGTH_SHORT).show();
            onComplete();
        }

    }

    @Override
    public void onComplete() {

        Toast.makeText(context, "http is Complete", Toast.LENGTH_SHORT).show();
        // todo some common as  dismiss loadding
    }


    public abstract void onError(ResponseThrowable e);

    @Override
    public void onNext(Object o) {
        BaseResponse baseResponse = (BaseResponse) o;
        if (baseResponse.getCode() == 200) {
            onResult((T) baseResponse.getResult());
        } else if (baseResponse.getCode() == 330) {
            ToastUtils.showShort(baseResponse.getMessage());
        } else if (baseResponse.getCode() == 503) {
            LogUtils.e(baseResponse.getMessage());
        } else {
            ToastUtils.showShort("操作失败！错误代码:" + baseResponse.getCode());
        }
    }
}
