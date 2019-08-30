package com.aegps.location.bean.event;

/**
 * Created by zhouyibo on 2017/4/24.
 */

public class EBProgressEvent {
    private float progress;//下载进度
    private boolean isForce;//是否是强制更新

    public EBProgressEvent(float progress, boolean isForce) {
        this.progress = progress;
        this.isForce = isForce;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public boolean isForce() {
        return isForce;
    }

    public void setForce(boolean force) {
        isForce = force;
    }
}
