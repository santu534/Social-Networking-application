package com.creative.cutebond.adapter;

import java.util.Vector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.creative.cutebond.R;
import com.creative.cutebond.common.Item;
import com.creative.cutebond.imageloader.ImageLoader;

public class CommentsAdapter extends BaseAdapter {

	private LayoutInflater inflater = null;
	Vector<Item> vector = new Vector<Item>();
	private Context context = null;

	public ImageLoader imageLoader = null;
	
	public CommentsAdapter(Context context) {
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
		Item item = vector.get(position);

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.template_commets, null);
		}

		String str = item.getAttribute("userName");
		TextView title = (TextView) convertView.findViewById(R.id.cmt_usr);
		title.setText(str);

		/*str = item.getAttribute("date");
		title = (TextView) convertView.findViewById(R.id.template_td);
		title.setText(str);*/
		
		str = item.getAttribute("comment");
		title = (TextView) convertView.findViewById(R.id.cmt_txt);
		title.setText(str);				
		
		final ImageView icon = (ImageView) convertView.findViewById(R.id.cmt_icon);
		icon.setTag(item);
		
		String imageUri = item.getAttribute("userProfilepic");
							
		imageUri = "http://www.cutebond.com/webapp/userimages/"+imageUri;	
		
		imageLoader.DisplayImage(imageUri, icon);				
		
		convertView.setTag(item);
		
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