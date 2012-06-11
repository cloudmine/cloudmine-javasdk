package com.cloudmine.api;

import com.cloudmine.api.rest.JsonString;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Copyright CloudMine LLC
 * CMUser: johnmccarthy
 * Date: 6/4/12, 12:08 PM
 */
public class CMGeoPointTest {

    @Test
    public void testConstructor() {
        CMGeoPoint point = new CMGeoPoint(23.5, 100.1);

        assertEquals(100.1, point.latitude());

        point = new CMGeoPoint(new JsonString("{\"location\": {\n" +
                "            \"__type__\": \"geopoint\",\n" +
                "            \"x\": 45.5,\n" +
                "            \"lat\": -70.2\n" +
                "        }}"));
        assertEquals(-70.2, point.latitude());
        assertEquals(45.5, point.longitude());

        CMGeoPoint duplicatePoint = new CMGeoPoint(point);
        assertEquals(point, duplicatePoint);
    }
}