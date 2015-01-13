package com.creative.cutebond;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Enumeration;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.creative.cutebond.common.Constants;
import com.creative.cutebond.common.Item;

public class PhotoUpload extends Activity {

	private Dialog dialog = null;
	private Uri imageUri;
	private final int JPEG = 0;
	private final int PNG = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photoupload);
		showOptionsDialog();

		findViewById(R.id.close_btn).setOnClickListener(onclick);
		findViewById(R.id.pu_ca_but).setOnClickListener(onclick);
		findViewById(R.id.tag_people).setOnClickListener(onclick);

	}

	private void showOptionsDialog() {

		dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.TRANSPARENT));
		dialog.setCancelable(false);

		View view = View.inflate(this, R.layout.pu_popup, null);

		view.findViewById(R.id.pu_cr).setOnClickListener(onclick);
		view.findViewById(R.id.pu_pl).setOnClickListener(onclick);
		view.findViewById(R.id.pu_url).setOnClickListener(onclick);
		view.findViewById(R.id.pu_cancel).setOnClickListener(onclick);

		dialog.setContentView(view);
		dialog.show();

	}

	OnClickListener onclick = new OnClickListener() {

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			
			case R.id.temp_p_tag:
				
				LinearLayout layout = (LinearLayout)findViewById(R.id.p_tag);				
				layout.removeView((View)view.getTag());
				
				if(layout.getChildCount() == 1)
					findViewById(R.id.tag_people).setVisibility(View.VISIBLE);
				
				break;

			case R.id.tag_people:
				launchActivity(UserContacts.class, 4);
				break;

			case R.id.pu_ca_but:
				launchActivity(CreateAlbum.class, 3);
				break;

			case R.id.close_btn:
				PhotoUpload.this.finish();
				break;

			case R.id.pu_cr:
				takePicture(0);
				break;

			case R.id.pu_pl:
				openGallery();
				break;

			case R.id.pu_url:
				closeDialog();
				break;

			case R.id.pu_cancel:
				closeDialog();
				PhotoUpload.this.finish();
				break;

			default:
				break;
			}

		}
	};

	private void openGallery() {
		Intent imageIntent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		imageIntent.setType("image/*");
		startActivityForResult(imageIntent, 1000);
	}

	private void takePicture(int encodingType) {

		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");

		File photo = createCaptureFile(encodingType);
		intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(photo));
		this.imageUri = Uri.fromFile(photo);

		startActivityForResult(intent, 2000);

	}

	private File createCaptureFile(int encodingType) {
		File photo = null;
		if (encodingType == JPEG) {
			photo = new File(getTempDirectoryPath(), System.currentTimeMillis()
					+ ".jpg");
		} else if (encodingType == PNG) {
			photo = new File(getTempDirectoryPath(), System.currentTimeMillis()
					+ ".png");
		} else {
			throw new IllegalArgumentException("Invalid Encoding Type: "
					+ encodingType);
		}
		return photo;
	}

	private String getTempDirectoryPath() {
		File cache = null;

		// SD Card Mounted
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {

			cache = new File(Constants.CACHEPHOTO + File.separator);

		} else { // Use internal storage
			cache = getCacheDir();
		}

		// Create the cache directory if it doesn't exist
		cache.mkdirs();
		return cache.getAbsolutePath();
	}

	private void closeDialog() {
		if (dialog != null)
			dialog.dismiss();
		dialog = null;
	}

	@Override
	protected void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);

		if (resultCode == RESULT_OK)
			switch (reqCode) {

			case 4:
				prepareTagView();
				break;

			case 1000:
				closeDialog();
				Log.e("data.getData()", data.getData() + "");
				String path = getContentFilePath(data.getData() + "");
				Log.e("path ::::::", path + "");
				imageUri = Uri.parse("file://" + path);

				Log.e("imageUri ::::::", imageUri + "");

				loadFromPhone(imageUri);
				break;

			case 2000:
				closeDialog();
				loadFromPhone(imageUri);
				break;

			}
	}

	private void loadFromPhone(Uri contentURI) {

		InputStream inputStream = null;

		try {

			//ContentResolver cr = getContentResolver();
			//inputStream = cr.openInputStream(contentURI);
			//BitmapFactory.Options options = new BitmapFactory.Options();
			//options.inSampleSize = 8;
			//Bitmap thumb = BitmapFactory.decodeStream(inputStream, null, null);
			//((ImageView) findViewById(R.id.photo_state)).setImageBitmap(thumb);
			((ImageView) findViewById(R.id.photo_state)).setImageURI(contentURI);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null)
					inputStream.close();
				inputStream = null;
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	@Override
	protected void onDestroy() {
		((ImageView) findViewById(R.id.photo_state)).setImageBitmap(null);
		super.onDestroy();
	}

	private String getContentFilePath(String postdata) {

		Cursor cursor = getContentResolver().query(Uri.parse(postdata), null,
				null, null, null);
		String value = "";
		if (cursor != null) {
			while (cursor.moveToNext()) {
				String[] resultsColumns = cursor.getColumnNames();
				for (int i = 0; i < resultsColumns.length; i++) {
					String key = resultsColumns[i];
					if (key.startsWith("_")) {
						key = key.substring(1, key.length());
					}
					value = cursor.getString(cursor
							.getColumnIndexOrThrow(resultsColumns[i]));
					if (value != null && key.equalsIgnoreCase("data")) {
						break;
					}
					/*
					 * if (value != null) { Log.e("key : " + key, "value  : " +
					 * value); if (key.equalsIgnoreCase("data")) postdata =
					 * value; }
					 */
				}
			}
			cursor.close();
		}
		return value;
	}

	private void launchActivity(Class<?> cls, int requestCode) {
		Intent intent = new Intent(PhotoUpload.this, cls);
		intent.putExtra("url",
				"http://www.cutebond.com/sott_json/photos/albums?userid="
						+ getFromStore("userid"));
		intent.putExtra("type", requestCode);
		startActivityForResult(intent, requestCode);
	}

	public String getFromStore(String key) {
		SharedPreferences pref = this.getSharedPreferences("VF", MODE_PRIVATE);
		String res = pref.getString(key, "");
		return res;
	}

	private void prepareTagView() {

		Item item = deserializeData();

		findViewById(R.id.tag_people).setVisibility(View.GONE);
		LinearLayout linear = (LinearLayout) findViewById(R.id.p_tag);

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
