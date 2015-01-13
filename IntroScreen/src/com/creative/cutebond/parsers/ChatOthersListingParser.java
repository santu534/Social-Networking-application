package com.creative.cutebond.parsers;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import com.creative.cutebond.callbacks.IItemHandler;
import com.creative.cutebond.common.Item;

public class ChatOthersListingParser {

	private IItemHandler handler;

	private int REQ_TYPE;

	private Vector<Item> items = null;

	private Item item = null;

	public ChatOthersListingParser(IItemHandler aHandler, int requestId)
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
			
			items = new Vector<Item>();
			
			JSONObject innerObj = new JSONObject(jsonStr);
			
			if(innerObj != null) {
				Item item = new Item("");
				item.setAttribute("total_pages", innerObj.getString("total_pages_others")+"");
				item.setAttribute("perpage", innerObj.getString("perpage")+"");
				item.setAttribute("total", innerObj.getString("total_other_messages")+"");
				item.setAttribute("others_cnt", innerObj.getString("others_cnt")+"");
				items.add(item);
			} 
			
			JSONArray user_videos = (JSONArray) innerObj.get("others");
			
			for (int i = 0; i < user_videos.length(); i++) {

				item = new Item("values");

				JSONObject obj = user_videos.getJSONObject(i);

				Iterator<String> iterator = obj.keys();

				while (iterator.hasNext()) {

					String tempKey = iterator.next();

					Object tempValue = obj.get(tempKey);

					item.setAttribute(tempKey, tempValue + "");
				}
				items.add(item);
			}										

			handler.onFinish(items, REQ_TYPE);

		} catch (Exception e) {
			handler.onError("Parser Exception", REQ_TYPE);
			e.printStackTrace();
			throw e;
		}
	}

}