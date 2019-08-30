package com.aegps.location.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.aegps.location.R;
import com.aegps.location.bean.event.EBProgressEvent;
import com.aegps.location.update.view.NumberProgressBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by hqd on 2016/11/9.
 */
public class UpdateAppDialog extends Dialog implements View.OnClickListener {
    private View rootView;
    private TextView message;
    private TextView okButton;
    private TextView cancalButton;
    private ButtonListener lift;
    private ButtonListener right;
    private NumberProgressBar mProgress;
    private boolean isCancle;
    private Context mContext;

    public UpdateAppDialog(Context context) {
        super(context, R.style.dialog);
        init(context);
    }

    public UpdateAppDialog(Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        EventBus.getDefault().register(UpdateAppDialog.this);
        rootView = LayoutInflater.from(context).inflate(R.layout.dialog_update_app, null);
        setContentView(rootView);
        mProgress = ((NumberProgressBar) rootView.findViewById(R.id.npb));
        message = (TextView) rootView.findViewById(R.id.textview_dialog_msg);
        okButton = (TextView) rootView.findViewById(R.id.textview_dialog_ok);
        okButton.setOnClickListener(this);
        cancalButton = (TextView) rootView.findViewById(R.id.textview_dialog_cancal);
        cancalButton.setOnClickListener(this);
    }

    @Override
    public void setCancelable(boolean flag) {
        super.setCancelable(flag);
        isCancle = flag;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textview_dialog_ok:
                lift.buttonOkClick(this);
                if (isCancle) {
                    cancel();
                }
                break;
            case R.id.textview_dialog_cancal:
                right.buttonOkClick(this);
                cancel();
                break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(final EBProgressEvent event) {
        float progress = event.getProgress();
        setProgress(progress);
        /**
         * 下载完成更新文案
         */
        if (Math.round(progress) == 100 && event.isForce()) {
            setLeft("免流量更新");
        }
    }

    @Override
    public void setOnDismissListener(@Nullable OnDismissListener listener) {
        super.setOnDismissListener(listener);
        EventBus.getDefault().unregister(UpdateAppDialog.this);
    }

    public void setMessage(String message) {
        this.message.setText(message);
    }

    public void setLeft(String left) {
        this.okButton.setText(left);
    }

    public void setLeftTextColor(int color){
        this.okButton.setTextColor(color);
    }

    public void setLeftBackgroud(int color){
        this.okButton.setBackgroundColor(color);
    }

    public void setRightTextColor(int color){
        this.cancalButton.setTextColor(color);
    }

    public void setRightBackgroud(int color){
        this.cancalButton.setBackgroundColor(color);
    }

    public void setRight(String right) {
        this.cancalButton.setText(right);
    }

    public void setLiftButtonListener(ButtonListener buttonListener) {
        this.lift = buttonListener;
    }

    public void setRightButtonListener(ButtonListener buttonListener) {
        this.right = buttonListener;
    }
    public int getProgressVisible() {
        return mProgress.getVisibility();
    }
    public void setProgressVisible(int visible) {
        mProgress.setVisibility(visible);
    }
    public void setProgress(float progress) {
        mProgress.setProgress(Math.round(progress));
        mProgress.setMax(100);
    }

    public interface ButtonListener {
        void buttonOkClick(Dialog dialog);
    }
}
