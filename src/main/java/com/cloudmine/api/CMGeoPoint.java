package com.cloudmine.api;

import com.cloudmine.api.exceptions.CreationException;
import com.cloudmine.api.exceptions.JsonConversionException;
import com.cloudmine.api.rest.Json;

/**
 * A SimpleCMObject that has location information associated with it. This allows you to perform
 * geographic searches for this data. For more information, including search information, view the
 * <a href="https://cloudmine.me/docs/data-storage#ref/query_geo">CloudMine geo query documentation</a>
 * Copyright CloudMine LLC
 */
public class CMGeoPoint extends SimpleCMObject {
    private static final String[] LATITUDE_KEYS = {"latitude", "lat", "y"};
    private static final String[] LONGITUDE_KEYS = {"longitude", "lon", "x"};
    public static final String GEOPOINT_TYPE = "geopoint";
    public static final String GEOPOINT_CLASS = "CMGeoPoint";
    public static final String LONGITUDE_KEY = "longitude";
    public static final String LATITUDE_KEY = "latitude";

    /**
     * Instantiate a CMGeoPoint with the given latitude and longitude and a random unique objectId
     * @param longitude a double between [-180, 180)
     * @param latitude a double between [-90, 90]
     * @return a new CMGeoPoint
     */
    public static CMGeoPoint CMGeoPoint(double longitude, double latitude) {
        return new CMGeoPoint(longitude, latitude);
    }

    /**
     * Instantiate a CMGeoPoint with the given latitude and longitude and the given objectId
     * @param longitude a double between [-180, 180)
     * @param latitude a double between [-90, 90]
     * @param objectId the objectId. If null, a random unique objectId will be generated
     * @return a new CMGeoPoint
     */
    public static CMGeoPoint CMGeoPoint(double longitude, double latitude, String objectId) {
        return new CMGeoPoint(longitude, latitude, objectId);
    }

    /**
     * Instantiate a new CMGeoPoint based on the given JSON. The JSON must include a top level key, a "__type__":"geopoint" property,
     * and a latitude and longitude.
     * @param json a valid JSON representation of a CMGeoPoint
     * @return a new CMGeoPoint
     * @throws CreationException if given improper JSON; either malformed or lacking a __type__, latitude, or longitude.
     */
    public static CMGeoPoint CMGeoPoint(Json json) throws CreationException {
        try {
            return new CMGeoPoint(json);
        } catch (JsonConversionException e) {
            throw new CreationException(e);
        }
    }

    public static void main(String... args) {
    }

    CMGeoPoint(double longitude, double latitude) throws CreationException {
        this(longitude, latitude, generateUniqueObjectId());
    }

    CMGeoPoint(double longitude, double latitude, String key) {
        super(key);
        setClass(GEOPOINT_CLASS);
        setType(CMType.GEO_POINT);
        add(LONGITUDE_KEY, longitude);
        add(LATITUDE_KEY, latitude);
    }

    /**
     * Constructs a geopoint from the given Json. Is assumed to be in the format { "key": {geopoint json}}
     * @param json
     * @throws JsonConversionException
     */
    CMGeoPoint(Json json) throws JsonConversionException, CreationException {
        super(json);
        boolean isMissingAnything = !(isType(CMType.GEO_POINT) && hasLatitude() && hasLongitude());
        if(isMissingAnything) {
            throw new JsonConversionException("Given non geopoint class to construct geopoint: " + json);
        }
        setClass(GEOPOINT_CLASS);
        setType(CMType.GEO_POINT);
    }

    private boolean hasLatitude() {
        return hasKeyNumber(LATITUDE_KEYS);
    }

    private boolean hasLongitude() {
        return hasKeyNumber(LONGITUDE_KEYS);
    }

    private boolean hasKeyNumber(String[] keys) {
        for(int i = 0; i < keys.length; i++) {
            Object toGet = get(keys[i]);
            if(toGet != null && toGet instanceof Number) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the longitude
     * @return the longitude
     * @throws JsonConversionException if there is a longitude value, but it is not a double
     */
    public double getLongitude() throws JsonConversionException {
        return getDouble(LONGITUDE_KEYS);
    }

    /**
     * Get the latitude
     * @return the latitude
     * @throws JsonConversionException if there is a latitude value, but it is not a double
     */
    public double getLatitude() throws JsonConversionException {
        return getDouble(LATITUDE_KEYS);
    }

    /**
     * A string representation of this GeoPoint
     * @return longitude, latitude
     * @throws JsonConversionException if a latitude/longitude value exists, but are not doubles
     */
    public String getLocationString() throws JsonConversionException {
        return getLongitude() + ", " + getLatitude();
    }
}
