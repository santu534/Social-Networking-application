package com.creative.cutebond.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.creative.cutebond.R;

public class IntroAdapter extends PagerAdapter {

	private LayoutInflater inflater = null;
	private Context context = null;
	private Integer[] slides = null;

	public IntroAdapter(Context context, Integer[] slides) {
		this.context = context;
		this.slides = slides;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getItemPosition(Object object) {
		return slides.length;
	}

	public int getCount() {
		return slides.length;
	}

	public Object instantiateItem(View collection, int position) {

		View view = inflater.inflate(R.layout.template_pager, null);

		final ImageView icon = (ImageView) view
				.findViewById(R.id.template_icon);
		icon.setTag(position);

		icon.setBackgroundResource(slides[position]);		

		((ViewPager) collection).addView(view, 0);

		return view;
	}

	@Override
	public void destroyItem(View view, int position, Object arg2) {
		((ViewPager) view).removeView((View) arg2);
	}

	@Override
	public void finishUpdate(View arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == ((View) arg1);
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public Parcelable saveState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void startUpdate(View arg0) {
		// TODO Auto-generated method stub

	}

}
