package com.creative.cutebond.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.creative.cutebond.R;

public class ProgressControl extends LinearLayout {

	private Context mContext = null;
	private TextView textView = null;
	private ProgressBar pbar = null;

	/**
	 * ApplicationDownloader - Constructor which accept Context as its parameter
	 * 
	 * @param Context
	 */
	public ProgressControl(Context context) {
		super(context);
		mContext = context;
		init();
	}

	/**
	 * ApplicationDownloader - Constructor which accept Context and AttirbuteSet
	 * as its parameters
	 * 
	 * @param Context
	 */
	public ProgressControl(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	/**
	 * init - initialize the view.
	 * 
	 * @param String
	 * @param String
	 */
	public void init() {

		this.setOrientation(LinearLayout.VERTICAL);

		textView = new TextView(mContext);
		textView.setText(R.string.downloading);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		params.setMargins(2, 0, 0, 0);
		textView.setLayoutParams(params);
		this.addView(textView);

		pbar = new ProgressBar(mContext, null,
				android.R.attr.progressBarStyleHorizontal);
		params = new LayoutParams(LayoutParams.MATCH_PARENT, 10);
		params.setMargins(2, 0, 2, 0);
		pbar.setLayoutParams(params);
		this.addView(pbar);
		pbar.getProgressDrawable().setColorFilter(
				getResources().getColor(R.color.bluecolor),
				android.graphics.PorterDuff.Mode.MULTIPLY);

	}

	public void setProgressText(int bytesReceived, int totalBytes) {
		textView.setText("Downloading... "
				+ (bytesReceived + " Kb read / " + totalBytes + " Kb"));
	}

	public void setMaxProgress(int max) {
		pbar.setMax(max);
	}

	public void setCurrentProgress(int current) {
		pbar.setProgress(current);
	}

	public void clear() {
		textView.setText(R.string.downloading);
		pbar.setMax(0);
		pbar.setProgress(0);

	}

}
