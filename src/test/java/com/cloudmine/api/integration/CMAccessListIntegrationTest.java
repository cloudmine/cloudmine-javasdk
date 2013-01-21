package com.cloudmine.api.integration;

import com.cloudmine.api.*;
import com.cloudmine.api.rest.UserCMWebService;
import com.cloudmine.api.rest.callbacks.CMObjectResponseCallback;
import com.cloudmine.api.rest.callbacks.CreationResponseCallback;
import com.cloudmine.api.rest.options.CMRequestOptions;
import com.cloudmine.api.rest.options.CMSharedDataOptions;
import com.cloudmine.api.rest.response.CMObjectResponse;
import com.cloudmine.api.rest.response.CreationResponse;
import com.cloudmine.test.ServiceTestBase;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static com.cloudmine.test.AsyncTestResultsCoordinator.waitThenAssertTestResults;
import static com.cloudmine.test.TestServiceCallback.testCallback;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * <br>
 * Copyright CloudMine LLC. All rights reserved<br>
 * See LICENSE file included with SDK for details.
 */
public class CMAccessListIntegrationTest extends ServiceTestBase {

    @Test
    public void testStoreAccessList() {
        final CMUser anotherUser = randomUser();
        anotherUser.createUser(hasSuccess);
        waitThenAssertTestResults();

        final CMUser user = user();
        user.createUser(testCallback());
        waitThenAssertTestResults();
        user.login(hasSuccess);
        waitThenAssertTestResults();

        List<String> userObjectIds = Arrays.asList("freddy", "teddy", "george", "puddin");
        CMAccessList list = new CMAccessList(user, CMAccessPermission.READ, CMAccessPermission.UPDATE);
        list.grantAccessTo(userObjectIds);
        list.grantAccessTo(anotherUser);
        list.grantPermissions(CMAccessPermission.READ);
        list.save(testCallback(new CreationResponseCallback() {
            @Override
            public void onCompletion(CreationResponse response) {
                assertTrue(response.wasSuccess());
//                assertEquals(list.getObjectId(), response.getObjectId());
            }
        }));
        waitThenAssertTestResults();


        final SimpleCMObject anObject = new SimpleCMObject();
        anObject.add("aSecret", true);
        anObject.grantAccess(list);
        System.out.println("AO: " + anObject.transportableRepresentation());
        anObject.saveWithUser(user, hasSuccessAndHasModified(anObject));
        waitThenAssertTestResults();

        anotherUser.login(hasSuccess);
        waitThenAssertTestResults();
        CMSessionToken token = anotherUser.getSessionToken();

        UserCMWebService userWebService = service.getUserWebService(token);
        userWebService.asyncLoadObject(anObject.getObjectId(), testCallback(new CMObjectResponseCallback() {
            @Override
            public void onCompletion(CMObjectResponse response) {
                assertTrue(response.hasSuccess());
                assertEquals(1, response.getObjects().size());
                CMObject loadedObject = response.getCMObject(anObject.getObjectId());
                assertEquals(anObject, loadedObject);
            }
        }), CMRequestOptions.CMRequestOptions(CMSharedDataOptions.SHARED_OPTIONS));
        waitThenAssertTestResults();

        CMRequestOptions requestOptions = new CMRequestOptions(CMSharedDataOptions.getShared());
        userWebService.asyncLoadObjects(testCallback(new CMObjectResponseCallback() {
            public void onCompletion(CMObjectResponse response) {

                assertTrue(response.hasSuccess());
                assertEquals(1, response.getObjects().size());
                CMObject loadedObject = response.getCMObject(anObject.getObjectId());
                assertEquals(anObject, loadedObject);
            }

        }), requestOptions);
        waitThenAssertTestResults();
    }


    @Test
    public void testGetAccessList() {
        CMUser user = user();
        final CMAccessList list = new CMAccessList(user, CMAccessPermission.CREATE);
        list.grantAccessTo("whatever");
        list.save(hasSuccess);
        waitThenAssertTestResults();

        user.loadAccessLists(testCallback(new CMObjectResponseCallback() {
            public void onCompletion(CMObjectResponse response) {
                assertTrue(response.wasSuccess());
                CMObject loadedList = response.getCMObject(list.getObjectId());
                assertEquals(list, loadedList);
            }
        }));
        waitThenAssertTestResults();
    }
}
