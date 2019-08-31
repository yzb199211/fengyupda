package com.yyy.fengyupda.util;

import android.widget.Toast;

import com.yyy.fengyupda.application.BaseApplication;
import com.yyy.fengyupda.interfaces.ResponseListener;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetUtil {
    BaseApplication application = BaseApplication.getInstance();

    OkHttpClient okHttpClient = application.getClient();
    Request request;

    List<NetParams> list;
    String url;

    ResponseListener responseListener;

    /**
     * @param responseListener
     */
    public NetUtil(ResponseListener responseListener) {
        this.responseListener = responseListener;
    }

    public NetUtil() {
    }

    /**
     * 直接调用接口
     *
     * @param list
     * @param url
     * @param responseListener
     */
    public NetUtil(List<NetParams> list, String url, ResponseListener responseListener) {

        this.responseListener = responseListener;
        this.list = list;
        this.url = url;

        params(list, url, true);
    }

    public OkHttpClient getClient() {
        return okHttpClient;
    }

    /**
     * 传入请求参数和地址
     *
     * @param list
     * @param url
     * @param b
     * @return
     */
    public NetUtil params(List<NetParams> list, String url, boolean b) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                builder.addFormDataPart(list.get(i).getKey(), list.get(i).getValue());
            }
            setRequest(url, builder);
            if (b)
                getData();
            return this;
        }
        return this;
    }

    /**
     * 请求数据
     */
    public void getData() {
        if (request != null)
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    responseListener.onFail(e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    responseListener.onSuccess(response.body().string());
                }
            });
        else {
            Toast.makeText(application, "请求参数为空", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * @param url
     * @param builder
     */
    private void setRequest(String url, MultipartBody.Builder builder) {
        if (StringUtil.isNotEmpty(url))
            request = new Request.Builder()
                    .url(url)
                    .post(builder.build()).build();

    }

    private Request build() {
        if (request != null)
            return request;
        return null;
    }

    public List<NetParams> getList() {
        return list;
    }

    public String getUrl() {
        return url;
    }
}
