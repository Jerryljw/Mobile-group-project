package com.comp90018.proj2.utils;

import com.google.firebase.firestore.GeoPoint;

/**
 * For sharing location data in the whole MainActivity
 */
public interface LocationCommunication {
    GeoPoint getLocation();
}
