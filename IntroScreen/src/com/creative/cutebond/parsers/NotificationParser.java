package com.creative.cutebond.parsers;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import com.creative.cutebond.callbacks.IItemHandler;
import com.creative.cutebond.common.Item;

public class NotificationParser {

	private IItemHandler handler;

	private int REQ_TYPE;

	private Vector<Item> items = null;

	private Item item = null;

	public NotificationParser(IItemHandler aHandler, int requestId)
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
			
			items = new Vector<Item>();
						
			JSONArray noti = new JSONArray(jsonStr);
			
			for (int i = 0; i < noti.length(); i++) {

				item = new Item("values");

				JSONObject obj = noti.getJSONObject(i);

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