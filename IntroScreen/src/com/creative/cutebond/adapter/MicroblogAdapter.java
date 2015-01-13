package com.creative.cutebond.adapter;

import java.util.Vector;

import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.creative.cutebond.CuteBond;
import com.creative.cutebond.R;
import com.creative.cutebond.common.Item;
import com.creative.cutebond.imageloader.ImageLoader;

public class MicroblogAdapter extends BaseAdapter {

	private LayoutInflater inflater = null;
	Vector<Item> vector = new Vector<Item>();
	private Context context = null;

	public ImageLoader imageLoader;	
	
	private Drawable knowline = null; 
	
	public MicroblogAdapter(Context context) {
		this.context = context;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(context, false);
		
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
		
		try {
									
		Item item = vector.get(position);

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.template_microblog, null);
			
			knowline = context.getResources().getDrawable(R.drawable.follow_hub);
			//like = context.getResources().getDrawable(R.drawable.like_enabled);
			//unlike = context.getResources().getDrawable(R.drawable.like_disabled);
		}

		String str = item.getAttribute("message");
		TextView title = (TextView) convertView.findViewById(R.id.mb_text);
		title.setText(str);
		
		//commemts_cnt
		
		//like_status
		String like_status = item.getAttribute("like_status");
		
		JSONObject like_status_obj = new JSONObject(like_status);
						
		((Button)convertView.findViewById(R.id.mb_like_btn)).setText(like_status_obj.optString("blog_likes_count"));
		
		((Button) convertView.findViewById(R.id.mb_like_btn)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_disabled, 0, 0, 0);
		if(!like_status_obj.optString("liked").equalsIgnoreCase("0")) {
			((Button) convertView.findViewById(R.id.mb_like_btn)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_enabled, 0, 0, 0);
		}		
		
		((Button)convertView.findViewById(R.id.mb_cmt_btn)).setText(item.getAttribute("commemts_cnt"));
		
		
		String follow_data = item.getAttribute("follow_data");
		JSONObject follow_data_obj = new JSONObject(follow_data);
		
		Button button = (Button)convertView.findViewById(R.id.mb_reshare_btn); 
		
		if(follow_data_obj.optString("total_followers").equalsIgnoreCase("0"))
			button.setVisibility(View.GONE);
		else {
			button.setVisibility(View.VISIBLE);
			button.setText("by " +follow_data_obj.optString("total_followers") +" of your friends");			
		}
		
		  button = (Button)convertView.findViewById(R.id.mb_follow_btn); 
			   button.setBackgroundResource(R.drawable.follow_hub);
			   button.setText("Follow");
			   button.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			   button.setCompoundDrawablePadding(0);
				
			   if(!follow_data_obj.optString("follow_status").equalsIgnoreCase("0")) {
				   button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.check, 0, 0, 0);
				   button.setCompoundDrawablePadding(5);
				   button.setBackgroundColor(Color.TRANSPARENT);
				   button.setText("Following");
			   }
		
		final ImageView icon = (ImageView) convertView.findViewById(R.id.mb_user_image);
		icon.setTag(item);				

		String imageUri = item.getAttribute("userProfilepic");
		
		imageUri = "http://www.cutebond.com/webapp/userimages/"+imageUri;

		if (imageUri.startsWith("http://")) {
			imageLoader.DisplayImage(imageUri, icon);
		}	
		
		convertView.setTag(item);
		convertView.setOnClickListener((CuteBond)context);
		
		} catch (Exception e) {
			e.printStackTrace();
		}

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