package com.aegps.location.widget.popupwindow.account;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.aegps.location.R;
import com.aegps.location.adapter.AccountAdapter;
import com.aegps.location.bean.ReturnTableResult;
import com.aegps.location.utils.DensityUtil;
import com.aegps.location.utils.DisplayUtil;
import com.aegps.location.widget.popupwindow.BasePWControl;

import java.util.List;

/**
 * Created by ShenHe on 2019/8/6.
 */

public abstract class AccountMenuWindow extends BasePWControl {

    private RecyclerView mRecyclerView;
    private AccountAdapter adapter;

    public AccountMenuWindow(Context context, ViewGroup layoutParent) {
        super(context, layoutParent);
    }

    @Override
    protected void initView() {
        mRecyclerView = ((RecyclerView) mView.findViewById(R.id.recyclerView));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new AccountAdapter(mContext);
        adapter.setItemListener((data, position) -> {
            selectAccount(data.getCountingRoomName());
            cancel();
        });
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    protected int injectLayout() {
        return R.layout.popup_account_menu;
    }

    @Override
    protected int injectAnimationStyle() {
        return 0;
    }


    @Override
    public int injectParamsHeight() {
        return LinearLayout.LayoutParams.WRAP_CONTENT;
    }

    @Override
    public int injectParamsWight() {
        return DisplayUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext, 90);
    }

    protected abstract void selectAccount(String accountName);

    public void setContent(List<ReturnTableResult.ReturnTableBean> content) {
        adapter.setData(content);
    }
}
