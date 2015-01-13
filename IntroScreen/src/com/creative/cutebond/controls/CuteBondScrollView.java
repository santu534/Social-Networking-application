package com.creative.cutebond.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class CuteBondScrollView extends ScrollView {
	private ScrollViewListener scrollViewListener = null;

	public CuteBondScrollView(Context context) {
		super(context);
	}

	public CuteBondScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CuteBondScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setScrollViewListener(ScrollViewListener scrollViewListener) {
		this.scrollViewListener = scrollViewListener;
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (scrollViewListener != null) {
			scrollViewListener.onScrollChanged(this, l, t, oldl, oldt);
		}
	}

	public interface ScrollViewListener {
		void onScrollChanged(CuteBondScrollView scrollView, int x, int y,
				int oldx, int oldy);
	}
}