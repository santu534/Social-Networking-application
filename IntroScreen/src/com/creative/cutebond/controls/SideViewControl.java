package com.creative.cutebond.controls;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.HorizontalScrollView;

public class SideViewControl extends HorizontalScrollView {

	public SideViewControl(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public SideViewControl(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public SideViewControl(Context context) {
		super(context);
		init(context);
	}

	void init(Context context) {
		setHorizontalFadingEdgeEnabled(false);
		setVerticalFadingEdgeEnabled(false);
	}

	public void initViews(View[] children, int scrollToViewIdx,
			SizeCallback sizeCallback) {

		ViewGroup parent = (ViewGroup) getChildAt(0);

		for (int i = 0; i < children.length; i++) {
			children[i].setVisibility(View.INVISIBLE);
			parent.addView(children[i]);
		}

		OnGlobalLayoutListener listener = new MyOnGlobalLayoutListener(parent,
				children, scrollToViewIdx, sizeCallback);
		getViewTreeObserver().addOnGlobalLayoutListener(listener);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		return false;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return false;
	}

	class MyOnGlobalLayoutListener implements OnGlobalLayoutListener {
		ViewGroup parent;
		View[] children;
		int scrollToViewIdx;
		int scrollToViewPos = 0;
		SizeCallback sizeCallback;

		public MyOnGlobalLayoutListener(ViewGroup parent, View[] children,
				int scrollToViewIdx, SizeCallback sizeCallback) {
			this.parent = parent;
			this.children = children;
			this.scrollToViewIdx = scrollToViewIdx;
			this.sizeCallback = sizeCallback;
		}

		@Override
		public void onGlobalLayout() {

			final HorizontalScrollView me = SideViewControl.this;

			me.getViewTreeObserver().removeGlobalOnLayoutListener(this);

			//sizeCallback.onGlobalLayout();

			parent.removeViewsInLayout(0, children.length);

			final int w = me.getMeasuredWidth();
			final int h = me.getMeasuredHeight();
			
			sizeCallback.onGlobalLayout();

			int[] dims = new int[2];
			scrollToViewPos = 0;
			for (int i = 0; i < children.length; i++) {
				sizeCallback.getViewSize(i, w, h, dims);
				children[i].setVisibility(View.VISIBLE);
				parent.addView(children[i], dims[0], dims[1]);
				if (i < scrollToViewIdx) {
					scrollToViewPos += dims[0];
				}
			}

			new Handler().post(new Runnable() {
				@Override
				public void run() {
					me.scrollBy(scrollToViewPos, 0);
				}
			});
		}
	}

	public interface SizeCallback {

		public void onGlobalLayout();

		public void getViewSize(int idx, int w, int h, int[] dims);
	}
}
