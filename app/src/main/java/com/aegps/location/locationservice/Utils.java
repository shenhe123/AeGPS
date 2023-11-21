/**
 *
 */
package com.aegps.location.locationservice;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.aegps.location.AeApplication;
import com.aegps.location.MainActivity;
import com.aegps.location.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;


public class Utils {
    private static String CLOSE_BRODECAST_INTENT_ACTION_NAME="com.aegps.location.locationservice.CloseService";
	private static SimpleDateFormat sdf = null;
    private static NotificationManager mNotificationManager;
    private final static String PRIMARY_CHANNEL = "com.aegps.location";

	/**
	 * 检测当的网络（WLAN、3G/2G）状态
	 * @return true 表示网络可用
	 */
	public static boolean getInternet() {
		ConnectivityManager connectivity = (ConnectivityManager) AeApplication.getAppContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo info = connectivity.getActiveNetworkInfo();
			if (info != null && info.isConnected())
			{
				// 当前网络是连接的
				if (info.getState() == NetworkInfo.State.CONNECTED)
				{
					// 当前所连接的网络可用
					return true;
				}
			}
		}
		return false;
	}

	public synchronized static String formatUTC(long l, String strPattern) {
		if (TextUtils.isEmpty(strPattern)) {
			strPattern = "yyyy-MM-dd HH:mm:ss";
		}
		if (sdf == null) {
			try {
				sdf = new SimpleDateFormat(strPattern, Locale.CHINA);
			} catch (Throwable e) {
			}
		} else {
			sdf.applyPattern(strPattern);
		}
		return sdf == null ? "NULL" : sdf.format(l);
	}

	public static Intent getExplicitIntent(Context context, Intent implicitIntent) {
		if (context.getApplicationInfo().targetSdkVersion < Build.VERSION_CODES.LOLLIPOP) {
			return implicitIntent;
		}

		// Retrieve all services that can match the given intent
		PackageManager pm = context.getPackageManager();
		List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);
		// Make sure only one match was found
		if (resolveInfo == null || resolveInfo.size() != 1) {
			return null;
		}
		// Get component info and create ComponentName
		ResolveInfo serviceInfo = resolveInfo.get(0);
		String packageName = serviceInfo.serviceInfo.packageName;
		String className = serviceInfo.serviceInfo.name;
		ComponentName component = new ComponentName(packageName, className);
		// Create a new intent. Use the old one for extras and such reuse
		Intent explicitIntent = new Intent(implicitIntent);
		// Set the component to be explicit
		explicitIntent.setComponent(component);

		return explicitIntent;
	}

	public static void saveFile(String toSaveString, String fileName, boolean append) {
		try {
			String sdCardRoot = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
			File saveFile = new File(sdCardRoot + "/" + fileName);
			if (!saveFile.exists()) {
				File dir = new File(saveFile.getParent());
				dir.mkdirs();
				saveFile.createNewFile();
			}
			FileOutputStream outStream = new FileOutputStream(saveFile, append);
			outStream.write(toSaveString.getBytes());
			outStream.write("\n".getBytes());
			outStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void saveFile2(Context context, String toSaveString, String fileName, boolean append) {
		try {
			String mDownloadUrl = context.getExternalCacheDir().getPath();

			File dir = new File(mDownloadUrl);
			File mFile = new File(dir.getPath(),fileName);

			Log.e("下载地址下载地址下载地址", dir.getPath()+dir.getName()+"");

			if(!dir.exists()){
				dir.mkdirs();
			}

			FileOutputStream outStream = new FileOutputStream(mFile, append);
			outStream.write(toSaveString.getBytes());
			outStream.write("\n".getBytes());
			outStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public static Notification buildNotification(Context context) {
		Notification notification = null;
		//设置后台定位
		//android8.0及以上使用NotificationUtils
		if (Build.VERSION.SDK_INT >= 26) {
			NotificationUtils notificationUtils = new NotificationUtils(context);
			Notification.Builder builder = notificationUtils.getAndroidChannelNotification
					("云物流", "正在后台定位");
			notification = builder.build();
		} else {
			//获取一个Notification构造器
			Notification.Builder builder = new Notification.Builder(context);
			Intent nfIntent = new Intent(context, MainActivity.class);

			builder.setContentIntent(PendingIntent.
							getActivity(context, 0, nfIntent, 0)) // 设置PendingIntent
					.setContentTitle("云物流") // 设置下拉列表里的标题
					.setSmallIcon(R.mipmap.ic_logo) // 设置状态栏内的小图标
					.setContentText("正在后台定位") // 设置上下文内容
					.setWhen(System.currentTimeMillis()); // 设置该通知发生的时间

			notification = builder.build(); // 获取构建好的Notification
		}
		notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
		return notification;
	}

    private static NotificationManager getNotificationManager(Context context) {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager)context.getSystemService(
                    Context.NOTIFICATION_SERVICE);
        }
        return mNotificationManager;
    }

    public static void startWifi(Context context) {
		WifiManager wm = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		wm.setWifiEnabled(true);
		wm.reconnect();
	}

	public static boolean isWifiEnabled(Context context) {
		WifiManager wm = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		return wm.isWifiEnabled();
	}

	public static String getManufacture(Context context) {
		return Build.MANUFACTURER;
	}

	public static Intent getCloseBrodecastIntent() {
		return new Intent(CLOSE_BRODECAST_INTENT_ACTION_NAME);
	}

	public static IntentFilter getCloseServiceFilter() {
		return new IntentFilter(CLOSE_BRODECAST_INTENT_ACTION_NAME);
	}

	public static class CloseServiceReceiver extends BroadcastReceiver {

		Service mService;

		public CloseServiceReceiver(Service service) {
			this.mService = service;
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			if (mService == null) {
				return;
			}
			mService.onDestroy();
		}
	}
}
