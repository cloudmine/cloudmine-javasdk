package com.cloudmine.api.rest;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotSame;

/**
 * Copyright CloudMine LLC
 * CMUser: johnmccarthy
 * Date: 5/16/12, 11:57 AM
 */
public class CMURLBuilderTest {

    public static final String APP_ID = "taskList";

    @Test
    public void testGet() {
        CMURLBuilder builder = new CMURLBuilder(APP_ID);
        String expectedUrl = expectedBaseUrl();
        assertEquals(expectedUrl, builder.urlString());

        expectedUrl += "/text";
        assertEquals(expectedUrl, builder.text().urlString());
    }

    @Test
    public void testSearch() {
        CMURLBuilder builder = new CMURLBuilder(APP_ID).search("[ingredients=\"chicken\"]");
        assertEquals(expectedBaseUrl() + "/search?q=%5Bingredients%3D%22chicken%22%5D", builder.urlString());
    }

    @Test
    public void testAccount() {
        CMURLBuilder builder = new CMURLBuilder(APP_ID).account();
        assertEquals(expectedBaseUrl() + "/account", builder.urlString());

        assertEquals(expectedBaseUrl() + "/account/create", builder.create().urlString());
    }

    @Test
    public void testDelete() {
        CMURLBuilder builder = new CMURLBuilder(APP_ID).deleteAll();
        String expectedUrl = expectedBaseUrl() + "/data?all=true";
        assertEquals(expectedUrl, builder.urlString());
        assertEquals(expectedUrl, builder.url().toString());
    }

    @Test
    public void testImmutable() {
        CMURLBuilder builder = new CMURLBuilder(APP_ID);

        CMURLBuilder modifiedBuilder = builder.addQuery("all", "true");
        assertNotSame(builder, modifiedBuilder);
    }

    @Test
    public void testUser() {
        CMURLBuilder builder = new CMURLBuilder(APP_ID);

        assertEquals("/" + APP_ID + "/user", builder.user().appPath());
    }

    @Test
    public void testExtractAppId() {
        assertEquals("/" + APP_ID, CMURLBuilder.extractAppId(expectedBaseUrl()));
        assertEquals("/" + APP_ID + "/user", CMURLBuilder.extractAppId(new CMURLBuilder(APP_ID).user().urlString()));
    }

    private String expectedBaseUrl() {
        return CMURLBuilder.CLOUD_MINE_URL + CMURLBuilder.DEFAULT_VERSION + CMURLBuilder.APP + BaseURL.SEPARATOR + APP_ID;
    }

}