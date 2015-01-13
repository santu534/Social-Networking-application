package com.creative.cutebond;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Enumeration;

import com.creative.cutebond.common.Constants;
import com.creative.cutebond.common.Item;
import com.creative.cutebond.controls.AudioRecordingControl;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class AudioUpload extends Activity {

	private AudioRecordingControl control = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.audioupload);

		init();
	}

	private void init() {

		control = (AudioRecordingControl) findViewById(R.id.recording_state);

		findViewById(R.id.pick_contact).setOnClickListener(onclick);
		findViewById(R.id.share_btn).setOnClickListener(onclick);
		findViewById(R.id.close_btn).setOnClickListener(onclick);
		findViewById(R.id.ar_new).setOnClickListener(onclick);
		findViewById(R.id.a_lang).setOnClickListener(onclick);

		findViewById(R.id.au_ca_but).setOnClickListener(onclick);

		findViewById(R.id.tag_people).setOnClickListener(onclick);

	}

	OnClickListener onclick = new OnClickListener() {

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			
			case R.id.temp_p_tag:
				
				LinearLayout layout = (LinearLayout)findViewById(R.id.a_tag);				
				layout.removeView((View)view.getTag());
				
				if(layout.getChildCount() == 1)
					findViewById(R.id.tag_people).setVisibility(View.VISIBLE);
				
				break;

			case R.id.tag_people:
				launchActivity(UserContacts.class, 4);
				break;

			case R.id.au_ca_but:
				launchActivity(CreateAlbum.class, 3);
				break;

			case R.id.a_lang:
				launchActivity(CntryLang.class, 2);
				break;

			case R.id.ar_new:
				control.deleteRecording();
				control.newRecording();
				break;

			case R.id.share_btn:

				break;

			case R.id.close_btn:
				AudioUpload.this.finish();
				break;

			/*
			 * case R.id.recording_state: break;
			 */

			case R.id.pick_contact:

				break;

			default:
				break;
			}

		}
	};

	private void launchActivity(Class<?> cls, int requestCode) {
		Intent intent = new Intent(AudioUpload.this, cls);
		intent.putExtra("url",
				"http://www.cutebond.com/sott_json/hub/audio_albums?userid="
						+ getFromStore("userid"));
		intent.putExtra("type", requestCode);
		startActivityForResult(intent, requestCode);
	}

	public String getFromStore(String key) {
		SharedPreferences pref = this.getSharedPreferences("VF", MODE_PRIVATE);
		String res = pref.getString(key, "");
		return res;
	}

	@Override
	protected void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);

		if (resultCode != RESULT_OK)
			return;

		switch (reqCode) {

		case 2:
			((TextView) findViewById(R.id.a_lang)).setText(data
					.getStringExtra("language"));
			break;

		case 4:
			prepareTagView();
			break;

		}
	}

	private void prepareTagView() {

		Item item = deserializeData();

		/*
		 * if(item.size() == 0) return;
		 */

		findViewById(R.id.tag_people).setVisibility(View.GONE);
		LinearLayout linear = (LinearLayout) findViewById(R.id.a_tag);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.setMargins(5, 5, 5, 5);
		Enumeration keys = item.keys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement().toString();
			Item value = (Item) item.get(key);
			View view = View.inflate(this, R.layout.template_people_tag, null);
			((TextView) view).setText(value.getAttribute("userName"));

			view.setOnClickListener(onclick);
			linear.addView(view, params);
			view.setTag(view);
		}
	}

	private Item deserializeData() {
		Item item = null;
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = openFileInput(Constants.PTAG);
			ObjectInputStream ois = new ObjectInputStream(fileInputStream);
			item = (Item) ois.readObject();
			ois.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return item;
	}

}
