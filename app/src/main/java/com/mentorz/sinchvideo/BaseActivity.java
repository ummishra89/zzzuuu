package com.mentorz.sinchvideo;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.linkedin.platform.LISessionManager;
import com.mentorz.MentorzApplication;
import com.mentorz.database.DbManager;
import com.mentorz.listener.SessionExpiredListener;
import com.mentorz.utils.Global;
import com.sinch.android.rtc.SinchError;

import org.jetbrains.annotations.NotNull;

public abstract class BaseActivity extends com.mentorz.activities.BaseActivity implements ServiceConnection, SinchService.StartFailedListener, SessionExpiredListener {

    private SinchService.SinchServiceInterface mSinchServiceInterface;

    private DbManager dbManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getApplicationContext().bindService(new Intent(this, SinchService.class), this,
                BIND_AUTO_CREATE);
        dbManager = DbManager.Companion.getInstance(getApplicationContext());
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        if (SinchService.class.getName().equals(componentName.getClassName())) {
            mSinchServiceInterface = (SinchService.SinchServiceInterface) iBinder;
            onServiceConnected();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        if (SinchService.class.getName().equals(componentName.getClassName())) {
            mSinchServiceInterface = null;
            onServiceDisconnected();
        }
    }

    protected void onServiceDisconnected() {
    }

    protected void onServiceConnected() {
        mSinchServiceInterface.setStartListener(this);
         onStarted();
    }

    protected SinchService.SinchServiceInterface getSinchServiceInterface() {
        return mSinchServiceInterface;
    }

    @Override
    public void onStartFailed(@NotNull SinchError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStarted() {
        if (!mSinchServiceInterface.isStarted()) {
            if (MentorzApplication.Companion.getInstance().getPrefs().getUserId() != 0) {
                mSinchServiceInterface.startClient(String.valueOf(MentorzApplication.Companion.getInstance().getPrefs().getUserId()));
            }
        }
    }

    @Override
    public void onSessionExpired() {
        Global.Companion.getUserInterests().clear();
        Global.Companion.getDefaultInterestsMap().clear();
        Global.Companion.getDefaultExpertiseMap().clear();
        Global.Companion.getMyexpertises().clear();
        MentorzApplication.Companion.getInstance().getPrefs().clear();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                dbManager.clear();
            }
        });
        thread.start();
        LoginManager.getInstance().logOut();
        LISessionManager.getInstance(getApplicationContext()).clearSession();
    }


}
