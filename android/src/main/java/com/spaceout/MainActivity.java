package com.spaceout;

import android.app.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.os.Bundle;

import android.util.Log;

import android.media.MediaRecorder;
import android.media.AudioRecord;

import android.view.View;

import android.widget.TextView;

public class MainActivity extends Activity {

    private static final int REQUEST_CODE = 1;

	/**
	 * The audio sample properties expected by Wit.
	 */
	private static final int WIT_SAMPLE_RATE_HZ = 8000;
	private static final int WIT_CHANNEL_CONFIG = AudioRecord.CHANNEL_IN_MONO;
	private static final int WIT_AUDIO_FORMAT = AudioRecord.ENCODING_PCM_16BIT;
	private static final short WIT_MAX_DURATION_S = 10

    private AudioRecord audioRecorder = null;

    private String ipAddress;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            TextView subtitles = (TextView) findViewById(R.id.subtitles);
            subtitles.append("User is bored!\n");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        audioRecorder = new AudioRecord(
			MediaRecorder.AudioSource.MIC,
			WIT_SAMPLE_RATE_HZ,
			WIT_CHANNEL_CONFIG,
			WIT_AUDIO_FORMAT,
			2 * AudioRecord.getMinBufferSize(
				WIT_SAMPLE_RATE_HZ,
				WIT_CHANNEL_CONFIG,
				WIT_AUDIO_FORMAT
			)
		);
		audioRecorder.startRecording();
    }

    @Override
    protected void onResume() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(NeuralAlertnessService.USER_IS_BORED);
        registerReceiver(receiver, filter);
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
    }

    public void setNetwork(View view) {
        Intent networkIntent = new Intent(this, NetworkActivity.class);
        networkIntent.putExtra("ipAddress", this.ipAddress);
        startActivityForResult(networkIntent, REQUEST_CODE);
    }

    public void clearSubtitles(View view) {
        if (this.ipAddress != null) {
            TextView subtitles = (TextView) findViewById(R.id.subtitles);
            subtitles.setText("IP address: " + this.ipAddress + "\n\n---\n\n");
        }
    }

    @Override
    public void onDestroy() {
		if (audioRecorder != null) {
			audioRecorder.release();
			audioRecorder = null;
		}

        stopService(new Intent(this, NeuralAlertnessService.class));

        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                this.ipAddress = data.getStringExtra("ipAddress");
                Log.d("SPACEOUT", "ip address of server set to " + this.ipAddress);

                // restart the service with the new ip address
                stopService(new Intent(this, NeuralAlertnessService.class));
                Intent serviceIntent = new Intent(this, NeuralAlertnessService.class);
                serviceIntent.putExtra("ipAddress", this.ipAddress);
                startService(serviceIntent);

                clearSubtitles(null);
            }
        }
    }

}
