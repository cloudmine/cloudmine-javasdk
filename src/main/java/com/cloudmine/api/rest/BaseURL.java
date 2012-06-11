package com.cloudmine.api.rest;

/**
 * An entire or partial URL. Should never end with "/"
 * Copyright CloudMine LLC
 * CMUser: johnmccarthy
 * Date: 5/16/12, 11:32 AM
 */
public interface BaseURL {
    String SEPARATOR = "/";

    public String urlString();
}
