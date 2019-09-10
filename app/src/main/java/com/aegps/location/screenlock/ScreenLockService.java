package com.aegps.location.screenlock;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;

public class ScreenLockService extends Service {

    private BroadcastReceiver screenLockReceiver;

    public ScreenLockService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (screenLockReceiver == null) {
            screenLockReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                        Intent lockScreen = new Intent(ScreenLockService.this, ScreenLockActivity.class);
                        lockScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        startActivity(lockScreen);
                    }

//                    if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
//                        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
//                        if (keyguardManager.isKeyguardSecure()) {
//                            Intent i = new Intent(NOTIFY_USER_PRESENT);
//                            context.sendBroadcast(i);
//                        }
//                    }
                }
            };

            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            registerReceiver(screenLockReceiver, filter);
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        if (screenLockReceiver != null) {
            unregisterReceiver(screenLockReceiver);
            screenLockReceiver = null;
        }
        super.onDestroy();
    }
}
