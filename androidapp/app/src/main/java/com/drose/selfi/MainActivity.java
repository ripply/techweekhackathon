package com.drose.selfi;

import android.app.ActivityManager;
import android.content.Context;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.zebra.mpact.mpactclient.MPactBeaconType;
import com.zebra.mpact.mpactclient.MPactClient;
import com.zebra.mpact.mpactclient.MPactClientConsumer;
import com.zebra.mpact.mpactclient.MPactClientNotifier;
import com.zebra.mpact.mpactclient.MPactProximity;
import com.zebra.mpact.mpactclient.MPactServerInfo;
import com.zebra.mpact.mpactclient.MPactTag;

import java.net.CookieHandler;
import java.net.CookieManager;

public class MainActivity extends ActionBarActivity implements MPactClientConsumer, MPactClientNotifier,
        Switch.OnCheckedChangeListener, AdapterView.OnItemSelectedListener {

    MPactClient mpactClient;
    private static CookieManager cookieManager;
    TextView hello;
    boolean inRange;
    int major;
    int minor;
    String beaconUuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inRange = false;
        hello = (TextView)findViewById(R.id.hello);

        mpactClient = MPactClient.getInstanceForApplication(this.getApplicationContext());
        mpactClient.bind(this);

        cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMPactClientServiceConnect() {
        mpactClient.setNotifier(this);

        MPactServerInfo mpactServerInfo = new MPactServerInfo();
        //mpactClient.setClientName("3b824fa62ad3f3cd");
        mpactServerInfo.setHost("54.166.9.255");
        mpactServerInfo.setPort(443);
        mpactServerInfo.setLoginID("techweek");
        mpactServerInfo.setPassword("TechWeek123#");
        //Switch switchTemp = (Switch)findViewById(R.id.switchHTTPS);
        mpactServerInfo.setUseHTTPS(true);
        //switchTemp = (Switch)findViewById(R.id.switchAuthenticate);
        mpactServerInfo.setAuthenticate(true);
        //mpactClient.setServer(mpactServerInfo);
        //mpactClient.setiBeaconUUID("fe913213-b311-4a42-8c16-47fae-ac938db");

        try {
            mpactClient.Start();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void didDetermineClosestTag(MPactTag mPactTag) {
        Toast.makeText(this, "Determined closest tag", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void didDetermineState(int state) {
        switch (state) {
            case INSIDE:
                inRange = true;
                break;
            case OUTSIDE:
                inRange = false;
                break;
            default:
                break;
        }
    }

    @Override
    public void didDetermineState(int state, Integer major, Integer minor) {
        switch (state) {
            case INSIDE:
                inRange = true;
                break;
            case OUTSIDE:
                inRange = false;
                break;
            default:
                break;
        }

        this.major = major;
        this.minor = minor;
    }

    @Override
    public void didChangeIBeaconUUID(String s) {
        beaconUuid = s;
    }

    @Override
    public void didChangeBeaconType(MPactBeaconType mPactBeaconType) {
        Toast.makeText(this, "Beacon Type!!!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void didChangeProximityRange(MPactProximity mPactProximity) {
        Toast.makeText(this, "Proximity range!!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
