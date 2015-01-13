package com.creative.cutebond.adapter;

import java.util.Vector;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
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

public class PhotosAdapter extends BaseAdapter {

	private LayoutInflater inflater = null;
	Vector<Item> vector = new Vector<Item>();
	private Context context = null;

	public ImageLoader imageLoader;
	
	private int pink_color = 0;
	private int blue_color = 0;
	
	public String[] multicolor = null;

	public PhotosAdapter(Context context) {
		this.context = context;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(context, false);
		pink_color = context.getResources().getColor(R.color.p_p_line);
		blue_color = context.getResources().getColor(R.color.p_b_line);
		
		multicolor = ((CuteBond)context).multicolor;
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
			convertView = inflater.inflate(R.layout.template_photo, null);
		}

		String str = item.getAttribute("userName");
		TextView title = (TextView) convertView.findViewById(R.id.temp_usr_name);
		title.setText(str);

		str = item.getAttribute("photo_categories");
		title = (TextView) convertView.findViewById(R.id.temp_usr_nam);
		title.setText(str);
		
		str = item.getAttribute("PhotoDescription");
		title = (TextView) convertView.findViewById(R.id.temp_pt_title);
		title.setText(Html.fromHtml(str));
		
		str = item.getAttribute("comment_cnt");
		((Button)convertView.findViewById(R.id.temp_pt_cmt)).setText(str);
		
		str = item.getAttribute("like_status");
		((Button)convertView.findViewById(R.id.temp_pt_like)).setText(str);
		
		str = item.getAttribute("share_count");
		((Button)convertView.findViewById(R.id.temp_pt_shr)).setText(str);
		
		str = item.getAttribute("userGender");
		if(str.equalsIgnoreCase("male"))
			((View)convertView.findViewById(R.id.temp_pt_sp)).setBackgroundColor(pink_color);
		else
			((View)convertView.findViewById(R.id.temp_pt_sp)).setBackgroundColor(blue_color);
			
		/*float height = 100.0f;
		str = item.getAttribute("new_height");
		if(str.length() > 0){
			height = Float.parseFloat(str);
		}*/
		
		final ImageView icon = (ImageView) convertView.findViewById(R.id.temp_pt_icon);
		icon.setTag(item);

		String imageUri = item.getAttribute("Photo");
		
		imageUri = "http://www.cutebond.com/webapp/uploads/"+imageUri;

		if (imageUri.startsWith("http://")) {
			imageLoader.DisplayImage(imageUri, icon);
		}
		
		if (multicolor != null && multicolor.length > 0) {
			int colorPos = position % multicolor.length;
			
			//icon.setImageResource(resId)
			icon.setBackgroundColor(Color
					.parseColor(multicolor[colorPos]));
		}
		
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