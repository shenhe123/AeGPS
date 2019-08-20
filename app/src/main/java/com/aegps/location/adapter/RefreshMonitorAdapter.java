package com.aegps.location.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aegps.location.R;
import com.aegps.location.bean.net.RefreshMonitor;
import com.aegps.location.widget.CustomView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shenhe on 2019/8/7.
 *
 * @description
 */
public class RefreshMonitorAdapter extends HeaderRecycleAdapter<RefreshMonitor.MonitorEntryTableBean> implements MultiTypeSupport<RefreshMonitor.MonitorEntryTableBean> {
    private Context mContext;

    public RefreshMonitorAdapter(Context context, List<RefreshMonitor.MonitorEntryTableBean> dataList) {
        super(context, dataList, R.layout.item_refresh_monitor);
        this.mContext = context;
    }

    @Override
    public void bindData(final CommonViewHolder holder, final RefreshMonitor.MonitorEntryTableBean data) {
        ((CustomView) holder.getView(R.id.freight_order_number)).setRightText(data.getExpressCode() == null ? "" : data.getExpressCode());
        ((CustomView) holder.getView(R.id.client)).setRightText(data.getBaccName() == null ? "" : data.getBaccName());
        ((CustomView) holder.getView(R.id.address)).setRightText(data.getDeliveryAddress() == null ? "" : data.getDeliveryAddress());
        ((CustomView) holder.getView(R.id.city)).setRightText(data.getDeliveryCity() == null ? "" : data.getDeliveryCity());
        ((CustomView) holder.getView(R.id.contact)).setRightText(data.getContactPerson() == null ? "" : data.getContactPerson());
        ((CustomView) holder.getView(R.id.phone)).setRightText(data.getMobileTeleCode() == null ? "" : data.getMobileTeleCode());
        ((CustomView) holder.getView(R.id.tel)).setRightText(data.getTelephoneCode() == null ? "" : data.getTelephoneCode());
        ((CustomView) holder.getView(R.id.freight_receipt_time)).setRightText(data.getEndingTime() == null ? "" : data.getEndingTime());
        ((CustomView) holder.getView(R.id.freight_driving_distance)).setRightText(data.getMileageMeasure() + "公里");
        ((CustomView) holder.getView(R.id.remark)).setRightText(data.getRemarkSub() == null ? "" : data.getRemarkSub());
    }

    @Override
    public int getLayoutId(RefreshMonitor.MonitorEntryTableBean item, int position) {
        return R.layout.item_refresh_monitor;
    }
}
