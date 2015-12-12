package com.xubin.infofeed.cache;

import java.util.ArrayList;

import com.xubin.infofeed.Constants;

public class WeiboCache<E> extends ArrayList<E> {
    private static WeiboCache wc = null;

    private WeiboCache(){
        super(Constants.WEIBO_CACHE_SIZE);
    }

    public static WeiboCache getInstance(){
        if (null == wc){
            wc = new WeiboCache();
        }
        return wc;
    }

    @Override
    public boolean add(E object){
        super.add(0, object);
        if (this.size() > Constants.WEIBO_CACHE_SIZE){
            this.remove(Constants.WEIBO_CACHE_SIZE);
        }
        return true;
    }

}
