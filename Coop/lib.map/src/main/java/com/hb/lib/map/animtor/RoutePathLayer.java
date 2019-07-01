package com.hb.lib.map.animtor;

import android.graphics.Color;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amal.chandran on 22/12/16.
 */

public class RoutePathLayer {

    private static final String PATH_ROUTE_ID = "PATH_ROUTE_ID";
    private static final String SRC_PATH_ROUTE_ID = "src_PATH_ROUTE_ID";

    private static RoutePathLayer mapAnimator;

    private static final int GREY = Color.parseColor("#FFA7A6A6");

    private int mBackgroundColor = 0xffff0000;
    private int mForegroundColor = GREY;
    private int mPathWidth = 5;

    private RoutePathLayer() {
    }

    public static RoutePathLayer getInstance() {
        if (mapAnimator == null)
            mapAnimator = new RoutePathLayer();
        return mapAnimator;
    }

    public RoutePathLayer colorBackground(int color) {
        mBackgroundColor = color;
        return this;
    }

    public RoutePathLayer colorForeground(int color) {
        mForegroundColor = color;
        return this;
    }

    public RoutePathLayer width(int w) {
        mPathWidth = w;
        return this;
    }


    public void clear(MapboxMap vbdMap) {
        if (vbdMap == null)
            return;

        vbdMap.removeLayer(PATH_ROUTE_ID + "_01");
        vbdMap.removeLayer(PATH_ROUTE_ID + "_02");
        vbdMap.removeSource(SRC_PATH_ROUTE_ID);
    }

    public void draw(MapboxMap map, final List<LatLng> path) {
        clear(map);
        addLineLayer(map, path);
    }

    private void addLineLayer(MapboxMap vbdMap, List<LatLng> path) {

        if (path == null || path.isEmpty())
            return;

        ArrayList<Feature> list = new ArrayList<>();
        ArrayList<Point> line = new ArrayList<>();
        String id = PATH_ROUTE_ID;

        for (LatLng ll : path) {
            line.add(Point.fromLngLat(ll.getLongitude(), ll.getLatitude()));
        }

        Feature f = Feature.fromGeometry(LineString.fromLngLats(line));
        list.add(f);

        vbdMap.addSource(new GeoJsonSource(SRC_PATH_ROUTE_ID, FeatureCollection.fromFeatures(list)));

        vbdMap.addLayerAt(new LineLayer(id + "_01", SRC_PATH_ROUTE_ID)
                        .withProperties(PropertyFactory.lineColor(mBackgroundColor))
                        .withProperties(PropertyFactory.lineWidth((float) (mPathWidth + 2)))
                        .withProperties(PropertyFactory.lineCap(Property.LINE_CAP_ROUND))
                , firstSymbolLayerIndex(vbdMap));

        vbdMap.addLayerAt(new LineLayer(id + "_02", SRC_PATH_ROUTE_ID)
                        .withProperties(PropertyFactory.lineColor(mForegroundColor))
                        .withProperties(PropertyFactory.lineWidth((float) mPathWidth))
                        .withProperties(PropertyFactory.lineCap(Property.LINE_CAP_ROUND))
                , firstSymbolLayerIndex(vbdMap));
    }

    private int firstSymbolLayerIndex(MapboxMap map) {
        List<Layer> layers = map.getLayers();
        if (layers == null) {
            return 0;
        } else {
            int i = 0;

            for (int n = layers.size(); i < n; ++i) {
                Layer l = layers.get(i);
                if (l instanceof SymbolLayer) {
                    return i;
                }
            }

            return layers.size();
        }
    }
}

