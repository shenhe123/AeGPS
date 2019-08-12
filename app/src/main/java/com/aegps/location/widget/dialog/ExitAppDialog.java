package com.aegps.location.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.aegps.location.R;

/**
 * Created by hqd on 2016/11/9.
 */
public class ExitAppDialog extends Dialog implements View.OnClickListener {
    private View rootView;
    private TextView message;
    private TextView okButton;
    private TextView cancalButton;
    private ButtonListener lift;
    private ButtonListener right;
    private boolean isCancle;

    public ExitAppDialog(Context context) {
        super(context, R.style.dialog);
        init(context);
    }

    public ExitAppDialog(Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    private void init(Context context) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(false);
        rootView = LayoutInflater.from(context).inflate(R.layout.dialog_app_exit, null);
        setContentView(rootView);
        message = (TextView) rootView.findViewById(R.id.textview_dialog_msg);
        okButton = (TextView) rootView.findViewById(R.id.textview_dialog_ok);
        okButton.setOnClickListener(this);
        cancalButton = (TextView) rootView.findViewById(R.id.textview_dialog_cancal);
        cancalButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textview_dialog_ok:
                lift.buttonOkClick(this);
                cancel();
                break;
            case R.id.textview_dialog_cancal:
                right.buttonOkClick(this);
                cancel();
                break;
            default:
                break;
        }
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

    public interface ButtonListener {
        void buttonOkClick(Dialog dialog);
    }
}
