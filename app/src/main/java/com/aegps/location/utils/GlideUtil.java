package com.aegps.location.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.ImageView;

import com.aegps.location.R;
import com.aegps.location.widget.transformation.GlideRoundTransform;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.Target;

/**
 * Create by shenhe 2019-4-14
 */
public class GlideUtil {

    /**
     * 简单获取图片
     *
     * @param path
     * @param view
     */
    public static void getImage(Context context, String path, ImageView view) {
        if (checkContext(context)) return;
        if (view == null) return;
        if (path == null) path = "";
        Glide.with(context)
                .load(path)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                })
                .diskCacheStrategy(DiskCacheStrategy.ALL) //设置缓存
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .priority(Priority.NORMAL) //下载优先级
                .into(view);
    }

    private static boolean checkContext(Context context) {
        if (null == context) return true;
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            if (activity.isFinishing() || activity.isDestroyed()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据指定大小加载图片
     *
     * @param path
     * @param width
     * @param height
     * @param imageView
     */
    public static void getImageBySize(Context context, String path, int width, int height, ImageView imageView) {
        if (checkContext(context)) return;
        if (imageView == null) return;
        if (path == null) path = "";
        Glide.with(context)
                .load(path)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                })
                .diskCacheStrategy(DiskCacheStrategy.ALL) //设置缓存
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .override(width, height)
                .into(imageView);
    }

    /**
     * 指定加载等待跟加载失败图片
     *
     * @param path
     * @param imageView
     * @param errorRes
     * @param loadingRes
     */
    public static void getImageWithErrorLoadingImg(Context context, String path, ImageView imageView, int errorRes, int loadingRes) {
        if (checkContext(context)) return;
        if (imageView == null) return;
        if (path == null) path = "";
        Glide.with(context)
                .load(path)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                })
                .diskCacheStrategy(DiskCacheStrategy.ALL) //设置缓存
                .placeholder(loadingRes)
                .error(errorRes)
                .into(imageView);
    }

    /**
     * 是否跳过内存加载
     *
     * @param path
     * @param view
     * @param isSkipMemoryCache
     */
    public static void getImage(Context context, String path, ImageView view, boolean isSkipMemoryCache) {
        if (checkContext(context)) return;
        if (view == null) return;
        if (path == null) path = "";
        Glide.with(context)
                .load(path)
                .skipMemoryCache(isSkipMemoryCache)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                })
                .diskCacheStrategy(DiskCacheStrategy.ALL) //设置缓存
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(view);
    }

    /**
     * 设置加载图片时的动画
     *
     * @param path
     * @param imageView
     * @param anim
     */
    public static void getImageWithAnim(Context context, String path, ImageView imageView, int anim) {
        if (checkContext(context)) return;
        if (imageView == null) return;
        if (path == null) path = "";
        Glide.with(context)
                .load(path)
                .animate(anim)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                })
                .diskCacheStrategy(DiskCacheStrategy.ALL) //设置缓存
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(imageView);
    }

    /**
     * 加载Gif
     *
     * @param path
     * @param imageView
     */
    public static void getImageByGif(Context context, String path, ImageView imageView) {
        if (checkContext(context)) return;
        if (imageView == null) return;
        if (path == null) path = "";
        Glide.with(context)
                .load(path)
                .asBitmap()
                .listener(new RequestListener<String, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                })
                .diskCacheStrategy(DiskCacheStrategy.ALL) //设置缓存
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .priority(Priority.NORMAL) //下载优先级
                .into(imageView);
    }

    /**
     * 加载图片centerCrop()
     *
     * @param path
     * @param view
     */
    public static void getImageCenterCrop(Context context, String path, ImageView view) {
        if (checkContext(context)) return;
        if (view == null) return;
        if (path == null) path = "";
        Glide.with(context)
                .load(path)
                .centerCrop()
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                })
                .diskCacheStrategy(DiskCacheStrategy.ALL) //设置缓存
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .priority(Priority.NORMAL) //下载优先级
                .into(view);
    }


    /**
     * 带监听器加载图片
     *
     * @param path
     * @param imageView
     * @param requstlistener
     */
    public static void getImageWithListener(Context context, String path, ImageView imageView, RequestListener<String, GlideDrawable> requstlistener) {
        if (checkContext(context)) return;
        if (imageView == null) return;
        if (path == null) path = "";
        Glide.with(context)
                .load(path)
                .centerCrop()
                .listener(requstlistener)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .priority(Priority.NORMAL) //下载优先级
                .into(imageView);
    }

    //加载圆角图片
    public static void loadRoundImage(Context context, String url, final ImageView imageView, int density) {
        if (checkContext(context)) return;
        if (imageView == null) return;
        if (url == null) url = "";
        Glide.with(context)
                .load(url)
                .dontAnimate()//防止设置placeholder导致第一次不显示网络图片(或显示的图片有问题),只显示默认图片的问题,
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .diskCacheStrategy(DiskCacheStrategy.ALL) //设置缓存
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                })
//                .centerCrop() 千万不要加，加了就没有圆角效果了
                .transform(new CenterCrop(context), new GlideRoundTransform(context,density))
                .into(imageView);
    }

    /**
     * 加载圆角图片
     * 默认头像
     */
    public static void loadRoundImage(Context context, String url, int resourceId, final ImageView imageView, int density) {
        if (checkContext(context)) return;
        if (imageView == null) return;
        if (url == null) url = "";
        Glide.with(context)
                .load(url)
                .dontAnimate()//防止设置placeholder导致第一次不显示网络图片(或显示的图片有问题),只显示默认图片的问题,
                .placeholder(resourceId)
                .error(resourceId)
                .diskCacheStrategy(DiskCacheStrategy.ALL) //设置缓存
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                })
//                .centerCrop() 千万不要加，加了就没有圆角效果了
                .transform(new CenterCrop(context), new GlideRoundTransform(context,density))
                .into(imageView);
    }


    //加载圆形图片
    public static void loadCirclePic(Context context, String url, final ImageView imageView) {
        if (checkContext(context)) return;
        if (imageView == null) return;
        if (url == null) url = "";
        Glide.with(context)
                .load(url)
                .asBitmap()
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .listener(new RequestListener<String, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                })
                .diskCacheStrategy(DiskCacheStrategy.ALL) //设置缓存
                .into(new BitmapImageViewTarget(imageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        imageView.setImageDrawable(circularBitmapDrawable);
                    }
                });

    }

    //加载圆形图片
    public static void loadCircleDefaultPic(Context context, String url, final ImageView imageView, int resId) {
        if (checkContext(context)) return;
        if (imageView == null) return;
        if (url == null) url = "";
        Glide.with(context)
                .load(url)
                .asBitmap()
                .placeholder(resId)
                .error(resId)
                .listener(new RequestListener<String, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                })
                .diskCacheStrategy(DiskCacheStrategy.ALL) //设置缓存
                .into(new BitmapImageViewTarget(imageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        imageView.setImageDrawable(circularBitmapDrawable);
                    }
                });

    }

    /**
     * 清理内存缓存
     */
    public static void clearMemaryCache(Context context) {
        if (checkContext(context)) return;
        Glide.get(context)
                .clearMemory();
    }

    /**
     * 清理磁盘缓存
     */
    public static void clearDiskCache(Context context) {
        if (checkContext(context)) return;
        Glide.get(context)
                .clearDiskCache();
    }
}
