package com.linkface.baselibrary.widget;

import android.os.Handler;
import android.widget.ImageView;

/**
 * @Title: 图片轮播显示
 * @Package com.example.animationlist
 * @Description: 用于图片轮播使用，可替代大量图片下的FrameAnimation帧动画，减少内存压力
 * @date 2015-6-8
 */
public class SceneAnimation {
	private ImageView mImageView;
	/** 需要播放的图片资源集合 */
	private int[] mImageRess;
	/** 每张图片播放的间隔 */
	private int[] mDurations;
	/** 如果播放间隔一致，即为所有图片公用的播放时间 */
	private int mDuration;
	/** 轮播图片当前的索引*/
	private int mCurIndex;
	/** 集合中图片的总数量，用于计算无限轮播 */
	private int mImagesCount;
	/** 一轮播放完毕后，与下一轮的播放间隔 */
	private int mBreakDelay;
	String mImageUri = "drawable://";

//	如果使用ImageLoader轮播会跳帧，已弃用
//	private ImageLoader mImageLoader = ImageLoader.getInstance(); 

	/**
	 * 初始化轮播类
	 * @param mImageView 显示图片的mImageView
	 * @param mImageRess 显示的图片集合
	 * @param mDuration  所有图片统一的间隔时间
	 */
	public SceneAnimation(ImageView mImageView, int[] mImageRess, int mDuration) {
		super();
		this.mImageView = mImageView;
		this.mImageRess = mImageRess;
		this.mDuration = mDuration;
		mImagesCount = mImageRess.length;

		mImageView.setBackgroundResource(mImageRess[0]);
		play(1);
	}

	/**
	 * 初始化轮播类的重载，每张图片的播放时间可不同
	 * @param mImageView 显示图片的mImageView
	 * @param mImageRess 显示的图片集合
	 * @param mDurations 所有图片的间隔时间数组
	 */
	public SceneAnimation(ImageView mImageView, int[] mImageRess,
                          int[] mDurations) {
		super();
		this.mImageView = mImageView;
		this.mImageRess = mImageRess;
		this.mDurations = mDurations;
		mImagesCount = mImageRess.length;

		mImageView.setBackgroundResource(mImageRess[0]);
		play(1);
	}

	/**
	 * 初始化轮播类的重载，每张图片的播放时间可不同,同时定义每一轮播放结束后，第二轮开始播放的间隔时间
	 * @param mImageView 显示图片的mImageView
	 * @param mImageRess 显示的图片集合
	 * @param mDurations 所有图片的间隔时间数组
	 * @param mBreakDelay 图片播放到最后一张2轮播放之间的间隔
	 */
	public SceneAnimation(ImageView mImageView, int[] mImageRess,
                          int[] mDurations, int mBreakDelay) {
		super();
		this.mImageView = mImageView;
		this.mImageRess = mImageRess;
		this.mDurations = mDurations;
		this.mBreakDelay = mBreakDelay;
		mImagesCount = mImageRess.length;

		mImageView.setBackgroundResource(mImageRess[0]);
		playInterval(1);
	}

	/**
	 * 初始化轮播类,同时定义每一轮播放结束后，第二轮开始播放前的间隔时间
	 * @param mImageView 显示图片的mImageView
	 * @param mImageRess 显示的图片集合
	 * @param mDuration  所有图片统一的间隔时间
	 * @param mBreakDelay 图片播放到最后一张2轮播放之间的间隔
	 */
	public SceneAnimation(ImageView mImageView, int[] mImageRess,
                          int mDuration, int mBreakDelay) {
		super();
		this.mImageView = mImageView;
		this.mImageRess = mImageRess;
		this.mDuration = mDuration;
		this.mBreakDelay = mBreakDelay;
		mImagesCount = mImageRess.length;

		mImageView.setBackgroundResource(mImageRess[0]);
		playInterval(1);
	}

	/**
	 * 循环播放图片
	 * @param mCurIndex 下一张需要播放图片图片的id
	 */
	private void play(final int mCurIndex) {
		this.mCurIndex = mCurIndex;
		handler.postDelayed(runnable, mDurations == null ? mDuration : mDurations[mCurIndex]);
	}

	/**
	 * 循环播放图片，每一轮带有间隔时间
	 * @param mCurIndex 下一张需要播放图片图片的id
	 */
	private void playInterval(final int mCurIndex) {
		this.mCurIndex = mCurIndex;
		handler.postDelayed(runnableDelay, mCurIndex == mImagesCount - 1 && mBreakDelay > 0 ? mBreakDelay
				: mDurations == null ? mDuration : mDurations[mCurIndex]);
		
	}
	
	private Handler handler = new Handler();
	
	/**
	 * 实例化runnable，方便handler回收，防止内存泄露
	 */
	private Runnable runnable = new Runnable() {
		
		@Override
		public void run() {
			mImageView.setBackgroundResource( mImageRess[mCurIndex] );
			mCurIndex = (mCurIndex+1) % mImagesCount;
			handler.postDelayed(this, mDurations == null ? mDuration : mDurations[mCurIndex]);
		}
	};
	
	/**
	 * 实例化runnable，方便handler回收，防止内存泄露
	 */
	private Runnable runnableDelay = new Runnable() {
		
		@Override
		public void run() {
			mImageView.setBackgroundResource( mImageRess[mCurIndex] );
			mCurIndex = (mCurIndex+1) % mImagesCount;
			handler.postDelayed(this, mCurIndex == mImagesCount - 1 && mBreakDelay > 0 ? mBreakDelay
					: mDurations == null ? mDuration : mDurations[mCurIndex]);
		}
	};
	
	/**
	 * 终止递归操作，防止内存泄露
	 */
	public void removeCallBacks() {
		if(runnable != null) {
			handler.removeCallbacks(runnable);
		}
		if(runnableDelay!=null) {
			handler.removeCallbacks(runnableDelay);
		}
	}

}