package com.sys1yagi.rxandroid.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.ActionBarActivity
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.util.Log
import android.util.Pair
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.arasthel.swissknife.SwissKnife
import com.arasthel.swissknife.annotations.InjectView
import com.sys1yagi.rxandroid.R
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_validation);
        SwissKnife.inject(this)
        setSupportActionBar(toolbar)

        ViewObservable
                .clicks(submit)
                .map(
                { event ->
                    String email = email.getText().toString()
                    if (TextUtils.isEmpty(email)) {
                        emailError.setText("*Enter your e-mail address.")
                        emailError.setVisibility(View.VISIBLE)
                        return null
                    }
                    emailError.setVisibility(View.GONE)
                    return true
                })
                .map(
                { Boolean isValid ->
                    String password = password.getText().toString()
                    if (TextUtils.isEmpty(password)) {
                        passwordError.setText("*Enter your password.")
                        passwordError.setVisibility(View.VISIBLE)
                        return false
                    }
                    passwordError.setVisibility(View.GONE)
                    return isValid && true
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
