package com.aegps.location.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.aegps.location.R;
import com.aegps.location.bean.net.RefreshMonitor;
import com.aegps.location.bean.net.RemoteLoginResult;
import com.aegps.location.widget.CustomView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shenhe on 2019/8/7.
 *
 * @description
 */
public class RefreshMonitorAdapter extends RecyclerView.Adapter<RefreshMonitorAdapter.ViewHolder> {
    private List<RefreshMonitor.MonitorEntryTableBean> dataList = new ArrayList<>();
    private Context context;
    private CompanyMenuAdapter.onRecyclerItemClickerListener mListener;

    public RefreshMonitorAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_refresh_monitor, viewGroup, false);
        return new RefreshMonitorAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.mFreightOrderNumber.setRightText(dataList.get(i).getExpressCode() == null ? "" : dataList.get(i).getExpressCode());
        viewHolder.mClient.setRightText(dataList.get(i).getBaccName() == null ? "" : dataList.get(i).getBaccName());
        viewHolder.mAddress.setRightText(dataList.get(i).getDeliveryAddress() == null ? "" : dataList.get(i).getDeliveryAddress());
        viewHolder.mCity.setRightText(dataList.get(i).getDeliveryCity() == null ? "" : dataList.get(i).getDeliveryCity());
        viewHolder.mContact.setRightText(dataList.get(i).getContactPerson() == null ? "" : dataList.get(i).getContactPerson());
        viewHolder.mPhone.setRightText(dataList.get(i).getMobileTeleCode() == null ? "" : dataList.get(i).getMobileTeleCode());
        viewHolder.mTel.setRightText(dataList.get(i).getTelephoneCode() == null ? "" : dataList.get(i).getTelephoneCode());
        viewHolder.mFreightReceiptTime.setRightText(dataList.get(i).getEndingTime() == null ? "" : dataList.get(i).getEndingTime());
        viewHolder.mFreightDrivingDistance.setRightText(dataList.get(i).getMileageMeasure() + "公里");
        viewHolder.mRemark.setRightText(dataList.get(i).getRemarkSub() == null ? "" : dataList.get(i).getRemarkSub());
        viewHolder.mLayoutRoot.setBackgroundColor(TextUtils.isEmpty(dataList.get(i).getEndingTime())
                ? context.getResources().getColor(R.color.colorWhite)
                : context.getResources().getColor(R.color.color_f7f7f7));

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    /**
     * 设置数据源
     */
    public void setData(List<RefreshMonitor.MonitorEntryTableBean> dataList) {
        if (null != dataList) {
            this.dataList.clear();
            this.dataList.addAll(dataList);
            notifyDataSetChanged();
        }
    }

    /**
     * 增加点击监听
     */
    public void setItemListener(CompanyMenuAdapter.onRecyclerItemClickerListener mListener) {
        this.mListener = mListener;
    }

    /**
     * 点击监听回调接口
     */
    public interface onRecyclerItemClickerListener {
        void onRecyclerItemClick(RemoteLoginResult.ReturnTableBean data, int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private LinearLayout mLayoutRoot;
        private CustomView mFreightOrderNumber;
        private CustomView mClient;
        private CustomView mAddress;
        private CustomView mCity;
        private CustomView mContact;
        private CustomView mPhone;
        private CustomView mTel;
        private CustomView mFreightReceiptTime;
        private CustomView mFreightDrivingDistance;
        private CustomView mRemark;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mLayoutRoot = itemView.findViewById(R.id.layout_root);
            mFreightOrderNumber = itemView.findViewById(R.id.freight_order_number);
            mClient = itemView.findViewById(R.id.client);
            mAddress = itemView.findViewById(R.id.address);
            mCity = itemView.findViewById(R.id.city);
            mContact = itemView.findViewById(R.id.contact);
            mPhone = itemView.findViewById(R.id.phone);
            mTel = itemView.findViewById(R.id.tel);
            mFreightReceiptTime = itemView.findViewById(R.id.freight_receipt_time);
            mFreightDrivingDistance = itemView.findViewById(R.id.freight_driving_distance);
            mRemark = itemView.findViewById(R.id.remark);

        }
    }
}
