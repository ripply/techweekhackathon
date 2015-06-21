package com.drose.selfi;

/**
 * Created by ripply on 6/20/15.
 */
public interface AccountCallback {
    public void loginComplete(boolean success);
    public void signupComplete(boolean success);
}
