package com.creative.cutebond.parsers;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.creative.cutebond.callbacks.IItemHandler;
import com.creative.cutebond.common.Item;

public class JSONCommonParser {
	
	private IItemHandler handler;
	
	private int REQ_TYPE;	
	
	private Item data = new Item("data");
	
	private Vector<Item> items = null;
	
	private Item item = null;

	public JSONCommonParser(IItemHandler aHandler, int requestId)
			throws Exception {
		handler = aHandler;
		REQ_TYPE = requestId;

		try {

		} catch (Exception e) {
			handler.onError("Parser Exception", REQ_TYPE);
			e.printStackTrace();
			throw e;
		}
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
			
			if(jsonStr.contains("Failed")){
				handler.onError("No Content", REQ_TYPE);
				return;
			}
			
			//{"status":"Failed","statuscode":"0003","statusdescription":"Data not available."}

			
			//Log.e("jsonStr : ", jsonStr+"");

			JSONObject jsonObj = new JSONObject(jsonStr);

			Iterator<String> itr = jsonObj.keys();
			
			while (itr.hasNext()) {

				String key = itr.next();

				items = new Vector<Item>();
				
				JSONObject innerObj = jsonObj.getJSONObject(key);

				Iterator<String> inrItr = innerObj.keys();

				while (inrItr.hasNext()) {
					
					String inrKey = inrItr.next();
					
					Object object = innerObj.get(inrKey);
					
					if (object instanceof JSONObject) {
						
						item = new Item("pager");
						
						JSONObject obj = (JSONObject) object;
												
						Iterator<String> iterator = obj.keys();
						
						while (iterator.hasNext()) {

							String tempKey = iterator.next();
							
							Object tempValue = obj.get(tempKey);							
							
							item.setAttribute(tempKey, tempValue+"");

						}
						
						items.add(item);
						
						
						Log.e("-=-=-=-=", "-=-=-=object-=-");
					} else if (object instanceof JSONArray) {

						JSONArray temp = (JSONArray) object;

						for (int i = 0; i < temp.length(); i++) {
							
							item = new Item("values");
							
							JSONObject obj = temp.getJSONObject(i);
							
							Iterator<String> iterator = obj.keys();
							
							while (iterator.hasNext()) {

								String tempKey = iterator.next();
								
								Object tempValue = obj.get(tempKey);

								//Log.e("Key : " + tempKey, "value : "+ tempValue);
								
								item.setAttribute(tempKey, tempValue+"");
							}
							items.add(item);
						}
					}
				}

				data.setAttributeValue(key, items);
			}
			
			handler.onFinish(data, REQ_TYPE);

		} catch (Exception e) {
			handler.onError("Parser Exception", REQ_TYPE);
			e.printStackTrace();
			throw e;
		}
	}

}