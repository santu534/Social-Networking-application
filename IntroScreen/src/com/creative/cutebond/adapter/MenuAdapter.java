package com.creative.cutebond.adapter;

import java.util.Vector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.creative.cutebond.R;
import com.creative.cutebond.common.Item;

public class MenuAdapter extends BaseAdapter {

	private LayoutInflater inflater = null;
	private Vector<Item> vector = new Vector<Item>();
	private Context context = null;

	public MenuAdapter(Context context) {
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
			convertView = inflater.inflate(R.layout.template_left_menu, null);
		}

		TextView title = (TextView) convertView
				.findViewById(R.id.template_title);
		
		String val = item.getAttribute("categoryName");
		if(val.length() == 0)
			val = item.getAttribute("photo_categories");
				
		title.setText(val);		

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