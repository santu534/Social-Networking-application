package com.creative.cutebond;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;

import com.creative.cutebond.adapter.IntroAdapter;

public class IntroScreen extends Activity {

	private IntroAdapter adapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.introscreen);

		findViewById(R.id.login).setOnClickListener(onclick);
		findViewById(R.id.facebook).setOnClickListener(onclick);
		
		Integer[] slides = { R.drawable.slide1, R.drawable.slide2,
				R.drawable.slide3, R.drawable.slide4, R.drawable.slide5,
				R.drawable.part3 };

		adapter = new IntroAdapter(this, slides);
		((ViewPager) findViewById(R.id.introscreen)).setAdapter(adapter);
		((ViewPager) findViewById(R.id.introscreen)).setOnPageChangeListener(onpage);		

	}

	OnPageChangeListener onpage = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int position) {
			findViewById(R.id.lastinfo).setVisibility(View.GONE);
			if (position == 5)
				findViewById(R.id.lastinfo).setVisibility(View.VISIBLE);
		}

		@Override
		public void onPageScrolled(int pageone, float arg1, int pagetwo) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	};

	private void launchActivity(Class<?> cls) {
		IntroScreen.this.finish();
		Intent mainIntent = new Intent(this, cls);
		startActivity(mainIntent);
	}

	OnClickListener onclick = new OnClickListener() {

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.login:
				launchActivity(CuteBond.class);
				break;

			case R.id.facebook:
				launchActivity(Facebook.class);
				break;

			default:
				break;
			}

		}
	};

}
