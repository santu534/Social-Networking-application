package com.creative.cutebond.fragments;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.creative.cutebond.CuteBond;
import com.creative.cutebond.R;
import com.creative.cutebond.callbacks.IItemHandler;
import com.creative.cutebond.callbacks.PlayerCallback;
import com.creative.cutebond.common.Constants;
import com.creative.cutebond.common.Item;
import com.creative.cutebond.controls.HorizontalPager;
import com.creative.cutebond.controls.MediaControl;
import com.creative.cutebond.imageloader.ImageLoader;
import com.creative.cutebond.tasks.HTTPBackgroundTask;
import com.creative.cutebond.utils.Utils;

public class Detail extends Fragment implements IItemHandler, PlayerCallback {

	private CuteBond cutebond = null;
	private Item item = null;
	public ImageLoader imageLoader = null;
	private Drawable audio = null;
	private Drawable video = null;

	private ImageView type_icon = null;
	private ImageView icon = null;
	
	private MediaControl mediaControl = null;		

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		cutebond = (CuteBond) activity;
		imageLoader = new ImageLoader(cutebond, false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View layout = null;
		
		if(cutebond.getFromStore("fromtype").equalsIgnoreCase("mb")) {
			layout = inflater.inflate(R.layout.template_microblogdetail, container, false);
			cutebond.addToStore("fromtype", "");
		} else {
			layout = inflater.inflate(R.layout.template_detail, container, false);
		}
		
		//View layout = inflater.inflate(R.layout.template_detail, container, false);				
		
		return layout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initViews();
		showDetails();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onDestroyView() {
		audio = null;
		video = null;

		if(type_icon != null) 
			type_icon.setImageDrawable(null);
		
		type_icon = null;

		if(icon != null) {
			icon.setImageBitmap(null);
			icon.setImageDrawable(null);	
		}		
		
		icon = null;
		
		if(mediaControl != null)
			mediaControl.deInit();
		
		mediaControl = null;

		super.onDestroyView();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {		
		super.onHiddenChanged(hidden);		
	}

	public boolean back() {
		return false;
	}

	private void initViews() {
		
		getView().findViewById(R.id.back_arrow).setOnClickListener(cutebond);
		
		if(getView().findViewById(R.id.template_detail) == null)
			return;
		
		audio = cutebond.getResources().getDrawable(R.drawable.audio_button);
		video = cutebond.getResources().getDrawable(R.drawable.play_button);
		
		icon = (ImageView) getView().findViewById(R.id.prv_image);
		//icon.setOnClickListener(cutebond);
		icon.setOnLongClickListener(cutebond);
	}

	public void showDetails() {

		String imageUri = "";
		
		item = deserializeData();

		if(icon != null) {
			icon.setTag(item);

			type_icon = (ImageView) getView().findViewById(R.id.temp_vid_def);
			type_icon.setOnClickListener(cutebond);
			type_icon.setTag(item);	
		}		
		
		String typestr = item.getAttribute("audio_image");
		
		if (item.getAttribute("fromtype").equalsIgnoreCase("v")) {
			if (typestr.equalsIgnoreCase("null")) {

				type_icon.setImageDrawable(video);
				imageUri = item.getAttribute("fileId");

				/*if (imageUri.length() > 0 )
					imageUri = imageUri.substring(imageUri.indexOf("?v=") + 3,imageUri.length());*/
				
				if(imageUri.length() > 0 && imageUri.contains("?v="))
					imageUri = imageUri.substring(imageUri.indexOf("?v=")+3, imageUri.length());
				else
					imageUri = imageUri.substring(imageUri.lastIndexOf("/")+1, imageUri.length());

				imageUri = "http://img.youtube.com/vi/" + imageUri + "/0.jpg";
				
				if(item.getAttribute("like_status").equalsIgnoreCase("0"))
				((ImageView)getView().findViewById(R.id.prv_like_header)).setImageResource(R.drawable.like_disbled_inner);

			} else {

				type_icon.setImageDrawable(audio);
				imageUri = "http://cutebond.com/webapp/audio/images/"
						+ item.getAttribute("audio_image");

			}
			
			imageLoader.DisplayImage(imageUri, icon);
			
		} else if (item.getAttribute("fromtype").equalsIgnoreCase("p")) {

			int width = getWidthResolution();

			float new_width = width;

			String new_width_txt = item.getAttribute("new_width");
			if (new_width_txt.length() > 0) {
				new_width = Float.parseFloat(new_width_txt);
			}

			float new_height = 100.0f;
			String new_height_txt = item.getAttribute("new_height");
			if (new_height_txt.length() > 0) {
				new_height = Float.parseFloat(new_height_txt);
			}

			int height = (int) new_height;

			height = (int) ((width * new_height) / new_width);

			Log.e("height :: ", height + "");

			icon.setOnClickListener(null);
			icon.getLayoutParams().height = height;
			imageUri = "http://www.cutebond.com/webapp/uploads/"
					+ item.getAttribute("Photo");
			
			imageLoader.DisplayImage(imageUri, icon);
			
		} else if (item.getAttribute("fromtype").equalsIgnoreCase("mh")) {

		} else if (item.getAttribute("fromtype").equalsIgnoreCase("mb")) {
			
		}

		// icon = (ImageView)getView().findViewById(R.id.prv_image);
		//imageLoader.DisplayImage(imageUri, icon);

		getOtherDetails();
	}

	/**
	 * deserializeData -
	 * 
	 * @return Item
	 */
	private Item deserializeData() {
		Item item = null;
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = cutebond.openFileInput(Constants.ITEM);
			ObjectInputStream ois = new ObjectInputStream(fileInputStream);
			item = (Item) ois.readObject();
			ois.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return item;
	}

	private void getOtherDetails() {

		String url = "";
		HTTPBackgroundTask task = null;
		
		ImageView template_detail_icon = (ImageView) getView().findViewById(R.id.template_detail_icon);		
		String usericon = "http://www.cutebond.com/webapp/userimages/"+item.getAttribute("userProfilepic");
		imageLoader.DisplayImage(usericon, template_detail_icon, true);

		if (item.getAttribute("fromtype").equalsIgnoreCase("v")) {

			url = cutebond.getPropertyValue("v_preview");
			url = url.replace("(UID)", cutebond.getFromStore("userid"));
			url = url.replace("(VID)", item.getAttribute("videoId"));

			task = new HTTPBackgroundTask(this, cutebond, 7, 1);

		} else if (item.getAttribute("fromtype").equalsIgnoreCase("p")) {

			url = cutebond.getPropertyValue("p_preview");
			url = url.replace("(UID)", cutebond.getFromStore("userid"));
			url = url.replace("(PID)", item.getAttribute("photoId"));

			task = new HTTPBackgroundTask(this, cutebond, 9, 1);

		} else if (item.getAttribute("fromtype").equalsIgnoreCase("mh")) {

			url = cutebond.getPropertyValue("mh_preview");
			url = url.replace("(UID)", cutebond.getFromStore("userid"));
			url = url.replace("(WID)", item.getAttribute("wallId"));
			Log.e("09090909090909",url);
			task = new HTTPBackgroundTask(this, cutebond, 9, 1);
			
		} else if (item.getAttribute("fromtype").equalsIgnoreCase("mb")) {

			url = cutebond.getPropertyValue("mb_preview");			
			url = url.replace("(UID)", item.getAttribute("userId"));
			url = url.replace("(WID)", item.getAttribute("wallId"));
			url = url.replace("(SID)", cutebond.getFromStore("userid"));

			Log.e("09090909090909",url);
			
			task = new HTTPBackgroundTask(this, cutebond, 16, 1);
		}

		Utils.showProgress(getString(R.string.pw), cutebond);

		task.execute(url);
	}

	@Override
	public void onFinish(Object results, int requestType) {

		Utils.dismissProgress();

		switch (requestType) {
		case 1:

			if (results == null)
				return;

			Item items = (Item) results;
			updateDetails(items);

			break;

		case 2:

			if (results == null)
				return;

			Vector<Item> vditems = (Vector<Item>) results;
			String rtsp = vditems.get(0).getAttribute("url");

			Intent intent = new Intent();
			intent.setAction(android.content.Intent.ACTION_VIEW);
			intent.setData(Uri.parse(rtsp));
			startActivity(intent);

			break;

		default:
			break;
		}

	}

	@Override
	public void onError(String errorCode, int requestType) {
		Utils.dismissProgress();
		cutebond.showToast(errorCode);

	}

	private void updateDetails(Item items) {

		if (this.item.getAttribute("fromtype").equalsIgnoreCase("v")) {

			Item item = (Item) items.getAttribValue("video_results");

			((TextView) getView().findViewById(R.id.prv_posted_hr))
					.setText(getString(R.string.postedon)+" "+item.getAttribute("uploadDate"));

			((TextView) getView().findViewById(R.id.prv_name)).setText(item
					.getAttribute("userName"));
			((TextView) getView().findViewById(R.id.prv_time)).setText(item
					.getAttribute("videoCategory"));
			((TextView) getView().findViewById(R.id.prv_title)).setText(item
					.getAttribute("videoTitle"));
			((TextView) getView().findViewById(R.id.prv_desc)).setText(item
					.getAttribute("videoDescription"));

			Item vlc = (Item) items.getAttribValue("video_likes_count");
			((TextView) getView().findViewById(R.id.prv_like_hrt)).setText(vlc
					.getAttribute("video_likes_count"));
			getView().findViewById(R.id.prv_like_hrt).setOnClickListener(cutebond);			

			LinearLayout linear = (LinearLayout) getView().findViewById(
					R.id.prv_mlayout);

			Vector<Item> vt = (Vector<Item>) items
					.getAttribValue("video_tagged");
			if (vt.size() > 0) {
				HorizontalPager pager = new HorizontalPager(cutebond,
						(ScrollView) getView().findViewById(
								R.id.template_detail), vt.size() + " Tags",
						R.drawable.details_tag_icon);
				pager.setItems(vt, R.id.template_layout_tag);
				linear.addView(pager.getView());
			}

			Vector<Item> vc = (Vector<Item>) items
					.getAttribValue("video_comments");
			if (vc.size() > 0) {
				HorizontalPager pager = new HorizontalPager(cutebond,
						(ScrollView) getView().findViewById(
								R.id.template_detail), vc.size() + " Comments",
						0);
				pager.setItems(vc, R.id.template_layout_cmts);
				linear.addView(pager.getView());
			} else {
				HorizontalPager pager = new HorizontalPager(cutebond,
						(ScrollView) getView().findViewById(
								R.id.template_detail), vc.size() + " Comments",
						0);
				pager.setItems(vc, R.id.template_layout_cmts);
				linear.addView(pager.getView());
			}

			Vector<Item> vlp = (Vector<Item>) items
					.getAttribValue("likes_videos");
			if (vlp.size() > 0) {
				HorizontalPager pager = new HorizontalPager(cutebond,
						(ScrollView) getView().findViewById(
								R.id.template_detail), vlp.size() + "",
						R.drawable.like_disbled_inner);
				pager.setItems(vlp, R.id.template_layout_likes);
				linear.addView(pager.getView());
			}

		} else if (item.getAttribute("fromtype").equalsIgnoreCase("p")) {

			Item item = (Item) items.getAttribValue("photo_results");

			((TextView) getView().findViewById(R.id.prv_posted_hr))
					.setText(getString(R.string.postedon)+" "+item.getAttribute("uploaddate"));

			((TextView) getView().findViewById(R.id.prv_name)).setText(item
					.getAttribute("userName"));
			((TextView) getView().findViewById(R.id.prv_time)).setText(item
					.getAttribute("PhotoCategory"));
			((TextView) getView().findViewById(R.id.prv_title)).setText(item
					.getAttribute("PhotoTitle"));
			((TextView) getView().findViewById(R.id.prv_desc)).setText(item
					.getAttribute("PhotoDescription"));

			Item vlc = (Item) items.getAttribValue("photos_likes_count");
			((TextView) getView().findViewById(R.id.prv_like_hrt)).setText(vlc
					.getAttribute("photo_likes_count"));
			getView().findViewById(R.id.prv_like_hrt).setOnClickListener(cutebond);

			LinearLayout linear = (LinearLayout) getView().findViewById(
					R.id.prv_mlayout);

			Vector<Item> vc = (Vector<Item>) items
					.getAttribValue("photos_comments");
			if (vc.size() > 0) {
				HorizontalPager pager = new HorizontalPager(cutebond,
						(ScrollView) getView().findViewById(
								R.id.template_detail), vc.size() + " Comments",
						0);
				pager.setItems(vc, R.id.template_layout_cmts);
				linear.addView(pager.getView());
			} else {
				HorizontalPager pager = new HorizontalPager(cutebond,
						(ScrollView) getView().findViewById(
								R.id.template_detail), vc.size() + " Comments",
						0);
				pager.setItems(vc, R.id.template_layout_cmts);
				linear.addView(pager.getView());
			}

			Vector<Item> vlp = (Vector<Item>) items
					.getAttribValue("likes_photos");
			if (vlp.size() > 0) {
				HorizontalPager pager = new HorizontalPager(cutebond,
						(ScrollView) getView().findViewById(
								R.id.template_detail), vlp.size() + "",
						R.drawable.like_disbled_inner);
				pager.setItems(vlp, R.id.template_layout_likes);
				linear.addView(pager.getView());
			}
		} else if (item.getAttribute("fromtype").equalsIgnoreCase("mh")) {
			
		} else if (item.getAttribute("fromtype").equalsIgnoreCase("mb")) {
			
			Item item = (Item) items.getAttribValue("microblog_det");

			((TextView) getView().findViewById(R.id.prv_posted_hr))
					.setText(getString(R.string.postedon)+" "+item.getAttribute("date"));
			
			((TextView) getView().findViewById(R.id.mb_desc))
			.setText(item.getAttribute("message"));												

			((TextView) getView().findViewById(R.id.prv_name)).setText(item
					.getAttribute("userName"));
			((TextView) getView().findViewById(R.id.prv_time)).setText(item
					.getAttribute("videoCategory"));								
			
			Item vlc = (Item) items.getAttribValue("hub_likes_count");
			((TextView) getView().findViewById(R.id.prv_like_hrt)).setText(vlc
					.getAttribute("blog_likes_count"));
			getView().findViewById(R.id.prv_like_hrt).setOnClickListener(cutebond);
			
			Item follow_data = (Item) items.getAttribValue("follow_data");
			((TextView) getView().findViewById(R.id.prv_follow_img)).setText(follow_data.getAttribute("total_followers"));									

			LinearLayout linear = (LinearLayout) getView().findViewById(
					R.id.prv_mlayout);

			Vector<Item> vc = (Vector<Item>) items.getAttribValue("blog_comments");
			if (vc.size() > 0) {
				HorizontalPager pager = new HorizontalPager(cutebond,
						(ScrollView) getView().findViewById(
								R.id.template_detail), vc.size() + " Comments", 0);
				pager.setItems(vc, R.id.template_layout_cmts);
				linear.addView(pager.getView());
			} else {
				HorizontalPager pager = new HorizontalPager(cutebond,
						(ScrollView) getView().findViewById(
								R.id.template_detail), vc.size() + " Comments",
						0);
				pager.setItems(vc, R.id.template_layout_cmts);
				linear.addView(pager.getView());
			}

			Vector<Item> vlp = (Vector<Item>) items.getAttribValue("likes_photos");
			if (vlp.size() > 0) {
				HorizontalPager pager = new HorizontalPager(cutebond,
						(ScrollView) getView().findViewById(
								R.id.template_detail), vlp.size() + "",
						R.drawable.like_disbled_inner);
				pager.setItems(vlp, R.id.template_layout_likes);
				linear.addView(pager.getView());
			}			
			
		}
	}

	private int getWidthResolution() {
		WindowManager windowManager = (WindowManager) cutebond
				.getSystemService(Context.WINDOW_SERVICE);
		// int height = windowManager.getDefaultDisplay().getHeight();
		int width = windowManager.getDefaultDisplay().getWidth();
		return width;
	}

	public void playVideoFile(String videoId) {
		
		if(videoId.length() > 0 && videoId.contains("?v="))
			videoId = videoId.substring(videoId.indexOf("?v=") + 3, videoId.length());
		else
			videoId = videoId.substring(videoId.lastIndexOf("/")+1, videoId.length());
		
		//videoId = videoId.substring(videoId.indexOf("?v=") + 3, videoId.length());
		videoId = "http://gdata.youtube.com/feeds/mobile/videos/" + videoId+ "?alt=json";

		Utils.showProgress(getString(R.string.pw), cutebond);

		HTTPBackgroundTask task = new HTTPBackgroundTask(this, cutebond, 15, 2);
		task.execute(videoId);
	}
	
	public void playAudioFile(String audioId, String audioName, View view) {
		
		if (mediaControl != null) {
			if (mediaControl.getFileState() == 1) {
				cutebond.showToast(R.string.pwlc);
				return;
			} else if (mediaControl.getFileState() == 3) {
				mediaControl.playSameFile();
				((ImageView)getView().findViewById(R.id.temp_vid_def)).setImageResource(R.drawable.audio_pause);
				return;
			} else if (mediaControl.isPlaying() == 0) {
				mediaControl.handleCommand(Constants.ON_PAUSE);
				((ImageView)getView().findViewById(R.id.temp_vid_def)).setImageResource(R.drawable.audio_play);
				return;
			} else if (mediaControl.isPlaying() == -1) {
				mediaControl.handleCommand(Constants.ON_PLAY);
				((ImageView)getView().findViewById(R.id.temp_vid_def)).setImageResource(R.drawable.audio_pause);
				return;
			}
			
			return;
		}				
		
		mediaControl = new MediaControl(cutebond);
		ProgressBar progressBar = new ProgressBar(cutebond);
		mediaControl.init(this, progressBar);
		mediaControl.playFile(audioId, audioName);
	}		

	@Override
	public void onMediaStart() {
		// TODO Auto-generated method stub
		((ImageView)getView().findViewById(R.id.temp_vid_def)).setImageResource(R.drawable.audio_pause);
	}

	@Override
	public void onMediaFinish() {
		// TODO Auto-generated method stub
		((ImageView)getView().findViewById(R.id.temp_vid_def)).setImageResource(R.drawable.audio_play);
	}

	@Override
	public void onMediaError(String errorData) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onDestroy() {		
		if(mediaControl != null)
			mediaControl.deInit();		
		super.onDestroy();
	}
	
	@Override
	public void onDetach() {		
		super.onDetach();
	}
	
	public void stopMedia() {
		if(mediaControl != null)
			mediaControl.deInit();
	}

}