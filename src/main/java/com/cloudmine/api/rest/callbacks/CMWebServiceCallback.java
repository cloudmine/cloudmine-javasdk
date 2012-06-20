package com.cloudmine.api.rest.callbacks;


import com.cloudmine.api.rest.response.ResponseConstructor;

/**
 * Base class for all Callback classes
 * Copyright CloudMine LLC
 */
public abstract class CMWebServiceCallback<T> implements WebServiceCallback<T> {
    private final ResponseConstructor<T> constructor;

    /**
     * Classes that extend this should have a noargs constructor that provides this constructor based on T
     * @param constructor a way of constructing T from an {@link org.apache.http.HttpResponse}
     */
    public CMWebServiceCallback(ResponseConstructor<T> constructor) {
        this.constructor = constructor;
    }


    @Override
    public void onCompletion(T response) {

    }

    @Override
    public void onFailure(Throwable thrown, String message) {

    }

    public ResponseConstructor<T> constructor() {
        return constructor;
    }
}
