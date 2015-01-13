package com.creative.cutebond.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.creative.cutebond.R;
import com.creative.cutebond.common.Item;

public class ExpandAdapter extends BaseExpandableListAdapter {
 
    private Context context;
    private Item child  = new Item("");
    
    private List<String> group = new ArrayList<String>();        
    
    private int colorid = -1;
 
    public ExpandAdapter(Context context) {
        this.context = context;                
    }        
 
    @Override
    public Object getChild(int groupPosition, int childPosititon) {
    	String groupLabel = group.get(groupPosition);
    	Vector<Item> items = (Vector<Item>)child.getAttribValue(groupLabel);    	
        return items.get(childPosititon);
    }
 
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
 
    @Override
    public View getChildView(int groupPosition, final int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {
 
        Item item = (Item) getChild(groupPosition, childPosition);
 
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.template_item_child, null);            
        }
 
        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);
 
        String value = item.getAttribute("photo_categories");
        if(value.length() == 0)
        	value = item.getAttribute("categoryName");
        if(value.length() == 0)
        	value = item.getAttribute("topic");
        if(value.length() == 0)
        	value = item.getAttribute("group_name");
        
        
        txtListChild.setText(value);
        
        convertView.setTag(item);
        
        return convertView;
    }
 
    @Override
    public int getChildrenCount(int groupPosition) {    	    	
    	
    	String groupLabel = group.get(groupPosition);
    	Vector<Item> items = (Vector<Item>)child.getAttribValue(groupLabel);    
    	
    	if(items == null)
    		return 0;
    	
        return items.size();
    }
 
    @Override
    public Object getGroup(int groupPosition) {
        return group.get(groupPosition);
    }
 
    @Override
    public int getGroupCount() {
        return group.size();
    }
 
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
 
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
            View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.template_list_group, null);
            colorid = context.getResources().getColor(R.color.pinkish);
        }
        
        convertView.setBackgroundColor(colorid);
        convertView.findViewById(R.id.lblListHeader).setVisibility(View.VISIBLE);
        
        if(getGroupCount() == 1) {
        	convertView.setBackgroundColor(Color.TRANSPARENT);
        	convertView.setPadding(0, 0, 0, 0);
        	convertView.findViewById(R.id.lblListHeader).setVisibility(View.GONE);
        	//convertView.getLayoutParams().height = 0;
        	return convertView;
        }
 
        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);
 
        convertView.setTag(headerTitle);
        
        return convertView;
    }   
 
    @Override
    public boolean hasStableIds() {
        return false;
    }
 
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
    
    public void setGroupItem(List<String> group){
    	this.group = group;
    }
    
    public List<String> getGroupItem(){
    	return group;
    }
    
    public void setChildItem(Item item){
    	this.child = item;
    }
    
    public Item getChildItem(){
    	return child;
    }
    
    public void clear(){
    	group.clear();
    	child.clear();
    }
    
}