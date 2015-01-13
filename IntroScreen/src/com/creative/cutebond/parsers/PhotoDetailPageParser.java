package com.creative.cutebond.parsers;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import com.creative.cutebond.callbacks.IItemHandler;
import com.creative.cutebond.common.Item;

public class PhotoDetailPageParser {

	private IItemHandler handler;

	private int REQ_TYPE;

	private Item items = null;

	private Item item = null;

	public PhotoDetailPageParser(IItemHandler aHandler, int requestId)
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
			// { object & [ array
			bytebuf = baos.toByteArray();

			String jsonStr = new String(bytebuf, "UTF-8");

			items = new Item("");

			//Log.e("jsonStr : ", jsonStr + "");

			JSONObject innerObj = new JSONObject(jsonStr);

			//video results
			JSONObject video_results = innerObj.getJSONObject("photo_results");
			item = new Item("values");
			Iterator<String> iterators = video_results.keys();
			while (iterators.hasNext()) {
				String tempKey = iterators.next();
				Object tempValue = video_results.get(tempKey);
				item.setAttribute(tempKey, tempValue + "");
			}
			items.setAttributeValue("photo_results",item);
			
			JSONArray video_comments = innerObj.getJSONArray("photos_comments");
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
			items.setAttributeValue("photos_comments", vc);
			
			//Photo like count
			JSONObject video_likes_cnt = innerObj.getJSONObject("photos_likes_count");
			item = new Item("values");
			iterators = video_likes_cnt.keys();
			while (iterators.hasNext()) {
				String tempKey = iterators.next();
				Object tempValue = video_likes_cnt.get(tempKey);
				item.setAttribute(tempKey, tempValue + "");
			} 
			items.setAttributeValue("photos_likes_count", item);
						
			//Videos: likes_videos
			JSONArray likes_videos = innerObj.getJSONArray("likes_photos");
			Vector<Item> lv = new Vector<Item>();
			for (int i = 0; i < likes_videos.length(); i++) {
				item = new Item("values");
				JSONObject obj = likes_videos.getJSONObject(i);
				Iterator<String> iterator = obj.keys();
				while (iterator.hasNext()) {
					String tempKey = iterator.next();
					Object tempValue = obj.get(tempKey);
					item.setAttribute(tempKey, tempValue + "");
				}
				lv.add(item);
			}
			
			items.setAttributeValue("likes_photos", lv);
			
			
			/*String video_url = innerObj.get("video_url").toString();
			items.setAttribute("video_url", video_url);*/
						
			handler.onFinish(items, REQ_TYPE);

		} catch (Exception e) {
			handler.onError("Parser Exception", REQ_TYPE);
			e.printStackTrace();
			throw e;
		}
	}

}