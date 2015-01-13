package com.creative.cutebond.fragments;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.creative.cutebond.CuteBond;
import com.creative.cutebond.R;
import com.creative.cutebond.adapter.ExpandAdapter;
import com.creative.cutebond.callbacks.IItemHandler;
import com.creative.cutebond.common.Constants;
import com.creative.cutebond.common.Item;
import com.creative.cutebond.controls.CuteBondScrollView;
import com.creative.cutebond.controls.CuteBondScrollView.ScrollViewListener;
import com.creative.cutebond.controls.SideBySideControl;
import com.creative.cutebond.tasks.HTTPBackgroundTask;

public class Photos extends Fragment implements IItemHandler, ScrollViewListener {

	private CuteBond cutebond = null;

	private SideBySideControl grid = null;

	private View footer = null;

	private HTTPBackgroundTask onscrolltask = null;
	
	private int cPage = 1;
	
	private ExpandAdapter menuAdpt = null;
	
	private String CATID = "";

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

		View layout = inflater.inflate(R.layout.photo_main, container, false);
		return layout;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		grid = (SideBySideControl) getView().findViewById(R.id.pht_list);
		
		((CuteBondScrollView)getView().findViewById(R.id.cutebondsv)).setScrollViewListener(this);
		
		footer = getView().findViewById(R.id.efooter);
		footer.setVisibility(View.GONE);

		menuAdpt = new ExpandAdapter(cutebond);
		
		getData();
		
		getPhotoCategories();
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

		String link = cutebond.getPropertyValue("content_listing_p");

		link = link.replace("(UID)", cutebond.getFromStore("userid"));
		link = link.replace("(CATID)", "");
		link = link.replace("(PNO)", cPage+"");

		HTTPBackgroundTask task = new HTTPBackgroundTask(this, getActivity(),
				4, 1);
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
				
				getView().findViewById(R.id.pht_pbar).setVisibility(View.GONE);
				
				if (results != null) {
					
					Vector<Item> items = (Vector<Item>) results;
					
					Item paging = items.remove(0);						
					
					grid.setTag(
							"currentPage : " + cPage //got data for page 1...now request for page 2
									+ "totalPages : "
									+ paging.getAttribute("total_pages"));
					
					// adapter.setItems(items);
					// adapter.notifyDataSetChanged();
					grid.createView(items);
					return;
				}

				// adapter.clear();
				// adapter.notifyDataSetChanged();
				getView().findViewById(R.id.pht_txt)
						.setVisibility(View.VISIBLE);

				break;
				
			case 2:
				footer.setVisibility(View.GONE);
				if (results != null) {
					
					Vector<Item> items = (Vector<Item>) results;
					
					if(items != null && items.size() > 0){
						
						Item paging = items.remove(0);
					
						grid.setTag(
								"currentPage : " + cPage //got data for page 1...now request for page 2
										+ "totalPages : "
										+ paging.getAttribute("total_pages"));
						
						if(items.size() == 0)
							return;
												
						//Vector<Item> oldCContain = adapter.getItems();
						//oldCContain.addAll(items);
						//adapter.setItems(oldCContain);
						//adapter.notifyDataSetChanged();
						grid.createView(items);
						
					}
					
				}

				break;
				

			case 3:
				if (results != null) {
					
					Item item = (Item)results;
					
					List<String> group = new ArrayList<String>();					
								
					@SuppressWarnings("unchecked")
					Enumeration<String> keys = item.keys();			
					while (keys.hasMoreElements()) {
						String key = keys.nextElement().toString();
						group.add(key);				
					}
					
					menuAdpt.setGroupItem(group);
					
					menuAdpt.setChildItem(item);
					
					menuAdpt.notifyDataSetChanged();
										
					/*Vector<Item> items = (Vector<Item>) results;										
					menuAdpt.setItems(items);
					menuAdpt.notifyDataSetChanged();*/					
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
		getView().findViewById(R.id.pht_pbar).setVisibility(View.GONE);

		footer.setVisibility(View.GONE);
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

	public void showDetails(Item item) {

		getView().findViewById(R.id.vid_list).setVisibility(View.GONE);

		FrameLayout layout = (FrameLayout) getView().findViewById(
				R.id.catgr_view);

		View view = View.inflate(cutebond, R.layout.template_detail, null);

		layout.addView(view);
	}
	
	private void getPhotoCategories() {
		HTTPBackgroundTask menu_task = new HTTPBackgroundTask(this,
				getActivity(), 3, 3);
		menu_task.setRawAttri(true, Constants.P_L_MENU, R.raw.p_l_menu);
		menu_task.execute("");
	}
	
	public ExpandAdapter getMenuAdtr(){
		return menuAdpt;
	}
	
public void filterContent(Item item) {
		
		getView().findViewById(R.id.pht_pbar).setVisibility(View.VISIBLE);
		getView().findViewById(R.id.pht_txt).setVisibility(View.GONE);
	
		cPage = 1;
		CATID = "";		
		
		grid.clearViews();		
		
		getView().findViewById(R.id.pht_pbar).setVisibility(View.VISIBLE);
		getView().findViewById(R.id.pht_txt).setVisibility(View.GONE);
		
		CATID = item.getAttribute("photo_id");
		
		String link = cutebond.getPropertyValue("content_listing_p");
		link = link.replace("(UID)", cutebond.getFromStore("userid"));
		link = link.replace("(PNO)", cPage+"");
		link = link.replace("(CATID)", CATID);
		link = link.replace("(CID)", "");
		
		HTTPBackgroundTask task = new HTTPBackgroundTask(this, getActivity(), 4, 1);
		task.execute(link);				
				
	}

@Override
public void onScrollChanged(CuteBondScrollView scrollView, int x, int y,
		int oldx, int oldy) {
	View view = (View) scrollView.getChildAt(scrollView.getChildCount() - 1);
    int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));

    if(footer.isShown())
    	return;
    
    if (diff == 0) {
    	Log.e("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-", "=-=-=-=-=-=--=-=-=-=-=-");
    	Object obj = grid.getTag();

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
			
			String link = cutebond.getPropertyValue("content_listing_p");

			link = link.replace("(UID)", cutebond.getFromStore("userid"));
			link = link.replace("(CATID)", CATID);
			link = link.replace("(PNO)", cPage+"");

			onscrolltask = new HTTPBackgroundTask(this, cutebond, 4, 2);
			onscrolltask.execute(link);
			
			this.cPage = cPage;

		} else if (cPage == tPage) {
			footer.setVisibility(View.GONE);
		}
    }
	
}

}