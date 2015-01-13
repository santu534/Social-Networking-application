package com.creative.cutebond.parsers;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import com.creative.cutebond.callbacks.IItemHandler;
import com.creative.cutebond.common.Item;

public class MicroBLogDetailPageParser {

	private IItemHandler handler;

	private int REQ_TYPE;

	private Item items = null;

	private Item item = null;

	public MicroBLogDetailPageParser(IItemHandler aHandler, int requestId)
			throws Exception {
		handler = aHandler;
		REQ_TYPE = requestId;
	}

	@SuppressWarnings("unchecked")
	public void parseXmlData(InputStream inputStream) throws Exception {
		try {

			byte[] bytebuf = new byte[0x1000];

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			for (;;) {
				int len = inputStream.read(bytebuf);
				if (len < 0)
					break;
				baos.write(bytebuf, 0, len);
			}

			bytebuf = baos.toByteArray();

			String jsonStr = new String(bytebuf, "UTF-8");

			items = new Item("");

			JSONObject innerObj = new JSONObject(jsonStr);

			JSONObject video_results = innerObj.getJSONObject("microblog_det");
			item = new Item("values");
			Iterator<String> iterators = video_results.keys();
			while (iterators.hasNext()) {
				String tempKey = iterators.next();
				Object tempValue = video_results.get(tempKey);
				item.setAttribute(tempKey, tempValue + "");
			}
			items.setAttributeValue("microblog_det",item);
			
			JSONObject hub_likes_count = innerObj.getJSONObject("hub_likes_count");
			item = new Item("values");
			Iterator<String> hub_likes_count_iterators = hub_likes_count.keys();
			while (hub_likes_count_iterators.hasNext()) {
				String tempKey = hub_likes_count_iterators.next();
				Object tempValue = hub_likes_count.opt(tempKey)+"";
				item.setAttribute(tempKey, tempValue + "");
			}
			items.setAttributeValue("hub_likes_count", item);
			
			
			JSONObject follow_data = innerObj.getJSONObject("follow_data");
			item = new Item("values");
			Iterator<String> follow_data_iterators = follow_data.keys();
			while (follow_data_iterators.hasNext()) {
				String tempKey = follow_data_iterators.next();
				Object tempValue = follow_data.opt(tempKey)+"";
				item.setAttribute(tempKey, tempValue + "");
			}
			items.setAttributeValue("follow_data", item);
						
			JSONArray video_comments = innerObj.getJSONArray("blog_comments");
			Vector<Item> vc = new Vector<Item>();
			for (int i = 0; i < video_comments.length(); i++) {
				item = new Item("values");
				JSONObject obj = video_comments.getJSONObject(i);
				Iterator<String> iterator = obj.keys();
				while (iterator.hasNext()) {
					String tempKey = iterator.next();
					Object tempValue = obj.get(tempKey);
					item.setAttribute(tempKey, tempValue + "");
				}
				vc.add(item);
			}
			items.setAttributeValue("blog_comments", vc);
			
			JSONArray likes_photos = innerObj.getJSONArray("likes_photos");
			Vector<Item> vlpc = new Vector<Item>();
			for (int i = 0; i < likes_photos.length(); i++) {
				item = new Item("values");
				JSONObject obj = likes_photos.getJSONObject(i);
				Iterator<String> iterator = obj.keys();
				while (iterator.hasNext()) {
					String tempKey = iterator.next();
					Object tempValue = obj.get(tempKey);
					item.setAttribute(tempKey, tempValue + "");
				}
				vlpc.add(item);
			}
			items.setAttributeValue("likes_photos", vlpc);									
			
						
			handler.onFinish(items, REQ_TYPE);

		} catch (Exception e) {
			handler.onError("Parser Exception", REQ_TYPE);
			e.printStackTrace();
			throw e;
		}
	}

}