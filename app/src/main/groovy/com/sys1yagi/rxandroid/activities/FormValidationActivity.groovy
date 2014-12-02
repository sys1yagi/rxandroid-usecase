package com.sys1yagi.rxandroid.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.ActionBarActivity
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.arasthel.swissknife.SwissKnife
import com.arasthel.swissknife.annotations.InjectView
import com.sys1yagi.rxandroid.R
import com.sys1yagi.rxandroid.events.OnEditorActionEvent
import com.sys1yagi.rxandroid.observables.EditTextObservable
import groovy.transform.CompileStatic
import rx.android.observables.ViewObservable

@CompileStatic
public class FormValidationActivity extends ActionBarActivity {

    def static Intent createIntent(Context context) {
        new Intent(context, FormValidationActivity.class)
    }

    @InjectView(R.id.toolbar)
    Toolbar toolbar

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

    def static Closure<Void> hideError(TextView errorView) {
        return {
            errorView.setVisibility(View.GONE)
        }
    }

    def static Closure<Void> showError(TextView errorView, String message) {
        return {
            errorView.setText(message)
            errorView.setVisibility(View.VISIBLE)
        }
    }

    def static boolean validateEmpty(EditText editText, Closure hideError, Closure showError) {
        String text = editText.getText().toString()
        if (TextUtils.isEmpty(text)) {
            showError.call()
            return false
        }
        hideError.call()
        return true
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_validation);
        SwissKnife.inject(this)
        setSupportActionBar(toolbar)

        //It's implemented own.
        EditTextObservable.editorAction(password).subscribe({ OnEditorActionEvent event ->
            if (event.actionId == EditorInfo.IME_ACTION_DONE) {
                submit.performClick()
            }
        })

        //It's RxAndroid component.
        ViewObservable
                .clicks(submit)
                .map(
                { event ->
                    return validateEmpty(email,
                            hideError(emailError),
                            showError(emailError, "*Enter your e-mail address.")
                    )
                })
                .map(
                { Boolean isValid ->
                    return validateEmpty(password,
                            hideError(passwordError),
                            showError(passwordError, "*Enter your password")
                    ) && isValid
                })
                .filter(
                { Boolean isValid ->
                    isValid
                })
                .subscribe(
                { Boolean isValid ->
                    Toast.makeText(getApplicationContext(),
                            "submit!",
                            Toast.LENGTH_SHORT).show()
                })

    }
}
