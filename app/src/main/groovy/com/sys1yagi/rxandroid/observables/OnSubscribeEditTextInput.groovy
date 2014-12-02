package com.sys1yagi.rxandroid.observables

import android.view.KeyEvent
import android.widget.EditText
import android.widget.TextView
import com.sys1yagi.rxandroid.events.OnEditorActionEvent
import groovy.transform.CompileStatic
import rx.Subscriber
import rx.Subscription
import rx.android.subscriptions.AndroidSubscriptions

@CompileStatic
class OnSubscribeEditTextInput implements rx.Observable.OnSubscribe<OnEditorActionEvent> {

    EditText input

    public OnSubscribeEditTextInput(EditText input) {
        this.input = input;
    }

    @Override
    void call(Subscriber<? super OnEditorActionEvent> subscriber) {
        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                subscriber.onNext(OnEditorActionEvent.create(v, actionId, event))
                return false
            }
        })

        final Subscription subscription = AndroidSubscriptions.unsubscribeInUiThread({
            input.setOnEditorActionListener(null)
        });

        subscriber.add(subscription)
    }
}
