package com.quncao.core.statistics;

import com.google.gson.Gson;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

class HttpClientHelper <T> implements IHttpClient <T> {
    private IHttpCallback<T> mHttpCallback;
    private IConverter<List<T>, String> mConverter;
    private boolean mEnableRetry = true;
    private boolean mIsBusy = false;
    private boolean mIsCanceled = false;
    private int mRetryTotalTimes = 2;
    private int mRetryCurrentTimes = mRetryTotalTimes;

    private String mUrl;
    private List<T> mRequestContent;

    private OkHttpClient mOkHttpClient;

    @Override
    public void request(String url, T content) {
        List<T> contentList = new ArrayList<>();
        contentList.add(content);
        request(url, contentList);
    }

    @Override
    public void request(String url, List<T> content) {
        setBusy(true);
        mRetryCurrentTimes = mRetryTotalTimes;

        mUrl = url;
        mRequestContent = content;
        doRequest(url, convert2JsonString(content));
    }

    /**
     * 发起网络请求
     * @param url url
     * @param content requestBody
     */
    private void doRequest(String url, String content){
        Logger.d("---request url: " + url + "  content: " + content);
        if(null == url || "".equals(url)){
            Logger.e("---Illegal argument: url---");
            return;
        }

        try{
            content = null == content ? "" : content;
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), content);
            Request request = new Request.Builder().url(url).post(requestBody)
                    .header("sign", LarkSignUtils.getSign(content)).build();
            synchronized (HttpClientHelper.class) {
                if(!mIsCanceled) {
                    mOkHttpClient = new OkHttpClient.Builder()
                            .sslSocketFactory(getSSLSocketFactory())
                            .hostnameVerifier(getHostnameVerifier())
                            .build();
                    Response response = mOkHttpClient.newCall(request).execute();
                    handleResponse(response);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            retry(e.getMessage());
        }
    }

    /**
     * 处理 response
     * <p>成功则清除本次请求的参数资源并回调</p>
     * <p>失败则 retry (是否真正 retry 在 retry 方法体内控制)</p>
     * @param response 网络响应
     */
    private void handleResponse(Response response){
        Logger.d("---response: " + response);
        try {
            HttpResultBean result = new Gson().fromJson(response.body().string(), HttpResultBean.class);
            Logger.d("---result: " + result);
            if(null != result && 200 == result.errcode) {
                mUrl = null;
                if(null != mRequestContent)
                    mRequestContent.clear();
                mRequestContent = null;
                if(null != mHttpCallback)
                    mHttpCallback.onSuccess();

                setBusy(false);
            } else {
                retry(null != result ? result.errmsg : "-UNKNOWN-"); //response.message()
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setCallback(IHttpCallback<T> callback) {
        mHttpCallback = callback;
    }

    @Override
    public void enableRetry(boolean enabled) {
        mEnableRetry = enabled;
    }

    @Override
    public void setRetryTimes(int retryTimes) {
        mRetryTotalTimes = retryTimes;
        mRetryCurrentTimes = retryTimes;
    }

    @Override
    public boolean isBusy() {
        return mIsBusy;
    }

    @Override
    public void cancel() {
        mIsCanceled = true;
        if(isBusy()) {
            synchronized (HttpClientHelper.class) {
                if(null != mOkHttpClient) {
                    mOkHttpClient.dispatcher().cancelAll();
                }
            }
            onFailed("Cancel task");
            setBusy(false);
        }
    }

    @Override
    public void setConverter(IConverter<List<T>, String> converter) {
        mConverter = converter;
    }

    private void setBusy(boolean busy){
        mIsBusy = busy;
    }

    private String convert2JsonString(List<T> data){
        if(null != mConverter){
            return mConverter.convert(data);
        }else{
            throw new IllegalStateException("---You must call setConverter(HttpRequestConverter<T> converter) before any request---");
        }
    }

    private void retry(String errMsg){
        if(mEnableRetry && mRetryCurrentTimes > 0){
            mRetryCurrentTimes--;
            doRequest(mUrl, convert2JsonString(mRequestContent));
        } else {
            onFailed(errMsg);
            setBusy(false);
        }
    }

    private void onFailed(String msg){
        if (null != mHttpCallback)
            mHttpCallback.onFailed(msg, mRequestContent);
    }

    private SSLSocketFactory getSSLSocketFactory(){
        try{
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{new MyTrustManager()}, new SecureRandom());
            return sslContext.getSocketFactory();
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private HostnameVerifier getHostnameVerifier(){
        return new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
    }

    private class MyTrustManager implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }
}
