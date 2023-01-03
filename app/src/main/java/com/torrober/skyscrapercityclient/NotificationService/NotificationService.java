package com.torrober.skyscrapercityclient.NotificationService;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class NotificationService extends Service {
    public String notificationURL = "https://www.skyscrapercity.com/account/visitor-menu?_xfRequestUri=%2F&_xfWithData=1&_xfResponseType=json&_xfToken=";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
