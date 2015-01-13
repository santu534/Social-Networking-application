package com.creative.cutebond;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.creative.cutebond.imageloader.ImageLoader;

public class ImageActivity extends Activity {

	private ImageView image = null;	
	public ImageLoader imageLoader = null;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.imageactivity);

		imageLoader = new ImageLoader(this, false);
		image = (ImageView) findViewById(R.id.imageactivity);

		Intent intent = this.getIntent();
		if (intent != null) {
			String url = intent.getStringExtra("imageurl");
			
			loadFromServer(url);			
		}
		
		findViewById(R.id.ia_btn).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ImageActivity.this.finish();
			}
		});
		
	}

	private void loadFromServer(String url) {
		imageLoader.DisplayImage(url, image, true);
	}

	@Override
	protected void onDestroy() {
		image.setImageBitmap(null);
		
		imageLoader.clearCache();
		
		imageLoader = null;

		super.onDestroy();
	}	

}
