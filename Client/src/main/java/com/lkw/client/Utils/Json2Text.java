package com.lkw.client.Utils;



import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;


public class Json2Text  {

	public static String creat(String jsonstr) {
   		//转换传输的消息格式
		//格式:{"username":"lkw","mode":1,"data":{"text":"lkw111111111"}}
	   JSONObject json = JSONObject.parseObject(jsonstr);
	   if(json.containsKey("data"))
	   {
		   JSONObject data = json.getJSONObject("data");
		   return  (String) data.get("text");
	   }
	   return null;
   }
}
