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


import it.bz.beacon.adminapp.swagger.client.model.AuthenticationRequest;
import it.bz.beacon.adminapp.swagger.client.model.AuthenticationToken;
import it.bz.beacon.adminapp.swagger.client.model.AuthenticationTokenCheck;
import it.bz.beacon.adminapp.swagger.client.model.AuthenticationTokenCheckRequest;
import it.bz.beacon.adminapp.swagger.client.model.BaseMessage;
import it.bz.beacon.adminapp.swagger.client.model.ResetPasswordChange;
import it.bz.beacon.adminapp.swagger.client.model.ResetPasswordRequest;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthControllerApi {
    private ApiClient apiClient;

    public AuthControllerApi() {
        this(Configuration.getDefaultApiClient());
    }

    public AuthControllerApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Build call for checkTokenUsingPOST
     * @param request request (required)
     * @param progressListener Progress listener
     * @param progressRequestListener Progress request listener
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public com.squareup.okhttp.Call checkTokenUsingPOSTCall(AuthenticationTokenCheckRequest request, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = request;

        // create path and map variables
        String localVarPath = "/v1/checkToken";

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

        String[] localVarAuthNames = new String[] {  };
        return apiClient.buildCall(localVarPath, "POST", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAuthNames, progressRequestListener);
    }

    @SuppressWarnings("rawtypes")
    private com.squareup.okhttp.Call checkTokenUsingPOSTValidateBeforeCall(AuthenticationTokenCheckRequest request, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        
        // verify the required parameter 'request' is set
        if (request == null) {
            throw new ApiException("Missing the required parameter 'request' when calling checkTokenUsingPOST(Async)");
        }
        

        com.squareup.okhttp.Call call = checkTokenUsingPOSTCall(request, progressListener, progressRequestListener);
        return call;

    }

    /**
     * Check whether a token is valid or not
     * 
     * @param request request (required)
     * @return AuthenticationTokenCheck
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public AuthenticationTokenCheck checkTokenUsingPOST(AuthenticationTokenCheckRequest request) throws ApiException {
        ApiResponse<AuthenticationTokenCheck> resp = checkTokenUsingPOSTWithHttpInfo(request);
        return resp.getData();
    }

    /**
     * Check whether a token is valid or not
     * 
     * @param request request (required)
     * @return ApiResponse&lt;AuthenticationTokenCheck&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<AuthenticationTokenCheck> checkTokenUsingPOSTWithHttpInfo(AuthenticationTokenCheckRequest request) throws ApiException {
        com.squareup.okhttp.Call call = checkTokenUsingPOSTValidateBeforeCall(request, null, null);
        Type localVarReturnType = new TypeToken<AuthenticationTokenCheck>(){}.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * Check whether a token is valid or not (asynchronously)
     * 
     * @param request request (required)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call checkTokenUsingPOSTAsync(AuthenticationTokenCheckRequest request, final ApiCallback<AuthenticationTokenCheck> callback) throws ApiException {

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

        com.squareup.okhttp.Call call = checkTokenUsingPOSTValidateBeforeCall(request, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<AuthenticationTokenCheck>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
    /**
     * Build call for resetPasswordChangeUsingPOST
     * @param resetPasswordChange resetPasswordChange (required)
     * @param progressListener Progress listener
     * @param progressRequestListener Progress request listener
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public com.squareup.okhttp.Call resetPasswordChangeUsingPOSTCall(ResetPasswordChange resetPasswordChange, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = resetPasswordChange;

        // create path and map variables
        String localVarPath = "/v1/resetPassword/change";

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

        String[] localVarAuthNames = new String[] {  };
        return apiClient.buildCall(localVarPath, "POST", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAuthNames, progressRequestListener);
    }

    @SuppressWarnings("rawtypes")
    private com.squareup.okhttp.Call resetPasswordChangeUsingPOSTValidateBeforeCall(ResetPasswordChange resetPasswordChange, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        
        // verify the required parameter 'resetPasswordChange' is set
        if (resetPasswordChange == null) {
            throw new ApiException("Missing the required parameter 'resetPasswordChange' when calling resetPasswordChangeUsingPOST(Async)");
        }
        

        com.squareup.okhttp.Call call = resetPasswordChangeUsingPOSTCall(resetPasswordChange, progressListener, progressRequestListener);
        return call;

    }

    /**
     * Check whether a token is valid or not
     * 
     * @param resetPasswordChange resetPasswordChange (required)
     * @return BaseMessage
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public BaseMessage resetPasswordChangeUsingPOST(ResetPasswordChange resetPasswordChange) throws ApiException {
        ApiResponse<BaseMessage> resp = resetPasswordChangeUsingPOSTWithHttpInfo(resetPasswordChange);
        return resp.getData();
    }

    /**
     * Check whether a token is valid or not
     * 
     * @param resetPasswordChange resetPasswordChange (required)
     * @return ApiResponse&lt;BaseMessage&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<BaseMessage> resetPasswordChangeUsingPOSTWithHttpInfo(ResetPasswordChange resetPasswordChange) throws ApiException {
        com.squareup.okhttp.Call call = resetPasswordChangeUsingPOSTValidateBeforeCall(resetPasswordChange, null, null);
        Type localVarReturnType = new TypeToken<BaseMessage>(){}.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * Check whether a token is valid or not (asynchronously)
     * 
     * @param resetPasswordChange resetPasswordChange (required)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call resetPasswordChangeUsingPOSTAsync(ResetPasswordChange resetPasswordChange, final ApiCallback<BaseMessage> callback) throws ApiException {

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

        com.squareup.okhttp.Call call = resetPasswordChangeUsingPOSTValidateBeforeCall(resetPasswordChange, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<BaseMessage>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
    /**
     * Build call for resetPasswordRequestUsingPOST
     * @param resetPasswordRequest resetPasswordRequest (required)
     * @param progressListener Progress listener
     * @param progressRequestListener Progress request listener
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public com.squareup.okhttp.Call resetPasswordRequestUsingPOSTCall(ResetPasswordRequest resetPasswordRequest, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = resetPasswordRequest;

        // create path and map variables
        String localVarPath = "/v1/resetPassword/request";

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

        String[] localVarAuthNames = new String[] {  };
        return apiClient.buildCall(localVarPath, "POST", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAuthNames, progressRequestListener);
    }

    @SuppressWarnings("rawtypes")
    private com.squareup.okhttp.Call resetPasswordRequestUsingPOSTValidateBeforeCall(ResetPasswordRequest resetPasswordRequest, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        
        // verify the required parameter 'resetPasswordRequest' is set
        if (resetPasswordRequest == null) {
            throw new ApiException("Missing the required parameter 'resetPasswordRequest' when calling resetPasswordRequestUsingPOST(Async)");
        }
        

        com.squareup.okhttp.Call call = resetPasswordRequestUsingPOSTCall(resetPasswordRequest, progressListener, progressRequestListener);
        return call;

    }

    /**
     * Check whether a token is valid or not
     * 
     * @param resetPasswordRequest resetPasswordRequest (required)
     * @return BaseMessage
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public BaseMessage resetPasswordRequestUsingPOST(ResetPasswordRequest resetPasswordRequest) throws ApiException {
        ApiResponse<BaseMessage> resp = resetPasswordRequestUsingPOSTWithHttpInfo(resetPasswordRequest);
        return resp.getData();
    }

    /**
     * Check whether a token is valid or not
     * 
     * @param resetPasswordRequest resetPasswordRequest (required)
     * @return ApiResponse&lt;BaseMessage&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<BaseMessage> resetPasswordRequestUsingPOSTWithHttpInfo(ResetPasswordRequest resetPasswordRequest) throws ApiException {
        com.squareup.okhttp.Call call = resetPasswordRequestUsingPOSTValidateBeforeCall(resetPasswordRequest, null, null);
        Type localVarReturnType = new TypeToken<BaseMessage>(){}.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * Check whether a token is valid or not (asynchronously)
     * 
     * @param resetPasswordRequest resetPasswordRequest (required)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call resetPasswordRequestUsingPOSTAsync(ResetPasswordRequest resetPasswordRequest, final ApiCallback<BaseMessage> callback) throws ApiException {

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

        com.squareup.okhttp.Call call = resetPasswordRequestUsingPOSTValidateBeforeCall(resetPasswordRequest, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<BaseMessage>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
    /**
     * Build call for signinUsingPOST
     * @param data data (required)
     * @param progressListener Progress listener
     * @param progressRequestListener Progress request listener
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public com.squareup.okhttp.Call signinUsingPOSTCall(AuthenticationRequest data, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = data;

        // create path and map variables
        String localVarPath = "/v1/signin";

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();

        Map<String, String> localVarHeaderParams = new HashMap<String, String>();

        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = {
            "*/*"
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

        String[] localVarAuthNames = new String[] {  };
        return apiClient.buildCall(localVarPath, "POST", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAuthNames, progressRequestListener);
    }

    @SuppressWarnings("rawtypes")
    private com.squareup.okhttp.Call signinUsingPOSTValidateBeforeCall(AuthenticationRequest data, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        
        // verify the required parameter 'data' is set
        if (data == null) {
            throw new ApiException("Missing the required parameter 'data' when calling signinUsingPOST(Async)");
        }
        

        com.squareup.okhttp.Call call = signinUsingPOSTCall(data, progressListener, progressRequestListener);
        return call;

    }

    /**
     * signin
     * 
     * @param data data (required)
     * @return AuthenticationToken
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public AuthenticationToken signinUsingPOST(AuthenticationRequest data) throws ApiException {
        ApiResponse<AuthenticationToken> resp = signinUsingPOSTWithHttpInfo(data);
        return resp.getData();
    }

    /**
     * signin
     * 
     * @param data data (required)
     * @return ApiResponse&lt;AuthenticationToken&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<AuthenticationToken> signinUsingPOSTWithHttpInfo(AuthenticationRequest data) throws ApiException {
        com.squareup.okhttp.Call call = signinUsingPOSTValidateBeforeCall(data, null, null);
        Type localVarReturnType = new TypeToken<AuthenticationToken>(){}.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * signin (asynchronously)
     * 
     * @param data data (required)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call signinUsingPOSTAsync(AuthenticationRequest data, final ApiCallback<AuthenticationToken> callback) throws ApiException {

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

        com.squareup.okhttp.Call call = signinUsingPOSTValidateBeforeCall(data, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<AuthenticationToken>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
}
