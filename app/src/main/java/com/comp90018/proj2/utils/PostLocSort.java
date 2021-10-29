package com.comp90018.proj2.utils;

import static com.comp90018.proj2.MainActivity.caldistance;

import com.comp90018.proj2.data.model.CardItem;
import com.google.firebase.firestore.GeoPoint;

import java.util.Comparator;

// nearest location
public class PostLocSort implements Comparator {

    private GeoPoint current;

    public GeoPoint getCurrent() {
        return current;
    }

    public void setCurrent(GeoPoint current) {
        this.current = current;
    }

    public PostLocSort() {
    }

    public PostLocSort(GeoPoint current) {
        this.current = current;
    }

    @Override
    public int compare(Object o1, Object o2) {
        CardItem cardItem1 = (CardItem) o1;
        CardItem cardItem2 = (CardItem) o2;
        double distance1 = caldistance(current, cardItem1.getPoint());
        double distance2 = caldistance(current, cardItem2.getPoint());
        int flag = 1;
        if (distance1 < distance2) {
            flag = -1;
        }
        return flag;
    }
}
