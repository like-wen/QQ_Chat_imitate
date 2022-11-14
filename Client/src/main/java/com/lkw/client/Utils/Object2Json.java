package com.lkw.client.Utils;

import com.alibaba.fastjson2.JSON;


import java.util.HashMap;
import java.util.Map;

public class Object2Json {
    private String username;

    private Integer mode;
    private Map<String,Object> data=new HashMap<>();


    //私有化
    private Object2Json(){
    }

    /**
     *
     * @param username
     * @param mode 在这里 1是文本传送,2是...
     * @return
     */
    public static Object2Json creat(String username,Integer mode){
        Object2Json object2Json = new Object2Json();
        object2Json.setUsername(username);
        object2Json.setMode(mode);
        return object2Json;
    }

    public Object2Json addObject(String key,Object value){
        this.data.put(key,value);
        return this;
    }

    public String  buildJson(){
        return JSON.toJSONString(this);
    }



    //getter setter

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
