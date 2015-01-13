package com.creative.cutebond;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.Properties;
import java.util.Vector;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.creative.cutebond.adapter.CommentsAdapter;
import com.creative.cutebond.adapter.ExpandAdapter;
import com.creative.cutebond.common.Constants;
import com.creative.cutebond.common.Item;
import com.creative.cutebond.controls.SideViewControl;
import com.creative.cutebond.controls.SideViewControl.SizeCallback;
import com.creative.cutebond.fragments.Detail;
import com.creative.cutebond.fragments.MyHub;
import com.creative.cutebond.fragments.Notifications;
import com.creative.cutebond.fragments.Peoples;
import com.creative.cutebond.fragments.Photos;
import com.creative.cutebond.fragments.Videos;

public class CuteBond extends FragmentActivity implements OnClickListener,
		OnLongClickListener {

	private SideViewControl sideView = null;

	private Properties properties = null;

	private FragmentManager manager = null;

	private int oldId = R.id.videos;

	private Fragment current, previous = null;

	private Integer[] sel = { R.drawable.video_unsel, R.drawable.photo_unsel,
			R.drawable.upload_sel, R.drawable.people_unsel,
			R.drawable.myhub_unsel };

	private Integer[] unsel = { R.drawable.video_sel, R.drawable.photo_sel,
			R.drawable.upload_sel, R.drawable.people_sel, R.drawable.myhub_sel };

	public String[] multicolor = { "#FACDDC", "#D99400", "#B59FC7", "#7D1B7E",
			"#008080", "#254117", "#9DC209", "#FFDB58", "#FFCBA4", "#C19A6B",
			"#483C32", "#C35817", "#E67451", "#E77471", "#F75D59", "#C48793",
			"#E38AAE", "#00FFFF", "#43C6DB" };

	boolean menuOut = false;

	private Videos videos = null;

	private Photos photos = null;

	private Peoples peoples = null;

	private MyHub myhub = null;
	
	private Notifications noti = null;

	private Detail detailPage = null;

	private Dialog dialog = null;

	private CommentsAdapter cmtAdapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LayoutInflater inflater = LayoutInflater.from(this);

		sideView = (SideViewControl) inflater.inflate(R.layout.landing, null);

		setContentView(sideView);

		View mainLayout = inflater.inflate(R.layout.main, null);
		View rightLayout = inflater.inflate(R.layout.rightlayout, null);
		View leftLayout = inflater.inflate(R.layout.leftlayout, null);

		ViewGroup header = (ViewGroup) mainLayout
				.findViewById(R.id.menu_header);

		header.findViewById(R.id.menu_icon).setOnClickListener(
				new ClickListenerForScrolling(sideView, leftLayout));

		ImageView menu = (ImageView) header.findViewById(R.id.menu_icon);

		final View[] children = new View[] { leftLayout, mainLayout,
				rightLayout };

		int scrollToViewIdx = 0;

		sideView.initViews(children, scrollToViewIdx, new SizeCallbackForMenu(
				menu));

		loadProperties();

		addToStore("userid", "73"); //72

		findViewById(R.id.videos).setOnClickListener(onclick);
		findViewById(R.id.photos).setOnClickListener(onclick);
		findViewById(R.id.upload).setOnClickListener(onclick);
		findViewById(R.id.peoples).setOnClickListener(onclick);
		findViewById(R.id.myhub).setOnClickListener(onclick);
		findViewById(R.id.filter).setOnClickListener(onclick);
		
		findViewById(R.id.chatmessage).setOnClickListener(onclick);
		findViewById(R.id.request).setOnClickListener(onclick);
		findViewById(R.id.notifi).setOnClickListener(onclick);
		
		videos = new Videos();
		manager = getSupportFragmentManager();

		FragmentTransaction transaction = manager.beginTransaction();
		transaction.add(R.id.placeholder, videos, "videos");
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		transaction.commit();

		current = videos;

		((ExpandableListView) findViewById(R.id.menu_expand))
				.setOnChildClickListener(onchild);

	}

	/**
	 * loadProperties - Loads properties file from raw folder.
	 */
	private void loadProperties() {
		try {
			InputStream rawResource = getResources().openRawResource(
					R.raw.settings);
			properties = new Properties();
			properties.load(rawResource);
			rawResource.close();
			rawResource = null;
		} catch (Resources.NotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void swiftFragments(Fragment frag, String tag, View view) {

		FragmentTransaction trans = manager.beginTransaction();
		if (frag.isAdded() && frag.isVisible())
			return;
		else if (frag.isAdded() && frag.isHidden()) {
			trans.hide(current);
			trans.show(frag);
		} else if (!frag.isAdded()) {

			trans.hide(current);

			trans.add(R.id.placeholder, frag, tag);
			trans.show(frag);
		}

		if (current instanceof Detail)
			trans.remove(current);

		if (view != null) {

			ImageView img = (ImageView) findViewById(oldId);
			Integer i = Integer.parseInt(img.getTag().toString());
			img.setImageDrawable(null);
			img.setImageDrawable(getResources().getDrawable(unsel[i]));
			img.setBackgroundColor(Color.TRANSPARENT);

			Integer id = Integer.parseInt(view.getTag().toString());
			((ImageView) view).setImageDrawable(getResources().getDrawable(
					sel[id]));
			((ImageView) view).setBackgroundColor(getResources().getColor(
					R.color.tab_sel_pinkish));
			oldId = view.getId();
		}

		trans.commit();
		trans = null;
		current = frag;
	}

	private void showDetailPage(Item item) {

		findViewById(R.id.menu_header).setVisibility(View.GONE);
		findViewById(R.id.tablayout).setVisibility(View.GONE);

		serializeData(item);

		if (detailPage != null && detailPage.isVisible()) {
			detailPage.showDetails();
			return;
		}

		previous = current;

		if (detailPage == null)
			detailPage = new Detail();

		swiftFragments(detailPage, "detailPage", null);
	}

	OnClickListener onclick = new OnClickListener() {

		@Override
		public void onClick(View view) {
			switch (view.getId()) {

			/*case R.id.soc_close:
				if (dialog != null)
					dialog.dismiss();
				dialog = null;
				break;*/

			case R.id.chatmessage:
				launchActivity(ChatActivity.class, 9000);
				break;
				
			case R.id.notifi:
				
				if (noti == null)
					noti = new Notifications();
				swiftFragments(noti, "notifications", null);
				
				break;
				
			case R.id.request:
				break;
				
			case R.id.v_upload:
				launchActivity(VideoUpload.class, 2000);
				removeUploadView();
				break;

			case R.id.p_upload:
				launchActivity(PhotoUpload.class, 3000);
				removeUploadView();
				break;

			case R.id.a_upload:
				launchActivity(AudioUpload.class, 1000);
				removeUploadView();
				break;

			case R.id.m_upload:
				launchActivity(MicroblogUpload.class, 6000);
				removeUploadView();
				break;

			case R.id.chat_upload:
				launchActivity(ChatActivity.class, 7000);
				removeUploadView();
				break;

			case R.id.myhub_upload:

				removeUploadView();
				findViewById(R.id.filter).setVisibility(View.GONE);
				if (myhub == null)
					myhub = new MyHub();
				swiftFragments(myhub, "myhub", findViewById(R.id.myhub));

				break;

			case R.id.filter:
				launchActivity(CntryLang.class, 4000);
				break;

			case R.id.upload_layout:
				removeUploadView();
				break;

			case R.id.videos:

				findViewById(R.id.filter).setVisibility(View.VISIBLE);
				swiftFragments(videos, "videos", view);

				break;

			case R.id.photos:

				findViewById(R.id.filter).setVisibility(View.GONE);
				if (photos == null)
					photos = new Photos();

				swiftFragments(photos, "photos", view);

				break;

			case R.id.upload:
				showUploadView();
				break;

			case R.id.peoples:

				findViewById(R.id.filter).setVisibility(View.GONE);
				if (peoples == null)
					peoples = new Peoples();
				swiftFragments(peoples, "peoples", view);

				break;

			case R.id.myhub:

				findViewById(R.id.filter).setVisibility(View.GONE);
				if (myhub == null)
					myhub = new MyHub();
				swiftFragments(myhub, "myhub", view);

				break;

			default:
				break;
			}

		}
	};

	private void showUploadView() {
		View view = View.inflate(this, R.layout.template_upload, null);

		view.findViewById(R.id.upload_layout).setOnClickListener(onclick);

		((RelativeLayout) findViewById(R.id.parent_layout)).addView(view);

		findViewById(R.id.v_upload).setOnClickListener(onclick);
		findViewById(R.id.p_upload).setOnClickListener(onclick);
		findViewById(R.id.a_upload).setOnClickListener(onclick);
		findViewById(R.id.m_upload).setOnClickListener(onclick);
		findViewById(R.id.chat_upload).setOnClickListener(onclick);
		findViewById(R.id.myhub_upload).setOnClickListener(onclick);

	}

	private void removeUploadView() {
		((RelativeLayout) findViewById(R.id.parent_layout))
				.removeView(findViewById(R.id.upload_layout));
	}

	private void serializeData(Item item) {
		try {

			FileOutputStream fos = openFileOutput(Constants.ITEM,
					Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(item);
			oos.flush();
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void serializeData(Vector<Item> items) {
		try {

			FileOutputStream fos = openFileOutput(Constants.VITEM,
					Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(items);
			oos.flush();
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * getFromStore - Returns value associated with a key in the shared
	 * preference.
	 * 
	 * @param String
	 * @return String
	 */
	public String getFromStore(String key) {
		SharedPreferences pref = this.getSharedPreferences("VF", MODE_PRIVATE);
		String res = pref.getString(key, "");
		return res;
	}

	public Properties getProperties() {
		return properties;
	}

	public String getPropertyValue(String key) {
		return properties.getProperty(key);
	}

	public void showToast(int text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	public void showToast(String value) {
		Toast.makeText(this, value, Toast.LENGTH_SHORT).show();
	}

	/**
	 * addToStore - This method is used to persist data in shared preferences.
	 * It accept two parameter, 1st parameter is key and second parameter is
	 * value
	 * 
	 * @param String
	 * @param String
	 */
	public void addToStore(String key, String value) {
		SharedPreferences pref = this.getSharedPreferences("VF", MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(key, value);
		editor.commit();
	}

	private boolean closeSideView() {
		if (menuOut) {
			int left = findViewById(R.id.menu_icon).getMeasuredWidth();
			left = left + 30;
			sideView.smoothScrollTo(sideView.getMeasuredWidth() - left, 0);
			menuOut = !menuOut;
			return true;
		}
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == 4) {

			if (closeSideView()) {
				return true;
			}

			if (videos != null && videos.isVisible()) {
				if (videos.back() == true)
					return true;
			}

			if (photos != null && photos.isVisible()) {
				if (photos.back() == true)
					return true;
			}

			if (detailPage != null && detailPage.isVisible()) {

				if (detailPage.back() == true)
					return true;

				FragmentTransaction trans = manager.beginTransaction();
				trans.remove(detailPage);

				trans.show(previous);
				previous.onResume();
				trans.commit();

				current = previous;

				findViewById(R.id.menu_header).setVisibility(View.VISIBLE);
				findViewById(R.id.tablayout).setVisibility(View.VISIBLE);
				
				return false;
			}

		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * Helper that remembers the width of the 'slide' button, so that the
	 * 'slide' button remains in view, even when the menu is showing.
	 */
	class SizeCallbackForMenu implements SizeCallback {
		private int btnWidth;
		private View btnSlide;

		public SizeCallbackForMenu(View btnSlide) {
			super();
			this.btnSlide = btnSlide;
		}

		@Override
		public void onGlobalLayout() {
			btnWidth = btnSlide.getMeasuredWidth();
			System.out.println("btnWidth=" + btnWidth);
		}

		@Override
		public void getViewSize(int idx, int w, int h, int[] dims) {
			dims[0] = w;
			dims[1] = h;
			if (idx == 0 || idx == 2) {
				dims[0] = w - (btnWidth + 30);
			}
		}
	}

	/**
	 * Helper for examples with a HSV that should be scrolled by a menu View's
	 * width.
	 */
	class ClickListenerForScrolling implements OnClickListener {
		HorizontalScrollView scrollView;
		View menu;

		public ClickListenerForScrolling(HorizontalScrollView scrollView,
				View menu) {
			super();
			this.scrollView = scrollView;
			this.menu = menu;
		}

		@Override
		public void onClick(View view) {

			int menuWidth = menu.getMeasuredWidth();

			menu.setVisibility(View.VISIBLE);

			if (view.getId() == R.id.menu_icon) {
				if (!menuOut) {
					setMenuAdapter();
					scrollView.fullScroll(HorizontalScrollView.FOCUS_LEFT);
				} else {
					int left = menuWidth;
					scrollView.smoothScrollTo(left, 0);

				}
				menuOut = !menuOut;
				return;
			}
		}
	}

	private void showMoreComments(Vector<Item> items) {
		
		serializeData(items);
		
		Intent intent = new Intent(CuteBond.this, CommentsActivity.class);
		startActivityForResult(intent, 10000);
		
		/*dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.TRANSPARENT));
		View view = View.inflate(this, R.layout.template_popup_cmts, null);
		view.findViewById(R.id.soc_close).setOnClickListener(onclick);
		view.findViewById(R.id.viewcomments_pbar).setVisibility(View.GONE);

		if (cmtAdapter == null)
			cmtAdapter = new CommentsAdapter(this);

		cmtAdapter.clear();
		cmtAdapter.setItems(items);
		cmtAdapter.notifyDataSetChanged();

		((ListView) view.findViewById(R.id.viewcomments_list))
				.setAdapter(cmtAdapter);

		dialog.setContentView(view);
		dialog.show();*/
	}

	@Override
	public void onClick(View view) {

		Item item = null;

		Object obj = view.getTag();

		if (obj instanceof Item)
			item = (Item) obj;

		// Item item = (Item) view.getTag();

		switch (view.getId()) {
		
		case R.id.prv_like_hrt:
			showLFS(item, 1);
			break;
		
		case R.id.template_microblog:
			item.setAttribute("fromtype", "mb");
			addToStore("fromtype", "mb");
			showDetailPage(item);
			break;

		case R.id.showmore:
			showMoreComments((Vector<Item>) obj);
			break;

		case R.id.search_btn:
			break;

		//case R.id.prv_image:
		case R.id.temp_vid_def:	

			try {
			
				if(item.getAttribute("audio_image").equalsIgnoreCase("null")) {					
					detailPage.playVideoFile(item.getAttribute("fileId"));																								
					return;
				}
				
				String audioId = getPropertyValue("play_audio").replace("(AID)", item.getAttribute("fileId"));
				String fileName = item.getAttribute("fileId");
				fileName = fileName.substring(0, fileName.indexOf("."));
				
				detailPage.playAudioFile(audioId, item.getAttribute("videoTitle").replaceAll(" ", "")+"_"+fileName, view);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
						
			break;

		case R.id.back_arrow:
			if (detailPage != null && detailPage.isVisible()) {

				/*
				 * if (detailPage.back() == true) return true;
				 */

				FragmentTransaction trans = manager.beginTransaction();
				trans.remove(detailPage);

				trans.show(previous);
				previous.onResume();
				trans.commit();

				current = previous;

				findViewById(R.id.menu_header).setVisibility(View.VISIBLE);
				findViewById(R.id.tablayout).setVisibility(View.VISIBLE);

			}
			break;

		case R.id.people_tabs:
			peoples.onTabChange(view.getId());
			break;

		case R.id.microblog_tabs:
			peoples.onTabChange(view.getId());
			break;

		case R.id.template_video_layout:
			item.setAttribute("fromtype", "v");
			showDetailPage(item);
			break;

		/*case R.id.template_layout_photo:
			item.setAttribute("fromtype", "p");
			showDetailPage(item);
			break;*/
			
		case R.id.temp_pt_icon:
			item.setAttribute("fromtype", "p");
			showDetailPage(item);
			break;
			
		case R.id.template_mh_layout:
			item.setAttribute("fromtype", "mh");
			showDetailPage(item);
			break;

		default:
			break;
		}

	}

	private void setMenuAdapter() {

		// LinearLayout layout = (LinearLayout)findViewById(R.id.leftlayout);

		// ((ExpandableListView)
		// findViewById(R.id.menu_expand)).setAdapter(null);

		if (videos != null && videos.isVisible())
			((ExpandableListView) findViewById(R.id.menu_expand))
					.setAdapter(videos.getMenuAdtr());

		if (photos != null && photos.isVisible()) {
			ExpandAdapter aadpt = photos.getMenuAdtr();
			((ExpandableListView) findViewById(R.id.menu_expand))
					.setAdapter(aadpt);
			((ExpandableListView) findViewById(R.id.menu_expand))
					.expandGroup(0);
		}

		if (peoples != null && peoples.isVisible()) {
			ExpandAdapter aadpt = peoples.getMenuAdtr();
			((ExpandableListView) findViewById(R.id.menu_expand))
					.setAdapter(aadpt);
			((ExpandableListView) findViewById(R.id.menu_expand))
					.expandGroup(0);
		}

		if (myhub != null && myhub.isVisible()) {
			ExpandAdapter aadpt = myhub.getMenuAdtr();
			((ExpandableListView) findViewById(R.id.menu_expand))
					.setAdapter(aadpt);
			/*((ExpandableListView) findViewById(R.id.menu_expand))
					.expandGroup(0);*/
		}
	
	}

	private void launchActivity(Class<?> cls, int requestCode) {
		Intent intent = new Intent(CuteBond.this, cls);
		startActivityForResult(intent, requestCode);
	}
	
	public void launchActivity(Class<?> cls, int requestCode, int type) {
		Intent intent = new Intent(CuteBond.this, cls);
		intent.putExtra("type", type);
		startActivityForResult(intent, requestCode);
	}

	OnChildClickListener onchild = new OnChildClickListener() {

		@Override
		public boolean onChildClick(ExpandableListView parent, View view,
				int groupPosition, int childPosition, long id) {

			closeSideView();

			Item item = (Item) view.getTag();

			if (videos != null && videos.isVisible())
				videos.filterContent(item);

			if (photos != null && photos.isVisible())
				photos.filterContent(item);
			
			if (peoples != null && peoples.isVisible())
				peoples.filterContent(item);
						

			return false;
		}
	};

	@Override
	protected void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);

		if (resultCode != RESULT_OK)
			return;

		switch (reqCode) {

		case 4000:
			/*
			 * Log.e(data.getStringExtra("lang")," :::: lang ::::");
			 * Log.e(data.getStringExtra("cntry")," :::: cntry :::: ");
			 */
			videos.filterByCntryLang(data.getStringExtra("lang"),
					data.getStringExtra("cntry"));
			break;

		}
	}

	@Override
	public boolean onLongClick(View view) {
		switch (view.getId()) {
		case R.id.prv_image:
			showLFS((Item) view.getTag(), 0);
			break;

		default:
			break;
		}
		return false;
	}

	private void showLFS(Item item, int type) {
		dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.TRANSPARENT));

		View view = View.inflate(this, R.layout.likesharefollow, null);

		view.findViewById(R.id.like_public).setOnClickListener(onslf);
		view.findViewById(R.id.like_public).setOnClickListener(onslf);
		view.findViewById(R.id.share_public).setOnClickListener(onslf);
		view.findViewById(R.id.share_public).setOnClickListener(onslf);
		view.findViewById(R.id.follow_public).setOnClickListener(onslf);
		view.findViewById(R.id.follow_public).setOnClickListener(onslf);
		
		switch (type) {
		case 1:
			view.findViewById(R.id.sharetemp).setVisibility(View.GONE);
			view.findViewById(R.id.followtemp).setVisibility(View.GONE);
			break;
			
		case 2:
			view.findViewById(R.id.sharetemp).setVisibility(View.GONE);
			view.findViewById(R.id.liketemp).setVisibility(View.GONE);
			break;
			
		case 3:
			view.findViewById(R.id.liketemp).setVisibility(View.GONE);
			view.findViewById(R.id.followtemp).setVisibility(View.GONE);
			break;

		default:
			break;
		}

		dialog.setContentView(view);
		dialog.show();
	}

	OnClickListener onslf = new OnClickListener() {

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.like_public:

				break;

			case R.id.like_private:

				break;

			case R.id.share_public:

				break;

			case R.id.share_private:

				break;

			case R.id.follow_public:

				break;

			case R.id.follow_private:

				break;

			default:
				break;
			}

		}
	};
	
	

}
