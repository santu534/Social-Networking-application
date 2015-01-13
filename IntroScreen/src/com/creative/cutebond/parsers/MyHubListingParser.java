package com.creative.cutebond.parsers;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import com.creative.cutebond.callbacks.IItemHandler;
import com.creative.cutebond.common.Item;

public class MyHubListingParser {

	private IItemHandler handler;

	private int REQ_TYPE;

	private Vector<Item> items = null;

	private Item item = null;

	public MyHubListingParser(IItemHandler aHandler, int requestId)
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

			JSONObject innerObj = new JSONObject(jsonStr);

			if (innerObj != null) {
				Item item = new Item("");
				item.setAttribute("total_pages",
						innerObj.getString("total_pages") + "");
				item.setAttribute("perpage", innerObj.getString("perpage") + "");
				item.setAttribute("total_posts",
						innerObj.getString("total_posts") + "");
				items.add(item);
			}

			JSONObject jsonObject = (JSONObject) innerObj.get("frnds_wall_arr");
			
			Iterator<String> itr = jsonObject.keys();
			
			while (itr.hasNext()) {
				
				item = new Item("");//complete 0,1,2,3 
								
				String key = itr.next();
				
				JSONObject obj = jsonObject.getJSONObject(key);								
				
				//WALL ID
				JSONObject temp = obj.getJSONObject("wallid");								
				Iterator<String> inrItr = temp.keys();
				Item initem = new Item("");
				while (inrItr.hasNext()) {
					String tempKey = inrItr.next();					
					String tempValue = temp.optString(tempKey);												
					initem.setAttribute(tempKey, tempValue+"");
				}
				item.setAttributeValue("wallid", initem);
				
				//User Details
				JSONArray array = obj.getJSONArray("user_det");
				
				//Friends Comments count
				array = obj.getJSONArray("frnd_comments_cnt");
				
				//Share count
				String share_cnt = obj.get("share_cnt").toString();
				
				item.setAttribute("share_cnt", share_cnt+"");
				
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