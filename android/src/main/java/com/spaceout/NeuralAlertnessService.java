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

import org.json.JSONException;
import org.json.JSONObject;

public class NeuralAlertnessService extends Service {
    public static final String USER_IS_ALERT = "com.spaceout.user_alert";
    public static final String USER_IS_BORED = "com.spaceout.user_bored";

    private Timer refreshTimer;
    private HttpClient client;

    private String ipAddress;

    private boolean userIsBored = false;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO -- implement
        return null;
    }

    // returns true if the user is bored, false otherwise
    public void checkIsBored(String ipAddress) {
        // http request to server for boredom
        HttpMethod method = new PostMethod(
            "http://" + ipAddress + ":8080/neural"
        );

        try {
            this.client.executeMethod(method);
            byte[] responseBody = method.getResponseBody();
            method.releaseConnection();
            if (responseBody == null) {
                Log.d("SPACEOUT", "no response received");
                // TODO -- show error on android GUI
                return;
            }

            String response = new String(responseBody);
            Log.d("SPACEOUT", "response was: " + response);

            // parse the JSON response
            try {
                JSONObject result = new JSONObject(response);
                this.userIsBored = result.getBoolean("spaced out?");
            } catch (JSONException jsEx) {
                Log.e("SPACEOUT", "Failed to parse result from server!");
                Log.e("SPACEOUT", jsEx.getMessage());
            }
        } catch (IOException ex) {
            // TODO -- show error on android GUI
            Log.e("SPACEOUT", "Failed to connect to server!");
            Log.e("SPACEOUT", ex.getMessage());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.ipAddress = intent.getStringExtra("ipAddress");

        this.client = new HttpClient();
        this.refreshTimer = new Timer();
        this.refreshTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                Log.d(
                    "SPACEOUT",
                    "polling " + ipAddress + " for alertness..."
                );

                boolean userWasAlreadyBored = userIsBored;
                checkIsBored(ipAddress);
                if (!userWasAlreadyBored && userIsBored) {
                    // broadcast that the user is bored
                    Log.d("SPACEOUT", "user is bored!");
                    Intent broadcast = new Intent();
                    broadcast.setAction(USER_IS_BORED);
                    sendBroadcast(broadcast);
                }
            }
        }, 0, 1000);

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        // kill the refresh timer
        this.refreshTimer.cancel();

        super.onDestroy();
    }
}
