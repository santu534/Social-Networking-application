package com.creative.cutebond;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class MicroblogUpload extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.microblogupload);

		init();
	}

	private void init() {								
		findViewById(R.id.close_btn).setOnClickListener(onclick);			
	}

	OnClickListener onclick = new OnClickListener() {

		@Override
		public void onClick(View view) {
			switch (view.getId()) {

			
			case R.id.close_btn:
				MicroblogUpload.this.finish();
				break;
			
			default:
				break;
			}

		}
	};	

}
