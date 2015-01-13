package com.creative.cutebond.adapter;

import java.util.Vector;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.creative.cutebond.R;
import com.creative.cutebond.common.Item;

public class UserContactsAdapter extends BaseAdapter {

	private LayoutInflater inflater = null;
	private Vector<Item> vector = new Vector<Item>();
	private Context context = null;
	
	private Drawable check = null;

	public UserContactsAdapter(Context context) {
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
			convertView = inflater.inflate(R.layout.template_cntrylang, null);
			check = context.getResources().getDrawable(R.drawable.check);
		}

		TextView title = (TextView) convertView.findViewById(R.id.template_title);
		title.setText(item.getAttribute("userName"));
		
		convertView.findViewById(R.id.template_check).setTag(item);
		convertView.findViewById(R.id.template_check).setOnClickListener((OnClickListener)context);
		
		ImageView check_icon = (ImageView) convertView.findViewById(R.id.template_check);
		check_icon.setImageDrawable(null);
		if(item.getAttribute("selected").equalsIgnoreCase("yes"))
			check_icon.setImageDrawable(check);
		else
			check_icon.setImageDrawable(null);

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