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
import android.widget.ImageView;
import android.widget.TextView;

import com.creative.cutebond.R;
import com.creative.cutebond.common.Item;
import com.creative.cutebond.imageloader.ImageLoader;

public class MyHubAdapter extends BaseAdapter {

	private LayoutInflater inflater = null;
	Vector<Item> vector = new Vector<Item>();
	private Context context = null;

	public ImageLoader imageLoader = null;
	
	private int width = LayoutParams.MATCH_PARENT;
	private int height = 0;
	
	private Drawable audio = null;
	private Drawable video = null;

	public MyHubAdapter(Context context, int width, int heigth) {
		this.context = context;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(context, false);
		this.width = width;
		this.height = heigth;
		
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
		
		Item mainitem = vector.get(position);
		
		Item item = (Item)mainitem.getAttribValue("wallid");

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.template_myhub, null);
			audio = context.getResources().getDrawable(R.drawable.audio_button);
			video = context.getResources().getDrawable(R.drawable.play_button);
		}

		String str = item.getAttribute("userName");
		TextView title = (TextView) convertView.findViewById(R.id.template_usr);
		title.setText(str);

		str = item.getAttribute("date");
		title = (TextView) convertView.findViewById(R.id.template_td);
		title.setText(str);								
				
		str = item.getAttribute("message");
		title = (TextView) convertView.findViewById(R.id.tp_mh_txt);
		title.setText(str);
		
		//main image
		ImageView icon =  (ImageView)convertView.findViewById(R.id.temp_myhub_icon);		
		icon.setVisibility(View.GONE);
		
		convertView.findViewById(R.id.temp_myhub_def).setVisibility(View.GONE);				
		((ImageView) convertView.findViewById(R.id.temp_myhub_def)).setImageResource(0);
		
		if(item.getAttribute("image").length() > 0 ) {
			
			//final ImageView icon = (ImageView) convertView.findViewById(R.id.temp_myhub_icon);
			icon.setVisibility(View.VISIBLE);
			icon.setTag(item);
			icon.getLayoutParams().height = height/3;
			icon.getLayoutParams().width = width;
			String imageUri = "";
			
			imageUri = "http://cutebond.com/webapp/wallimages/"+item.getAttribute("image");
			
			imageLoader.DisplayImage(imageUri, icon);
		} 

		if(item.getAttribute("url").length() > 0) {		//"<null>"	
			
			((ImageView) convertView.findViewById(R.id.temp_myhub_def)).setImageResource(R.drawable.play_button);
			
			convertView.findViewById(R.id.temp_myhub_def).setVisibility(View.VISIBLE);
			//final ImageView icon = (ImageView) convertView.findViewById(R.id.temp_myhub_icon);
			icon.setVisibility(View.VISIBLE);
			icon.setTag(item);
			icon.getLayoutParams().height = height/3;
			icon.getLayoutParams().width = width;
			
			String imageUri = item.getAttribute("url");
			
			//String videoId = item.getAttribute("url");
			
			/*if(videoId.length() > 0 && videoId.contains("v="))
				videoId = videoId.substring(videoId.indexOf("v=")+2, videoId.length());*/
			
			if(imageUri.length() > 0 && imageUri.contains("?v="))
				imageUri = imageUri.substring(imageUri.indexOf("?v=")+3, imageUri.length());
			else
				imageUri = imageUri.substring(imageUri.lastIndexOf("/")+1, imageUri.length());
			
			imageUri = "http://img.youtube.com/vi/"+imageUri+"/0.jpg";
			
			imageLoader.DisplayImage(imageUri, icon);
			
		}
		
		ImageView user_icon = (ImageView) convertView.findViewById(R.id.template_icon);
		String usericon = "http://www.cutebond.com/webapp/userimages/"+item.getAttribute("userProfilepic");					
		imageLoader.DisplayImage(usericon, user_icon, true);		
		
		ImageView imageView = (ImageView) convertView.findViewById(R.id.myhub_like);
		if(item.getAttribute("like_status").equalsIgnoreCase("1"))
			imageView.setImageResource(R.drawable.like_enabled);
		else
			imageView.setImageResource(R.drawable.like_disabled);
		
		imageView = (ImageView) convertView.findViewById(R.id.myhub_follow);
		if(item.getAttribute("share_status").equalsIgnoreCase("1"))
			imageView.setImageResource(R.drawable.share_hub);
		else
			imageView.setImageResource(R.drawable.share_hub);
						
		//imageView = (ImageView) convertView.findViewById(R.id.myhub_comment);
						
		/*String imageUri = item.getAttribute("fileId");
		if(imageUri.length() > 0)
			imageUri = imageUri.substring(imageUri.indexOf("?v=")+3, imageUri.length());

		imageUri = "http://img.youtube.com/vi/"+imageUri+"/0.jpg";
*/		//imageLoader.DisplayImage(imageUri, icon);
		
		/*final ImageView type_icon = (ImageView) convertView.findViewById(R.id.temp_myhub_def);			
		String typestr = item.getAttribute("audio_image");		
		if(typestr.equalsIgnoreCase("null")){
			type_icon.setImageDrawable(video);
		} else {
			type_icon.setImageDrawable(audio);
		}*/
				
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
	}

	
	public void release() {
		vector.clear();
		imageLoader.clearCache();
		vector = null;
		imageLoader = null;
	}

}