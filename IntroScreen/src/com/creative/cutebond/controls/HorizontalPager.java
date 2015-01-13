package com.creative.cutebond.controls;

import java.util.Vector;

import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.creative.cutebond.CuteBond;
import com.creative.cutebond.R;
import com.creative.cutebond.common.Item;
import com.creative.cutebond.imageloader.ImageLoader;

public class HorizontalPager implements OnTouchListener {

	private Context context = null;	
	private View view = null;
	private ScrollView parent = null;	
	private String name;
	
	public ImageLoader imageLoader = null;
	
	private LinearLayout hp_ll = null;
	
	public String[] multicolor = null;

	public HorizontalPager(Context context, ScrollView parent, String name, int drawable) {
		imageLoader = new ImageLoader(context, false);
		this.context = context;
		this.parent = parent;
		this.name = name;
		multicolor = ((CuteBond)context).multicolor;
		init(drawable);
	}

	private void init(int drawable) {

		view = View.inflate(context, R.layout.horizontalpager_template, null);
		hp_ll = (LinearLayout)view.findViewById(R.id.hp_ll);							
		
		TextView txtView  = (TextView)view.findViewById(R.id.hp_tempt_txt);
		txtView.setText(name);
		txtView.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawable, 0);

	}

	public View getView() {
		return view;
	}

	public void onDestroy() {
		//hent.onDestroy();
	}

	public void setItems(final Vector<Item> results, int id) {

		view.setTag(results);
		
		if(results == null)
			return;

		//if (results != null && results.size() > 0) {
			
			switch (id) {
			case R.id.template_layout_cmts:
				createCommentsView(results);
				break;
				
			case R.id.template_layout_tag:
				createTagsView(results);
				break;
				
			case R.id.template_layout_likes:
				hp_ll.setOrientation(LinearLayout.HORIZONTAL);
				createLikesView(results);
				break;

			default:
				break;
			}
			
			return;
		//}

	}
	
	private void createTagsView(Vector<Item> items) {
		
		for(int i = 0; i < items.size(); i++) {
			
			if(i == 2)
				break;
			
			Item item = items.get(i);
			View convertView = View.inflate(context, R.layout.template_tag, null);	
			String str = item.getAttribute("userName");
			TextView title = (TextView) convertView.findViewById(R.id.template_user_tag);
			title.setText(str);			
			
			str = item.getAttribute("designation");
			title = (TextView) convertView.findViewById(R.id.template_msg_tag);
			title.setText(str);		
			
			str = item.getAttribute("like_status");
			Button but = (Button)convertView.findViewById(R.id.template_like);
			but.setText(item.getAttribute("like_count"));
			but.setCompoundDrawablePadding(5);
			but.setCompoundDrawablesWithIntrinsicBounds(R.drawable.play_disabled_videos, 0, 0, 0);
			if(str.equalsIgnoreCase("1")) {
				but.setCompoundDrawablesWithIntrinsicBounds(R.drawable.play_enabled_videos, 0, 0, 0);
			}

			if(item.getAttribute("userGender").equalsIgnoreCase("female"))
				((ImageView) convertView.findViewById(R.id.template_cht)).setImageResource(R.drawable.ap_f_message);
			else
				((ImageView) convertView.findViewById(R.id.template_cht)).setImageResource(R.drawable.ap_m_message);
			
			final ImageView icon = (ImageView) convertView.findViewById(R.id.template_icon);
			icon.setTag(item);
			
			String imageUri = item.getAttribute("userProfilepic");
								
			imageUri = "http://www.cutebond.com/webapp/userimages/"+imageUri;	
			
			imageLoader.DisplayImage(imageUri, icon);				
			
			convertView.setTag(item);
			
			if (multicolor != null && multicolor.length > 0) {
				int colorPos = i % multicolor.length;
				
				//icon.setImageResource(resId)
				icon.setBackgroundColor(Color
						.parseColor(multicolor[colorPos]));
			}
			
			hp_ll.addView(convertView);
			
		}
	}
	
	private void createCommentsView(Vector<Item> items) {						
		
		for(int i = 0; i < items.size(); i++) {
			
			if(i == 2)
				break;
			
			Item item = items.get(i);
			View convertView = View.inflate(context, R.layout.template_commets, null);	
			String str = item.getAttribute("userName");
			TextView title = (TextView) convertView.findViewById(R.id.cmt_usr);
			title.setText(str);			
			
			str = item.getAttribute("comment");
			title = (TextView) convertView.findViewById(R.id.cmt_txt);
			title.setText(str);				
			
			final ImageView icon = (ImageView) convertView.findViewById(R.id.cmt_icon);
			icon.setTag(item);
			
			String imageUri = item.getAttribute("userProfilepic");
								
			imageUri = "http://www.cutebond.com/webapp/userimages/"+imageUri;	
			
			imageLoader.DisplayImage(imageUri, icon);				
			
			convertView.setTag(item);
			
			if (multicolor != null && multicolor.length > 0) {
				int colorPos = i % multicolor.length;
				
				//icon.setImageResource(resId)
				icon.setBackgroundColor(Color
						.parseColor(multicolor[colorPos]));
			}
			
			hp_ll.addView(convertView);							
		}
		
		View convertView = View.inflate(context, R.layout.template_search, null);
		convertView.findViewById(R.id.search_btn).setOnClickListener((CuteBond)context);
		hp_ll.addView(convertView);
		
		if(items != null && items.size() > 2) {
			view.findViewById(R.id.showmore).setTag(items);
			view.findViewById(R.id.showmore).setVisibility(View.VISIBLE);
			view.findViewById(R.id.showmore).setOnClickListener((CuteBond)context);
		}			
		
	}
	
	private void createLikesView(Vector<Item> items) {
		
		View defaultview = View.inflate(context, R.layout.template_likes, null);															
		final ImageView icondefault = (ImageView) defaultview.findViewById(R.id.template_icon);
		icondefault.setImageResource(R.drawable.likes_like);
		hp_ll.addView(defaultview);
		
		for(int i = 0; i < items.size(); i++) {						
			
			Item item = items.get(i);
			View convertView = View.inflate(context, R.layout.template_likes, null);													
			
			final ImageView icon = (ImageView) convertView.findViewById(R.id.template_icon);
			icon.setTag(item);
			
			String imageUri = item.getAttribute("userProfilepic");
								
			imageUri = "http://www.cutebond.com/webapp/userimages/"+imageUri;	
			
			imageLoader.DisplayImage(imageUri, icon);				
			
			convertView.setTag(item);
			
			if (multicolor != null && multicolor.length > 0) {
				int colorPos = i % multicolor.length;
				
				//icon.setImageResource(resId)
				icon.setBackgroundColor(Color
						.parseColor(multicolor[colorPos]));
			}
			
			hp_ll.addView(convertView);							
			
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_MOVE && parent != null) {
			parent.requestDisallowInterceptTouchEvent(true);
		}
		return false;
	}

}
