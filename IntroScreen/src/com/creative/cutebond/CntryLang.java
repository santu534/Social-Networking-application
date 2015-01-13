package com.creative.cutebond;

import java.util.Vector;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.Toast;

import com.creative.cutebond.adapter.CntryLangAdapter;
import com.creative.cutebond.callbacks.IItemHandler;
import com.creative.cutebond.common.Constants;
import com.creative.cutebond.common.Item;
import com.creative.cutebond.tasks.HTTPBackgroundTask;

public class CntryLang extends Activity implements OnClickListener,
		IItemHandler {

	private CntryLangAdapter adapter = null;	
	private Item data = null;
	private AutoCompleteTextView actv = null;
	private String[] values = null;
	
	private int type = -1;

	// language //country
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cntrylang);

		adapter = new CntryLangAdapter(this);		

		((ListView) findViewById(R.id.cl_list)).setAdapter(adapter);
		((ListView) findViewById(R.id.cl_list)).setOnItemClickListener(onclick);
		actv =  (AutoCompleteTextView)findViewById(R.id.searchtext);		
		actv.setOnItemClickListener(onItemClick);
		findViewById(R.id.sc_tabs).setOnClickListener(this);
		findViewById(R.id.sl_tabs).setOnClickListener(this);
		findViewById(R.id.cl_done).setOnClickListener(this);
		
		type = getIntent().getIntExtra("type", -1);
		if(type == 1) {
			findViewById(R.id.sl_tabs).setVisibility(View.GONE);
			findViewById(R.id.po_v_line).setVisibility(View.GONE);
			
		}
		else if(type == 2) {
			findViewById(R.id.sc_tabs).setVisibility(View.GONE);
			findViewById(R.id.po_v_line).setVisibility(View.GONE);
		}
		getData();
						
	}
	
	private void setAutoCompleteAdapter(Vector<Item> items) {
		
		values =  null;
		values =  new String[items.size()];
		for(int i = 0 ; i < items.size(); i++){
			
			Item item = items.get(i);
			
			String val = item.getAttribute("countryName");
			if(val.length() == 0)
				val = item.getAttribute("language");
			
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
		
		case R.id.cl_done:
			takeSelectedValues();
			break;
		
		case R.id.template_check:

			break;

		case R.id.sc_tabs:
			actv.setText("");
			updatedCustmTab(R.id.sc_tabs, R.id.sl_tabs);
			setData("country");
			break;

		case R.id.sl_tabs:
			actv.setText("");
			updatedCustmTab(R.id.sl_tabs, R.id.sc_tabs);
			setData("language");			
			break;
			
		default:
			break;
		}

	}
	
	OnItemClickListener onItemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View arg1, int position,
				long arg3) {
			// TODO Auto-generated method stub
			String selection = (String) parent.getItemAtPosition(position);
			Log.e("position :: ", selection+" ");
			int selectpos = 0;						
			
			Vector<Item> items  = adapter.getItems();
			for(int i = 0; i < items.size(); i++) {
				selectpos = i;
				Item item = items.get(i);
				String val = item.getAttribute("countryName");
				if(val.length() == 0)
					val = item.getAttribute("language");
				
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
		
		Intent intent = new Intent();
				
		Vector<Item> items = (Vector<Item>) data.getAttribValue("country");
		for(int i = 0; i < items.size(); i++){
			Item item = items.get(i);
			if(item.getAttribute("selected").equalsIgnoreCase("yes")){
				intent.putExtra("cntry", item.getAttribute("countryId"));
				intent.putExtra("countryName", item.getAttribute("countryName"));
				
				break;
			}
		}
		items = (Vector<Item>) data.getAttribValue("language");
		for(int i = 0; i < items.size(); i++){
			Item item = items.get(i);
			if(item.getAttribute("selected").equalsIgnoreCase("yes")) {
				intent.putExtra("lang", item.getAttribute("languageId"));
				intent.putExtra("language", item.getAttribute("language"));
				break;
			}
		}
		setResult(RESULT_OK, intent);
		CntryLang.this.finish();
		
	}
	
	private void getData() {
		HTTPBackgroundTask menu_task = new HTTPBackgroundTask(this, this, 1, 1);
		menu_task.setRawAttri(true, Constants.CNTRYLANG, R.raw.countryandlang);
		menu_task.execute("");
	}
	
	private void setData(String key) {
		//adapter.clear();
		
		Vector<Item> items = (Vector<Item>) data.getAttribValue(key);
		
		adapter.setItems(items);
		adapter.notifyDataSetChanged();
				
		setAutoCompleteAdapter(items);
	}

	@Override
	public void onFinish(Object results, int requestType) {
		findViewById(R.id.cl_pbar).setVisibility(View.GONE);

		data = (Item) results;

		if (results != null) {
			
			updatedCustmTab(R.id.sc_tabs, R.id.sl_tabs);
			
			//Item item = (Item) results;

			Vector<Item> items = null;
			
			if(type== -1 || type == 1) {
				items = (Vector<Item>) data.getAttribValue("country");	
			} else  if(type == 2) {
				items = (Vector<Item>) data.getAttribValue("language");
			}
			
			 
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

	private void updatedCustmTab(int id, int release) {
		Button button = (Button) findViewById(id);
		button.setBackgroundColor(getResources().getColor(R.color.pinkish));
		button.setTextColor(getResources().getColor(R.color.white));
		
		button = (Button) findViewById(release);
		button.setBackgroundColor(getResources().getColor(R.color.white));
		button.setTextColor(getResources().getColor(R.color.pinkish));
	}

}
