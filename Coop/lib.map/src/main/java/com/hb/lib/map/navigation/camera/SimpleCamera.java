package com.hb.lib.map.navigation.camera;

import android.location.Location;

import com.hb.lib.map.model.FindShortestPath;
import com.mapbox.geojson.Point;
import com.mapbox.turf.TurfMeasurement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimpleCamera implements Camera {

    protected static final int DEFAULT_TILT = 50;
    protected static final double DEFAULT_ZOOM = 17d;

    private List<Point> routeCoordinates = new ArrayList<>();
    private double initialBearing;
    private FindShortestPath initialRoute;

    @Override
    public double bearing(RouteInformation routeInformation) {
        if (routeInformation.getRoute() != null) {
            setupLineStringAndBearing(routeInformation.getRoute());
            return initialBearing;
        } else if (routeInformation.getLocation() != null) {
            return routeInformation.getLocation().getBearing();
        }
        return 0;
    }

    @Override
    public Point target(RouteInformation routeInformation) {
        double lng;
        double lat;
        Point targetPoint = null;
        if (routeInformation.getRoute() != null) {
            setupLineStringAndBearing(routeInformation.getRoute());
            lng = routeCoordinates.get(0).longitude();
            lat = routeCoordinates.get(0).latitude();
            return Point.fromLngLat(lng, lat);
        } else if (routeInformation.getLocation() != null) {
            Location location = routeInformation.getLocation();
            lng = location.getLongitude();
            lat = location.getLatitude();
            targetPoint = Point.fromLngLat(lng, lat);
        }
        return targetPoint;
    }

    @Override
    public double tilt(RouteInformation routeInformation) {
        return DEFAULT_TILT;
    }

    @Override
    public double zoom(RouteInformation routeInformation) {
        return DEFAULT_ZOOM;
    }

    @Override
    public List<Point> overview(RouteInformation routeInformation) {
        boolean invalidCoordinates = routeCoordinates == null || routeCoordinates.isEmpty();
        if (invalidCoordinates) {
            buildRouteCoordinatesFromRouteData(routeInformation);
        }
        return routeCoordinates;
    }

    private void buildRouteCoordinatesFromRouteData(RouteInformation routeInformation) {
        if (routeInformation.getRoute() != null) {
            setupLineStringAndBearing(routeInformation.getRoute());
        }
    }

    private void setupLineStringAndBearing(FindShortestPath route) {
        if (initialRoute != null && route == initialRoute) {
            return;
        }
        initialRoute = route;
        routeCoordinates = generateRouteCoordinates(route);
        initialBearing = TurfMeasurement.bearing(
                Point.fromLngLat(routeCoordinates.get(0).longitude(), routeCoordinates.get(0).latitude()),
                Point.fromLngLat(routeCoordinates.get(1).longitude(), routeCoordinates.get(1).latitude())
        );
    }


    private List<Point> generateRouteCoordinates(FindShortestPath route) {
        if (route == null) {
            return Collections.emptyList();
        }
        return FindShortestPath.Companion.map2Points(route);
    }
}
