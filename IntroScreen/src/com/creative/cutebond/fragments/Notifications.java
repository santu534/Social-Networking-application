package com.creative.cutebond.fragments;
//http://www.cutebond.com/webapp/audio/1398497231.mp3
import java.util.Vector;

import android.app.Activity;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.creative.cutebond.CuteBond;
import com.creative.cutebond.R;
import com.creative.cutebond.adapter.NotificationAdapter;
import com.creative.cutebond.callbacks.IItemHandler;
import com.creative.cutebond.common.Item;
import com.creative.cutebond.tasks.HTTPBackgroundTask;

public class Notifications extends Fragment implements IItemHandler, OnScrollListener {

	private CuteBond cutebond = null;
	private ListView list = null;

	private View footer = null;
	private int visibleItemCount = 0;
	private int totalItemCount = 0;	

	private HTTPBackgroundTask onscrolltask = null;

	private int cPage = 1;
	
	private NotificationAdapter adapter = null;
	
	private String[] multicolor = { "#FACDDC", "#D99400", "#B59FC7", "#7D1B7E",
			"#008080", "#254117", "#9DC209", "#FFDB58", "#FFCBA4", "#C19A6B",
			"#483C32", "#C35817", "#E67451", "#E77471", "#F75D59", "#C48793",
			"#E38AAE", "#00FFFF", "#43C6DB" };
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		cutebond = (CuteBond) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View layout = inflater.inflate(R.layout.notificationlayout, container, false);
		return layout;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		list = (ListView) getView().findViewById(R.id.noti_list);
		list.setOnItemClickListener(onItemClick);
		list.setOnScrollListener(this);

		footer = View.inflate(cutebond, R.layout.footer, null);

		list.addFooterView(footer);
		footer.setVisibility(View.GONE);
		
		getData();
		
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	private void getData() {

		if (adapter == null) {
			adapter = new NotificationAdapter(cutebond, multicolor);
			list.setAdapter(adapter);
		}

		String link = cutebond.getPropertyValue("content_listing_nofi");
		link = link.replace("(UID)", cutebond.getFromStore("userid"));
		link = link.replace("(PNO)", cPage+"");			
		
		HTTPBackgroundTask task = new HTTPBackgroundTask(this, getActivity(),
				21, 1);
		// task.setCacheType(task.getCACHE_ITEMS_INTRN());
		task.execute(link);				
		
	}	

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onFinish(Object results, int requestType) {

		try {			

			switch (requestType) {
			case 1:
				if (results != null) {
					getView().findViewById(R.id.noti_pbar).setVisibility(View.GONE);		
					Vector<Item> items = (Vector<Item>) results;
					
					/*Item paging = items.remove(0);
					
					if(items.size() == 0) {
						adapter.clear();
						adapter.notifyDataSetChanged();
						getView().findViewById(R.id.noti_txt)
								.setVisibility(View.VISIBLE);
						return;
					}
					
					getView().findViewById(R.id.noti_list).setTag(
							"currentPage : " + cPage //got data for page 1...now request for page 2
									+ "totalPages : "
									+ paging.getAttribute("total_pages"));*/
					
					adapter.setItems(items);
					adapter.notifyDataSetChanged();
					return;
				}

				adapter.clear();
				adapter.notifyDataSetChanged();
				getView().findViewById(R.id.noti_txt)
						.setVisibility(View.VISIBLE);

				break;
				
			case 2:
				footer.setVisibility(View.GONE);
				if (results != null) {
					
					Vector<Item> items = (Vector<Item>) results;
					
					if(items != null && items.size() > 0){
						
						/*Item paging = items.remove(0);
					
						getView().findViewById(R.id.vid_list).setTag(
								"currentPage : " + cPage //got data for page 1...now request for page 2
										+ "totalPages : "
										+ paging.getAttribute("total_pages"));
						
						if(items.size() == 0)
							return;*/
												
						Vector<Item> oldCContain = adapter.getItems();
						oldCContain.addAll(items);
						adapter.setItems(oldCContain);
						adapter.notifyDataSetChanged();
						
					}				
				}
				break;
							
			default:
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onError(String errorCode, int requestType) {
		cutebond.showToast(errorCode);
		//getView().findViewById(R.id.vid_pbar).setVisibility(View.GONE);

		footer.setVisibility(View.GONE);

	}

	OnItemClickListener onItemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> listview, View view,
				int position, long arg3) {
			Item item = (Item) view.getTag();

			switch (listview.getId()) {

			case R.id.vid_list:
				showDetails(item);
				break;

			default:
				break;
			}

		}
	};

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		this.totalItemCount = totalItemCount;
		this.visibleItemCount = firstVisibleItem + visibleItemCount;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		switch (scrollState) {
		case OnScrollListener.SCROLL_STATE_IDLE:
			
			if (!footer.isShown())
				if (visibleItemCount == totalItemCount) {

					Object obj = getView().findViewById(R.id.noti_list).getTag();

					if (obj == null)
						return;

					String tag = obj.toString();

					String currentPage = tag.substring(14,
							tag.indexOf("totalPages"));

					String totalPages = tag.substring(
							tag.indexOf("totalPages") + 13, tag.length());

					int cPage = Integer.parseInt(currentPage);
					cPage = cPage + 1;
					
					int tPage = Integer.parseInt(totalPages);

					if (cPage <= tPage) {
						footer.setVisibility(View.VISIBLE);						
						
						String link = cutebond.getPropertyValue("content_listing_noti");
						link = link.replace("(UID)", cutebond.getFromStore("userid"));
						link = link.replace("(PNO)", cPage+"");
						
						onscrolltask = new HTTPBackgroundTask(this, cutebond, 2, 2);
						onscrolltask.execute(link);
						
						this.cPage = cPage;

					} else if (cPage == tPage) {
						footer.setVisibility(View.GONE);
					}
				}

			break;
		case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
			break;
		case OnScrollListener.SCROLL_STATE_FLING:
			break;
		}
	}

	/**
	 * cancelRequest - This method cancel's request to server.
	 */
	private void cancelRequest() {
		if (onscrolltask != null && onscrolltask.getStatus() == Status.RUNNING) {
			onscrolltask.cancel(true);
			onscrolltask = null;
		}
	}

	public void showDetails(Item item) {

		getView().findViewById(R.id.vid_list).setVisibility(View.GONE);

		FrameLayout layout = (FrameLayout) getView().findViewById(
				R.id.catgr_view);

		View view = View.inflate(cutebond, R.layout.template_detail, null);

		layout.addView(view);

		String str = item.getAttribute("userName");
		TextView title = (TextView) getView().findViewById(R.id.prv_name);
		title.setText(str);

		str = item.getAttribute("likes_count");
		title = (TextView) getView().findViewById(R.id.prv_like_hrt);
		title.setText(str);

	}

	public boolean back() {

		if (getView().findViewById(R.id.template_detail) != null
				&& getView().findViewById(R.id.template_detail).isShown()) {

			getView().findViewById(R.id.vid_list).setVisibility(View.VISIBLE);

			((FrameLayout) getView().findViewById(R.id.catgr_view))
					.removeView(getView().findViewById(R.id.template_detail));

			return true;
		}

		return false;

	}			

}