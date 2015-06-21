package com.drose.selfi;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class CreateAccount extends ActionBarActivity {

    Button createAccount;
    EditText editTextUserName;
    EditText editTextPassword;

    private String number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        createAccount = (Button) findViewById(R.id.buttonCreateAccount);
        editTextUserName = (EditText) findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        final Context context = getBaseContext();

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountManager.getInstance().signup(editTextUserName.getText().toString(), editTextPassword.getText().toString(), number, new AccountCallback() {
                    @Override
                    public void loginComplete(boolean success) {

                    }

                    @Override
                    public void signupComplete(boolean success) {
                        final boolean finalSuccess = success;
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                if (finalSuccess) {
                                    Toast.makeText(context, "Successfully signed up!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Failed to sign up :(", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    private String getPhoneNumber() {
        TelephonyManager tMgr = (TelephonyManager)getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        number = tMgr.getLine1Number();
        return number;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_account, menu);
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
}
