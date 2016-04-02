package com.sinch.android.rtc.sample.video;

import com.sinch.android.rtc.SinchError;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends BaseActivity implements SinchService.StartFailedListener {

    private Button mLoginButton;
    private EditText mLoginName;
    private ProgressDialog mSpinner;
    private String MyUser;
    SharedPreferences sharedPreferences;
    private boolean newUser = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        sharedPreferences = getSharedPreferences(Utilities.MY_PREFS, Context.MODE_PRIVATE);
        MyUser = sharedPreferences.getString(Utilities.MY_USER_NAME, "");
        Log.d("app", MyUser + " -is my user");

        if (MyUser== "")
        {
            mLoginName = (EditText) findViewById(R.id.loginName);

            mLoginButton = (Button) findViewById(R.id.loginButton);
            mLoginButton.setEnabled(false);
            mLoginButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    loginClicked();
                }
            });

        }
        else
            newUser = false;


    }

    @Override
    protected void onServiceConnected() {
        getSinchServiceInterface().setStartListener(this);
        if (newUser == true)
        {
            mLoginButton.setEnabled(true);
        }
        else
        {
            if (!getSinchServiceInterface().isStarted()) {
                getSinchServiceInterface().startClient(MyUser);
                showSpinner();
            } else {

                openPlaceCallActivity();

            }
        }

    }

    @Override
    protected void onPause() {
        if (mSpinner != null) {
            mSpinner.dismiss();
        }
        super.onPause();
    }

    @Override
    public void onStartFailed(SinchError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
        if (mSpinner != null) {
            mSpinner.dismiss();
        }
    }

    @Override
    public void onStarted() {
        openPlaceCallActivity();
    }

    private void loginClicked() {
        String userName = mLoginName.getText().toString();

        if (userName.isEmpty()) {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show();
            return;
        }

        if (!getSinchServiceInterface().isStarted()) {
            getSinchServiceInterface().startClient(userName);
            showSpinner();
        } else {

            openPlaceCallActivity();

        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Utilities.MY_USER_NAME, userName);
        editor.commit();
    }

    private void openPlaceCallActivity() {
        Intent mainActivity = new Intent(this, PlaceCallActivity.class);
        startActivity(mainActivity);
        finish();
    }

    private void showSpinner() {
        mSpinner = new ProgressDialog(this);
        mSpinner.setTitle("Logging in");
        mSpinner.setMessage("Please wait...");
        mSpinner.show();
    }
}
