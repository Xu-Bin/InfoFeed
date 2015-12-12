package com.xubin.infofeed;

import android.graphics.Bitmap;

public class RowStructure {
    public Bitmap avatar;
    public String author;
    public String publish_time;
    public String content;

    public RowStructure(Bitmap avatar, String author, String publish_time, String content){
        this.avatar = avatar;
        this.author = author;
        this.publish_time = publish_time;
        this.content = content;
    }
}
