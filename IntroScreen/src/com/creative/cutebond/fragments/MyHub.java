package com.creative.cutebond.fragments;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.creative.cutebond.CuteBond;
import com.creative.cutebond.R;
import com.creative.cutebond.adapter.ExpandAdapter;
import com.creative.cutebond.adapter.MyHubAdapter;
import com.creative.cutebond.callbacks.IItemHandler;
import com.creative.cutebond.common.Constants;
import com.creative.cutebond.common.Item;
import com.creative.cutebond.tasks.HTTPBackgroundTask;

public class MyHub extends Fragment implements IItemHandler,
		OnScrollListener {
	
	private MyHubAdapter adapter = null;
	private CuteBond cutebond = null;
	private ListView list = null;

	private View footer = null;
	private int visibleItemCount = 0;
	private int totalItemCount = 0;
	private String cevent = "";

	private HTTPBackgroundTask onscrolltask = null;		
	
	private int width = LayoutParams.MATCH_PARENT;
	private int height = LayoutParams.MATCH_PARENT;
	
	private int cPage = 1;
	
	private ExpandAdapter menuAdpt = null;

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
		
		View layout = inflater.inflate(R.layout.myhub_main, container, false);
		return layout;
		
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		list = (ListView) getView().findViewById(R.id.myhub_list);

		footer = View.inflate(cutebond, R.layout.footer, null);

		list.addFooterView(footer);
		footer.setVisibility(View.GONE);
		list.setOnScrollListener(this);

		getSize();
		
		menuAdpt = new ExpandAdapter(cutebond);
		
		getMyHubCategories();

	}
	
	/*private void getMyHubCategories() {
		HTTPBackgroundTask menu_task = new HTTPBackgroundTask(this,
				getActivity(), 10, 11);
		menu_task.setRawAttri(true, Constants.MH_L_MENU, R.raw.mh_l_menu);
		menu_task.execute("");
	}*/
	
	private void getMyHubCategories() {
		HTTPBackgroundTask menu_task = new HTTPBackgroundTask(this,
				getActivity(), 17, 11);
		menu_task.setRawAttri(true, Constants.MH_L_MENU, R.raw.mh_l_menu);
		menu_task.execute("");
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onDestroyView() {
		//catAdpt.release();
		//catAdpt = null;
		super.onDestroyView();
	}
	
	private void getSize() {
		ViewTreeObserver vt = getView().findViewById(R.id.catgr_view)
				.getViewTreeObserver();
		vt.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				if (getView().findViewById(R.id.catgr_view).getTag() == null) {
					getView().findViewById(R.id.catgr_view).setTag(1);
					height = getView().findViewById(R.id.catgr_view).getHeight();
					width = getView().findViewById(R.id.catgr_view).getWidth();

					getData();
				}
			}
		});
	}

	/*private void getData() {
		HTTPBackgroundTask task = new HTTPBackgroundTask(this, getActivity(), 1, 1);		
		task.setCacheType(task.getCACHE_ITEMS_INTRN());
		task.execute("");
	}*/
	
	private void getData() {
		
		if(adapter == null){			
			adapter = new MyHubAdapter(cutebond, width, height);
			list.setAdapter(adapter);
		}
		
		String link = cutebond.getPropertyValue("content_listing_mh");
		link = link.replace("(UID)", cutebond.getFromStore("userid"));
		link = link.replace("(PNO)", cPage+"");		
		
		HTTPBackgroundTask task = new HTTPBackgroundTask(this, getActivity(), 11, 1);	
		//task.setCacheType(task.getCACHE_ITEMS_INTRN());
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
					
					getView().findViewById(R.id.myhub_pbar).setVisibility(View.GONE);
					
					Vector<Item> items = (Vector<Item>) results;
					
					Item paging = items.remove(0);
					
					if(items.size() == 0) {
						adapter.clear();
						adapter.notifyDataSetChanged();
						getView().findViewById(R.id.myhub_txt)
								.setVisibility(View.VISIBLE);
						return;
					}
														
					list.setTag(
							"currentPage : " + cPage //got data for page 1...now request for page 2
									+ "totalPages : "
									+ paging.getAttribute("total_pages"));
					
					adapter.setItems(items);
					adapter.notifyDataSetChanged();
					return;
				}

				adapter.clear();
				adapter.notifyDataSetChanged();
				getView().findViewById(R.id.myhub_txt)
						.setVisibility(View.VISIBLE);

				break;
						
			case 4:
				footer.setVisibility(View.GONE);
				if (results != null) {
					
					Vector<Item> items = (Vector<Item>) results;
					
					Item paging = items.remove(0);
					
					if(items.size() == 0) {
						adapter.clear();
						adapter.notifyDataSetChanged();
						getView().findViewById(R.id.myhub_txt)
								.setVisibility(View.VISIBLE);
						return;
					}
														
					list.setTag(
							"currentPage : " + cPage //got data for page 1...now request for page 2
									+ "totalPages : "
									+ paging.getAttribute("total_pages"));
					
					
					if(items.size() == 0)
						return;
											
					Vector<Item> oldCContain = adapter.getItems();
					oldCContain.addAll(items);
					adapter.setItems(oldCContain);
					adapter.notifyDataSetChanged();					

				}

				break;
				
			case 11:
				
				if (results != null) {
					
					Item item = (Item)results;
					
					List<String> group = new ArrayList<String>();
					//group.add("peoples");
					
					Enumeration<String> keys = item.keys();			
					while (keys.hasMoreElements()) {
						String key = keys.nextElement().toString();
						group.add(key);				
					}
													
					menuAdpt.setGroupItem(group);					
					menuAdpt.setChildItem(item);					
					menuAdpt.notifyDataSetChanged();
					return;					
				}

				menuAdpt.clear();
				menuAdpt.notifyDataSetChanged();
				
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
		getView().findViewById(R.id.myhub_pbar).setVisibility(View.GONE);

		footer.setVisibility(View.GONE);

	}

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

					Object obj = list.getTag();

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

						String link = cutebond.getPropertyValue("content_listing_mh");
						link = link.replace("(UID)", cutebond.getFromStore("userid"));
						link = link.replace("(PNO)", cPage+"");												

						onscrolltask = new HTTPBackgroundTask(this, cutebond,
								11, 4);
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
	
	public ExpandAdapter getMenuAdtr() {		
			return menuAdpt;
	}

}