package com.creative.cutebond.controls;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.creative.cutebond.AudioUpload;
import com.creative.cutebond.R;
import com.creative.cutebond.common.Constants;

public class AudioRecordingControl extends ImageView implements
		OnClickListener, OnCompletionListener {

	private boolean ON_PAUSE_STATE = false;

	private int currentPosition = 0;

	private static final String AUDIO_RECORDER_FOLDER = Constants.CACHEAUDIO;
	private Context mContext = null; //mp3
	private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";
	private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
	private MediaRecorder recorder = null;
	private int currentFormat = 0;
	private String file_exts[] = { AUDIO_RECORDER_FILE_EXT_MP4,
			AUDIO_RECORDER_FILE_EXT_3GP };
	private int output_formats[] = { MediaRecorder.OutputFormat.MPEG_4,
			MediaRecorder.OutputFormat.THREE_GPP };

	private MediaPlayer mPlayer = null;

	private String recordingPath = "";

	public AudioRecordingControl(Context context) {
		super(context);
		mContext = context;
		init();
	}

	public AudioRecordingControl(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	public AudioRecordingControl(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		init();
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.imimobile.odp.controls.common.IControl#init(com.imimobile.odp.controls.common.CControl)
	 */
	private void init() {

		this.setOnClickListener(this);
		this.setTag("Record");

		/*
		 * param = (String) parent.params.get("extension"); if(param != null &&
		 * !param.equalsIgnoreCase("mp4"))
		 */
		currentFormat = 1;

	}

	/**
	 * getFilename - returns file path of recoding.
	 * 
	 * @return String
	 */
	private String getFilename() {
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(filepath, AUDIO_RECORDER_FOLDER);

		if (!file.exists()) {
			file.mkdirs();
		}
		recordingPath = (file.getAbsolutePath() + "/"
				+ System.currentTimeMillis() + file_exts[currentFormat]);
		return recordingPath;
	}

	/**
	 * startRecording - starts recording
	 */
	private void startRecording() {
		recorder = new MediaRecorder();

		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(output_formats[currentFormat]);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		recorder.setOutputFile(getFilename());
		recorder.setOnErrorListener(errorListener);
		recorder.setOnInfoListener(infoListener);

		try {
			recorder.prepare();
			recorder.start();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * stopRecording - stop recording
	 */
	private void stopRecording() {
		if (null != recorder) {
			recorder.stop();
			recorder.reset();
			recorder.release();
			recorder = null;
		}
	}

	/**
	 * pauseRecording - pause recording
	 */
	private void pauseRecording() {

	}

	/**
	 * playRecording - plays newly recorded file
	 */
	private void playRecording() {
		if (recorder == null) {

			if (ON_PAUSE_STATE) {
				mPlayer.start();
				mPlayer.seekTo(currentPosition);
				return;
			}

			if (mPlayer == null) {
				mPlayer = new MediaPlayer();
				mPlayer.setOnCompletionListener(this);
			}

			try {
				mPlayer.setDataSource(recordingPath);
				mPlayer.prepare();
				mPlayer.start();
			} catch (IOException e) {
				Log.e("MediaPlayer : ", "prepare() failed");
			}
		} else {
			Toast.makeText(mContext, "Recording ...please wait",
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * stopPlaying - stop playing recoded file
	 */
	private void stopPlaying() {
		mPlayer.release();
		mPlayer = null;
	}

	/**
	 * deleteRecording - delete recording
	 */
	public void deleteRecording() {
		if (recorder == null) {
			File file = new File(recordingPath);
			if (file.exists()) {
				if (file.delete())
					Toast.makeText(mContext, "Recording deleted successfully",
							Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(mContext, "File doesnt exits",
						Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(mContext, "Recording ...please wait",
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Error listener for audio recording
	 */
	private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {

		public void onError(MediaRecorder mr, int what, int extra) {
			Log.e("Error: " + what, extra + " ");
		}
	};

	/**
	 * Info listener for audio recording
	 */
	private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {

		public void onInfo(MediaRecorder mr, int what, int extra) {
			Log.e("Warning: " + what, extra + " ");
		}
	};

	/**
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	public void onClick(View view) {

		if (view.getTag().equals("Record")) {

			view.setTag("Stop");
			startRecording();
			this.setImageResource(R.drawable.audio_stop);
			updateText("Recording....");
			updateTextColor(R.color.red);
			((TextView)((AudioUpload)mContext).findViewById(R.id.ar_new)).setVisibility(View.GONE);

		} else if (view.getTag().equals("Stop")) {

			view.setTag("Play");
			stopRecording();
			this.setImageResource(R.drawable.audio_play);
			updateText("Stopped");
			updateTextColor(R.color.gray);
			((TextView)((AudioUpload)mContext).findViewById(R.id.ar_new)).setVisibility(View.VISIBLE);

		} else if (view.getTag().equals("Pause")) {

			view.setTag("Play");
			pausePlayer();
			this.setImageResource(R.drawable.audio_play);
			updateText("Paused");
			updateTextColor(R.color.gray);
			((TextView)((AudioUpload)mContext).findViewById(R.id.ar_new)).setVisibility(View.VISIBLE);

		} else if (view.getTag().equals("Play")) {

			view.setTag("Pause");
			playRecording();
			this.setImageResource(R.drawable.audio_pause);
			updateText("Playing....");
			updateTextColor(R.color.gray);
			((TextView)((AudioUpload)mContext).findViewById(R.id.ar_new)).setVisibility(View.GONE);

		} else if (view.getTag().equals("Delete")) {
			/*
			 * Toast.makeText(mContext, view.getTag().toString(),
			 * Toast.LENGTH_SHORT).show(); deleteRecording();
			 */
		}
	}

	private void pausePlayer() {
		if (mPlayer.isPlaying()) {
			mPlayer.pause();
			currentPosition = mPlayer.getCurrentPosition();
			ON_PAUSE_STATE = true;
		}
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		stopPlaying();
		this.setTag("Play");
		this.setImageResource(R.drawable.audio_play);
		
		updateText("Stopped");
		updateTextColor(R.color.gray);
				
		((TextView)((AudioUpload)mContext).findViewById(R.id.ar_new)).setVisibility(View.VISIBLE);
		
	}
	
	private void updateText(String text) {
		((TextView)((AudioUpload)mContext).findViewById(R.id.record_status_txt)).setText(text); 
	}
	
	private void updateTextColor(int color) {
		((TextView)((AudioUpload)mContext).findViewById(R.id.record_status_txt)).setTextColor(mContext.getResources().getColor(color)); 
	}
	
	public void newRecording() {
		this.setTag("Record");
		onClick(this);
	}

}
