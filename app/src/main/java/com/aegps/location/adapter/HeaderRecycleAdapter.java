package com.aegps.location.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by shenhe on 2018/9/19.
 */

public abstract class HeaderRecycleAdapter<T> extends RecyclerView.Adapter<CommonViewHolder> {
    public static final int TYPE_HEADER = 0;

    protected LayoutInflater layoutInflater;

    protected List<T> dataList;

    protected int layoutId;

    private View mHeaderView;

    protected MultiTypeSupport<T> multiTypeSupport;

    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        notifyItemInserted(0);
    }

    public View getHeaderView() {
        return mHeaderView;
    }

    public HeaderRecycleAdapter(Context context, List<T> dataList, int layoutId) {
        this.layoutInflater = LayoutInflater.from(context);
        this.dataList = dataList;
        this.layoutId = layoutId;
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeaderView == null) {
            if (multiTypeSupport != null) {
                return multiTypeSupport.getLayoutId(dataList.get(position), position);
            }
        } else {
            if (position == 0) return TYPE_HEADER;
            if (multiTypeSupport != null) {
                return multiTypeSupport.getLayoutId(dataList.get(position - 1), position - 1);
            }
        }
        return layoutId;
    }

    @Override
    public CommonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderView != null && viewType == TYPE_HEADER) {
            return new CommonViewHolder(mHeaderView);
        } else {
            if (multiTypeSupport != null) {
                layoutId = viewType;
            }
            View itemView = layoutInflater.inflate(layoutId, parent, false);
            return new CommonViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(CommonViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_HEADER) return;
        final int pos = getRealPosition(holder);
        bindData(holder, dataList.get(pos));
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return getItemViewType(position) == TYPE_HEADER
                            ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }

    @Override
    public void onViewAttachedToWindow(CommonViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp != null
                && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
            p.setFullSpan(holder.getLayoutPosition() == 0);
        }
    }

    public int getRealPosition(RecyclerView.ViewHolder holder) {
        int position = holder.getLayoutPosition();
        return mHeaderView == null ? position : position - 1;
    }

    @Override
    public int getItemCount() {
        return mHeaderView == null ? dataList.size() : dataList.size() + 1;
    }

    public abstract void bindData(CommonViewHolder holder, T data);
}
