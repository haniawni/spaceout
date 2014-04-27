/*
 * Audio recording service that captures audio from the mic to files in
 * internal storage.
 *
 * The application needs to have the permission to write to external storage
 * if the output file is written to the external storage, and also the
 * permission to record audio. These permissions must be set in the
 * application's AndroidManifest.xml file, with something like:
 *
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 * <uses-permission android:name="android.permission.RECORD_AUDIO" />
 *
 */
package com.spaceout;

import android.app.Service;
import android.os.Bundle;
import android.os.Environment;
import android.view.ViewGroup;
import android.util.Log;
import android.media.MediaRecorder;

import java.io.IOException;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class AudioRecorderService extends Service
{
    private static final String LOG_TAG = "AudioRecorderService";
    private static String mFileNamePrefix = "spaceout_";

    private MediaRecorder mRecorder = null;

	// audio must be broken into chunks because Wit cannot consume audio clips
	// longer than 10 seconds
	
	// the duration of each audio chunk in seconds; since Wit accepts a
	// maximum of 10s, we define our chunk duration slightly less because Java
	// does not guarantee scheduled events will fire exactly on time so we
	// give Java a half second of padding
	private static final double CHUNK_DURATION_S = 9.5

	// index number of current audio chunk
	private int latestFileChunkNumber = -1;

	private ScheduledThreadPoolExecutor scheduler =
		new ScheduledThreadPoolExecutor(1);


    private void startRecording() {
        mRecorder = new MediaRecorder();

		scheduler.scheduleAtFixedRate(
			<command>,
			0,
			CHUNK_DURATION,
			TimeUnit.SECONDS
		)
    }

	private void startRecordingChunk() {

		this.latestFileChunk++;

        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setOutputFile(mFileName + latestFileChunkNumber.toString());

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
	}

	public class TransitionToNextChunk implements Runnable {

		@Override
		public void run() {
			AudioRecorderService.this.stopRecordingChunk();
			AudioRecorderService.this.startRecordingChunk();
		}
	}

	private void stopRecordingChunk() {
        mRecorder.stop();
	}

    private void stopRecording() {
		scheduler.shutdownNow();
		stopRecordingChunk();
        mRecorder.release();
        mRecorder = null;
    }

	@Override
	public IBinder onBind(Intent intent) {
		startRecording();
	}

	@Override
	public void onDestroy() {
		stopRecording();
	}
}

