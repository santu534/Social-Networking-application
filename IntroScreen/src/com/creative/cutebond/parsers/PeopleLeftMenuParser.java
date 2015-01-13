package com.creative.cutebond.parsers;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Vector;

import org.json.JSONArray;

import com.creative.cutebond.callbacks.IItemHandler;
import com.creative.cutebond.common.Item;

public class PeopleLeftMenuParser {

	private IItemHandler handler;

	private int REQ_TYPE;

	private Item data = null;

	public PeopleLeftMenuParser(IItemHandler aHandler, int requestId)
			throws Exception {
		handler = aHandler;
		REQ_TYPE = requestId;
	}

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

			Vector<Item> items = new Vector<Item>();
			
			JSONArray array = new JSONArray(jsonStr);
			
			for(int i = 0; i < array.length(); i++){
				Item item = new Item("");
				item.setAttribute("categoryName", array.getString(i));
				items.add(item);
			}
			
			data.setAttributeValue("peoples", items);

			handler.onFinish(data, REQ_TYPE);

		} catch (Exception e) {
			handler.onError("Parser Exception", REQ_TYPE);
			e.printStackTrace();
			throw e;
		}
	}

}