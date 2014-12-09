package com.sys1yagi.rxandroid.observables

import android.text.TextUtils
import android.widget.TextView
import groovy.transform.CompileStatic
import rx.Observable
import rx.Subscriber
import rx.functions.Action0

@CompileStatic
class EmptyValidator {

    def static Observable<Boolean> notEmpty(TextView textView) {
        return notEmpty(textView, null, null)
    }

    def static Observable<Boolean> notEmpty(TextView textView,
            Action0 success,
            Action0 failure) {
        return Observable.create({ Subscriber<? super Boolean> subscriber ->
            if (TextUtils.isEmpty(textView.getText())) {
                if (failure != null) {
                    failure.call()
                }
                subscriber.onNext(false)
            } else {
                if (success != null) {
                    success.call()
                }
                subscriber.onNext(true)
            }
            subscriber.onCompleted()
        } as Observable.OnSubscribe<Boolean>)
    }
}

