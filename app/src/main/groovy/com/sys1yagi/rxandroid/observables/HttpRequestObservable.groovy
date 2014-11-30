package com.sys1yagi.rxandroid.observables

import com.ning.http.client.AsyncHttpClient
import com.ning.http.client.Response
import org.apache.http.HttpStatus
import rx.Subscriber
import rx.Observable

import java.util.concurrent.Future


class HttpRequestObservable {
    def static Observable.OnSubscribe<String> get(String url) {
        return { Subscriber<String> subscriber ->
            AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
            Future<Response> future = asyncHttpClient.prepareGet(url).execute();
            Response response = future.get()
            if (response.getStatusCode() == HttpStatus.SC_OK) {
                subscriber.onNext(response.responseBody)
            } else {
                subscriber.onError(new Exception(response.getStatusText()))
            }
            subscriber.onCompleted()
        } as Observable.OnSubscribe<String>
    }
}