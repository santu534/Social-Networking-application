package com.creative.cutebond;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.creative.cutebond.adapter.UserContactsAdapter;
import com.creative.cutebond.callbacks.IItemHandler;
import com.creative.cutebond.common.Constants;
import com.creative.cutebond.common.Item;
import com.creative.cutebond.tasks.HTTPBackgroundTask;

public class UserContacts extends Activity implements OnClickListener,
		IItemHandler {

	private UserContactsAdapter adapter = null;		
	private AutoCompleteTextView actv = null;
	private String[] values = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.createalbum);

		adapter = new UserContactsAdapter(this);
		
		((TextView)findViewById(R.id.htitle)).setText(R.string.csl);
		((Button)findViewById(R.id.createalbum)).setText(R.string.done);		

		((ListView) findViewById(R.id.cl_list)).setAdapter(adapter);
		((ListView) findViewById(R.id.cl_list)).setOnItemClickListener(onclick);
		actv =  (AutoCompleteTextView)findViewById(R.id.searchtext);		
		actv.setOnItemClickListener(onItemClick);
		
		findViewById(R.id.createalbum).setOnClickListener(this);
		findViewById(R.id.back_arrow).setOnClickListener(this);
		
				
		getData("http://www.cutebond.com/sott_json/videos/allpeople?userid="+getFromStore("userid"));
						
	}
	
	private void setAutoCompleteAdapter(Vector<Item> items) {
		
		values =  null;
		values =  new String[items.size()];
		for(int i = 0 ; i < items.size(); i++){
			
			Item item = items.get(i);
			
			String val = item.getAttribute("userName");
			
			values[i] = val;
		}
		
		actv.setAdapter(null);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, values);        
		actv.setAdapter(adapter);
		
	}

	OnItemClickListener onclick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long arg3) {
			markSelected(position);
		}
	};
	
	private void markSelected(int position) {
		Item item  =  adapter.getItems().get(position);
		if(item.getAttribute("selected").equalsIgnoreCase("yes")) 
			item.setAttribute("selected", "");
		 else 
			 item.setAttribute("selected", "yes");
		
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		
		case R.id.back_arrow:
			UserContacts.this.finish();
			break;
		
		case R.id.createalbum:
			takeSelectedValues();
			break;
		
		case R.id.template_check:

			break;
				
		default:
			break;
		}

	}
	
	OnItemClickListener onItemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View arg1, int position,
				long arg3) {
			String selection = (String) parent.getItemAtPosition(position);
			Log.e("position :: ", selection+" ");
			int selectpos = 0;						
			
			Vector<Item> items  = adapter.getItems();
			for(int i = 0; i < items.size(); i++) {
				selectpos = i;
				Item item = items.get(i);
				String val = item.getAttribute("userName");				
				
				if(val.equalsIgnoreCase(selection)) {
					item.setAttribute("selected", "yes");
					break;
				}								
			}
			adapter.notifyDataSetChanged();
			((ListView) findViewById(R.id.cl_list)).setSelection(selectpos);
			
		}
	};

	private void takeSelectedValues() {
		
		
		Vector<Item> items  = adapter.getItems();
		
		Item selItems = new Item("values");
		
		for(int i = 0; i < items.size(); i++){
			Item item = items.get(i);
			if(item.getAttribute("selected").equalsIgnoreCase("yes")){
				selItems.setAttributeValue(i+"", item);															
			}
		}
		
		serializeData(selItems);
		
		Intent intent = new Intent();		
				
		setResult(RESULT_OK, intent);
		UserContacts.this.finish();
		
	}
	
	private void serializeData(Item item) {
		try {

			FileOutputStream fos = openFileOutput(Constants.PTAG,
					Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(item);
			oos.flush();
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void getData(String url) {
		HTTPBackgroundTask menu_task = new HTTPBackgroundTask(this, this, 14, 1);		
		menu_task.execute(url);
	}

	@Override
	public void onFinish(Object results, int requestType) {
		findViewById(R.id.cl_pbar).setVisibility(View.GONE);

		if (results != null) {
			
			Vector<Item> items = (Vector<Item>) results;;					
			 
			if (items != null && items.size() > 0) {
				adapter.setItems(items);
				adapter.notifyDataSetChanged();
				
				setAutoCompleteAdapter(items);				
			}
			return;
		}

		adapter.clear();
		adapter.notifyDataSetChanged();
		findViewById(R.id.cl_txt).setVisibility(View.VISIBLE);

	}

	@Override
	public void onError(String errorCode, int requestType) {
		findViewById(R.id.cl_pbar).setVisibility(View.GONE);
		showToast(errorCode);
	}		

	public void showToast(String value) {
		Toast.makeText(this, value, Toast.LENGTH_SHORT).show();
	}
	
	public String getFromStore(String key) {
		SharedPreferences pref = this.getSharedPreferences("VF", MODE_PRIVATE);
		String res = pref.getString(key, "");
		return res;
	}

}
