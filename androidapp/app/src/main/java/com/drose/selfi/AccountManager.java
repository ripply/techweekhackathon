package com.drose.selfi;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
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
        url = "http://localhost:300/";
    }

    private String userId;
    private String url;

    public boolean login(String username, String password) {
        List<NameValuePair> values = new ArrayList<NameValuePair>(2);
        values.add(new BasicNameValuePair("user", username));
        values.add(new BasicNameValuePair("password", password));

        HttpResponse response = postData("login", values);
        if (response.getStatusLine().getStatusCode() == 200) {


            return true;
        } else {
            return false;
        }
    }

    public boolean signup(String username, String password) {
        List<NameValuePair> values = new ArrayList<NameValuePair>(2);
        values.add(new BasicNameValuePair("user", username));
        values.add(new BasicNameValuePair("password", password));

        HttpResponse response = postData("user", values);
        if (response.getStatusLine().getStatusCode() == 200) {
            return login(username, password);
        } else {
            return false;
        }
    }

    public void logout() {
        userId = null;
    }

    public HttpResponse postData(String resource, List<NameValuePair> pairs) {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url + resource);

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
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }

        return null;
    }

}
