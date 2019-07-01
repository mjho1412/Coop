/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hb.lib.map.route;


import android.location.Location;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.List;

import static com.hb.lib.map.route.MathUtil.arcHav;
import static com.hb.lib.map.route.MathUtil.havDistance;
import static com.hb.lib.map.route.MathUtil.wrap;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.tan;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;


public class SphericalUtil {

    private SphericalUtil() {}

    /**
     * Returns the heading from one LatLng to another LatLng. Headings are
     * expressed in degrees clockwise from North within the range [-180,180).
     * @return The heading in degrees clockwise from north.
     */
    public static double computeHeading(LatLng from, LatLng to) {
        // http://williams.best.vwh.net/avform.htm#Crs
        double fromLat = toRadians(from.getLatitude());
        double fromLng = toRadians(from.getLongitude());
        double toLat = toRadians(to.getLatitude());
        double toLng = toRadians(to.getLongitude());
        double dLng = toLng - fromLng;
        double heading = atan2(
                sin(dLng) * cos(toLat),
                cos(fromLat) * sin(toLat) - sin(fromLat) * cos(toLat) * cos(dLng));
        return wrap(toDegrees(heading), -180, 180);
    }

    /**
     * Returns the LatLng resulting from moving a distance from an origin
     * in the specified heading (expressed in degrees clockwise from north).
     * @param from     The LatLng from which to start.
     * @param distance The distance to travel.
     * @param heading  The heading in degrees clockwise from north.
     */
    public static LatLng computeOffset(LatLng from, double distance, double heading) {
        distance /= MathUtil.EARTH_RADIUS;
        heading = toRadians(heading);
        // http://williams.best.vwh.net/avform.htm#LL
        double fromLat = toRadians(from.getLatitude());
        double fromLng = toRadians(from.getLongitude());
        double cosDistance = cos(distance);
        double sinDistance = sin(distance);
        double sinFromLat = sin(fromLat);
        double cosFromLat = cos(fromLat);
        double sinLat = cosDistance * sinFromLat + sinDistance * cosFromLat * cos(heading);
        double dLng = atan2(
                sinDistance * cosFromLat * sin(heading),
                cosDistance - sinFromLat * sinLat);
        return new LatLng(toDegrees(asin(sinLat)), toDegrees(fromLng + dLng));
    }

    /**
     * Returns the location of origin when provided with a LatLng destination,
     * meters travelled and original heading. Headings are expressed in degrees
     * clockwise from North. This function returns null when no solution is
     * available.
     * @param to       The destination LatLng.
     * @param distance The distance travelled, in meters.
     * @param heading  The heading in degrees clockwise from north.
     */
    public static LatLng computeOffsetOrigin(LatLng to, double distance, double heading) {
        heading = toRadians(heading);
        distance /= MathUtil.EARTH_RADIUS;
        // http://lists.maptools.org/pipermail/proj/2008-October/003939.html
        double n1 = cos(distance);
        double n2 = sin(distance) * cos(heading);
        double n3 = sin(distance) * sin(heading);
        double n4 = sin(toRadians(to.getLatitude()));
        // There are two solutions for b. b = n2 * n4 +/- sqrt(), one solution results
        // in the latitude outside the [-90, 90] range. We first try one solution and
        // back off to the other if we are outside that range.
        double n12 = n1 * n1;
        double discriminant = n2 * n2 * n12 + n12 * n12 - n12 * n4 * n4;
        if (discriminant < 0) {
            // No real solution which would make sense in LatLng-space.
            return null;
        }
        double b = n2 * n4 + sqrt(discriminant);
        b /= n1 * n1 + n2 * n2;
        double a = (n4 - n2 * b) / n1;
        double fromLatRadians = atan2(a, b);
        if (fromLatRadians < -PI / 2 || fromLatRadians > PI / 2) {
            b = n2 * n4 - sqrt(discriminant);
            b /= n1 * n1 + n2 * n2;
            fromLatRadians = atan2(a, b);
        }
        if (fromLatRadians < -PI / 2 || fromLatRadians > PI / 2) {
            // No solution which would make sense in LatLng-space.
            return null;
        }
        double fromLngRadians = toRadians(to.getLongitude()) -
                atan2(n3, n1 * cos(fromLatRadians) - n2 * sin(fromLatRadians));
        return new LatLng(toDegrees(fromLatRadians), toDegrees(fromLngRadians));
    }

    /**
     * Returns the LatLng which lies the given fraction of the way between the
     * origin LatLng and the destination LatLng.
     * @param from     The LatLng from which to start.
     * @param to       The LatLng toward which to travel.
     * @param fraction A fraction of the distance to travel.
     * @return The interpolated LatLng.
     */
    public static LatLng interpolate(LatLng from, LatLng to, double fraction) {
        // http://en.wikipedia.org/wiki/Slerp
        double fromLat = toRadians(from.getLatitude());
        double fromLng = toRadians(from.getLongitude());
        double toLat = toRadians(to.getLatitude());
        double toLng = toRadians(to.getLongitude());
        double cosFromLat = cos(fromLat);
        double cosToLat = cos(toLat);

        // Computes Spherical interpolation coefficients.
        double angle = computeAngleBetween(from, to);
        double sinAngle = sin(angle);
        if (sinAngle < 1E-6) {
            return from;
        }
        double a = sin((1 - fraction) * angle) / sinAngle;
        double b = sin(fraction * angle) / sinAngle;

        // Converts from polar to vector and interpolate.
        double x = a * cosFromLat * cos(fromLng) + b * cosToLat * cos(toLng);
        double y = a * cosFromLat * sin(fromLng) + b * cosToLat * sin(toLng);
        double z = a * sin(fromLat) + b * sin(toLat);

        // Converts interpolated vector back to polar.
        double lat = atan2(z, sqrt(x * x + y * y));
        double lng = atan2(y, x);
        return new LatLng(toDegrees(lat), toDegrees(lng));
    }

    /**
     * Returns distance on the unit sphere; the arguments are in radians.
     */
    private static double distanceRadians(double lat1, double lng1, double lat2, double lng2) {
        return arcHav(havDistance(lat1, lat2, lng1 - lng2));
    }



    /**
     * Returns the angle between two LatLngs, in radians. This is the same as the distance
     * on the unit sphere.
     */
    static double computeAngleBetween(LatLng from, LatLng to) {
        return distanceRadians(toRadians(from.getLatitude()), toRadians(from.getLongitude()),
                toRadians(to.getLatitude()), toRadians(to.getLongitude()));
    }

    /**
     * Returns the distance between two LatLngs, in meters.
     */
    public static double computeDistanceBetween(LatLng from, LatLng to) {
        return computeAngleBetween(from, to) * MathUtil.EARTH_RADIUS;
    }

    /**
     * Returns the length of the given path, in meters, on Earth.
     */
    public static double computeLength(List<LatLng> path) {
        if (path.size() < 2) {
            return 0;
        }
        double length = 0;
        LatLng prev = path.get(0);
        double prevLat = toRadians(prev.getLatitude());
        double prevLng = toRadians(prev.getLongitude());
        for (LatLng point : path) {
            double lat = toRadians(point.getLatitude());
            double lng = toRadians(point.getLongitude());
            length += distanceRadians(prevLat, prevLng, lat, lng);
            prevLat = lat;
            prevLng = lng;
        }
        return length * MathUtil.EARTH_RADIUS;
    }

    public static double computeLength(List<LatLng> path, LatLng pt, int index) {
        if (path.size() < 2) {
            return 0;
        }
        double length = 0;
        LatLng prev = pt;
        double prevLat = toRadians(prev.getLatitude());
        double prevLng = toRadians(prev.getLongitude());
        for (int i = index + 1, n = path.size(); i < n; i++) {
            LatLng point = path.get(i);
            double lat = toRadians(point.getLatitude());
            double lng = toRadians(point.getLongitude());
            length += distanceRadians(prevLat, prevLng, lat, lng);
            prevLat = lat;
            prevLng = lng;
        }
        return length * MathUtil.EARTH_RADIUS;
    }

    /**
     * Returns the area of a closed path on Earth.
     * @param path A closed path.
     * @return The path's area in square meters.
     */
    public static double computeArea(List<LatLng> path) {
        return abs(computeSignedArea(path));
    }

    /**
     * Returns the signed area of a closed path on Earth. The sign of the area may be used to
     * determine the orientation of the path.
     * "inside" is the surface that does not contain the South Pole.
     * @param path A closed path.
     * @return The loop's area in square meters.
     */
    public static double computeSignedArea(List<LatLng> path) {
        return computeSignedArea(path, MathUtil.EARTH_RADIUS);
    }

    /**
     * Returns the signed area of a closed path on a sphere of given radius.
     * The computed area uses the same units as the radius squared.
     * Used by SphericalUtilTest.
     */
    static double computeSignedArea(List<LatLng> path, double radius) {
        int size = path.size();
        if (size < 3) { return 0; }
        double total = 0;
        LatLng prev = path.get(size - 1);
        double prevTanLat = tan((PI / 2 - toRadians(prev.getLatitude())) / 2);
        double prevLng = toRadians(prev.getLongitude());
        // For each edge, accumulate the signed area of the triangle formed by the North Pole
        // and that edge ("polar triangle").
        for (LatLng point : path) {
            double tanLat = tan((PI / 2 - toRadians(point.getLatitude())) / 2);
            double lng = toRadians(point.getLongitude());
            total += polarTriangleArea(tanLat, lng, prevTanLat, prevLng);
            prevTanLat = tanLat;
            prevLng = lng;
        }
        return total * (radius * radius);
    }

    /**
     * Returns the signed area of a triangle which has North Pole as a vertex.
     * Formula derived from "Area of a spherical triangle given two edges and the included angle"
     * as per "Spherical Trigonometry" by Todhunter, page 71, section 103, point 2.
     * See http://books.google.com/books?id=3uBHAAAAIAAJ&pg=PA71
     * The arguments named "tan" are tan((pi/2 - latitude)/2).
     */
    private static double polarTriangleArea(double tan1, double lng1, double tan2, double lng2) {
        double deltaLng = lng1 - lng2;
        double t = tan1 * tan2;
        return 2 * atan2(t * sin(deltaLng), 1 + t * cos(deltaLng));
    }


    private static double degreeToRadians(double latLong) {
        return (Math.PI * latLong / 180.0);
    }

    private static double radiansToDegree(double latLong) {
        return (latLong * 180.0 / Math.PI);
    }

    public static double getBearing(LatLng src, LatLng des) {

        double lat1 = src.getLatitude();
        double lng1 = src.getLongitude();

        double lat2 = des.getLatitude();
        double lng2 = des.getLongitude();

        double fLat = degreeToRadians(lat1);
        double fLong = degreeToRadians(lng1);
        double tLat = degreeToRadians(lat2);
        double tLong = degreeToRadians(lng2);

        double dLon = (tLong - fLong);

        double degree = radiansToDegree(Math.atan2(sin(dLon) * cos(tLat),
                cos(fLat) * sin(tLat) - sin(fLat) * cos(tLat) * cos(dLon)));

        if (degree >= 0) {
            return degree;
        } else {
            return 360 + degree;
        }
    }

    public static double getBearing(Location src, Location des) {

        double lat1 = src.getLatitude();
        double lng1 = src.getLongitude();

        double lat2 = des.getLatitude();
        double lng2 = des.getLongitude();

        double fLat = degreeToRadians(lat1);
        double fLong = degreeToRadians(lng1);
        double tLat = degreeToRadians(lat2);
        double tLong = degreeToRadians(lng2);

        double dLon = (tLong - fLong);

        double degree = radiansToDegree(Math.atan2(sin(dLon) * cos(tLat),
                cos(fLat) * sin(tLat) - sin(fLat) * cos(tLat) * cos(dLon)));

        if (degree >= 0) {
            return degree;
        } else {
            return 360 + degree;
        }
    }

    static public LatLng closestPoint(LatLng p, LatLng s1, LatLng s2)
    {
        double factor = projectionFactor(p, s1, s2);
        if (factor > 0 && factor < 1) {
            return project(p, s1, s2, factor);
        }

        double dist0 = computeDistanceBetween(s1,p);
        double dist1 = computeDistanceBetween(s2,p);
        if (dist0 < dist1)
            return s1;
        return s2;
    }
    static public double projectionFactor(LatLng p, LatLng s1, LatLng s2)
    {
        double dx = s2.getLongitude() - s1.getLongitude();
        double dy = s2.getLatitude() - s1.getLatitude();
        double len = dx * dx + dy * dy;

        // handle zero-length segments
        if (len <= 0.0) return Double.NaN;

        double r = ( (p.getLongitude() - s1.getLongitude()) * dx + (p.getLatitude() - s1.getLatitude()) * dy )
                / len;
        return r;
    }

    static public LatLng project(LatLng p, LatLng s1, LatLng s2, double r) {
        double lon = s1.getLongitude() + r * (s2.getLongitude() - s1.getLongitude());
        double lat = s1.getLatitude() + r * (s2.getLatitude() - s1.getLatitude());
        return new LatLng(lat, lon);
    }
}
