package com.spaceout;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;

import android.app.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import android.os.IBinder;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class SpeechToTextService extends Service {
    public static final String SPEECH_DETECTED = "com.spaceout.speech_detected";
    private static final String API_KEY = "[KEY]";

    private HttpClient client;

	/**
	 * The audio sample properties expected by Wit.
	 */
	private static final int WIT_SAMPLE_RATE_HZ = 8000;
	private static final int WIT_CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
	private static final int WIT_AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int WIT_BUFFER_SIZE = 16 * 1024 * 10; // 16 Kbits per second

    private AudioRecord audioRecorder = null;
    private byte[] audioBuffer = new byte[WIT_BUFFER_SIZE];

    private boolean keepTranscoding = false;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO -- implement
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.client = new HttpClient();

        audioRecorder = new AudioRecord(
            MediaRecorder.AudioSource.MIC,
            WIT_SAMPLE_RATE_HZ,
            WIT_CHANNEL_CONFIG,
            WIT_AUDIO_FORMAT,
            WIT_BUFFER_SIZE
        );
        audioRecorder.startRecording();

        // register a listener for bored user
        Log.d("SPACEOUT", "registering hooks");
        IntentFilter filter = new IntentFilter();
        filter.addAction(NeuralAlertnessService.USER_IS_BORED);
        registerReceiver(boredReceiver, filter);
        filter = new IntentFilter();
        filter.addAction(NeuralAlertnessService.USER_IS_ALERT);
        registerReceiver(alertReceiver, filter);

        new Thread() {
            public void run() {
                while (audioRecorder != null) {
                    byte[] buffer = new byte[WIT_BUFFER_SIZE];
                    // continuously get the last X seconds as a byte array
                    audioRecorder.read(buffer, 0, WIT_BUFFER_SIZE);
                    audioBuffer = buffer;
                }
            }
        }.start();

        return Service.START_STICKY;
    }

    private BroadcastReceiver boredReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            keepTranscoding = true;
            new Thread() {
                public void run() {
                    while (keepTranscoding) {
                        Log.d("SPACEOUT", "Retrieving spoken text");
                        byte[] buffer = audioBuffer;
                        audioBuffer = new byte[WIT_BUFFER_SIZE];
                        String spokenText = getSpokenText(buffer);

                        if (spokenText == null) {
                            Log.d("SPACEOUT", "No spoken text was received");
                            continue;
                        }

                        Intent broadcast = new Intent();
                        broadcast.setAction(SPEECH_DETECTED);
                        broadcast.putExtra("text", spokenText);
                        sendBroadcast(broadcast);
                    }
                }
            }.start();
        }
    };

    private BroadcastReceiver alertReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            new Thread() {
                public void run() {
                    keepTranscoding = false;
                }
            }.start();
        }
    };

    public String getSpokenText(byte[] buffer) {
        // pass the byte array out to Wit.AI
        PostMethod post = new PostMethod("https://api.wit.ai/speech");
        post.setRequestHeader("Authorization", "Bearer " + API_KEY);
        post.setRequestHeader("Content-Type", "audio/raw;encoding=signed-integer;bits=16;rate=8000;endian=little");
        post.setRequestHeader("Content-Length", ""+WIT_BUFFER_SIZE);
        post.setRequestEntity(new ByteArrayRequestEntity(buffer));

        String messageID = null;
        try {
            int resultCode = this.client.executeMethod(post);
            Log.d("SPACEOUT", "POST response code: " + resultCode);

            byte[] responseBody = post.getResponseBody();
            post.releaseConnection();
            if (responseBody == null) {
                Log.d("SPACEOUT", "no POST response received from Wit.AI");
                // TODO -- show error on android GUI
                return null;
            }

            String response = new String(responseBody);
            Log.d("SPACEOUT", "response was: " + response);

            // parse the JSON response
            try {
                JSONObject result = new JSONObject(response);
                return result.getString("msg_body");
            } catch (JSONException jsEx) {
                Log.e("SPACEOUT", "Failed to parse POST result from Wit.AI!");
                Log.e("SPACEOUT", jsEx.getMessage());
            }
        } catch (IOException ex) {
            // TODO -- show error on android GUI
            Log.e("SPACEOUT", "Failed to connect to Wit.AI!");
            Log.e("SPACEOUT", ex.getMessage());
        }

        return null;
    }

    public void onDestroy() {
        if (audioRecorder != null) {
            audioRecorder.release();
            audioRecorder = null;
        }
        unregisterReceiver(boredReceiver);
        unregisterReceiver(alertReceiver);
        super.onDestroy();
    }
}
