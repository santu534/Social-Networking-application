package com.creative.cutebond.parsers;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import com.creative.cutebond.callbacks.IItemHandler;
import com.creative.cutebond.common.Item;

public class VideoDetailPageParser {

	private IItemHandler handler;

	private int REQ_TYPE;

	private Item items = null;

	private Item item = null;

	public VideoDetailPageParser(IItemHandler aHandler, int requestId)
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
			JSONObject video_results = innerObj.getJSONObject("video_results");
			item = new Item("values");
			Iterator<String> iterators = video_results.keys();
			while (iterators.hasNext()) {
				String tempKey = iterators.next();
				Object tempValue = video_results.get(tempKey);
				item.setAttribute(tempKey, tempValue + "");
			}
			items.setAttributeValue("video_results",item);
			
			JSONArray video_comments = innerObj.getJSONArray("video_comments");
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
			items.setAttributeValue("video_comments", vc);
			
			
			String video_url = innerObj.get("video_url").toString();
			items.setAttribute("video_url", video_url);
						
			handler.onFinish(items, REQ_TYPE);

		} catch (Exception e) {
			handler.onError("Parser Exception", REQ_TYPE);
			e.printStackTrace();
			throw e;
		}
	}

}