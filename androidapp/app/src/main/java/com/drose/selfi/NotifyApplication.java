package com.drose.selfi;

import android.app.Application;
import android.content.Intent;

public class NotifyApplication extends Application {
    public void onCreate() {
        super.onCreate();

        // Start up the background beacon monitoring service
        Intent myIntent = new Intent(this, MonitorService.class);
        startService(myIntent);
    }
}
