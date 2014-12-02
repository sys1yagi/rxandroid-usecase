package com.sys1yagi.rxandroid.events

import android.view.KeyEvent
import android.widget.TextView

public class OnEditorActionEvent {

    TextView view

    int actionId

    KeyEvent event

    OnEditorActionEvent(TextView view, int actionId, KeyEvent event) {
        this.view = view
        this.actionId = actionId
        this.event = event
    }

    public static OnEditorActionEvent create(final TextView view, int actionId,
            KeyEvent event) {
        return new OnEditorActionEvent(view, actionId, event)
    }
}
