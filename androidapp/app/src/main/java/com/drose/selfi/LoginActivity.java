package com.drose.selfi;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class LoginActivity extends ActionBarActivity {

    Button buttonSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        buttonSignUp = (Button) findViewById(R.id.buttonSignUp);
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCreateAccountActivity();
            }
        });
    }

    private void login(String username, String password) {
        final Context context = getApplicationContext();
        AccountManager.getInstance().login(username, password, new AccountCallback() {
            @Override
            public void loginComplete(boolean success) {
                if (success) {
                    goMainActivity();
                } else {
                    Toast.makeText(context, "Failed to login", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void signupComplete(boolean success) {
                // not used here
            }
        });
    }

    private void goMainActivity() {
        Intent createAccount = new Intent(this, MainActivity.class);
        startActivity(createAccount);
    }

    private void launchCreateAccountActivity() {
        Intent createAccount = new Intent(this, CreateAccount.class);
        startActivity(createAccount);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
