package com.cloudmine.api.rest.response;

import junit.framework.Assert;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Copyright CloudMine LLC
 * CMUser: johnmccarthy
 * Date: 6/7/12, 3:46 PM
 */
public class ResponseValueTest {


    @Test
    public void testResponseValue() {
        Assert.assertEquals(ResponseValue.MISSING, ResponseValue.getValue(null));
        Assert.assertEquals(ResponseValue.CREATED, ResponseValue.getValue("created"));
        Assert.assertEquals(ResponseValue.UPDATED, ResponseValue.getValue("updated"));
    }
}
