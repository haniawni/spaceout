package com.spaceout;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;

import android.app.Service;

import android.content.Intent;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import android.os.IBinder;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class SpeechToTextService extends Service {
    private static final String API_KEY = "[KEY]";

    private HttpClient client;

	/**
	 * The audio sample properties expected by Wit.
	 */
	private static final int WIT_SAMPLE_RATE_HZ = 8000;
	private static final int WIT_CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
	private static final int WIT_AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
	private static final short WIT_MAX_DURATION_S = 10;

    private AudioRecord audioRecorder = null;
    private int bufferSize;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO -- implement
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.client = new HttpClient();

        this.bufferSize = 2 * AudioRecord.getMinBufferSize(
            WIT_SAMPLE_RATE_HZ,
            WIT_CHANNEL_CONFIG,
            WIT_AUDIO_FORMAT
        );

        audioRecorder = new AudioRecord(
			MediaRecorder.AudioSource.MIC,
			WIT_SAMPLE_RATE_HZ,
			WIT_CHANNEL_CONFIG,
			WIT_AUDIO_FORMAT,
            this.bufferSize
		);
		audioRecorder.startRecording();

        return Service.START_STICKY;
    }

    public String getSpokenText() {
        // get the speech sound as a byte array
        byte[] buffer = new byte[bufferSize];
        audioRecorder.read(buffer, 0, bufferSize);

        // pass the byte array out to Wit.AI
        PostMethod method = new PostMethod("https://api.wit.ai/speech");
        method.setRequestHeader("Authorization", "Bearer: " + API_KEY);
        method.setRequestHeader("Content-Type", "audio/raw;encoding=unsigned-integer;bits=16;rate=8000;endian=big");
        method.setRequestEntity(new ByteArrayRequestEntity(buffer));

        String messageID = null;
        try {
            this.client.executeMethod(method);

            byte[] responseBody = method.getResponseBody();
            method.releaseConnection();
            if (responseBody == null) {
                Log.d("SPACEOUT", "no response received from Wit.AI");
                // TODO -- show error on android GUI
                return null;
            }

            String response = new String(responseBody);
            Log.d("SPACEOUT", "response was: " + response);

            // parse the JSON response
            try {
                JSONObject result = new JSONObject(response);
                messageID = result.getString("msg_id");
            } catch (JSONException jsEx) {
                Log.e("SPACEOUT", "Failed to parse result from Wit.AI!");
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
        super.onDestroy();
    }
}
