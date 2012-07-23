package com.cloudmine.api.rest;

import com.cloudmine.api.rest.callbacks.Callback;
import com.cloudmine.api.rest.response.ResponseConstructor;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * <br>
 * Copyright CloudMine LLC. All rights reserved<br>
 * See LICENSE file included with SDK for details.
 */
public class ApacheThreadedHttpClient implements AsynchronousHttpClient {
    private static final Logger LOG = LoggerFactory.getLogger(ApacheThreadedHttpClient.class);
    private ThreadLocal<DefaultHttpClient> client = new ThreadLocal<DefaultHttpClient>() {
//        private final PoolingClientConnectionManager connectionManager = new PoolingClientConnectionManager();
//        @Override
//        public DefaultHttpClient initialValue() {
//            return new DefaultHttpClient(connectionManager);
//        }
    };
    @Override
    public <T> void executeCommand(HttpUriRequest command, Callback<T> callback, ResponseConstructor<T> constructor) {
        new Thread(new RequestRunnable<T>(command, callback, constructor)).start();
    }

    public class RequestRunnable<T>implements Runnable {
        private final Callback<T> callback;
        private final ResponseConstructor<T> constructor;
        private RequestRunnable(HttpUriRequest request, Callback<T> callback, ResponseConstructor<T> constructor) {
            this.request = request;
            this.callback = callback;
            this.constructor = constructor;
        }
        private HttpUriRequest request;

        @Override
        public void run() {
            try {
                HttpResponse response = client.get().execute(request);
                callback.onCompletion(constructor.construct(response));
            } catch (IOException e) {
                LOG.error("Exception thrown", e);
                callback.onFailure(e, "Failed");
            }
        }
    }
}
