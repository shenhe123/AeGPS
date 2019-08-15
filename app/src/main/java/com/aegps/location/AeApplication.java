package com.aegps.location;

import com.aegps.location.base.BaseApplication;
import com.aegps.location.utils.LogUtil;
import com.aegps.location.utils.SharedPrefUtils;
import com.bumptech.glide.Glide;

/**
 * Created by ShenHe on 2019/3/3.
 */

public class AeApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        //初始化SharedPrefrence
        SharedPrefUtils.init(this);

//        LogUtil.init(BuildConfig.DEBUG,"");
        LogUtil.init(true,"");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Glide.with(this).onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            Glide.with(this).onLowMemory();
        }
        Glide.with(this).onTrimMemory(level);
    }
}
