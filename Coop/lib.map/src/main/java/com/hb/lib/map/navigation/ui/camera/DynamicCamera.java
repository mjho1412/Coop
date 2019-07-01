package com.hb.lib.map.navigation.ui.camera;


import android.location.Location;
import androidx.annotation.NonNull;
import com.hb.lib.map.model.FindShortestPath;
import com.hb.lib.map.navigation.camera.RouteInformation;
import com.hb.lib.map.navigation.camera.SimpleCamera;
import com.hb.lib.map.route.NavigateManager;
import com.hb.lib.map.route.WayPoint;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import java.util.ArrayList;
import java.util.List;

public class DynamicCamera extends SimpleCamera {

    private static final double MAX_CAMERA_TILT = 50d;
    private static final double MIN_CAMERA_TILT = 35d;
    private static final double MAX_CAMERA_ZOOM = 16d;
    private static final double MIN_CAMERA_ZOOM = 12d;
    private static final Point DEFAULT_TARGET = Point.fromLngLat(0d, 0d);


    private FindShortestPath.Step currentStep;
    private MapboxMap mapboxMap;
    private boolean forceUpdateZoom;
    private boolean isShutdown = false;

    public DynamicCamera(@NonNull MapboxMap map) {
        this.mapboxMap = map;
    }

    @Override
    public Point target(RouteInformation routeInformation) {

        if (isShutdown) {
            return DEFAULT_TARGET;
        }

        if (routeInformation.getLocation() != null) {
            Location target = routeInformation.getLocation();
            return Point.fromLngLat(target.getLongitude(), target.getLatitude());
        } else if (routeInformation.getRoute() != null) {
            return super.target(routeInformation);
        }
        // Without route or location info, return the current position
        LatLng currentTarget = mapboxMap.getCameraPosition().target;
        return Point.fromLngLat(currentTarget.getLongitude(), currentTarget.getLongitude());
    }

    @Override
    public double bearing(RouteInformation routeInformation) {
        if (routeInformation.getRouteProgress() != null) {
            WayPoint wp = routeInformation.getRouteProgress().getCurrentWayPoint();
            if (wp != null) {
                return wp.getBearing();
            }
        }
        return super.bearing(routeInformation);
    }

    @Override
    public double tilt(RouteInformation routeInformation) {
        if (isShutdown) {
            return DEFAULT_TILT;
        }

        NavigateManager routeProgress = routeInformation.getRouteProgress();
        if (routeProgress != null) {
            WayPoint wp = routeProgress.getCurrentWayPoint();
            if (wp != null) {
                double distanceRemaining = wp.getDistances()[0];
                return createTilt(distanceRemaining);
            }
        }
        return super.tilt(routeInformation);
    }

    @Override
    public double zoom(RouteInformation routeInformation) {
        if (isShutdown) {
            return DEFAULT_ZOOM;
        }

        if (validLocationAndProgress(routeInformation) && shouldUpdateZoom(routeInformation)) {
            return createZoom(routeInformation);
        } else if (routeInformation.getRoute() != null) {
            return super.zoom(routeInformation);
        }
        return mapboxMap.getCameraPosition().zoom;
    }

    public void forceResetZoomLevel() {
        forceUpdateZoom = true;
    }

    public void clearMap() {
        isShutdown = true;
        mapboxMap = null;
    }

    private double createTilt(double distanceRemaining) {
        double tilt = distanceRemaining / 5;
        if (tilt > MAX_CAMERA_TILT) {
            return MAX_CAMERA_TILT;
        } else if (tilt < MIN_CAMERA_TILT) {
            return MIN_CAMERA_TILT;
        }
        return Math.round(tilt);
    }

    private double createZoom(RouteInformation routeInformation) {
        CameraPosition position = createCameraPosition(
                routeInformation.getLocation(),
                routeInformation.getRouteProgress());
        if (position.zoom > MAX_CAMERA_ZOOM) {
            return MAX_CAMERA_ZOOM;
        } else if (position.zoom < MIN_CAMERA_ZOOM) {
            return MIN_CAMERA_ZOOM;
        }
        return position.zoom;
    }

    private CameraPosition createCameraPosition(Location location, NavigateManager nm) {
        if (nm == null)
            return mapboxMap.getCameraPosition();
        WayPoint upComingStep = nm.getWayPoint(location, false);
        if (upComingStep != null) {
            Point stepManeuverPoint = Point.fromLngLat(
                    upComingStep.getStep().getX(),
                    upComingStep.getStep().getY());

            List<LatLng> latLngs = new ArrayList<>();
            LatLng currentLatLng = new LatLng(location);
            LatLng maneuverLatLng = new LatLng(stepManeuverPoint.latitude(), stepManeuverPoint.longitude());
            latLngs.add(currentLatLng);
            latLngs.add(maneuverLatLng);

            if (latLngs.size() < 1 || currentLatLng.equals(maneuverLatLng)) {
                return mapboxMap.getCameraPosition();
            }

            LatLngBounds cameraBounds = new LatLngBounds.Builder()
                    .includes(latLngs)
                    .build();

            int[] padding = {0, 0, 0, 0};
            return mapboxMap.getCameraForLatLngBounds(cameraBounds, padding);
        }
        return mapboxMap.getCameraPosition();
    }

    private boolean isForceUpdate() {
        if (forceUpdateZoom) {
            forceUpdateZoom = false;
            return true;
        }
        return false;
    }

    private boolean isNewStep(NavigateManager routeProgress) {
        if (routeProgress == null)
            return true;
        WayPoint wp = routeProgress.getCurrentWayPoint();
        if (wp == null) return true;

        boolean isNewStep = currentStep == null || !currentStep.equals(routeProgress.getCurrentWayPoint().step);
        currentStep = routeProgress.getCurrentWayPoint().step;
        return isNewStep;
    }

    private boolean validLocationAndProgress(RouteInformation routeInformation) {
        return routeInformation.getLocation() != null && routeInformation.getRouteProgress() != null;
    }

    private boolean shouldUpdateZoom(RouteInformation routeInformation) {
        NavigateManager progress = routeInformation.getRouteProgress();
        return isForceUpdate()
                || isNewStep(progress);
//                || isLowAlert(progress)
//                || isMediumAlert(progress)
//                || isHighAlert(progress);
    }

//    private boolean isLowAlert(RouteProgress progress) {
//        if (!hasPassedLowAlertLevel) {
//            double durationRemaining = progress.currentLegProgress().currentStepProgress().durationRemaining();
//            double stepDuration = progress.currentLegProgress().currentStep().duration();
//            boolean isLowAlert = durationRemaining < NavigationConstants.NAVIGATION_LOW_ALERT_DURATION;
//            boolean hasValidStepDuration = stepDuration > NavigationConstants.NAVIGATION_LOW_ALERT_DURATION;
//            if (hasValidStepDuration && isLowAlert) {
//                hasPassedLowAlertLevel = true;
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private boolean isMediumAlert(RouteProgress progress) {
//        if (!hasPassedMediumAlertLevel) {
//            double durationRemaining = progress.currentLegProgress().currentStepProgress().durationRemaining();
//            double stepDuration = progress.currentLegProgress().currentStep().duration();
//            boolean isMediumAlert = durationRemaining < NavigationConstants.NAVIGATION_MEDIUM_ALERT_DURATION;
//            boolean hasValidStepDuration = stepDuration > NavigationConstants.NAVIGATION_MEDIUM_ALERT_DURATION;
//            if (hasValidStepDuration && isMediumAlert) {
//                hasPassedMediumAlertLevel = true;
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private boolean isHighAlert(RouteProgress progress) {
//        if (!hasPassedHighAlertLevel) {
//            double durationRemaining = progress.currentLegProgress().currentStepProgress().durationRemaining();
//            double stepDuration = progress.currentLegProgress().currentStep().duration();
//            boolean isHighAlert = durationRemaining < NavigationConstants.NAVIGATION_HIGH_ALERT_DURATION;
//            boolean hasValidStepDuration = stepDuration > NavigationConstants.NAVIGATION_HIGH_ALERT_DURATION;
//            if (hasValidStepDuration && isHighAlert) {
//                hasPassedHighAlertLevel = true;
//                return true;
//            }
//        }
//        return false;
//    }
}
