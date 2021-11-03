package com.comp90018.proj2.utils;

import android.location.Location;

import com.google.firebase.firestore.GeoPoint;

public class GeoPointUtils {

    /**
     * Calculate distance through two GeoPoint
     * @return the distance in km
     */
    public static double calDistance(GeoPoint p1, GeoPoint p2) {
        double lon1 = Math.toRadians(p1.getLongitude());
        double lon2 = Math.toRadians(p2.getLongitude());
        double lat1 = Math.toRadians(p1.getLatitude());
        double lat2 = Math.toRadians(p2.getLatitude());

        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2),2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in kilometers. Use 3956
        // for miles
        double r = 6371;

        // calculate the result
        return (c * r);
    }

    /**
     * In Java, Coordinates data is Location, while Firebase is GeoPoint
     * @param location java data type
     * @return current location in GeoPoint
     */
    public static GeoPoint locationCvtGeo(Location location){
        double lat = location.getLatitude();
        // Log.e("convert", String.valueOf(lat));
        double lng = location.getLongitude();
        return new GeoPoint(lat, lng);
    }
}
