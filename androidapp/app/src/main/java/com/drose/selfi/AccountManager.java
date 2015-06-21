package com.drose.selfi;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ripply on 6/20/15.
 */
public class AccountManager {

    public static String TAG = "AccountManager";

    private static AccountManager _instance;
    private SharedPreferences preferences;

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
    public void setSharedPreferences(SharedPreferences preferences) {
        this.preferences = preferences;
        if (userId == null) {
            userId = preferences.getString("id", "");
        }
    }

    public void login(String username, String password, AccountCallback callback) {
        LoginTask loginTask = new LoginTask(username, password, callback);
        loginTask.execute();
    }

    public void signup(String username, String password, String number, AccountCallback callback) {
        SignupTask signupTask = new SignupTask(username, password, number, callback);
        signupTask.execute();
    }

    public void enter(String url, String locationTag, AccountCallback callback) {
        EnterTask enterTask = new EnterTask(url, locationTag, callback);
        enterTask.execute();
    }

    public void logout() {
        if (userId != null || userId.length() > 0) {
            if (this.preferences != null) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("id", "");
                editor.commit();
            }
        }
        this.userId = null;
    }

    public boolean signedIn() {
        if (userId == null || userId.length() == 0) {
            if (preferences != null) {
                userId = preferences.getString("id", "");
            }
        }

        return userId != null && userId.length() > 0;
    }

    private void storeUserId(String id) {
        if (id != null && id.length() > 0) {
            if (preferences != null) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("id", id);
                if (editor.commit()) {
                    Log.i(TAG, "Successfully saved userid: " + id);
                } else {
                    Log.e(TAG, "FAILED to save userid: " + id);
                }
            }
            this.userId = id;
        }
    }

    private class EnterTask extends AsyncTask {

        String url;
        String location;
        AccountCallback callback;

        public EnterTask(String url, String location, AccountCallback callback) {
            this.url = url;
            this.location = location;
            this.callback = callback;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            List<NameValuePair> values = new ArrayList<NameValuePair>(2);
            values.add(new BasicNameValuePair("url", url));
            values.add(new BasicNameValuePair("location", location));
            values.add(new BasicNameValuePair("id", userId));

            HttpResponse response = postData("/entry", values);
            if (response != null && response.getStatusLine() != null) {
                callback.entryComplete(response.getStatusLine().getStatusCode() == 200);
                return null;
            }

            callback.entryComplete(false);
            return null;
        }
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

            HttpResponse response = postData("/login", values);
            if (response != null && response.getStatusLine() != null) {
                if (response.getStatusLine().getStatusCode() == 200) {
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                        StringBuilder builder = new StringBuilder();
                        for (String line = null; (line = reader.readLine()) != null; ) {
                            builder.append(line).append("\n");
                        }
                        JSONTokener tokener = new JSONTokener(builder.toString());
                        JSONObject jsonObject = new JSONObject(tokener);
                        String id = jsonObject.getString("id");
                        storeUserId(id);
                        callback.loginComplete(response.getStatusLine().getStatusCode() == 200);
                        return null;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
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
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                        StringBuilder builder = new StringBuilder();
                        for (String line = null; (line = reader.readLine()) != null; ) {
                            builder.append(line).append("\n");
                        }
                        JSONTokener tokener = new JSONTokener(builder.toString());
                        JSONObject jsonObject = new JSONObject(tokener);
                        String id = jsonObject.getString("id");
                        storeUserId(id);

                        callback.signupComplete(true);
                        return null;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    callback.signupComplete(false);
                }
            }

            callback.signupComplete(false);
            return null;
        }
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
