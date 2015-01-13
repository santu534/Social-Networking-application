package com.creative.cutebond.adapter;

import java.util.Vector;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.creative.cutebond.R;
import com.creative.cutebond.common.Item;
import com.creative.cutebond.imageloader.ImageLoader;

public class VideosAdapter extends BaseAdapter {

	private LayoutInflater inflater = null;
	Vector<Item> vector = new Vector<Item>();
	private Context context = null;

	public ImageLoader imageLoader = null;	
	
	private int width = LayoutParams.MATCH_PARENT;
	private int height = 0;
	
	private Drawable audio = null;
	private Drawable video = null;

	public VideosAdapter(Context context, int width, int heigth) {
		this.context = context;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(context, false);		
		this.width = width;
		this.height = (int)(heigth/2.5);	//change later if required		
	}	

	public int getCount() {
		return vector.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position + 1;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		Item item = vector.get(position);

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.template_video, null);
			audio = context.getResources().getDrawable(R.drawable.audio_button);
			video = context.getResources().getDrawable(R.drawable.play_button);
		}

		String str = item.getAttribute("userName");
		TextView title = (TextView) convertView.findViewById(R.id.template_usr);
		title.setText(str);

		str = item.getAttribute("uploadDate");
		title = (TextView) convertView.findViewById(R.id.template_td);
		title.setText(str);
		
		str = item.getAttribute("videoTitle");
		title = (TextView) convertView.findViewById(R.id.temp_vid_title);
		title.setText(str);
		
		str = item.getAttribute("comments_count");
		title = (TextView) convertView.findViewById(R.id.temp_vid_com);
		title.setText(str);
		
		str = item.getAttribute("like_status");
		Button but = (Button)convertView.findViewById(R.id.template_like);
		but.setText(item.getAttribute("likes_count"));  
		but.setCompoundDrawablePadding(5);
		but.setCompoundDrawablesWithIntrinsicBounds(R.drawable.play_disabled_videos, 0, 0, 0);
		if(str.equalsIgnoreCase("1")) {
			but.setCompoundDrawablesWithIntrinsicBounds(R.drawable.play_enabled_videos, 0, 0, 0);
		} 

		String imageUri = "";					
		
		final ImageView user_icon = (ImageView) convertView.findViewById(R.id.template_icon);
		String usericon = item.getAttribute("userProfilepic");
		if(usericon.length() > 0) {
			usericon = "http://www.cutebond.com/webapp/userimages/"+usericon;
			imageLoader.DisplayImage(usericon, user_icon, true);
		}
		
		final ImageView type_icon = (ImageView) convertView.findViewById(R.id.temp_vid_def);		
		String typestr = item.getAttribute("audio_image");		
		if(typestr.equalsIgnoreCase("null")) {
						
			type_icon.setImageDrawable(video);
			imageUri = item.getAttribute("fileId");
			
			if(imageUri.length() > 0 && imageUri.contains("?v="))
				imageUri = imageUri.substring(imageUri.indexOf("?v=")+3, imageUri.length());
			else
				imageUri = imageUri.substring(imageUri.lastIndexOf("/")+1, imageUri.length());
			
				imageUri = "http://img.youtube.com/vi/"+imageUri+"/0.jpg";			
			
		} else {
			type_icon.setImageDrawable(audio);
			imageUri = "http://cutebond.com/webapp/audio/images/"+item.getAttribute("audio_image");
		}
		
		ImageView icon = (ImageView) convertView.findViewById(R.id.temp_vid_icon);		
		icon.getLayoutParams().height = height;
		icon.getLayoutParams().width = width;
		
		imageLoader.DisplayImage(imageUri, icon);
		
		convertView.setTag(item);
		
		convertView.setOnClickListener((OnClickListener)context);
			
		return convertView;
	}

	public void setItems(Vector<Item> vector) {
		this.vector = vector;
	}

	public Vector<Item> getItems() {
		return this.vector;
	}

	public void clear() {
		vector.clear();
		imageLoader.adapterClear();
	}

	
	public void release() {
		vector.clear();
		imageLoader.clearCache();
		vector = null;
		imageLoader = null;
	}

}