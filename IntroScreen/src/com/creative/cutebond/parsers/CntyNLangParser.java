package com.creative.cutebond.parsers;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import com.creative.cutebond.callbacks.IItemHandler;
import com.creative.cutebond.common.Item;

public class CntyNLangParser {

	private IItemHandler handler;

	private int REQ_TYPE;

	private Item data = new Item("data");

	private Vector<Item> items = null;

	private Item item = null;

	public CntyNLangParser(IItemHandler aHandler, int requestId)
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

			//Log.e("jsonStr : ", jsonStr + "");

			JSONObject innerObj = new JSONObject(jsonStr);

			Iterator<String> inrItr = innerObj.keys();

			while (inrItr.hasNext()) {

				items = new Vector<Item>();

				String inrKey = inrItr.next();

				JSONArray temp = (JSONArray) innerObj.get(inrKey);

				for (int i = 0; i < temp.length(); i++) {

					item = new Item("values");

					JSONObject obj = temp.getJSONObject(i);

					Iterator<String> iterator = obj.keys();

					while (iterator.hasNext()) {

						String tempKey = iterator.next();

						Object tempValue = obj.get(tempKey);

						item.setAttribute(tempKey, tempValue + "");
					}
					items.add(item);
				}

				data.setAttributeValue(inrKey, items);
			}

			handler.onFinish(data, REQ_TYPE);

		} catch (Exception e) {
			handler.onError("Parser Exception", REQ_TYPE);
			e.printStackTrace();
			throw e;
		}
	}

}