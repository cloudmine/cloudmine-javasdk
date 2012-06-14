package com.cloudmine.api;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

/**
 * Copyright CloudMine LLC
 * CMUser: johnmccarthy
 * Date: 5/21/12, 3:49 PM
 */
public class UserTokenTest {

    @Test
    public void testFromJson() {
        String json = userTokenJson();

        CMUserToken token = CMUserToken.CMUserToken(json);

        assertEquals("44c31131ecac41cf92f49b28b84ebac4", token.sessionToken());
    }

    private String userTokenJson() {
        return "{\n" +
                    "    \"session_token\": \"44c31131ecac41cf92f49b28b84ebac4\",\n" +
                    "    \"expires\": \"Tue, 13 Mar 2012 20:03:45 GMT\"\n" +
                    "}";
    }

    @Test
    public void testIsValid() {
        CMUserToken token = CMUserToken.CMUserToken("null");
        assertFalse(token.isValid());
    }
}
