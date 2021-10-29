package com.comp90018.proj2.utils;


import android.util.Log;

import com.comp90018.proj2.data.model.CardItem;

import java.util.Comparator;

// latest post time
public class PostTimeSort implements Comparator {
    @Override
    public int compare(Object o1, Object o2) {
        CardItem cardItem1 = (CardItem) o1;
        CardItem cardItem2 = (CardItem) o2;
        int flag = cardItem1.getPostTime().compareTo(cardItem2.getPostTime());
        Log.e("Timesort", String.valueOf(flag));
        return flag;
    }
}
