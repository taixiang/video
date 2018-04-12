package com.video;

/**
 * Created by tx on 2018/4/9.
 */

public class Data {
    private String url;
    private int type;
    private String title;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Data{" +
                "url='" + url + '\'' +
                ", type=" + type +
                '}';
    }
}
