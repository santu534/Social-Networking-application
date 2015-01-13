package com.creative.cutebond.controls;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.AsyncTask.Status;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.creative.cutebond.callbacks.IRequestCallback;
import com.creative.cutebond.callbacks.PlayerCallback;
import com.creative.cutebond.common.Constants;
import com.creative.cutebond.tasks.HTTPGetTask;

public class MediaControl extends FrameLayout implements OnInfoListener,
		OnPreparedListener, OnErrorListener, OnCompletionListener,
		OnSeekCompleteListener, 
		SurfaceHolder.Callback, 
		OnBufferingUpdateListener, IRequestCallback {

	private Context mContext = null;

	private int progress = 0;

	public final static String LOGTAG = "CUSTOM_VIDEO_PLAYER";

	private MediaPlayer mediaPlayer = null;

	private ProgressBar progressBar = null;

	private boolean ON_PAUSE_STATE = false;
	
	private boolean FLAG = true;

	private HTTPGetTask getTask = null;

	private int fileState = 0;
	
	private PlayerCallback callback = null;

	private File file = null;

	public MediaControl(Context context) {
		super(context);
		mContext = context;
		init();
	}

	public MediaControl(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	public MediaControl(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		init();
	}

	private void init() {		
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnCompletionListener(this);
		mediaPlayer.setOnErrorListener(this);
		mediaPlayer.setOnInfoListener(this);
		mediaPlayer.setOnPreparedListener(this);
		mediaPlayer.setOnSeekCompleteListener(this);
		mediaPlayer.setOnBufferingUpdateListener(this);
	}

	public void init(PlayerCallback callback, ProgressBar progressBar) {
		this.callback = callback;		
		this.progressBar = progressBar;
	}

	public void playFile(String path, String fileName) {
		fileState = 1;
		progressBar.setVisibility(View.VISIBLE);
		
		file = new File(Constants.DWLPATH + "/" + fileName);
		if(file.exists()) {//TODO::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			fileState = 0;
			setPlayerData(file.toString());
			return;
		}
		
		fileDownload(path, fileName);		
	}

	public void onPrepared(MediaPlayer mp) {
		mediaPlayer.start();
	}

	public void deInit() {

		if (getTask != null && getTask.getStatus() == Status.RUNNING) {
			getTask.cancel(true);
			getTask = null;
		}

		if (mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
			// mediaPlayer.release();
		}
	}

	public boolean handleCommand(int commandId) {

		switch (commandId) {
		case Constants.ON_PLAY:

			if (ON_PAUSE_STATE) {
				ON_PAUSE_STATE = false;
				mediaPlayer.start();
				mediaPlayer.seekTo(mediaPlayer.getCurrentPosition());
			}
			
			break;

		case Constants.ON_PAUSE:
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.pause();
				ON_PAUSE_STATE = true;
			}
			break;

		default:
			break;
		}

		return false;
	}

	public boolean onError(MediaPlayer mp, int whatError, int extra) {
		FLAG = false;
		Log.e(LOGTAG, "ON ERROR CALLED");
		progressBar.setVisibility(View.GONE);
		
		if (whatError == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
			Log.e(LOGTAG, "MEDIA_ERROR_SERVER_DIED");
		} else if (whatError == MediaPlayer.MEDIA_ERROR_UNKNOWN) {
			Log.e(LOGTAG, "MEDIA_ERROR_UNKNOWN");
		}
		return false;
	}

	public void onCompletion(MediaPlayer arg0) {

		Log.e(LOGTAG, "ON MediaPlayer COMPLETE CALLED");

		if (FLAG) {
			mediaPlayer.stop();
			mediaPlayer.seekTo(0);			
			fileState = 3;
		}
		FLAG = true;

		callback.onMediaFinish();
	}

	public void onSeekComplete(MediaPlayer mp) {
		//Log.e("position on Seek complete : ", Position + "");
	}



	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.e(LOGTAG, "SURFACE CHANGED CALLED");
	}

	public void surfaceCreated(SurfaceHolder holder) {
		Log.e(LOGTAG, "SURFACECREATED CALLED");
		mediaPlayer.setDisplay(holder);
		
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.e(LOGTAG, "SURFACE DESTROY CALLED");
		mediaPlayer.stop();
	}

	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		return false;
	}

	

	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		secondarySeekBarProgressUpdater(percent);
	}

	private void secondarySeekBarProgressUpdater(int percent) {
		//seekBar.setSecondaryProgress(percent);
	}

	private void setPlayerData(String filePath) {
		try {
			mediaPlayer.seekTo(0);
			mediaPlayer.reset();
			mediaPlayer.setDataSource(filePath);
			mediaPlayer.prepare();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		mediaPlayer.start();
	}

	private void fileDownload(String url, String name) {
		getTask = new HTTPGetTask(mContext, this, true, name);
		getTask.execute(url);
		progressBar.setVisibility(View.VISIBLE);
	}

	@Override
	public void onRequestComplete(InputStream inputStream, String mimeType) {
		progressBar.setVisibility(View.GONE);
		fileState = 0;
	}

	@Override
	public void onRequestComplete(String data, String mimeType) {
		fileState = 0;
		
		progressBar.setVisibility(View.GONE);
		
		String[] temp = data.split("###");
		
		file = new File(Constants.DWLPATH + "/" + temp[1]);

		setPlayerData(file.toString());
		
		callback.onMediaStart();
	}

	@Override
	public void onRequestFailed(String errorData) {
		progressBar.setVisibility(View.GONE);
		Toast.makeText(mContext, errorData, Toast.LENGTH_SHORT).show();
		fileState = 2;
	}

	@Override
	public void onRequestCancelled(String extraInfo) {
		progressBar.setVisibility(View.GONE);
		fileState = 0;
	}

	@Override
	public void onRequestProgress(int bytesReceived, int totalBytes) {
		// TODO Auto-generated method stub

	}
	
	public int getFileState() {
		return fileState;
	}

	public int isPlaying() {
		if (mediaPlayer.isPlaying())
			return 0;
		else
			return -1;
	}

	public void playSameFile() {
		if (file != null)
			setPlayerData(file.toString());
	}

}
