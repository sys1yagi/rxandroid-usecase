package com.sys1yagi.rxandroid.observables

import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response
import rx.Observable
import rx.Subscriber

class HttpRequestObservable {

    static OkHttpClient CLIENT = new OkHttpClient();

    def static Observable.OnSubscribe<String> get(String url) {
        return { Subscriber<String> subscriber ->
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = CLIENT.newCall(request).execute();
            if (response.successful) {
                subscriber.onNext(response.body().string())
            } else {
                subscriber.onError(new Exception(response.message()))
            }
            subscriber.onCompleted()
        } as Observable.OnSubscribe<String>
    }
}
