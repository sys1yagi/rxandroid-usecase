package com.sys1yagi.rxandroid.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.arasthel.swissknife.annotations.InjectView
import com.sys1yagi.rxandroid.R
import com.sys1yagi.rxandroid.events.OnEditorActionEvent
import com.sys1yagi.rxandroid.observables.EditTextObservable
import com.sys1yagi.rxandroid.observables.FormValidator
import groovy.transform.CompileStatic
import rx.Observable
import rx.android.observables.ViewObservable
import rx.functions.Action0

@CompileStatic
public class FormValidationActivity extends ToolbarActivity {

    def static Intent createIntent(Context context) {
        new Intent(context, FormValidationActivity.class)
    }

    @InjectView(R.id.edit_email)
    EditText email;

    @InjectView(R.id.text_email_error)
    TextView emailError;

    @InjectView(R.id.edit_password)
    EditText password;

    @InjectView(R.id.text_password_error)
    TextView passwordError;

    @InjectView(R.id.buttom_submit)
    Button submit;

    def static Action0 hideError(TextView errorView) {
        return {
            errorView.setVisibility(View.GONE)
        } as Action0
    }

    def static Action0 showError(TextView errorView, String message) {
        return {
            errorView.setText(message)
            errorView.setVisibility(View.VISIBLE)
        } as Action0
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_validation);

        prepareEditorAction()
        def validators = prepareValidation()
                .reduce({ a, b -> a && b })
                .filter({ a -> a })

        ViewObservable
                .clicks(submit)
                .subscribe(
                { event ->
                    validators.subscribe({ _ ->
                        submit()
                    })
                })
    }

    def submit() {
        Toast.makeText(this, "submit!", Toast.LENGTH_SHORT).show()
    }

    def prepareEditorAction() {
        EditTextObservable.editorAction(password).subscribe({ OnEditorActionEvent event ->
            if (event.actionId == EditorInfo.IME_ACTION_DONE) {
                submit.performClick()
            }
        })
    }

    def Observable<Boolean> prepareValidation() {
        Observable<Boolean> emailEmptyValidator = FormValidator
                .notEmpty(email,
                hideError(emailError),
                showError(emailError, "*Enter your e-mail address."))

        Observable<Boolean> passwordEmptyValidator = FormValidator
                .notEmpty(password,
                hideError(passwordError),
                showError(passwordError, "*Enter your password address."))

        return Observable.concat(emailEmptyValidator, passwordEmptyValidator)
    }
}
