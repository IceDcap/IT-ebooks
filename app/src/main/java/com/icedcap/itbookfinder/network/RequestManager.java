package com.icedcap.itbookfinder.network;

import android.util.Log;

import com.icedcap.itbookfinder.persistence.Constants;

import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Author: doushuqi
 * Date: 16-3-29
 * Email: shuqi.dou@singuloid.com
 * LastUpdateTime:
 * LastUpdateBy:
 */
public class RequestManager {

    private static final String TAG = "RequestManager";
    private static final String URL = "http://it-ebooks-api.info/v1/";
    private static final String search = "search/";
    private static final String book = "book/";
    private OkHttpClient mClient;
    private ReqType mReqType;
    private String mReq;

    private RequestManager(Builder builder){
        this.mClient = new OkHttpClient();
        this.mReqType = builder.mReqType;
        this.mReq = builder.mReq;
    }

    public enum ReqType {
        BOOK(book),
        SEARCH(search);

        String type;
        ReqType(final String type){
            this.type = type;
        }
        private String getType(){
            return this.type;
        }
    }

    private String buildUrlFromReqType(ReqType type, String req){
        final String str = type.getType();
        final String requestUrl = URL + str + req;
        if (Constants.DEBUG) {
            Log.d(TAG, "request url = " + requestUrl);
        }
        return requestUrl;
    }

    public String getResponse() throws IOException {
        final String url = buildUrlFromReqType(mReqType, mReq);

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = mClient.newCall(request).execute();
        return response.body().string();
    }

    public static class Builder{
        private ReqType mReqType;
        private String mReq;

        public Builder(){}

        public Builder(ReqType reqType, String req) {
            mReqType = reqType;
            mReq = req;
        }

        public Builder reqType(ReqType type){
            mReqType = type;
            return this;
        }

        public Builder req(String req){
            mReq = req;
            return this;
        }

        public RequestManager build(){
            return new RequestManager(this);
        }
    }


}
