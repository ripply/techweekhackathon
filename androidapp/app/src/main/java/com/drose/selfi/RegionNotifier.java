package com.drose.selfi;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.zebra.mpact.mpactclient.MPactBeaconType;
import com.zebra.mpact.mpactclient.MPactClientNotifier;
import com.zebra.mpact.mpactclient.MPactProximity;
import com.zebra.mpact.mpactclient.MPactTag;

public class RegionNotifier implements MPactClientNotifier {

    private static RegionNotifier client = null;
    private Context context;
    private int regionState = MPactClientNotifier.OUTSIDE;
    private MainActivity mainActivity = null;

    public static RegionNotifier getInstanceForApplication(Context context) {
        if (client == null) {
            client = new RegionNotifier(context.getApplicationContext());
        }
        return client;
    }

    protected RegionNotifier(Context context) {
        this.context = context;
    }

    @Override
    public void didDetermineClosestTag(MPactTag mPactTag) {

    }

    @Override
    public void didDetermineState(int state) {
        if(state == MPactClientNotifier.INSIDE) {
            notify("Nows the perfect time for a selfie! Win prizes!");
        } else if(state == MPactClientNotifier.OUTSIDE) {
            notify("");
        }
        regionState = state;
    }

    @Override
    public void didDetermineState(int i, Integer integer, Integer integer2) {
    }

    @Override
    public void didChangeIBeaconUUID(String s) {

    }

    @Override
    public void didChangeBeaconType(MPactBeaconType mPactBeaconType) {
    }

    @Override
    public void didChangeProximityRange(MPactProximity mPactProximity) {

    }

    private void notify(String msg) {
        // Generate a notification
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Selfie Opportunity!")
                .setContentText(msg);
        Intent resultIntent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());

        // Turn on the screen if the device is sleeping
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK|PowerManager.ACQUIRE_CAUSES_WAKEUP, "Mpact alert");
        wl.acquire(3000);
        wl.release();
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }
}
