package com.spaceout;

import android.app.Activity;

import android.content.Intent;

import android.os.Bundle;

import android.media.MediaRecorder;
import android.media.AudioRecord;

public class MainActivity extends Activity {

	/**
	 * The audio sample properties expected by Wit.
	 */
	private static final int SAMPLE_RATE_HZ = 8000;
	private static final int CHANNEL_CONFIG = AudioRecord.CHANNEL_IN_MONO;
	private static final int AUDIO_FORMAT = AudioRecord.ENCODING_PCM_16BIT;
	private static final short DURATION_S = 10

    private AudioRecord audioRecorder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        audioRecorder = new AudioRecord(
			MediaRecorder.AudioSource.MIC,
			SAMPLE_RATE_HZ,
			CHANNEL_CONFIG,
			AUDIO_FORMAT,
			2 * AudioRecord.getMinBufferSize(
				SAMPLE_RATE_HZ,
				CHANNEL_CONFIG,
				AUDIO_FORMAT
			)
		);

		audioRecorder.startRecording();
    }

	@Override
	protected void onDestroy() {
		if (audioRecorder != null) {
			audioRecorder.release();
			audioRecorder = null;
		}
	}
}
