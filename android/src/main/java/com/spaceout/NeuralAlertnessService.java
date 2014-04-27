package com.spaceout;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;

import android.content.Context;
import android.content.Intent;

import android.os.IBinder;

import android.util.Log;

public class NeuralAlertnessService extends Service {
    private Timer refreshTimer;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO -- implement
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        this.refreshTimer = new Timer();
        this.refreshTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                // TODO - poll server to check if the user has spaced out
                Log.d(
                    "SPACE OUT: NeuralAlertnessService",
                    "polling for alertness..."
                );
            }
        }, 0, 1000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        // kill the refresh timer
        this.refreshTimer.cancel();

        super.onDestroy();
    }
}
