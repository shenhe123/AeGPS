package com.aegps.location.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aegps.location.R;

public class CustomView extends LinearLayout {

    private TextView mTvDesc, mTvName;

    public CustomView(Context context) {
        this(context, null);
    }

    public CustomView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomView);
        String tvStartText = a.getString(R.styleable.CustomView_tvStartText);
        String tvRightText = a.getString(R.styleable.CustomView_tvRightText);
        int textColor = a.getInt(R.styleable.CustomView_tvColor, 0x00000000);
        LayoutInflater inflater = LayoutInflater.from(context);
        View inflate = inflater.inflate(R.layout.label_transport, this, true);
        mTvDesc = ((TextView) inflate.findViewById(R.id.tv_desc));
        mTvName = ((TextView) inflate.findViewById(R.id.tv_name));
        mTvDesc.setText(tvStartText);
        mTvName.setText(tvRightText);
        mTvDesc.setTextColor(textColor);
        mTvName.setTextColor(textColor);
        a.recycle();
    }

    public CustomView setStartText(String startText) {
        mTvDesc.setText(startText);
        return this;
    }

    public CustomView setStartTextColor(int color) {
        mTvDesc.setTextColor(color);
        return this;
    }

    public CustomView setStartText(String startText, int color) {
        mTvDesc.setText(startText);
        mTvDesc.setTextColor(color);
        return this;
    }

    public CustomView setRightText(String rightText) {
        mTvName.setText(rightText);
        return this;
    }

    public CustomView setRightTextColor(int color) {
        mTvName.setTextColor(color);
        return this;
    }

    public CustomView setRightText(String rightText, int color) {
        mTvName.setText(rightText);
        mTvName.setTextColor(color);
        return this;
    }
}
