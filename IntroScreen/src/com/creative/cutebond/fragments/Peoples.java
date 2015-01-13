package com.creative.cutebond.fragments;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import android.app.Activity;
import android.content.Context;
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
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;

import com.creative.cutebond.AccountActivity;
import com.creative.cutebond.CuteBond;
import com.creative.cutebond.R;
import com.creative.cutebond.adapter.ExpandAdapter;
import com.creative.cutebond.adapter.MicroblogAdapter;
import com.creative.cutebond.adapter.PeoplesAdapter;
import com.creative.cutebond.callbacks.IItemHandler;
import com.creative.cutebond.common.Constants;
import com.creative.cutebond.common.Item;
import com.creative.cutebond.tasks.HTTPBackgroundTask;

public class Peoples extends Fragment implements IItemHandler, OnScrollListener {

	private PeoplesAdapter adapter = null;
	private MicroblogAdapter mbAdapter = null;
	
	private CuteBond cutebond = null;
	private GridView grid = null;
	private ListView list = null;

	private View footer = null;
	private View mbfooter = null;
	private int visibleItemCount = 0;
	private int totalItemCount = 0;
	
	private int mbvisibleItemCount = 0;
	private int mbtotalItemCount = 0;
	
	private int cPage = 1;
	private int mbPage = 1;
	
	private String PT = "all";
	private String TY = "microblog";

	private HTTPBackgroundTask onscrolltask = null;
	
	private ExpandAdapter menuAdpt = null;
	private ExpandAdapter mmenuAdpt = null;

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

		View layout = inflater.inflate(R.layout.people_main, container, false);
		return layout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		getView().findViewById(R.id.people_tabs).setOnClickListener(cutebond);
		getView().findViewById(R.id.microblog_tabs).setOnClickListener(cutebond);
		
		mbfooter = View.inflate(cutebond, R.layout.footer, null);
		
		grid = (GridView) getView().findViewById(R.id.po_grid);

		footer = getView().findViewById(R.id.efooter);
		footer.setVisibility(View.GONE);		
						
		grid.setOnItemClickListener(onItemClick);
		grid.setOnScrollListener(this);

		menuAdpt = new ExpandAdapter(cutebond);
		
		mmenuAdpt = new ExpandAdapter(cutebond);
		
		getData();
		
		getPeopleCategories();
		
		getMicroblogCategories();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onDestroyView() {
		// catAdpt.release();
		// catAdpt = null;
		super.onDestroyView();
	}

	private void getData() {

		if (adapter == null) {
			adapter = new PeoplesAdapter(cutebond);
			grid.setAdapter(adapter);
		}

		String link = cutebond.getPropertyValue("content_listing_po");

		link = link.replace("(UID)", cutebond.getFromStore("userid"));
		link = link.replace("(PT)", PT);		
		link = link.replace("(PNO)", cPage+"");

		HTTPBackgroundTask task = new HTTPBackgroundTask(this, getActivity(),
				5, 1);
		// task.setCacheType(task.getCACHE_ITEMS_INTRN());
		task.execute(link);

	}
	
	private void getPeopleCategories() {
				
		HTTPBackgroundTask menu_task = new HTTPBackgroundTask(this,
				getActivity(), 10, 10);
		menu_task.setRawAttri(true, Constants.PO_L_MENU, R.raw.po_l_menu);
		menu_task.execute("");
		
	}
	
	private void getMicroblogCategories() {
		
		HTTPBackgroundTask menu_task = new HTTPBackgroundTask(this,
				getActivity(), 10, 11);
		menu_task.setRawAttri(true, Constants.MB_L_MENU, R.raw.mb_l_menu);
		menu_task.execute("");
		
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
				
				getView().findViewById(R.id.po_pbar).setVisibility(View.GONE);
				
				if (results != null) {
					Vector<Item> items = (Vector<Item>) results;
					
					Item paging = items.remove(0);
					
					if(items.size() == 0) {
						adapter.clear();
						adapter.notifyDataSetChanged();
						getView().findViewById(R.id.po_txt)
								.setVisibility(View.VISIBLE);
						return;
					}
					
					getView().findViewById(R.id.po_pbar).setTag(
							"currentPage : " + cPage //got data for page 1...now request for page 2
									+ "totalPages : "
									+ paging.getAttribute("total_pages"));
					
					adapter.setItems(items);
					adapter.notifyDataSetChanged();
					return;
				}

				adapter.clear();
				adapter.notifyDataSetChanged();
				getView().findViewById(R.id.po_txt).setVisibility(View.VISIBLE);

				break;

			case 2:
				footer.setVisibility(View.GONE);
				if (results != null) {
					
					Vector<Item> items = (Vector<Item>) results;
					
					if(items != null && items.size() > 0){
						
						Item paging = items.remove(0);
					
						getView().findViewById(R.id.po_pbar).setTag(
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
				}

				break;
				
			case 3:
				
				getView().findViewById(R.id.mb_pbar).setVisibility(View.GONE);
				
				if (results != null) {										
					
					Vector<Item> items = (Vector<Item>) results;
					
					Item paging = items.remove(0);
					
					if(items.size() == 0) {
						adapter.clear();
						adapter.notifyDataSetChanged();
						getView().findViewById(R.id.mb_txt)
								.setVisibility(View.VISIBLE);
						return;
					}
					
					list.setTag(
							"currentPage : " + mbPage //got data for page 1...now request for page 2
									+ "totalPages : "
									+ paging.getAttribute("total_pages"));
					
					mbAdapter.setItems(items);
					mbAdapter.notifyDataSetChanged();
					return;
				}

				mbAdapter.clear();
				mbAdapter.notifyDataSetChanged();
				getView().findViewById(R.id.mb_txt).setVisibility(View.VISIBLE);

				break;
				
			case 4:
				mbfooter.setVisibility(View.GONE);
				if (results != null) {
					
					Vector<Item> items = (Vector<Item>) results;
					
					if(items != null && items.size() > 0){
						
						Item paging = items.remove(0);
					
						list.setTag(
								"currentPage : " + mbPage //got data for page 1...now request for page 2
										+ "totalPages : "
										+ paging.getAttribute("total_pages"));
						
						if(items.size() == 0)
							return;
												
						Vector<Item> oldCContain = mbAdapter.getItems();
						oldCContain.addAll(items);
						mbAdapter.setItems(oldCContain);
						mbAdapter.notifyDataSetChanged();					
					}
				}

				break;
				
			case 10:
				
				if (results != null) {
					
					Item item = (Item)results;
					
					List<String> group = new ArrayList<String>();
					group.add("peoples");
								
					/*@SuppressWarnings("unchecked")
					Enumeration<String> keys = item.keys();			
					while (keys.hasMoreElements()) {
						String key = keys.nextElement().toString();
						group.add(key);				
					}*/
					
					menuAdpt.setGroupItem(group);
					
					menuAdpt.setChildItem(item);
					
					menuAdpt.notifyDataSetChanged();
																
					return;					
				}

				menuAdpt.clear();
				menuAdpt.notifyDataSetChanged();
				
				break;
				
			case 11:
				
				if (results != null) {
					
					Item item = (Item)results;
					
					List<String> group = new ArrayList<String>();
					group.add("peoples");
								
					/*@SuppressWarnings("unchecked")
					Enumeration<String> keys = item.keys();			
					while (keys.hasMoreElements()) {
						String key = keys.nextElement().toString();
						group.add(key);				
					}*/
					
					mmenuAdpt.setGroupItem(group);
					
					mmenuAdpt.setChildItem(item);
					
					mmenuAdpt.notifyDataSetChanged();
																
					return;					
				}

				mmenuAdpt.clear();
				mmenuAdpt.notifyDataSetChanged();
				
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
		getView().findViewById(R.id.po_pbar).setVisibility(View.GONE);

		footer.setVisibility(View.GONE);
		mbfooter.setVisibility(View.GONE);
	}

	OnItemClickListener onItemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> listview, View view,
				int position, long arg3) {
			
			switch (listview.getId()) {
			case R.id.po_grid:
				Item item = (Item) view.getTag();
				serializeData(item);
				
				if(item.getAttribute("status").equalsIgnoreCase("0")) {
					cutebond.launchActivity(AccountActivity.class, 8000, 0);
					return;
				}						
				
				cutebond.launchActivity(AccountActivity.class, 8000, 1);	
				break;
				
			/*case R.id.mb_list:
				Log.e("=-=-=-=-","-0-0-0-0-0");
				break;*/

			default:
				break;
			}
			
					
		}
	};
	
	/**
	 * serializeData -
	 * 
	 * @param Item
	 *            item
	 */
	private void serializeData(Item item) {
		try {

			FileOutputStream fos = cutebond.openFileOutput(Constants.ITEM,
					Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(item);
			oos.flush();
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}		

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		
		if(view.getId() == R.id.mb_list) {
			
			this.mbtotalItemCount = totalItemCount;
			this.mbvisibleItemCount = firstVisibleItem + visibleItemCount;
			
		} else  if(view.getId() == R.id.po_grid) {
			this.totalItemCount = totalItemCount;
			this.visibleItemCount = firstVisibleItem + visibleItemCount;	
		}
		
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		switch (scrollState) {
		case OnScrollListener.SCROLL_STATE_IDLE:
			
			if (!footer.isShown() && view.getId() == R.id.po_grid) {
				if (visibleItemCount == totalItemCount) {

					Object obj = getView().findViewById(R.id.po_pbar).getTag();

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
						
						
						String link = cutebond.getPropertyValue("content_listing_po");

						link = link.replace("(UID)", cutebond.getFromStore("userid"));
						link = link.replace("(PT)", PT);		
						link = link.replace("(PNO)", cPage+"");
						
						onscrolltask = new HTTPBackgroundTask(this, cutebond,
								5, 2);
						onscrolltask.execute(link);
						
						this.cPage = cPage;

					} else if (cPage == tPage) {
						footer.setVisibility(View.GONE);
					}
				}
			
		} else if (!mbfooter.isShown() && view.getId() == R.id.mb_list)
					if (mbvisibleItemCount == mbtotalItemCount) {

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
							mbfooter.setVisibility(View.VISIBLE);
						
							String link = cutebond.getPropertyValue("content_listing_mb");

							link = link.replace("(UID)", cutebond.getFromStore("userid"));
							link = link.replace("(TY)", TY);
							link = link.replace("(PNO)", cPage+"");

							onscrolltask = new HTTPBackgroundTask(this, cutebond,
									8, 4);
							onscrolltask.execute(link);
							
							this.mbPage = cPage;

						} else if (cPage == tPage) {
							mbfooter.setVisibility(View.GONE);
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
	
	public void onTabChange(int viewId) {
		switch (viewId) {
		
		case R.id.people_tabs:
			
			((Button)getView().findViewById(R.id.people_tabs)).setTextColor(getResources().getColor(R.color.black));
			((View)getView().findViewById(R.id.people_tabs_line)).setBackgroundColor(getResources().getColor(R.color.pinkish));			
			
			((Button)getView().findViewById(R.id.microblog_tabs)).setTextColor(getResources().getColor(R.color.gray));
			((View)getView().findViewById(R.id.microblog_tabs_line)).setBackgroundColor(getResources().getColor(R.color.gray));
			
			getView().findViewById(R.id.po_view).setVisibility(View.VISIBLE);
			getView().findViewById(R.id.mb_view).setVisibility(View.GONE);
			
			break;
		
		case R.id.microblog_tabs:
			
			((Button)getView().findViewById(R.id.people_tabs)).setTextColor(getResources().getColor(R.color.gray));
			((View)getView().findViewById(R.id.people_tabs_line)).setBackgroundColor(getResources().getColor(R.color.gray));			
			
			((Button)getView().findViewById(R.id.microblog_tabs)).setTextColor(getResources().getColor(R.color.black));
			((View)getView().findViewById(R.id.microblog_tabs_line)).setBackgroundColor(getResources().getColor(R.color.pinkish));
			
			getView().findViewById(R.id.po_view).setVisibility(View.GONE);
			getView().findViewById(R.id.mb_view).setVisibility(View.VISIBLE);
			
			getMicroBlogData();
			
			/*if(getView().findViewById(R.id.microblog_tabs).getTag() == null) {
				getMicroBlogData();
				getView().findViewById(R.id.microblog_tabs).setTag("tag");
			}*/
			
			break;

		default:
			break;
		}
	}
	
	private void getMicroBlogData() {
		if(mbAdapter == null) {
			mbAdapter = new MicroblogAdapter(cutebond);			
			list = (ListView) getView().findViewById(R.id.mb_list);
			list.setOnScrollListener(this);						
			//list.setOnItemClickListener(onItemClick);
			
			list.addFooterView(mbfooter);
			
			mbfooter.setVisibility(View.GONE);
			
			list.setAdapter(mbAdapter);
			
			String link = cutebond.getPropertyValue("content_listing_mb");

			link = link.replace("(UID)", cutebond.getFromStore("userid"));
			link = link.replace("(TY)", TY);
			link = link.replace("(PNO)", mbPage+"");

			HTTPBackgroundTask task = new HTTPBackgroundTask(this, getActivity(),
					8, 3);
			task.execute(link);
			
		}
	}
	
	public ExpandAdapter getMenuAdtr() {
		
		if(getView().findViewById(R.id.po_view).isShown())
			return menuAdpt;
		
		return mmenuAdpt;
		
		/*getView().findViewById(R.id.po_view).setVisibility(View.VISIBLE);
		getView().findViewById(R.id.mb_view).setVisibility(View.GONE);*/
				
	}
	
	public void filterContent(Item item) {
		
		if(getView().findViewById(R.id.po_view).isShown()) {
			cPage = 1;
			
			adapter.clear();
			adapter.notifyDataSetChanged();
			getView().findViewById(R.id.po_pbar).setVisibility(View.VISIBLE);
			getView().findViewById(R.id.po_txt).setVisibility(View.GONE);
			
			String categoryName =  item.getAttribute("categoryName");
			categoryName = categoryName.toLowerCase(Locale.ENGLISH);
			categoryName = categoryName.replaceAll(" ", "_");
			
			PT =  categoryName;
								
			String link = cutebond.getPropertyValue("content_listing_po");
			link = link.replace("(UID)", cutebond.getFromStore("userid"));
			link = link.replace("(PT)", categoryName);		
			link = link.replace("(PNO)", cPage+"");
			
			HTTPBackgroundTask task = new HTTPBackgroundTask(this, getActivity(), 5, 1);
			task.execute(link);
			
		} else if(getView().findViewById(R.id.mb_view).isShown()) {
			
			mbPage = 1;
			
			mbAdapter.clear();
			mbAdapter.notifyDataSetChanged();
			getView().findViewById(R.id.mb_pbar).setVisibility(View.VISIBLE);
			getView().findViewById(R.id.mb_txt).setVisibility(View.GONE);
			
			String categoryName =  item.getAttribute("categoryName");
			categoryName = categoryName.toLowerCase(Locale.ENGLISH);
			categoryName = categoryName.replaceAll(" ", "_");
			
			TY =  categoryName;
			
			String link = cutebond.getPropertyValue("content_listing_mb");

			link = link.replace("(UID)", cutebond.getFromStore("userid"));
			link = link.replace("(TY)", TY);
			link = link.replace("(PNO)", mbPage+"");

			HTTPBackgroundTask task = new HTTPBackgroundTask(this, getActivity(),
					8, 3);
			task.execute(link);
			
		}												
				
	}

}