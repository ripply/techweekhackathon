package com.drose.selfi;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.zebra.mpact.mpactclient.MPactBeaconType;
import com.zebra.mpact.mpactclient.MPactClient;
import com.zebra.mpact.mpactclient.MPactClientConsumer;
import com.zebra.mpact.mpactclient.MPactClientNotifier;
import com.zebra.mpact.mpactclient.MPactLogLevel;
import com.zebra.mpact.mpactclient.MPactLogZone;
import com.zebra.mpact.mpactclient.MPactLogger;
import com.zebra.mpact.mpactclient.MPactProximity;
import com.zebra.mpact.mpactclient.MPactTag;

public class MonitorService extends Service
        implements MPactClientConsumer {

    static MPactClient mpactClient = null;
    static RegionNotifier regionNotifier = null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(mpactClient == null) {
            // Get instance of the MPact Service
            mpactClient = MPactClient.getInstanceForApplication(getApplicationContext());
        }

        // Bind to the MPact Service
        mpactClient.bind(this);

        // This return code will respawn the app if it gets killed
        return START_STICKY;
    }

    @Override
    public void onMPactClientServiceConnect() {
        // Setup parameters and start the MPact Client
        if(regionNotifier == null) {
            // Get instance of the MPact Service
            regionNotifier = RegionNotifier.getInstanceForApplication(getApplicationContext());
        }

        mpactClient.setNotifier(regionNotifier);
        mpactClient.setiBeaconUUID("fe913213-b311-4a42-8c16-47faeac938db");
        try {
            mpactClient.Start();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
