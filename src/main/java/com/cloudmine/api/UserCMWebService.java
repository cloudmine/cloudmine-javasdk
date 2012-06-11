package com.cloudmine.api;

import com.cloudmine.api.rest.AsynchronousHttpClient;
import com.cloudmine.api.rest.CMURLBuilder;
import com.cloudmine.api.rest.CMWebService;
import com.cloudmine.api.rest.callbacks.WebServiceCallback;
import com.cloudmine.api.rest.response.CMResponse;
import org.apache.http.Header;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.message.BasicHeader;

import java.util.concurrent.Future;

/**
 * Copyright CloudMine LLC
 * CMUser: johnmccarthy
 * Date: 5/22/12, 10:54 AM
 */
public class UserCMWebService extends CMWebService {

    private static final String SESSION_TOKEN_HEADER_KEY = "X-CloudMine-SessionToken";


    protected final CMUserToken userToken;
    private final Header userHeader;


    public UserCMWebService(CMURLBuilder baseUrl, CMUserToken token, AsynchronousHttpClient asynchronousHttpClient) {
        super(baseUrl, asynchronousHttpClient);
        this.userToken = token;
        userHeader = new BasicHeader(SESSION_TOKEN_HEADER_KEY, token.sessionToken());
    }



    @Override
    public void addCloudMineHeader(AbstractHttpMessage message) {
        super.addCloudMineHeader(message);
        message.addHeader(userHeader);
    }

    public Future<CMResponse> asyncLogout() {
        return asyncLogout(WebServiceCallback.DO_NOTHING);
    }

    public Future<CMResponse> asyncLogout(WebServiceCallback callback) {
        return asyncLogout(userToken, callback);
    }

    @Override
    public UserCMWebService userWebService(CMUserToken token) {
        return this;
    }
}
