package com.spaceout;

import java.io.IOException;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import android.app.Service;

import android.content.Context;
import android.content.Intent;

import android.os.IBinder;

import android.util.Log;

public class NeuralAlertnessService extends Service {
    private Timer refreshTimer;
    private HttpClient client;

    private String ipAddress = "192.168.1.169";

    @Override
    public IBinder onBind(Intent intent) {
        // TODO -- implement
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        this.client = new HttpClient();
        this.refreshTimer = new Timer();
        this.refreshTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                Log.d(
                    "SPACEOUT",
                    "polling for alertness..."
                );
                checkIsBored();
            }
        }, 0, 1000);
    }

    // returns true if the user is bored, false otherwise
    public boolean checkIsBored() {
        // TODO -- http request to server for boredom
        HttpMethod method = new PostMethod(
            "http://" + this.ipAddress + ":8080/neural"
        );

        try {
            this.client.executeMethod(method);
            byte[] responseBody = method.getResponseBody();
            method.releaseConnection();
            if (responseBody == null) {
                Log.d("SPACEOUT", "no response received");
                // TODO -- show error on android GUI
                return false;
            }

            String response = new String(responseBody);
            Log.d("SPACEOUT", "response was: " + response);

            // TODO -- parse JSON and check "spaced out?" key for true/false
        } catch (IOException ex) {
            // TODO -- show error on android GUI
            Log.e("SPACEOUT", "Failed to connect to server!");
            Log.e("SPACEOUT", ex.getMessage());
        }

        return false;
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
