package com.drose.selfi;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ripply on 6/20/15.
 */
public class AccountManager {

    private static AccountManager _instance;

    public static AccountManager getInstance() {
        if (_instance == null) {
            _instance = new AccountManager();
        }
        return _instance;
    }

    private AccountManager() {
        url = "ec2-52-26-252-12.us-west-2.compute.amazonaws.com";
        //url = "http://ec2-52-26-252-12.us-west-2.compute.amazonaws.com:3000/";
    }

    private String userId;
    private String url;

    public void login(String username, String password, AccountCallback callback) {
        LoginTask loginTask = new LoginTask(username, password, callback);
        loginTask.execute(null);
    }

    public void signup(String username, String password, String number, AccountCallback callback) {
        SignupTask signupTask = new SignupTask(username, password, number, callback);
        signupTask.execute(null);
    }

    private class LoginTask extends AsyncTask {

        String username;
        String password;
        AccountCallback callback;

        public LoginTask(String username, String password, AccountCallback callback) {
            this.username = username;
            this.password = password;
            this.callback = callback;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            List<NameValuePair> values = new ArrayList<NameValuePair>(2);
            values.add(new BasicNameValuePair("user", username));
            values.add(new BasicNameValuePair("password", password));

            HttpResponse response = postData("/user", values);
            if (response != null && response.getStatusLine() != null) {
                callback.loginComplete(response.getStatusLine().getStatusCode() == 200);
                return null;
            }

            callback.loginComplete(false);
            return null;
        }
    }

    private class SignupTask extends AsyncTask {

        String username;
        String password;
        String number;
        AccountCallback callback;

        public SignupTask(String username, String password, String number, AccountCallback callback) {
            this.username = username;
            this.password = password;
            this.number = number;
            this.callback = callback;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            List<NameValuePair> values = new ArrayList<NameValuePair>(3);
            values.add(new BasicNameValuePair("user", username));
            values.add(new BasicNameValuePair("password", password));
            values.add(new BasicNameValuePair("number", number));

            HttpResponse response = postData("/user", values);
            if (response != null && response.getStatusLine() != null) {
                if (response.getStatusLine().getStatusCode() == 200) {
                    List<NameValuePair> values2 = new ArrayList<NameValuePair>(2);
                    values.add(new BasicNameValuePair("user", username));
                    values.add(new BasicNameValuePair("password", password));

                    HttpResponse response2 = postData("/login", values);
                    callback.signupComplete(true);
                    return null;
                }
            }

            callback.signupComplete(false);
            return null;
        }
    }

    public void logout() {
        userId = null;
    }

    private HttpResponse postData(String resource, List<NameValuePair> pairs) {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        URI uri;
        try {
            uri = new URI("http", null, url, 3000, resource, null, null);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
        HttpPost httppost = new HttpPost(uri);
        //HttpPost httppost = new HttpPost(url + resource);

        try {
            // Add your data
            int extra = 0;
            if (userId != null) {
                extra = 1;
            }
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(pairs.size() + extra);
            if (userId != null) {
                nameValuePairs.add(new BasicNameValuePair("id", userId));
            }
            for (NameValuePair pair : pairs) {
                nameValuePairs.add(pair);
            }
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);

            return response;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            // TODO Auto-generated catch block
        } catch (IOException e) {
            e.printStackTrace();
            // TODO Auto-generated catch block
        }

        return null;
    }

}
