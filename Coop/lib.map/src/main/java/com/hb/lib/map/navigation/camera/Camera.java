package com.hb.lib.map.navigation.camera;

import com.mapbox.geojson.Point;

import java.util.List;

public interface Camera {

    double bearing(RouteInformation routeInformation);

    Point target(RouteInformation routeInformation);

    double tilt(RouteInformation routeInformation);

    double zoom(RouteInformation routeInformation);

    List<Point> overview(RouteInformation routeInformation);
}
