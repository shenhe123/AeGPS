package com.aegps.location.locationservice;

import android.content.Intent;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;


public class LocationService extends NotificationService {
    public LocationClient locationClient;
    private IWifiAutoCloseDelegate mWifiAutoCloseDelegate = new WifiAutoCloseDelegate();
    private boolean mIsWifiCloseable = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        applyNotiKeepMech(); //开启利用notification提高进程优先级的机制
        if (mWifiAutoCloseDelegate.isUseful(getApplicationContext())) {
            mIsWifiCloseable = true;
            mWifiAutoCloseDelegate.initOnServiceStarted(getApplicationContext());
        }

        startLocation();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        unApplyNotiKeepMech();
        stopLocation();
        super.onDestroy();
    }

    void startLocation() {
        stopLocation();

        initLocationOption();
    }

    void stopLocation() {
        if (null != locationClient) {
            locationClient.stop();
        }
    }

    /**
     * 初始化定位参数配置
     */

    private void initLocationOption() {
        if (locationClient == null) {
            try {
                //定位服务的客户端。宿主程序在客户端声明此类，并调用，目前只支持在主线程中启动
                locationClient = new LocationClient(getApplicationContext());
                //声明LocationClient类实例并配置定位参数
                LocationClientOption locationOption = new LocationClientOption();
                MyLocationListener myLocationListener = new MyLocationListener();
                //注册监听函数
                locationClient.registerLocationListener(myLocationListener);
                //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
                locationOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
                //可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
                locationOption.setCoorType("bd09ll");
                //可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
                locationOption.setScanSpan(5000);
                //可选，设置是否需要地址信息，默认不需要
                locationOption.setIsNeedAddress(true);
                //可选，设置是否需要地址描述
                locationOption.setIsNeedLocationDescribe(true);
                //可选，设置是否需要设备方向结果
                locationOption.setNeedDeviceDirect(false);
                //可选，默认false，设置是否当Gnss有效时按照1S1次频率输出Gnss结果
                locationOption.setLocationNotify(true);
                //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
                locationOption.setIgnoreKillProcess(true);
                //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
                locationOption.setIsNeedLocationDescribe(true);
                //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
                locationOption.setIsNeedLocationPoiList(true);
                //可选，默认false，设置是否收集CRASH信息，默认收集
                locationOption.SetIgnoreCacheException(false);
                //可选，默认false，设置是否开启卫星定位
                locationOption.setOpenGnss(true);
                //可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
                locationOption.setIsNeedAltitude(false);
                //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者，该模式下开发者无需再关心定位间隔是多少，定位SDK本身发现位置变化就会及时回调给开发者
//                locationOption.setOpenAutoNotifyMode();
                //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者
//                locationOption.setOpenAutoNotifyMode(3000, 1, LocationClientOption.LOC_SENSITIVITY_HIGHT);
                //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
                locationClient.setLocOption(locationOption);
                //开始定位
                locationClient.start();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            //开始定位
            locationClient.start();
        }
    }

    /**
     * 实现定位回调
     */
    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取经纬度相关（常用）的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

            //获取纬度信息
            double latitude = location.getLatitude();
            //获取经度信息
            double longitude = location.getLongitude();
            //获取定位精度，默认值为0.0f
            float radius = location.getRadius();
            //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准
            String coorType = location.getCoorType();
            //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
            int errorCode = location.getLocType();

            //发送结果的通知
            sendLocationBroadcast(location);

            if (!mIsWifiCloseable) {
                return;
            }

            if (errorCode == 61) {
                mWifiAutoCloseDelegate.onLocateSuccess(getApplicationContext(), PowerManagerUtil.getInstance().isScreenOn(getApplicationContext()), NetUtil.getInstance().isMobileAva(getApplicationContext()));
            } else {
                mWifiAutoCloseDelegate.onLocateFail(getApplicationContext(), errorCode, PowerManagerUtil.getInstance().isScreenOn(getApplicationContext()), NetUtil.getInstance().isWifiCon(getApplicationContext()));
            }
        }
    }

    private void sendLocationBroadcast(BDLocation location) {
        if (null != location) {
            Intent mIntent = new Intent(LocationChangBroadcastReceiver.RECEIVER_ACTION);
            mIntent.putExtra(LocationChangBroadcastReceiver.RECEIVER_DATA, location);
            sendBroadcast(mIntent);
//                String string =  Utils.formatUTC(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss")  + ", "+aMapLocation.getLatitude() + ", " + aMapLocation.getLongitude();
//                Utils.saveFile(string, "backlocation.txt", true);
        }
    }
}
