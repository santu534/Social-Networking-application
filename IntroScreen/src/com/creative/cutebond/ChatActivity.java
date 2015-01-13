package com.creative.cutebond;

import java.util.Vector;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.creative.cutebond.adapter.ChatInboxAdapter;
import com.creative.cutebond.callbacks.IItemHandler;
import com.creative.cutebond.common.Item;
import com.creative.cutebond.tasks.HTTPBackgroundTask;

public class ChatActivity extends Activity implements IItemHandler {
		
	private ChatInboxAdapter adapter, othersAdapter, blockAdapter = null;
	private ListView list = null;
	private View footer = null;
	private int cPage = 1;
	
	private String[] multicolor = { "#FACDDC", "#D99400", "#B59FC7", "#7D1B7E",
			"#008080", "#254117", "#9DC209", "#FFDB58", "#FFCBA4", "#C19A6B",
			"#483C32", "#C35817", "#E67451", "#E77471", "#F75D59", "#C48793",
			"#E38AAE", "#00FFFF", "#43C6DB" };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chatmain);
		init();
	}

	private void init() {
		findViewById(R.id.inbox_tabs).setOnClickListener(onclick);
		findViewById(R.id.others_tabs).setOnClickListener(onclick);
		findViewById(R.id.blocked_tabs).setOnClickListener(onclick);
		
		list = (ListView) findViewById(R.id.c_list);

		footer = View.inflate(this, R.layout.footer, null);			

		list.addFooterView(footer);
		footer.setVisibility(View.GONE);
		
		list.setTag(0);
		list.setOnItemClickListener(onItemClick);
		
		getInbox();
	}
	
	OnItemClickListener onItemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> listview, View view,
				int position, long arg3) {
			
			Item item = (Item) view.getTag();

			switch ((Integer) list.getTag()) {
			
			case 0:
				break;

			case 1:
				break;

			case 2:
				
				break;

			default:
				break;
			}
		}
	};
	
	private void getInbox() {
		
		/*if (adapter == null) {
			adapter = new ChatInboxAdapter(this, multicolor);
			list.setAdapter(adapter);
		}*/
		
		if(adapter != null) {
			list.setAdapter(null);
			list.setAdapter(adapter);
			return;
		}
		
		adapter = new ChatInboxAdapter(this, multicolor);
		list.setAdapter(adapter);

		//String link = cutebond.getPropertyValue("content_listing_inbox");
		String link = "http://www.cutebond.com/sott_json/users/messages_list_inbox?userid=(UID)&page=(PNO)";
		link = link.replace("(UID)", getFromStore("userid"));
		link = link.replace("(PNO)", cPage+"");				
		
		HTTPBackgroundTask task = new HTTPBackgroundTask(this, this, 18, 1);
		// task.setCacheType(task.getCACHE_ITEMS_INTRN());
		task.execute(link);
	}
	
	private void getOthers() {
		
		if(othersAdapter != null) {
			list.setAdapter(null);
			list.setAdapter(othersAdapter);
			return;
		}
		
		othersAdapter = new ChatInboxAdapter(this, multicolor);
		list.setAdapter(othersAdapter);
		
		findViewById(R.id.c_pbar).setVisibility(View.VISIBLE);
		findViewById(R.id.c_txt).setVisibility(View.GONE);

		//String link = cutebond.getPropertyValue("content_listing_inbox");
		String link = "http://www.cutebond.com/sott_json/users/messages_list_others?userid=(UID)&page=(PNO)";
		link = link.replace("(UID)", getFromStore("userid"));
		link = link.replace("(PNO)", cPage+"");				
		
		HTTPBackgroundTask task = new HTTPBackgroundTask(this, this, 19, 2);
		// task.setCacheType(task.getCACHE_ITEMS_INTRN());
		task.execute(link);
	}
	
	private void getBlock() {
		
		if(blockAdapter != null) {
			list.setAdapter(null);
			list.setAdapter(blockAdapter);
			return;
		}
		
		blockAdapter = new ChatInboxAdapter(this, multicolor);
		list.setAdapter(blockAdapter);
		
		findViewById(R.id.c_pbar).setVisibility(View.VISIBLE);
		findViewById(R.id.c_txt).setVisibility(View.GONE);

		//String link = cutebond.getPropertyValue("content_listing_inbox");
		String link = "http://www.cutebond.com/sott_json/users/messages_list_block?userid=(UID)&page=(PNO)";
		link = link.replace("(UID)", getFromStore("userid"));
		link = link.replace("(PNO)", cPage+"");				
		
		HTTPBackgroundTask task = new HTTPBackgroundTask(this, this, 20, 3);
		// task.setCacheType(task.getCACHE_ITEMS_INTRN());
		task.execute(link);
	}

	OnClickListener onclick = new OnClickListener() {

		@Override
		public void onClick(View view) {
			switch (view.getId()) {						
			
			case R.id.inbox_tabs:								
				
				((Button)findViewById(R.id.inbox_tabs)).setTextColor(getResources().getColor(R.color.pinkish));
				((Button)findViewById(R.id.others_tabs)).setTextColor(getResources().getColor(R.color.gray));
				((Button)findViewById(R.id.blocked_tabs)).setTextColor(getResources().getColor(R.color.gray));
				
				getInbox();
																
				break;
				
			case R.id.others_tabs:
				
				((Button)findViewById(R.id.inbox_tabs)).setTextColor(getResources().getColor(R.color.gray));
				((Button)findViewById(R.id.others_tabs)).setTextColor(getResources().getColor(R.color.pinkish));
				((Button)findViewById(R.id.blocked_tabs)).setTextColor(getResources().getColor(R.color.gray));
				
				getOthers();
				
				break;
				
			case R.id.blocked_tabs:
				
				((Button)findViewById(R.id.inbox_tabs)).setTextColor(getResources().getColor(R.color.gray));
				((Button)findViewById(R.id.others_tabs)).setTextColor(getResources().getColor(R.color.gray));
				((Button)findViewById(R.id.blocked_tabs)).setTextColor(getResources().getColor(R.color.pinkish));
				
				getBlock();
				
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

		findViewById(R.id.c_pbar).setVisibility(View.GONE);
		
		switch (requestType) {
		case 1:
			
			if (results != null) {
				
				Vector<Item> items = (Vector<Item>) results;
				
				Item paging = items.remove(0);
				
				if(items.size() == 0) {
					adapter.clear();
					adapter.notifyDataSetChanged();
					findViewById(R.id.c_txt).setVisibility(View.VISIBLE);
					return;
				}
				
				findViewById(R.id.c_txt).setTag(
						"currentPage : " + cPage //got data for page 1...now request for page 2
								+ "totalPages : "
								+ paging.getAttribute("total_pages"));
				
				adapter.setItems(items);
				adapter.notifyDataSetChanged();
				return;
			}

			adapter.clear();
			adapter.notifyDataSetChanged();
			findViewById(R.id.c_txt)
					.setVisibility(View.VISIBLE);
			
			break;
			
		case 2:
			
			if (results != null) {
				
				Vector<Item> items = (Vector<Item>) results;
				
				Item paging = items.remove(0);
				
				if(items.size() == 0) {
					othersAdapter.clear();
					othersAdapter.notifyDataSetChanged();
					findViewById(R.id.c_txt).setVisibility(View.VISIBLE);
					return;
				}
				
				findViewById(R.id.c_txt).setTag(
						"currentPage : " + cPage //got data for page 1...now request for page 2
								+ "totalPages : "
								+ paging.getAttribute("total_pages"));
				
				othersAdapter.setItems(items);
				othersAdapter.notifyDataSetChanged();
				return;
			}

			othersAdapter.clear();
			othersAdapter.notifyDataSetChanged();
			findViewById(R.id.c_txt)
					.setVisibility(View.VISIBLE);
			
			break;
			
		case 3:
			
			if (results != null) {
				
				Vector<Item> items = (Vector<Item>) results;
				
				Item paging = items.remove(0);
				
				if(items.size() == 0) {
					blockAdapter.clear();
					blockAdapter.notifyDataSetChanged();
					findViewById(R.id.c_txt).setVisibility(View.VISIBLE);
					return;
				}
				
				findViewById(R.id.c_txt).setTag(
						"currentPage : " + cPage //got data for page 1...now request for page 2
								+ "totalPages : "
								+ paging.getAttribute("total_pages"));
				
				blockAdapter.setItems(items);
				blockAdapter.notifyDataSetChanged();
				return;
			}

			blockAdapter.clear();
			blockAdapter.notifyDataSetChanged();
			findViewById(R.id.c_txt)
					.setVisibility(View.VISIBLE);
			
			break;

		default:
			break;
		}
	}

	@Override
	public void onError(String errorCode, int requestType) {
		// TODO Auto-generated method stub
		
	}

}
