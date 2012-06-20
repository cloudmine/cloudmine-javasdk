package com.cloudmine.api;

import com.cloudmine.api.exceptions.CreationException;

/**
 * Copyright CloudMine LLC
 * User: johnmccarthy
 * Date: 6/14/12, 3:09 PM
 */
public class StoreIdentifier {
    public static final StoreIdentifier DEFAULT = new StoreIdentifier(ObjectLevel.APPLICATION, null);
    private final ObjectLevel level; //never let this be null
    private final CMSessionToken sessionToken;

    public static StoreIdentifier applicationLevel() throws CreationException {
        return DEFAULT;
    }

    private StoreIdentifier(ObjectLevel level, CMSessionToken session) throws CreationException {
        if(session == null && ObjectLevel.APPLICATION != level) {
            throw new CreationException("User cannot be null unless we are saving to ");
        }
        if(level == null) {
            level = ObjectLevel.UNKNOWN;
        }
        this.level = level;
        this.sessionToken = session;
    }

    public StoreIdentifier(CMSessionToken session) throws CreationException {
        this(ObjectLevel.USER, session);
    }

    public StoreIdentifier() throws CreationException {
        this(ObjectLevel.APPLICATION, null);
    }

    public boolean isApplicationLevel() {
        return ObjectLevel.APPLICATION == level();
    }

    public boolean isUserLevel() {
        return ObjectLevel.USER == level();
    }

    public boolean isLevel(ObjectLevel level) {
        return level().equals(level);
    }

    public ObjectLevel level() {
        return level;
    }

    public CMSessionToken userToken() {
        return sessionToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StoreIdentifier that = (StoreIdentifier) o;

        if (level != that.level) return false;
        if (sessionToken != null ? !sessionToken.equals(that.sessionToken) : that.sessionToken != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = level != null ? level.hashCode() : 0;
        result = 31 * result + (sessionToken != null ? sessionToken.hashCode() : 0);
        return result;
    }
}
