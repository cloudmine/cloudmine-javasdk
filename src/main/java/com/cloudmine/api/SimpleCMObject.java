package com.cloudmine.api;

import com.cloudmine.api.exceptions.CreationException;
import com.cloudmine.api.exceptions.JsonConversionException;
import com.cloudmine.api.rest.CMStore;
import com.cloudmine.api.rest.Json;
import com.cloudmine.api.rest.JsonString;
import com.cloudmine.api.rest.JsonUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * An object that can be inserted, updated, deleted, and retrieved from CloudMine. Has a non optional
 * object id that is used to uniquely identify the object; if one is not provided at creation time, a
 * random id is generated. May be associated with a {@link CMStore} through the use of a {@link StoreIdentifier},
 * which allows the object to be modified on the CloudMine platform.
 * Values can be added to the object through the use of the add method. In general, values will be converted
 * to JSON based on the rules defined in the <a href="www.json.org">JSON specification.</a> The special cases are
 * as follows:<br>
 * Map<String, Object> are treated as JSON objects<br>
 * Dates are converted into JSON objects<br>
 * CloudMine specific types ({@link CMGeoPoint} and {@link CMFile}) are converted to JSON objects<br>
 * SimpleCMObjects have 2 optional properties: class and type. Class is used for loading all similar objects, for
 * example through {@link CMStore#loadUserObjectsOfClass(String)}<br>
 * Type is reserved for CloudMine specific types, such as geopoints or files. It is unlikely you will need
 * to set this type yourself<br>
 *
 * <br>Copyright CloudMine LLC. All rights reserved<br> See LICENSE file included with SDK for details.
 */
public class SimpleCMObject extends CMObject {
    private static final Logger LOG = LoggerFactory.getLogger(SimpleCMObject.class);

    private final Map<String, Object> contents;
    private final Map<String, Object> topLevelMap;


    /**
     * Instantiate a new SimpleCMObject with a randomly generated objectId
     * @return a new SimpleCMObject with a randomly generated objectId
     */
    public static SimpleCMObject SimpleCMObject() {
        return new SimpleCMObject();
    }

    /**
     * Instantiate a new SimpleCMObject with the given objectId
     * @param objectId the objectId for the new SimpleCMObject
     * @return a new SimpleCMObject with the given objectId
     */
    public static SimpleCMObject SimpleCMObject(String objectId) {
        return new SimpleCMObject(objectId);
    }

    /**
     * Instantiate a new SimpleCMObject based on the given JSON.
     * If the JSON has only one entry, it is assumed to be the objectId mapped to the contents of the object, unless that single entry is not a JSON object.
     * If the JSON has more than one top level key or consists of a single key mapped to a value instead of another JSON object,
     * an objectId is generated and the given JSON is assumed to be the contents.
     * @param json valid JSON
     * @return a new SimpleCMObject
     * @throws CreationException if unable to parse the given JSON
     */
    public static SimpleCMObject SimpleCMObject(Json json) throws CreationException {
        try {
            return new SimpleCMObject(json);
        } catch (JsonConversionException e) {
            throw new CreationException(e);
        }
    }

    /**
     * Instantiate a new SimpleCMObject with the given objectId and containing the given contents
     * @param objectId identifies the SimpleCMObject
     * @param contents key value pairs that will be converted to JSON and persisted with the object
     * @return a SimpleCMObject containing the given contents with the given objectId
     * @throws CreationException if unable to map the given contents to JSON
     */
    public static SimpleCMObject SimpleCMObject(final String objectId, final Map<String, Object> contents) throws CreationException {
        return new SimpleCMObject(objectId, contents);
    }

    /**
     * Creates a SimpleCMObject from a Map. If the map has only one entry, it is assumed to be the
     * objectId mapped to the contents of the object, unless that single entry is not a Map<String, Object>.
     * If the objectMap has more than one key or consists of a single key mapped to a non string keyed map value,
     * a top level key is generated and the objectMap is assumed to be the contents.
     * @param objectMap see above
     * @return a new SimpleCMObject
     * @throws CreationException if unable to map the given contents to JSON
     */
    public static SimpleCMObject SimpleCMObject(Map<String, Object> objectMap) throws CreationException {
        return new SimpleCMObject(objectMap);
    }

    SimpleCMObject() throws CreationException {
        this(CMObject.generateUniqueObjectId());
    }

    SimpleCMObject(String objectId) throws CreationException {
        this(objectId, new HashMap<String, Object>());
    }

    SimpleCMObject(Json json) throws JsonConversionException, CreationException {
        this(JsonUtilities.jsonToMap(json));
    }

    SimpleCMObject(final String objectId, final Map<String, Object> contents) throws CreationException {
        this(new HashMap<String, Object>() {
            {
                put(objectId, contents);
            }
        });
    }

    /**
     * Creates a SimpleCMObject from a Map. If the map has only one entry, it is assumed to be the
     * objectId mapped to the contents of the object, unless that single entry is not a Map<String, Object>.
     * If the objectMap has more than one key or consists of a single key mapped to a non string keyed map value,
     * a top level key is generated and the objectMap is assumed to be the contents.
     * @param objectMap
     */
    SimpleCMObject(Map<String, Object> objectMap) throws CreationException {
        super(extractObjectId(objectMap));
        if(objectMap.size() != 1 ||
                isMappedToAnotherMap(objectMap) == false) {
            contents = objectMap;
            topLevelMap = new HashMap<String, Object>();
            topLevelMap.put(getObjectId(), contents);
        } else {
            this.topLevelMap = objectMap;
            Set<Map.Entry<String, Object>> contentSet = objectMap.entrySet();
            Map.Entry<String, Object> contentsEntry = contentSet.iterator().next();

            try {
                contents = (Map<String, Object>)contentsEntry.getValue();
            } catch(ClassCastException e) {
                throw new CreationException("Passed a topLevelMap that does not contain a Map<String, Object>", e);
            }
        }
        add(JsonUtilities.OBJECT_ID_KEY, getObjectId());
        if(this.hasField(CMObject.ACCESS_KEY)) {
            setAccessListIds(new HashSet<String>(this.<String>getList(ACCESS_KEY)));
        }
        add(CMObject.ACCESS_KEY, getAccessListIds());
    }

    private static String extractObjectId(Map<String, Object> objectMap) {
        String objectId;
        if(objectMap.containsKey(JsonUtilities.OBJECT_ID_KEY)) {
            return objectMap.get(JsonUtilities.OBJECT_ID_KEY).toString();
        }
        if(objectMap.size() != 1 ||
                isMappedToAnotherMap(objectMap) == false) {
            objectId = CMObject.generateUniqueObjectId();
        } else {
            Set<Map.Entry<String, Object>> contentSet = objectMap.entrySet();
            Map.Entry<String, Object> contentsEntry = contentSet.iterator().next();
            objectId = contentsEntry.getKey();
        }
        return objectId;
    }

    private static boolean isMappedToAnotherMap(Map<String, Object> objectMap) {
        if(objectMap.size() == 1) {
            Object mapValue = objectMap.values().iterator().next();
            if(mapValue instanceof Map) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the value associated with the top level key; This will probably be a Map of Strings to
     * Objects, but may just be a single value
     * @return the value associated with the top level key; This will probably be a Map of Strings to
     * Objects, but may just be a single value
     */
    public Object getValue() {
        return topLevelMap.get(getObjectId());
    }

    /**
     * Check whether this has the specified field at the top level
     * @param key
     * @return
     */
    public boolean hasField(String key) {
        return contents.containsKey(key);
    }

    /**
     * Get the value associated with the given key. May be null if the key does not exist
     * @param key the key for the desired value
     * @return the value associated with the given key. May be null if the key does not exist
     */
    public Object get(String key) {
        return contents.get(key);
    }

    /**
     * Get a SimpleCMObject associated with the given objectId
     * @param objectId the objectId of the SimpleCMObject
     * @return the SimpleCMObject associated with the given objectId, or null if it is not a part of this SimpleCMObject
     * @throws JsonConversionException if there is a value associated with the given objectId, but it is not representable as a SimpleCMObject
     */
    public SimpleCMObject getSimpleCMObject(String objectId) throws JsonConversionException {
        return getValue(objectId, SimpleCMObject.class);
    }

    /**
     * Get a SimpleCMObject associated with the given objectId
     * @param objectId the objectId of the SimpleCMObject
     * @param alternative will be returned instead of null if there is nothing associated with the given objectId
     * @return the SimpleCMObject associated with the given objectId, or the alternative if it is not a part of this SimpleCMObject
     * @throws JsonConversionException if there is a value associated with the given objectId, but it is not representable as a SimpleCMObject
     */
    public SimpleCMObject getSimpleCMObject(String objectId, SimpleCMObject alternative) throws JsonConversionException {
        SimpleCMObject value = getSimpleCMObject(objectId);
        return value == null ?
                alternative :
                    value;
    }

    /**
     * Get the JSON array as a List associated with the given key
     * @param key the key for the JSON array
     * @param <T> the type of the list contents
     * @return a List of type T that is associated with the given key, or null if
     */
    public <T> List<T> getList(String key) {
        return getValue(key, List.class);
    }

    /**
     * Get a number value as an Integer
     * @param key the JSON key
     * @param alternative a value to use if no Number exists for the given key
     * @return the Number associated with the key if it exists, or alternative
     */
    public Integer getInteger(String key, Integer alternative) {
        Integer value = getInteger(key);
        return value == null ?
                alternative :
                    value;
    }

    /**
     * Get a number value as an Integer
     * @param key the JSON key
     * @return the Number associated with the key if it exists, or null
     */
    public Integer getInteger(String key)  {
        return getValue(key, Integer.class);
    }

    /**
     * Get a number value as a Double
     * @param key the JSON key
     * @return the Number associated with the key if it exists, or null
     */
    public Double getDouble(String key)  {
        return getValue(key, Double.class);
    }

    /**
     * Get the first number value that is associated with one of the given keys
     * @param keys the keys to check
     * @return the first Number value that is associated with one of the given keys, or null if none exist
     */
    public Double getDouble(String... keys)  {
        for(String key : keys) {
            Double val = getDouble(key);
            if(val != null) {
                return val;
            }
        }
        return null;
    }

    /**
     * Get a number value as a Double
     * @param key the JSON key
     * @param alternative will be returned if no Number value exists for the given key
     * @return the Number associated with the key if it exists, or alternative
     */
    public Double getDouble(String key, Double alternative)  {
        Double value = getDouble(key);
        return value == null ?
                alternative :
                    value;
    }

    /**
     * Get a boolean value
     * @param key the JSON key
     * @param alternative will be returned if no boolean value exists for the given key
     * @return the boolean associated with the key if it exists, or alternative
     */
    public Boolean getBoolean(String key, Boolean alternative)  {
        Boolean value = getBoolean(key);
        return value == null ?
                alternative :
                    value;
    }

    /**
     * Returns Boolean.TRUE, Boolean.FALSE, or null if the key does not exist
     * @param key the JSON key
     * @return the associated value, or null if the key doesn't exist, or if it does exist but isn't associated with a boolean value
     */
    public Boolean getBoolean(String key) {
        return getValue(key, Boolean.class);
    }

    /**
     * Returns the String value associated with the given key, or the alternative if it doesn't exist
     * @param key the JSON key
     * @param alternative an alternative to use if the value is not found
     * @return the String value associated with the given key, or the alternative if it doesn't exist
     */
    public String getString(String key, String alternative)  {
        String string = getString(key);
        return string == null ?
                alternative :
                    string;
    }

    /**
     * Returns the String value associated with the given key, or null if it doesn't exist
     * @param key the JSON key
     * @return the String value associated with the given key, or the alternative if it doesn't exist
     */
    public String getString(String key) {
        return getValue(key, String.class);
    }

    /**
     * Returns the Date value associated with the given key, or the alternative if it doesn't exist
     * @param key the JSON key
     * @param alternative an alternative Date value to use if the given key does not exist
     * @return the Date value associated with the given key, or the alternative if it doesn't exist
     */
    public Date getDate(String key, Date alternative) {
        Date date = getDate(key);
        return date == null ?
                alternative :
                    date;
    }

    /**
     * Returns the Date value associated with the given key, null if it doesn't exist
     * @param key the JSON key
     * @return the Date value associated with the given key, or null if it doesn't exist
     */
    public Date getDate(String key) {
        return getValue(key, Date.class);
    }


    /**
     * Returns the CMGeoPoint value associated with the given key, or null if it doesn't exist
     * @param objectId the JSON key
     * @return the CMGeoPoint value associated with the given key, or null if it doesn't exist
     * @throws if there is a value associated with the objectId, but it cannot be parsed into a CMGeoPoint
     */
    public CMGeoPoint getGeoPoint(String objectId) throws JsonConversionException {
        return getValue(objectId, CMGeoPoint.class);
    }

    /**
     * Returns the CMGeoPoint value associated with the given key, or the alternative if it doesn't exist
     * @param objectId the JSON key
     * @param alternative an alternative CMGeoPoint value to use if the given key does not exist
     * @return the Date value associated with the given key, or the alternative if it doesn't exist
     * @throws if there is a value associated with the objectId, but it cannot be parsed into a CMGeoPoint
     */
    public CMGeoPoint getGeoPoint(String objectId, CMGeoPoint alternative) throws JsonConversionException {
        CMGeoPoint point = getGeoPoint(objectId);
        return point == null ?
                alternative :
                    point;
    }

    /**
     * Returns null if the key or klass are null, if the value linked to the specified key is not convertable to
     * the given klass,
     * @param key
     * @param klass
     * @param <T>
     * @return
     */
    private <T> T getValue(String key, Class<T> klass) throws JsonConversionException {
        Object value = contents.get(key);
        if(key == null || klass == null || value == null) {
            return null;
        }
        Class<? extends Object> valueClass = value.getClass();
        if(value != null &&
                SimpleCMObject.class.isAssignableFrom(klass)) {
            boolean isMap = Map.class.isAssignableFrom(valueClass);
            boolean isJson = Json.class.isAssignableFrom(valueClass);
            String valueString = isMap ?
                                    JsonUtilities.mapToJson((Map)value) :
                                        isJson ?
                                                ((Json)value).asJson() :
                                                    value.toString();

            try {
                return newFromJson(valueString, klass);
            } catch (CreationException e) {
                throw new JsonConversionException(e);
            }
        }
        if(value != null && klass.isAssignableFrom(valueClass)) {
            return (T)value;
        }
        return null;
    }

    /**
     * Returns null if json is null, klass is null, or klass isn't assignable from SimpleCMObject
     * @param json
     * @param klass
     * @param <T>
     * @return
     */
    private <T> T newFromJson(String json, Class<T> klass) throws CreationException {
        if(json == null || klass == null)
            return null;
        //Need to start at the bottom of the inheritance chain and work up
        if(CMGeoPoint.class.isAssignableFrom(klass)) {
            return (T) CMGeoPoint.CMGeoPoint(new JsonString(json));
        } else if(SimpleCMObject.class.isAssignableFrom(klass)) {
            return (T) SimpleCMObject(new JsonString(json));
        }
        return null;
    }

    /**
     * Set the class property for this SimpleCMObject. This lets you do things like load all objects of a
     * certain class, through {@link CMStore#loadApplicationObjectsOfClass(String, com.cloudmine.api.rest.callbacks.Callback)}
     * @param className the classname for this SimpleCMObject
     */
    public void setClass(String className) {
        add(JsonUtilities.CLASS_KEY, className);
    }

    /**
     * Set the type of this SimpleCMObject. Types are CloudMine specific, and it is unlikely you should be doing this yourself
     * @param type the type to use
     */
    protected void setType(CMType type) {
        add(JsonUtilities.TYPE_KEY, type.getTypeId());
    }

    /**
     * Get the type of this SimpleCMObject. If it has not been set, {@link CMType#NONE} is returned
     * @return the type of this SimpleCMObject. If it has not been set, {@link CMType#NONE} is returned
     */
    public CMType getType() {
        Object type = get(JsonUtilities.TYPE_KEY);
        return CMType.getTypeById((String) type);
    }

    /**
     * Check whether this has the specified type
     * @param type the type to check
     * @return true if getType() would return the specified type
     */
    public boolean isType(CMType type) {
        return getType().equals(type);
    }

    /**
     * Add a property to this SimpleCMObject. The value can be anything, but if it is not a CMObject, Map, Number, String, Boolean, array, or Collection
     * it will not be deserialized properly and may cause issues later on.
     * @param key to associate with the value
     * @param value the property
     * @return this
     */
    public SimpleCMObject add(String key, Object value) {
        contents.put(key, value);
        return this;
    }

    /**
     * Add another SimpleCMObject to this SimpleCMObject. The objectId will be used as the key
     * @param value a SimpleCMObject
     * @return this
     */
    public SimpleCMObject add(CMObject value) {
        add(value.getObjectId(), value);
        return this;
    }

    /**
     * Removes the object with the given key.
     * @param key of the object to remove
     * @return the removed object if it exists
     */
    public final Object remove(String key) {
        return contents.remove(key);
    }

    /**
     * Get a representation of this SimpleCMObject in the form "objectId":{contents}
     * @return a representation of this SimpleCMObject in the form "objectId":{contents}
     * @throws JsonConversionException if this SimpleCMObject cannot be converted to JSON
     */
    public String asKeyedObject() throws JsonConversionException {
        return JsonUtilities.addQuotes(getObjectId()) + ":" + asUnkeyedObject();
    }

    /**
     * Get a JSON representation of this SimpleCMObject in the form of {contents}
     * @return a JSON representation of this SimpleCMObject in the form of {contents}
     * @throws JsonConversionException if this SimpleCMObject cannot be converted to JSON
     */
    public String asUnkeyedObject() throws JsonConversionException {
        return JsonUtilities.mapToJson(contents);
    }

    public String asJson() throws JsonConversionException {
        return JsonUtilities.mapToJson(topLevelMap);
    }

    public String toString() {
        try {
            return asJson();
        } catch (JsonConversionException e) {
            return "Invalid json: " + e.getMessage();
        }
    }

    /**
     * Checks whether the internal map that represents this SimpleCMObject is the same as the passed in Map.
     * Uses this object's map.equals method
     * @param topLevelMap
     * @return true if the maps are equal, false otherwise
     */
    public boolean isSameMap(Map<String, Object> topLevelMap) {
        return this.topLevelMap.equals(topLevelMap);
    }

    @Override
    public final boolean equals(Object another) {
        //This will make it work with subclasses of SimpleCMObject, but must make equals final so child classes
        //can't provide an implementation that would mean a.equals(b) != b.equals(a)
        if(another instanceof SimpleCMObject) {
            return ((SimpleCMObject) another).isSameMap(topLevelMap);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return topLevelMap.hashCode();
    }
}
