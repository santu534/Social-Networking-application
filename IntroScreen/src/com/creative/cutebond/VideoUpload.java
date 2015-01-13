package com.creative.cutebond;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.creative.cutebond.adapter.ExpandAdapter;
import com.creative.cutebond.callbacks.IItemHandler;
import com.creative.cutebond.common.Constants;
import com.creative.cutebond.common.Item;
import com.creative.cutebond.tasks.HTTPBackgroundTask;

public class VideoUpload extends Activity implements IItemHandler {

	private ExpandAdapter adapter;
	private FrameLayout vu_parent = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.videoupload);

		findViewById(R.id.close_btn).setOnClickListener(onclick);				
		
		vu_parent = (FrameLayout) findViewById(R.id.vu_parent);

		adapter = new ExpandAdapter(this);
		((ExpandableListView) findViewById(R.id.vu_expand)).setAdapter(adapter);

		((ExpandableListView) findViewById(R.id.vu_expand))
				.setOnChildClickListener(onChildClick);

		getVideoCategories();
	}

	private void getVideoCategories() {
		HTTPBackgroundTask menu_task = new HTTPBackgroundTask(this, this, 3, 3);
		menu_task.setRawAttri(true, Constants.V_L_MENU, R.raw.v_l_menu);
		menu_task.execute("");
	}

	@Override
	public void onFinish(Object results, int requestType) {

		if (results != null) {
			Item item = (Item) results;
			List<String> group = new ArrayList<String>();

			@SuppressWarnings("unchecked")
			Enumeration<String> keys = item.keys();
			while (keys.hasMoreElements()) {
				String key = keys.nextElement().toString();
				group.add(key);
			}

			adapter.setGroupItem(group);
			adapter.setChildItem(item);
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onError(String errorCode, int requestType) {
		showToast(errorCode);		
	}
	
	public void showToast(String value) {
		Toast.makeText(this, value, Toast.LENGTH_SHORT).show();
	}

	OnChildClickListener onChildClick = new OnChildClickListener() {

		@Override
		public boolean onChildClick(ExpandableListView parent, View view,
				int groupPosition, int childPosition, long id) {
			
			//Item item = (Item)view.getTag();
			takeMoreDetails((Item)view.getTag());
			
			return false;
		}
	};
	
	OnClickListener onclick = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			
			case R.id.temp_p_tag:
				
				LinearLayout layout = (LinearLayout)findViewById(R.id.v_tag);				
				layout.removeView((View)view.getTag());
				
				if(layout.getChildCount() == 1)
					findViewById(R.id.tag_people).setVisibility(View.VISIBLE);
				
				break;
			
			case R.id.tag_people:
				launchActivity(UserContacts.class, 4);
				break;
			
			case R.id.vu_ca_but:
				launchActivity(CreateAlbum.class, 3);
				break;
			
			case R.id.close_btn:
				VideoUpload.this.finish();
				break;
				
			case R.id.vu_done:
				validateNUpload();
				break;
				
				
			case R.id.v_lang: 				
				launchActivity(CntryLang.class, 2);
				break;
				
			case R.id.v_cntry:
				launchActivity(CntryLang.class, 1);
				break;				

			default:
				break;
			}
			
		}
	};
	
	private void validateNUpload() {
		
		String value = ((EditText)findViewById(R.id.v_url)).getText().toString();
		value = value.trim();
		
		if(value.length() == 0) {
			showToast("Please enter Video URL");
			return;
		}
		
		if(!value.startsWith("http://")) {
			showToast("Please enter a valid Video URL");
			return;
		}
		
		value = ((EditText)findViewById(R.id.v_title)).getText().toString();
		if(value.length() == 0) {
			showToast("Please enter video title");
			return;
		}
		
		value = ((EditText)findViewById(R.id.v_desc)).getText().toString();
		if(value.length() == 0) {
			showToast("Please enter video description");
			return;
		}
				
		value = ((TextView)findViewById(R.id.v_lang)).getText().toString();
		if(value.length() == 0) {
			showToast("Please select a Language");
			return;
		}
		
		value = ((TextView)findViewById(R.id.v_cntry)).getText().toString();
		if(value.length() == 0) {
			showToast("Please select a Country");
			return;
		}
		
		
	}
	
	private void launchActivity(Class<?> cls, int requestCode) {
		Intent intent = new Intent(VideoUpload.this, cls);
		intent.putExtra("url", "http://www.cutebond.com/sott_json/videos/albums?userid="+getFromStore("userid"));
		intent.putExtra("type", requestCode);
		startActivityForResult(intent, requestCode);
	}
	
	public String getFromStore(String key) {
		SharedPreferences pref = this.getSharedPreferences("VF", MODE_PRIVATE);
		String res = pref.getString(key, "");
		return res;
	}
	
	private void takeMoreDetails(Item item) {
		
		View view = View.inflate(this, R.layout.videoupload_detail, null);
		
		view.findViewById(R.id.v_lang).setOnClickListener(onclick);
		view.findViewById(R.id.v_cntry).setOnClickListener(onclick);
		view.findViewById(R.id.vu_ca_but).setOnClickListener(onclick);
		view.findViewById(R.id.tag_people).setOnClickListener(onclick);		
		
		vu_parent.getChildAt(vu_parent.getChildCount()-1).setVisibility(View.GONE);
		vu_parent.addView(view);
		
		
				
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == 4){
			if(vu_parent.getChildCount() > 1){
				vu_parent.removeViewAt(vu_parent.getChildCount() -1);
				vu_parent.getChildAt(vu_parent.getChildCount()-1).setVisibility(View.VISIBLE);
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);

		if(resultCode != RESULT_OK)
			return;
			
		switch (reqCode) {

		case 1:			
			((TextView)findViewById(R.id.v_cntry)).setText(data.getStringExtra("countryName")); 			
			break;
			
		case 2:
			((TextView)findViewById(R.id.v_lang)).setText(data.getStringExtra("language"));			
			break;
			
		case 3:
			break;
			
		case 4:						
			prepareTagView();
			break;
		}
	}
	
	private void prepareTagView() {
		
		Item item = deserializeData();
		
		/*if(item.size() == 0)
			return;*/
		
		findViewById(R.id.tag_people).setVisibility(View.GONE);
		LinearLayout linear = (LinearLayout)findViewById(R.id.v_tag);
		
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.setMargins(5, 5, 5, 5);		
			Enumeration keys = item.keys();
			while (keys.hasMoreElements()) {
				String key = keys.nextElement().toString();
				Item value  = (Item)item.get(key);							
				View view = View.inflate(this, R.layout.template_people_tag, null);
				((TextView)view).setText(value.getAttribute("userName"));
				
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