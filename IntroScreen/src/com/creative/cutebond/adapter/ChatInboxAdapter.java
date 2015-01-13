package com.creative.cutebond.adapter;

import java.util.Vector;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.creative.cutebond.R;
import com.creative.cutebond.common.Item;
import com.creative.cutebond.imageloader.ImageLoader;

public class ChatInboxAdapter extends BaseAdapter {

	private LayoutInflater inflater = null;
	private Vector<Item> vector = new Vector<Item>();
	private Context context = null;
	public ImageLoader imageLoader = null;
	private String[] multicolor = null;

	public ChatInboxAdapter(Context context, String[] multicolor) {
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(context, false);
		this.multicolor = multicolor;
		
		this.context = context;
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
			convertView = inflater.inflate(R.layout.template_ob, null);			
		}

		String val = item.getAttribute("userName");
		TextView text = (TextView) convertView.findViewById(R.id.template_title);		
		text.setText(val);
		
		val = item.getAttribute("message");
		text = (TextView) convertView.findViewById(R.id.template_ctype);		
		text.setText(val);				
		
		ImageView icon = (ImageView) convertView.findViewById(R.id.template_icon);		
		imageLoader.DisplayImage("http://www.cutebond.com/webapp/userimages/"+item.getAttribute("userProfilepic"), icon, true);
		
		/*if (multicolor != null && multicolor.length > 0) {
			int colorPos = position % multicolor.length;
			icon.setBackgroundColor(Color
					.parseColor(multicolor[colorPos]));
		}*/

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
		vector = null;
	}

}