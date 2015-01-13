package com.creative.cutebond.parsers;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Iterator;

import org.json.JSONObject;

import com.creative.cutebond.callbacks.IItemHandler;
import com.creative.cutebond.common.Item;

public class AccountsParser {

	private IItemHandler handler;

	private int REQ_TYPE;

	private Item items = null;

	private Item item = null;

	public AccountsParser(IItemHandler aHandler, int requestId)
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
			
			innerObj = (JSONObject) innerObj.get("user_details"); //frnd_ids - array, folowers_ids, folowing_ids, user_allocated_catg //
			
			item = new Item("values");			

			Iterator<String> iterator = innerObj.keys();

			while (iterator.hasNext()) {
					String tempKey = iterator.next();

					Object tempValue = innerObj.get(tempKey);

					item.setAttribute(tempKey, tempValue + "");
				}
			items.setAttributeValue("user_details", item);			
						
			handler.onFinish(items, REQ_TYPE);

		} catch (Exception e) {
			handler.onError("Parser Exception", REQ_TYPE);
			e.printStackTrace();
			throw e;
		}
	}

}