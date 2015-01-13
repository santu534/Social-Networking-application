package com.creative.cutebond.parsers;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import com.creative.cutebond.callbacks.IItemHandler;
import com.creative.cutebond.common.Item;

public class LeftMenuParser {

	private IItemHandler handler;

	private int REQ_TYPE;

	private Vector<Item> items = null;

	private Item item = null;
	
	private Item data = null;

	public LeftMenuParser(IItemHandler aHandler, int requestId)
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
			
			data = new Item("");

			JSONObject object = new JSONObject(jsonStr);
			Iterator<String> inrItr = object.keys();
			
			while (inrItr.hasNext()) {
				String inrKey = inrItr.next();
				
				JSONArray array = object.getJSONArray(inrKey);
				
				items = new Vector<Item>();

				for (int i = 0; i < array.length(); i++) {
					
					item = new Item("values");
					
					JSONObject obj = array.getJSONObject(i);
					
					Iterator<String> iterator = obj.keys();
					
					while (iterator.hasNext()) {

						String tempKey = iterator.next();
						
						Object tempValue = obj.get(tempKey);
						
						item.setAttribute(tempKey, tempValue+"");
					}
					items.add(item);
				}
				
				data.setAttributeValue(inrKey, items);
			}
			
			/*JSONArray array = new JSONArray(jsonStr);

			for (int i = 0; i < array.length(); i++) {

				item = new Item("values");

				JSONObject obj = array.getJSONObject(i);

				Iterator<String> iterator = obj.keys();

				while (iterator.hasNext()) {

					String tempKey = iterator.next();

					Object tempValue = obj.get(tempKey);

					item.setAttribute(tempKey, tempValue + "");
				}
				items.add(item);
			}*/

			handler.onFinish(data, REQ_TYPE);

		} catch (Exception e) {
			handler.onError("Parser Exception", REQ_TYPE);
			e.printStackTrace();
			throw e;
		}
	}

}