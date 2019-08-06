package com.aegps.location.widget.popupwindow.account;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aegps.location.R;
import com.aegps.location.widget.popupwindow.BasePWControl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ShenHe on 2019/8/6.
 */

public abstract class AccountMenuWindow extends BasePWControl {

    private TextView mMenuName;
    private List<String> content;

    public AccountMenuWindow(Context context, ViewGroup layoutParent) {
        super(context, layoutParent);
    }

    @Override
    protected void initView() {
        mMenuName = ((TextView) mView.findViewById(R.id.tv_menu_name));
        mMenuName.setOnClickListener(v -> selectAccount(mMenuName.getText().toString()));
    }

    @Override
    protected int injectLayout() {
        return R.layout.popup_account_menu;
    }

    @Override
    protected int injectAnimationStyle() {
        return R.style.bottom_popupwindow;
    }


    @Override
    public int injectParamsHeight() {
        return LinearLayout.LayoutParams.WRAP_CONTENT;
    }

    @Override
    public int injectParamsWight() {
        return LinearLayout.LayoutParams.WRAP_CONTENT;
    }

    protected abstract void selectAccount(String accountName);

    public void setContent(ArrayList<String> content) {
        this.content = content;
    }
}
