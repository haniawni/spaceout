package com.spaceout;

import android.app.Service;

import android.content.Intent;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import android.os.IBinder;

public class AudioRecordingService extends Service {
	/**
	 * The audio sample properties expected by Wit.
	 */
	private static final int WIT_SAMPLE_RATE_HZ = 8000;
	private static final int WIT_CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
	private static final int WIT_AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
	private static final short WIT_MAX_DURATION_S = 10;

    private AudioRecord audioRecorder = null;
    private byte[] audioBuffer;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO -- implement
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
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

        return Service.START_STICKY;
    }

    public void recordSample() {
		audioRecorder.startRecording();
    }

    public void onDestroy() {
		if (audioRecorder != null) {
			audioRecorder.release();
			audioRecorder = null;
		}
        super.onDestroy();
    }
}
