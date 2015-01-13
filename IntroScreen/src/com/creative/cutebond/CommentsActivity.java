package com.creative.cutebond;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Vector;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.creative.cutebond.adapter.CommentsAdapter;
import com.creative.cutebond.callbacks.IItemHandler;
import com.creative.cutebond.common.Constants;
import com.creative.cutebond.common.Item;

public class CommentsActivity extends Activity implements IItemHandler {
		
	private CommentsAdapter cmtAdapter = null;
	private ListView list = null;
	private View footer = null;
	private int cPage = 1;		
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.template_popup_cmts);
		init();
	}

	private void init() {		
		
		list = (ListView) findViewById(R.id.viewcomments_list);

		footer = View.inflate(this, R.layout.footer, null);			

		//list.addFooterView(footer);
		footer.setVisibility(View.GONE);
		
		list.setTag(0);
		list.setOnItemClickListener(onItemClick);
		
		findViewById(R.id.back_arrow).setOnClickListener(onclick);
		
		getData();
	}
	
	OnItemClickListener onItemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> listview, View view,
				int position, long arg3) {
			
			Item item = (Item) view.getTag();
			
		}
	};
	
	private void getData() {
		
		cmtAdapter = new CommentsAdapter(this);
		list.setAdapter(cmtAdapter);
		
		Vector<Item> items = deserializeData();
		cmtAdapter.setItems(items);
		cmtAdapter.notifyDataSetChanged();
	}	

	OnClickListener onclick = new OnClickListener() {

		@Override
		public void onClick(View view) {
			switch (view.getId()) {						
			
			case R.id.back_arrow:
				CommentsActivity.this.finish();
				break;
			
			default:
				break;
			}
		}
	};
	
	public String getFromStore(String key) {
		SharedPreferences pref = this.getSharedPreferences("VF", MODE_PRIVATE);
		String res = pref.getString(key, "");
		return res;
	}

	@Override
	public void onFinish(Object results, int requestType) {

		findViewById(R.id.viewcomments_pbar).setVisibility(View.GONE);
		
		switch (requestType) {
		case 1:									
			break;
										
		default:
			break;
		}
	}

	@Override
	public void onError(String errorCode, int requestType) {
		findViewById(R.id.viewcomments_pbar).setVisibility(View.GONE);
	}
	
	private Vector<Item> deserializeData() {
		Vector<Item> items = null;
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = openFileInput(Constants.VITEM);
			ObjectInputStream ois = new ObjectInputStream(fileInputStream);
			items = (Vector<Item>) ois.readObject();
			ois.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return items;
	}

}
