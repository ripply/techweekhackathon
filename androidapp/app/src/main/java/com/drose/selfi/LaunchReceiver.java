package com.drose.selfi;

        import android.content.BroadcastReceiver;

/**
 * The only purpose of this class is to startup the application through receiving a
 * broadcast message from the system.
 */
public class LaunchReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(android.content.Context context, android.content.Intent intent) {
        /* Uncomment this out to make the main activity visible in the task switcher
        Intent myIntent = new Intent(context, MainActivity.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(myIntent);
        */
    }
}
