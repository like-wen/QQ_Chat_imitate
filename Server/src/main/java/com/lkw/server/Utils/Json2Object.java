package com.lkw.server.Utils;

import com.alibaba.fastjson2.JSONObject;

public class Json2Object {
    private  String username;

    private String mode;

    private JSONObject data;

    public  Json2Object(String json){
        //转换传输的消息格式
        //格式   json:{"username":"lkw","mode":1,"data":{"text":"lkw111111111"}}
        JSONObject jsonObject = JSONObject.parseObject(json);
        this.setMode((String) jsonObject.get("mode"));
        this.setUsername((String)jsonObject.get("username"));
        this.setData(jsonObject.getJSONObject("data"));
    }

    public  String Json2Text() {
        //得到text对应的键值对的值
            return (String) this.data.get("text");
    }

    public boolean Json2logged() {        //获取键"logged"对应的值
        return (boolean) this.data.get("logged");
    }

    /**
     *
     * @return json
     */
    public String Json2PasswordCheck() {
       String password = (String) this.data.get("password");



        String json = Object2Json.creat(this.getUsername(), "logged")
                .addObject("logged", password.equals("123456"))
                .buildJson();

        return json;
    }









    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }


}
