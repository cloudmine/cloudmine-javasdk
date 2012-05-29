package com.cloudmine.api;

import com.cloudmine.api.exceptions.CreationException;
import com.cloudmine.api.rest.Json;
import com.cloudmine.api.rest.JsonUtilities;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Copyright CloudMine LLC
 * User: johnmccarthy
 * Date: 5/24/12, 1:29 PM
 */
public class SimpleCMObject implements Json {
    public static final String CLASS_KEY = "__class__";
    private final Map<String, Object> contents;
    private final Map<String, Object> topLevelMap;
    private final String topLevelKey;

    public SimpleCMObject() {
        this(null, new HashMap<String, Object>(), new HashMap<String, Object>());
    }

    public SimpleCMObject(String topLevelKey, Map<String, Object> contents, Map<String, Object> topLevelMap) {
        if(topLevelKey == null)
            topLevelKey = UUID.randomUUID().toString();
        this.topLevelKey = topLevelKey;
        this.contents = contents;
        this.topLevelMap = topLevelMap;
        topLevelMap.put(topLevelKey, contents);
    }

    public SimpleCMObject(JsonNode node) {
        this(node.toString());
    }

    public SimpleCMObject(String json) {
        this(JsonUtilities.jsonToMap(json));
    }

    public SimpleCMObject(final String topLevelKey, final Map<String, Object> contents) {
        this(new HashMap<String, Object>() {
            {
                put(topLevelKey, contents);
            }
        });
    }

    public SimpleCMObject(Map<String, Object> topLevelMap) {
        this.topLevelMap = topLevelMap;
        Set<Map.Entry<String, Object>> contentSet = topLevelMap.entrySet();
        if(contentSet.size() != 1) {
            throw new CreationException("Cannot create a CMObject from a map without exactly 1 key. Had: " + contentSet.size());
        }
        Map.Entry<String, Object> contentsEntry = contentSet.iterator().next();
        topLevelKey = contentsEntry.getKey();

        try {
            contents = (Map<String, Object>)contentsEntry.getValue();
        } catch(ClassCastException e) {
            throw new CreationException("Passed a topLevelMap that does not contain a Map<String, Object>", e);
        }
    }

    public Object get(String key) {
        return contents.get(key);
    }

    /**
     * Returns Boolean.TRUE, Boolean.FALSE, or null if the key does not exist
     * @param key
     * @return
     */
    public Boolean getBoolean(String key) {
        return getValue(key, Boolean.class);
    }

    public String getString(String key) {
        return getValue(key, String.class);
    }

    private <T> T getValue(String key, Class<T> klass) {
        Object value = contents.get(key);
        if(value != null && klass.isAssignableFrom(value.getClass())) {
            return (T)value;
        }
        return null;
    }

    public void setClass(String className) {
        add(CLASS_KEY, className);
    }

    public void add(String key, Object value) {
        if(value instanceof SimpleCMObject) {
            contents.put(key, ((SimpleCMObject)value).asJson());
        }else {
            contents.put(key, value);
        }
    }

    public String asKeyedObject() {
        return JsonUtilities.addQuotes(topLevelKey) + ":" + JsonUtilities.mapToJson(contents);
    }

    public String asJson() {
        return JsonUtilities.mapToJson(topLevelMap);
    }

    public String toString() {
        return asJson();
    }

}
