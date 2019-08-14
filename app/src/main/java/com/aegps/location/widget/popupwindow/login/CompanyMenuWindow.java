package com.aegps.location.widget.popupwindow.login;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.aegps.location.R;
import com.aegps.location.adapter.CompanyMenuAdapter;
import com.aegps.location.bean.net.RemoteLoginResult;
import com.aegps.location.utils.DensityUtil;
import com.aegps.location.utils.DisplayUtil;
import com.aegps.location.widget.popupwindow.BasePWControl;

import java.util.List;

/**
 * Created by ShenHe on 2019/8/6.
 */

public abstract class CompanyMenuWindow extends BasePWControl {

    private RecyclerView mRecyclerView;
    private CompanyMenuAdapter adapter;

    public CompanyMenuWindow(Context context, ViewGroup layoutParent) {
        super(context, layoutParent);
    }

    @Override
    protected void initView() {
        mRecyclerView = ((RecyclerView) mView.findViewById(R.id.recyclerView));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new CompanyMenuAdapter(mContext);
        adapter.setItemListener((data, position) -> {
            selectAccount(data.getCustomerName(), data.getCustomerCode());
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
        return DisplayUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext, 100);
    }

    protected abstract void selectAccount(String customerName, String customerCode);

    public void setContent(List<RemoteLoginResult.ReturnTableBean> content) {
        adapter.setData(content);
    }
}
