package com.cloudmine.test;

import com.cloudmine.api.CMObject;
import com.cloudmine.api.persistance.CloudMineObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <br>
 * Copyright CloudMine LLC. All rights reserved<br>
 * See LICENSE file included with SDK for details.
 */
@CloudMineObject
public class ExtendedCMObject extends CMObject {

    private Map<String, ExtendedCMObject> otherExtendedObjects = new HashMap<String, ExtendedCMObject>();
    private String name;
    private Date date;
    private int number;

    public ExtendedCMObject() {
        this("default", new Date(), 5);
    }

    public ExtendedCMObject(String name, Date date, int number) {
        super();
        this.name = name;
        this.date = date;
        this.number = number;
    }

    public Map<String, ExtendedCMObject> getOtherExtendedObjects() {
        return otherExtendedObjects;
    }

    public void setOtherExtendedObjects(Map<String, ExtendedCMObject> otherExtendedObjects) {
        this.otherExtendedObjects = otherExtendedObjects;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExtendedCMObject that = (ExtendedCMObject) o;
        if(!getObjectId().equals(that.getObjectId())) return false;
        if (number != that.number) return false;
        if(!dateEquals(date, that.date));
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (otherExtendedObjects != null ? !otherExtendedObjects.equals(that.otherExtendedObjects) : that.otherExtendedObjects != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = otherExtendedObjects != null ? otherExtendedObjects.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + number;
        return result;
    }

    @Override
    public String toString() {
        return asJson();
    }
}