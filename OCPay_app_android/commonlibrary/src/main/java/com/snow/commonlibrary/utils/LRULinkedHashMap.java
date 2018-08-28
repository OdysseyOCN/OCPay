package com.snow.commonlibrary.utils;

import java.util.LinkedHashMap;

/**
 * Created by y on 2017/7/3.
 */

public class LRULinkedHashMap<K, V> extends LinkedHashMap<K, V> {


    private int size;

    public LRULinkedHashMap(int i, float v, boolean b, int limitSize) {
        this(i, v, b);
        size = limitSize;
    }

    public LRULinkedHashMap(int i, float v, boolean b) {
        super(i, v, b);
    }


    @Override
    protected boolean removeEldestEntry(Entry<K, V> entry) {
        boolean tooBig = size() > size;
        return tooBig;
    }


}

