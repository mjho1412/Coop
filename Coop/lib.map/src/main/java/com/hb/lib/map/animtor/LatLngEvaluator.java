package com.hb.lib.map.animtor;

import android.animation.TypeEvaluator;

import com.mapbox.mapboxsdk.geometry.LatLng;


/**
 * Created by amal.chandran on 22/12/16.
 */

public class LatLngEvaluator implements TypeEvaluator<LatLng> {
    @Override
    public LatLng evaluate(float t, LatLng startPoint, LatLng endPoint) {

        double lat = startPoint.getLatitude() + t * (endPoint.getLatitude() - startPoint.getLatitude());
        double lng = startPoint.getLongitude() + t * (endPoint.getLongitude() - startPoint.getLongitude());
        return new LatLng(lat, lng);
    }
}
