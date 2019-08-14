package com.aegps.location.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aegps.location.R;
import com.aegps.location.bean.net.RemoteLoginResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shenhe on 2019/8/7.
 *
 * @description
 */
public class CompanyMenuAdapter extends RecyclerView.Adapter<CompanyMenuAdapter.ViewHolder>{
    private List<RemoteLoginResult.ReturnTableBean> dataList = new ArrayList<>();
    private Context context;
    private onRecyclerItemClickerListener mListener;

    public CompanyMenuAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_account_menu, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.text.setText(dataList.get(i).getCustomerName());
        viewHolder.itemView.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onRecyclerItemClick(dataList.get(i), i);
            }
        });
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
    public void setData(List<RemoteLoginResult.ReturnTableBean> dataList) {
        if (null != dataList) {
            this.dataList.clear();
            this.dataList.addAll(dataList);
            notifyDataSetChanged();
        }
    }

    /**
     * 增加点击监听
     */
    public void setItemListener(onRecyclerItemClickerListener mListener) {
        this.mListener = mListener;
    }

    /**
     * 点击监听回调接口
     */
    public interface onRecyclerItemClickerListener {
        void onRecyclerItemClick(RemoteLoginResult.ReturnTableBean data, int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.tv_menu_name);
        }
    }
}
