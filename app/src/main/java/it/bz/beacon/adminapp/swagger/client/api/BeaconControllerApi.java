// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

/*
 * Beacon Suedtirol API
 * The API for the Beacon Suedtirol project for configuring beacons and accessing beacon data.
 *
 * OpenAPI spec version: 0.1
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package it.bz.beacon.adminapp.swagger.client.api;

import it.bz.beacon.adminapp.swagger.client.ApiCallback;
import it.bz.beacon.adminapp.swagger.client.ApiClient;
import it.bz.beacon.adminapp.swagger.client.ApiException;
import it.bz.beacon.adminapp.swagger.client.ApiResponse;
import it.bz.beacon.adminapp.swagger.client.Configuration;
import it.bz.beacon.adminapp.swagger.client.Pair;
import it.bz.beacon.adminapp.swagger.client.ProgressRequestBody;
import it.bz.beacon.adminapp.swagger.client.ProgressResponseBody;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;


import it.bz.beacon.adminapp.swagger.client.model.Beacon;
import it.bz.beacon.adminapp.swagger.client.model.BeaconUpdate;
import it.bz.beacon.adminapp.swagger.client.model.ManufacturerOrder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeaconControllerApi {
    private ApiClient apiClient;

    public BeaconControllerApi() {
        this(Configuration.getDefaultApiClient());
    }

    public BeaconControllerApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Build call for createUsingPOST
     * @param order order (required)
     * @param progressListener Progress listener
     * @param progressRequestListener Progress request listener
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public com.squareup.okhttp.Call createUsingPOSTCall(ManufacturerOrder order, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = order;

        // create path and map variables
        String localVarPath = "/v1/admin/beacons/createByOrder";

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();

        Map<String, String> localVarHeaderParams = new HashMap<String, String>();

        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = {
            "application/json"
        };
        final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) localVarHeaderParams.put("Accept", localVarAccept);

        final String[] localVarContentTypes = {
            "application/json"
        };
        final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
        localVarHeaderParams.put("Content-Type", localVarContentType);

        if(progressListener != null) {
            apiClient.getHttpClient().networkInterceptors().add(new com.squareup.okhttp.Interceptor() {
                @Override
                public com.squareup.okhttp.Response intercept(Chain chain) throws IOException {
                    com.squareup.okhttp.Response originalResponse = chain.proceed(chain.request());
                    return originalResponse.newBuilder()
                    .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                    .build();
                }
            });
        }

        String[] localVarAuthNames = new String[] { "JWT" };
        return apiClient.buildCall(localVarPath, "POST", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAuthNames, progressRequestListener);
    }

    @SuppressWarnings("rawtypes")
    private com.squareup.okhttp.Call createUsingPOSTValidateBeforeCall(ManufacturerOrder order, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        
        // verify the required parameter 'order' is set
        if (order == null) {
            throw new ApiException("Missing the required parameter 'order' when calling createUsingPOST(Async)");
        }
        

        com.squareup.okhttp.Call call = createUsingPOSTCall(order, progressListener, progressRequestListener);
        return call;

    }

    /**
     * Create a beacon
     * 
     * @param order order (required)
     * @return List&lt;Beacon&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public List<Beacon> createUsingPOST(ManufacturerOrder order) throws ApiException {
        ApiResponse<List<Beacon>> resp = createUsingPOSTWithHttpInfo(order);
        return resp.getData();
    }

    /**
     * Create a beacon
     * 
     * @param order order (required)
     * @return ApiResponse&lt;List&lt;Beacon&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<List<Beacon>> createUsingPOSTWithHttpInfo(ManufacturerOrder order) throws ApiException {
        com.squareup.okhttp.Call call = createUsingPOSTValidateBeforeCall(order, null, null);
        Type localVarReturnType = new TypeToken<List<Beacon>>(){}.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * Create a beacon (asynchronously)
     * 
     * @param order order (required)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call createUsingPOSTAsync(ManufacturerOrder order, final ApiCallback<List<Beacon>> callback) throws ApiException {

        ProgressResponseBody.ProgressListener progressListener = null;
        ProgressRequestBody.ProgressRequestListener progressRequestListener = null;

        if (callback != null) {
            progressListener = new ProgressResponseBody.ProgressListener() {
                @Override
                public void update(long bytesRead, long contentLength, boolean done) {
                    callback.onDownloadProgress(bytesRead, contentLength, done);
                }
            };

            progressRequestListener = new ProgressRequestBody.ProgressRequestListener() {
                @Override
                public void onRequestProgress(long bytesWritten, long contentLength, boolean done) {
                    callback.onUploadProgress(bytesWritten, contentLength, done);
                }
            };
        }

        com.squareup.okhttp.Call call = createUsingPOSTValidateBeforeCall(order, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<List<Beacon>>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
    /**
     * Build call for getListUsingGET
     * @param groupId groupId (optional)
     * @param progressListener Progress listener
     * @param progressRequestListener Progress request listener
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public com.squareup.okhttp.Call getListUsingGETCall(Long groupId, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/v1/admin/beacons";

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        if (groupId != null)
        localVarQueryParams.addAll(apiClient.parameterToPair("groupId", groupId));

        Map<String, String> localVarHeaderParams = new HashMap<String, String>();

        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = {
            "application/json"
        };
        final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) localVarHeaderParams.put("Accept", localVarAccept);

        final String[] localVarContentTypes = {
            
        };
        final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
        localVarHeaderParams.put("Content-Type", localVarContentType);

        if(progressListener != null) {
            apiClient.getHttpClient().networkInterceptors().add(new com.squareup.okhttp.Interceptor() {
                @Override
                public com.squareup.okhttp.Response intercept(Chain chain) throws IOException {
                    com.squareup.okhttp.Response originalResponse = chain.proceed(chain.request());
                    return originalResponse.newBuilder()
                    .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                    .build();
                }
            });
        }

        String[] localVarAuthNames = new String[] { "JWT" };
        return apiClient.buildCall(localVarPath, "GET", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAuthNames, progressRequestListener);
    }

    @SuppressWarnings("rawtypes")
    private com.squareup.okhttp.Call getListUsingGETValidateBeforeCall(Long groupId, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        

        com.squareup.okhttp.Call call = getListUsingGETCall(groupId, progressListener, progressRequestListener);
        return call;

    }

    /**
     * View a list of available beacons
     * 
     * @param groupId groupId (optional)
     * @return List&lt;Beacon&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public List<Beacon> getListUsingGET(Long groupId) throws ApiException {
        ApiResponse<List<Beacon>> resp = getListUsingGETWithHttpInfo(groupId);
        return resp.getData();
    }

    /**
     * View a list of available beacons
     * 
     * @param groupId groupId (optional)
     * @return ApiResponse&lt;List&lt;Beacon&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<List<Beacon>> getListUsingGETWithHttpInfo(Long groupId) throws ApiException {
        com.squareup.okhttp.Call call = getListUsingGETValidateBeforeCall(groupId, null, null);
        Type localVarReturnType = new TypeToken<List<Beacon>>(){}.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * View a list of available beacons (asynchronously)
     * 
     * @param groupId groupId (optional)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call getListUsingGETAsync(Long groupId, final ApiCallback<List<Beacon>> callback) throws ApiException {

        ProgressResponseBody.ProgressListener progressListener = null;
        ProgressRequestBody.ProgressRequestListener progressRequestListener = null;

        if (callback != null) {
            progressListener = new ProgressResponseBody.ProgressListener() {
                @Override
                public void update(long bytesRead, long contentLength, boolean done) {
                    callback.onDownloadProgress(bytesRead, contentLength, done);
                }
            };

            progressRequestListener = new ProgressRequestBody.ProgressRequestListener() {
                @Override
                public void onRequestProgress(long bytesWritten, long contentLength, boolean done) {
                    callback.onUploadProgress(bytesWritten, contentLength, done);
                }
            };
        }

        com.squareup.okhttp.Call call = getListUsingGETValidateBeforeCall(groupId, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<List<Beacon>>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
    /**
     * Build call for getUsingGET
     * @param id id (required)
     * @param progressListener Progress listener
     * @param progressRequestListener Progress request listener
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public com.squareup.okhttp.Call getUsingGETCall(String id, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/v1/admin/beacons/{id}"
            .replaceAll("\\{" + "id" + "\\}", apiClient.escapeString(id.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();

        Map<String, String> localVarHeaderParams = new HashMap<String, String>();

        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = {
            "application/json"
        };
        final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) localVarHeaderParams.put("Accept", localVarAccept);

        final String[] localVarContentTypes = {
            
        };
        final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
        localVarHeaderParams.put("Content-Type", localVarContentType);

        if(progressListener != null) {
            apiClient.getHttpClient().networkInterceptors().add(new com.squareup.okhttp.Interceptor() {
                @Override
                public com.squareup.okhttp.Response intercept(Chain chain) throws IOException {
                    com.squareup.okhttp.Response originalResponse = chain.proceed(chain.request());
                    return originalResponse.newBuilder()
                    .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                    .build();
                }
            });
        }

        String[] localVarAuthNames = new String[] { "JWT" };
        return apiClient.buildCall(localVarPath, "GET", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAuthNames, progressRequestListener);
    }

    @SuppressWarnings("rawtypes")
    private com.squareup.okhttp.Call getUsingGETValidateBeforeCall(String id, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        
        // verify the required parameter 'id' is set
        if (id == null) {
            throw new ApiException("Missing the required parameter 'id' when calling getUsingGET(Async)");
        }
        

        com.squareup.okhttp.Call call = getUsingGETCall(id, progressListener, progressRequestListener);
        return call;

    }

    /**
     * Search a beacon with an ID
     * 
     * @param id id (required)
     * @return Beacon
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public Beacon getUsingGET(String id) throws ApiException {
        ApiResponse<Beacon> resp = getUsingGETWithHttpInfo(id);
        return resp.getData();
    }

    /**
     * Search a beacon with an ID
     * 
     * @param id id (required)
     * @return ApiResponse&lt;Beacon&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<Beacon> getUsingGETWithHttpInfo(String id) throws ApiException {
        com.squareup.okhttp.Call call = getUsingGETValidateBeforeCall(id, null, null);
        Type localVarReturnType = new TypeToken<Beacon>(){}.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * Search a beacon with an ID (asynchronously)
     * 
     * @param id id (required)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call getUsingGETAsync(String id, final ApiCallback<Beacon> callback) throws ApiException {

        ProgressResponseBody.ProgressListener progressListener = null;
        ProgressRequestBody.ProgressRequestListener progressRequestListener = null;

        if (callback != null) {
            progressListener = new ProgressResponseBody.ProgressListener() {
                @Override
                public void update(long bytesRead, long contentLength, boolean done) {
                    callback.onDownloadProgress(bytesRead, contentLength, done);
                }
            };

            progressRequestListener = new ProgressRequestBody.ProgressRequestListener() {
                @Override
                public void onRequestProgress(long bytesWritten, long contentLength, boolean done) {
                    callback.onUploadProgress(bytesWritten, contentLength, done);
                }
            };
        }

        com.squareup.okhttp.Call call = getUsingGETValidateBeforeCall(id, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<Beacon>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
    /**
     * Build call for updateUsingPATCH
     * @param beaconUpdate beaconUpdate (required)
     * @param id id (required)
     * @param progressListener Progress listener
     * @param progressRequestListener Progress request listener
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public com.squareup.okhttp.Call updateUsingPATCHCall(BeaconUpdate beaconUpdate, String id, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = beaconUpdate;

        // create path and map variables
        String localVarPath = "/v1/admin/beacons/{id}"
            .replaceAll("\\{" + "id" + "\\}", apiClient.escapeString(id.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();

        Map<String, String> localVarHeaderParams = new HashMap<String, String>();

        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = {
            "application/json"
        };
        final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) localVarHeaderParams.put("Accept", localVarAccept);

        final String[] localVarContentTypes = {
            "application/json"
        };
        final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
        localVarHeaderParams.put("Content-Type", localVarContentType);

        if(progressListener != null) {
            apiClient.getHttpClient().networkInterceptors().add(new com.squareup.okhttp.Interceptor() {
                @Override
                public com.squareup.okhttp.Response intercept(Chain chain) throws IOException {
                    com.squareup.okhttp.Response originalResponse = chain.proceed(chain.request());
                    return originalResponse.newBuilder()
                    .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                    .build();
                }
            });
        }

        String[] localVarAuthNames = new String[] { "JWT" };
        return apiClient.buildCall(localVarPath, "PATCH", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAuthNames, progressRequestListener);
    }

    @SuppressWarnings("rawtypes")
    private com.squareup.okhttp.Call updateUsingPATCHValidateBeforeCall(BeaconUpdate beaconUpdate, String id, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        
        // verify the required parameter 'beaconUpdate' is set
        if (beaconUpdate == null) {
            throw new ApiException("Missing the required parameter 'beaconUpdate' when calling updateUsingPATCH(Async)");
        }
        
        // verify the required parameter 'id' is set
        if (id == null) {
            throw new ApiException("Missing the required parameter 'id' when calling updateUsingPATCH(Async)");
        }
        

        com.squareup.okhttp.Call call = updateUsingPATCHCall(beaconUpdate, id, progressListener, progressRequestListener);
        return call;

    }

    /**
     * Update a beacon
     * 
     * @param beaconUpdate beaconUpdate (required)
     * @param id id (required)
     * @return Beacon
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public Beacon updateUsingPATCH(BeaconUpdate beaconUpdate, String id) throws ApiException {
        ApiResponse<Beacon> resp = updateUsingPATCHWithHttpInfo(beaconUpdate, id);
        return resp.getData();
    }

    /**
     * Update a beacon
     * 
     * @param beaconUpdate beaconUpdate (required)
     * @param id id (required)
     * @return ApiResponse&lt;Beacon&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<Beacon> updateUsingPATCHWithHttpInfo(BeaconUpdate beaconUpdate, String id) throws ApiException {
        com.squareup.okhttp.Call call = updateUsingPATCHValidateBeforeCall(beaconUpdate, id, null, null);
        Type localVarReturnType = new TypeToken<Beacon>(){}.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * Update a beacon (asynchronously)
     * 
     * @param beaconUpdate beaconUpdate (required)
     * @param id id (required)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call updateUsingPATCHAsync(BeaconUpdate beaconUpdate, String id, final ApiCallback<Beacon> callback) throws ApiException {

        ProgressResponseBody.ProgressListener progressListener = null;
        ProgressRequestBody.ProgressRequestListener progressRequestListener = null;

        if (callback != null) {
            progressListener = new ProgressResponseBody.ProgressListener() {
                @Override
                public void update(long bytesRead, long contentLength, boolean done) {
                    callback.onDownloadProgress(bytesRead, contentLength, done);
                }
            };

            progressRequestListener = new ProgressRequestBody.ProgressRequestListener() {
                @Override
                public void onRequestProgress(long bytesWritten, long contentLength, boolean done) {
                    callback.onUploadProgress(bytesWritten, contentLength, done);
                }
            };
        }

        com.squareup.okhttp.Call call = updateUsingPATCHValidateBeforeCall(beaconUpdate, id, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<Beacon>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
}
