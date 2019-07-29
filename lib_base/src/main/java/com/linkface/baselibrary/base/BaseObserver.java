package com.linkface.baselibrary.base;

import android.util.Log;

import com.linkface.baselibrary.http.BaseBean;
import com.linkface.baselibrary.http.ExceptionHandle;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


public abstract class BaseObserver<T> implements Observer<T>{

    @Override
    public void onError(Throwable e) {
        Log.e("shenhe_yuqinglog", e.getMessage());

        if(e instanceof ExceptionHandle.ResponeThrowable){
            onError((ExceptionHandle.ResponeThrowable)e);
        } else {
            onError(new ExceptionHandle.ResponeThrowable(e, ExceptionHandle.ERROR.UNKNOWN));
        }
    }

    @Override
    public void onSubscribe(Disposable d) {
        //可以弹出Dialog 提示正在加载
        showDialog();
    }

    protected abstract void hideDialog();

    protected abstract void showDialog();

    protected abstract void onSuccess(T t);

    protected abstract void onFailure(String message);

    @Override
    public void onComplete() {
        //可以取消Dialog 加载完毕
        hideDialog();
    }


    public void onError(ExceptionHandle.ResponeThrowable e) {
        hideDialog();
        onFailure(e.message);
    }

    @Override
    public void onNext(T t) {
        if (t instanceof BaseBean) {
            BaseBean baseBean = (BaseBean) t;
            switch (baseBean.getCode()) {
                case 200:
                    onSuccess(t);
                    break;
                case 401://token 失效
                    turnToLogin();
                    break;
                default:
                    onFailure(baseBean.getMessage());
                    break;
            }
        }
    }



    private void turnToLogin() {
//        Activity activity = AppManager.getAppManager().currentActivity();
//        if (activity instanceof LoginActivity) {
//            return;
//        }
//        Intent intent = new Intent(QYApplication.getAppContext(), LoginActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//        QYApplication.getAppContext().startActivity(intent);
    }
}
