package com.spaceout;

import android.app.Activity;

import android.content.Intent;

import android.os.Bundle;

import android.media.MediaRecorder;
import android.media.AudioRecord;

import android.view.View;

public class MainActivity extends Activity {

	/**
	 * The audio sample properties expected by Wit.
	 */
	private static final int WIT_SAMPLE_RATE_HZ = 8000;
	private static final int WIT_CHANNEL_CONFIG = AudioRecord.CHANNEL_IN_MONO;
	private static final int WIT_AUDIO_FORMAT = AudioRecord.ENCODING_PCM_16BIT;
	private static final short WIT_MAX_DURATION_S = 10

    private AudioRecord audioRecorder = null;

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

		startService(new Intent(this, NeuralAlertnessService.class));
    }

    public void setNetwork(View view) {
        startActivity(new Intent(this, NetworkActivity.class));
    }

	@Override
	protected void onDestroy() {
		if (audioRecorder != null) {
			audioRecorder.release();
			audioRecorder = null;
		}

		stopService(new Intent(this, NeuralAlertnessService.class));

        super.onDestroy();
	}
}
