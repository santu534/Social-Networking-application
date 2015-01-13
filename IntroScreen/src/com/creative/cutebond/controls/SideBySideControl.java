package com.creative.cutebond.controls;

import java.util.Vector;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.creative.cutebond.CuteBond;
import com.creative.cutebond.R;
import com.creative.cutebond.common.Item;
import com.creative.cutebond.imageloader.ImageLoader;

public class SideBySideControl extends LinearLayout {

	private Context context = null;
	public ImageLoader imageLoader;	
	
	public String[] multicolor = null;
	
	private LinearLayout leftside = null;
	private LinearLayout rightside = null;
	
	private float rightHeight = 0.0f;
	private float leftHeight = 0.0f;

	public SideBySideControl(Context context) {
		super(context);
		this.context = context;
		init();
	}

	public SideBySideControl(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	private void init() {
				
		imageLoader = new ImageLoader(context, false);
				
		multicolor = ((CuteBond)context).multicolor;
		
		this.setOrientation(LinearLayout.HORIZONTAL);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1);
		params.setMargins(0, 0, 0, 0);

		leftside = new LinearLayout(context);
		leftside.setOrientation(LinearLayout.VERTICAL);
		leftside.setBackgroundColor(Color.TRANSPARENT);

		this.addView(leftside, params);

		rightside = new LinearLayout(context);
		rightside.setOrientation(LinearLayout.VERTICAL);
		rightside.setBackgroundColor(Color.TRANSPARENT);

		this.addView(rightside, params);

	}
	
	public void clearViews() {
		leftside.removeAllViews();
		rightside.removeAllViews();
	}
	
	public void createView(Vector<Item> items) {
		
		/*LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.setMargins(5, 5, 5, 5);*/			
		
		for(int i = 0; i < items.size(); i++) {
			Item item = items.get(i);
			View convertView = View.inflate(context, R.layout.template_photo, null);
							
			float height = 100.0f;
			String str = item.getAttribute("new_height");
			if(str.length() > 0){
				height = Float.parseFloat(str);
			}					
			
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int)height);
			
			final ImageView icon = (ImageView) convertView.findViewById(R.id.temp_pt_icon);			
			
			//icon.getLayoutParams().height = (int)height;							

			String imageUri = item.getAttribute("Photo");
			
			imageUri = "http://www.cutebond.com/webapp/uploads/"+imageUri;

			if (imageUri.startsWith("http://")) {
				imageLoader.DisplayImage(imageUri, icon);
			}
			
			if (multicolor != null && multicolor.length > 0) {
				int colorPos = i % multicolor.length;
				
				icon.setBackgroundColor(Color
						.parseColor(multicolor[colorPos]));
			}
			
			convertView.setTag(item);
			convertView.setOnClickListener((OnClickListener)context);
			
			if(leftHeight == rightHeight) {
				leftHeight =  leftHeight+height;
				leftside.addView(convertView, params);
			} else if(leftHeight > rightHeight){
				rightHeight = rightHeight+height;
				rightside.addView(convertView, params);
			} else if(leftHeight < rightHeight){
				leftHeight =  leftHeight+height;
				leftside.addView(convertView, params);
			}
			
			/*if(i % 2 == 0) {
				leftside.addView(convertView, params);
			} else {
				rightside.addView(convertView, params);
			}*/
			
		}
		
	}

}
