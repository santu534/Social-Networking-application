package com.creative.cutebond;

import java.io.FileInputStream;
import java.io.ObjectInputStream;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.creative.cutebond.callbacks.IItemHandler;
import com.creative.cutebond.common.Constants;
import com.creative.cutebond.common.Item;
import com.creative.cutebond.imageloader.ImageLoader;
import com.creative.cutebond.tasks.HTTPBackgroundTask;
import com.creative.cutebond.utils.Utils;

public class AccountActivity extends Activity implements IItemHandler{

	private Item item = null;
	public ImageLoader imageLoader = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		item = deserializeData();
		
		imageLoader = new ImageLoader(this, false);
		
		Intent intent = getIntent();
		if(intent.getIntExtra("type", -1) == 0) {
			setContentView(R.layout.people_offline_detail);
			initOfflineView();
		} else {
			setContentView(R.layout.account_screen);	
			
			findViewById(R.id.ac_backBtn).setOnClickListener(onclick);
			
			getAccountDetails();
		}				
				
	}
	
	private void getAccountDetails() {
		
		//Utils.showProgress(getString(R.string.pw), this);		
		String url = "http://www.cutebond.com/sott_json/users/user_details?userid=(UID)&friend_id=(FID)";//cutebond.getPropertyValue("account_detail");
		url = url.replace("(UID)", getFromStore("userid"));		
		url = url.replace("(FID)", item.getAttribute("userId"));
		//url = url.replace("(FID)", "690");
		
		Log.e("url : ", url+"");
		HTTPBackgroundTask task = new HTTPBackgroundTask(this, this, 12, 1);
		task.execute(url);	
	}
	
	private String getFromStore(String key) {
		SharedPreferences pref = this.getSharedPreferences("VF", MODE_PRIVATE);
		String res = pref.getString(key, "");
		return res;
	}
	
	private void initOfflineView() {
		findViewById(R.id.back_arrow).setOnClickListener(onclick);
		findViewById(R.id.p_p_icon).setOnClickListener(onclick);
		findViewById(R.id.pd_mi).setOnClickListener(onclick);
		findViewById(R.id.pd_challenge).setOnClickListener(onclick);
		findViewById(R.id.pd_mfr).setOnClickListener(onclick);
		
		findViewById(R.id.people_name).setOnClickListener(onclick);
		
		
		String imageUri = item.getAttribute("userProfilepic");
		
		imageUri = "http://www.cutebond.com/webapp/userimages/"+imageUri;
		
		ImageView icon = (ImageView) findViewById(R.id.prv_image);
		getSize(icon);
						
		imageLoader.DisplayImage(imageUri, icon);
		
		((TextView)findViewById(R.id.people_name)).setText(item.getAttribute("userName"));
		
		if(item.getAttribute("userGender").equalsIgnoreCase("male")) 
			((ImageView)findViewById(R.id.pd_mi)).setImageResource(R.drawable.male_pd);
					
	}
	
	private void getSize(final ImageView icon) {
		ViewTreeObserver vt = icon.getViewTreeObserver();
		vt.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				if (icon.getTag() == null) {
					icon.setTag(1);					
					icon.getLayoutParams().height = icon.getWidth(); 					
				}
			}
		});
	}
	
	private void initAccountsView() {
		
	}
	
	OnClickListener onclick = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.back_arrow:
				AccountActivity.this.finish();
				break;
				
			case R.id.ac_backBtn:
				AccountActivity.this.finish();
				break;
				
			case R.id.people_name:
				
				AccountActivity.this.finish();
				launchActivity(AccountActivity.class, 1000);
				
				break;

			default:
				break;
			}
			
		}
	};
	
	private void launchActivity(Class<?> cls, int requestCode) {
		Intent intent = new Intent(AccountActivity.this, cls);		
		startActivityForResult(intent, requestCode);
	}
	
	/**
	 * deserializeData -
	 * 
	 * @return Item
	 */
	private Item deserializeData() {
		Item item = null;
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = openFileInput(Constants.ITEM);
			ObjectInputStream ois = new ObjectInputStream(fileInputStream);
			item = (Item) ois.readObject();
			ois.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return item;
	}
	
	@Override
	protected void onDestroy() {

		if(findViewById(R.id.prv_image) != null) {
			((ImageView) findViewById(R.id.prv_image)).setImageBitmap(null);
			((ImageView) findViewById(R.id.prv_image)).setImageDrawable(null);	
		}				
		
		super.onDestroy();
		
	}

	@Override
	public void onFinish(Object results, int requestType) {
		Utils.dismissProgress();
		
		if(results == null)
			return;
		
		fillView((Item)results);
	}

	@Override
	public void onError(String errorCode, int requestType) {
		Utils.dismissProgress();
		
	}
	
	private void fillView(Item item) {
		Item data = (Item)item.getAttribValue("user_details");
		//userProfilepic
		//usercoverpic
		//userGender
		((TextView)findViewById(R.id.ac_profile_name)).setText(data.getAttribute("userName"));
		
		if(!data.getAttribute("description").equalsIgnoreCase("null"))
			((TextView)findViewById(R.id.ac_wish_desc)).setText(data.getAttribute("description"));				
		
		String val = data.getAttribute("description");
		if(!val.equalsIgnoreCase("null"))
			((TextView)findViewById(R.id.ac_wish_desc)).setText(data.getAttribute("description"));
		
		String imageUri = data.getAttribute("userProfilepic");		
		imageUri = "http://www.cutebond.com/webapp/userimages/"+imageUri;		
		ImageView icon = (ImageView) findViewById(R.id.ac_profile_pic);							
		imageLoader.DisplayImage(imageUri, icon, true);
		

		//String	coverImageUri = "http://www.cutebond.com/sott_json/users/save_coverpic?userid="+data.getAttribute("userId")+"&status=0";
		String coverImageUri = "http://www.cutebond.com/webapp/userimages/"+data.getAttribute("usercoverpic");
		//coverImageUri = "http://www.cutebond.com/webapp/userimages/"+data.getAttribute("usercoverpic");
			ImageView covericon = (ImageView) findViewById(R.id.ac_cover_photo);							
			imageLoader.DisplayImage(coverImageUri, covericon);	
		
	}
	
}
