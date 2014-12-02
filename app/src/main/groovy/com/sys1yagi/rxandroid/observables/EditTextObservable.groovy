package com.sys1yagi.rxandroid.observables

import android.widget.EditText
import com.sys1yagi.rxandroid.events.OnEditorActionEvent
import groovy.transform.CompileStatic
import rx.Observable

@CompileStatic
class EditTextObservable {

    private EditTextObservable() {
    }

    public static Observable<OnEditorActionEvent> editorAction(EditText input) {
        return Observable.create(new OnSubscribeEditTextInput(input));
    }

}
