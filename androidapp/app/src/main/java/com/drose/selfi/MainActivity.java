package com.drose.selfi;

import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.drose.selfi.Camera.MainActivityC;
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

    Button buttonLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inRange = false;
        hello = (TextView)findViewById(R.id.hello);

        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        AccountManager.getInstance().setSharedPreferences(getPreferences(MODE_PRIVATE));

        mpactClient = MPactClient.getInstanceForApplication(this.getApplicationContext());
        mpactClient.bind(this);

        RegionNotifier.getInstanceForApplication(getApplicationContext()).setMainActivity(this);

        cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);

        if (!AccountManager.getInstance().signedIn()) {
            launchLoginActivity();
        } else if (inRange) {
            launchSelfieActivity();
        }
    }

    private void launchSelfieActivity() {
        onInRange();
    }

    private void launchLoginActivity() {
        Intent createAccount = new Intent(this, LoginActivity.class);
        startActivity(createAccount);
    }

    public void logout() {
        AccountManager.getInstance().logout();
        launchLoginActivity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mpactClient.unBind(this);
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
/*
        try {
            mpactClient.Start();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        */
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

        if (inRange) {
            launchSelfieActivity();
        }
    }

    public void onInRange(){
        Intent intent = new Intent(this, MainActivityC.class);
        if(inRange==true) {
            startActivity(intent);
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

        if (major != null) {
            this.major = major.intValue();
        }
        if (minor != null) {
            this.minor = minor.intValue();
        }

        if (inRange) {
            launchSelfieActivity();
        }
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
