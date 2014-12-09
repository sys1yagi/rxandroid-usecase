package com.sys1yagi.rxandroid.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.arasthel.swissknife.annotations.InjectView
import com.sys1yagi.rxandroid.R
import com.sys1yagi.rxandroid.observables.FormValidator
import groovy.transform.CompileStatic
import rx.Observable
import rx.android.observables.ViewObservable
import rx.functions.Func2

@CompileStatic
public class FormValidation2Activity extends ToolbarActivity {

    def static Intent createIntent(Context context) {
        new Intent(context, FormValidation2Activity.class)
    }

    @InjectView(R.id.edit_email)
    EditText email;

    @InjectView(R.id.edit_password)
    EditText password;

    @InjectView(R.id.buttom_submit)
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_validation2);

        def emailEmptyObservable = FormValidator.watchNotEmpty(email, true);
        def passwordEmptyObservable = FormValidator.watchNotEmpty(password, true);

        Observable.combineLatest(emailEmptyObservable, passwordEmptyObservable,
                { a, b ->
                    return a && b
                } as Func2)
                .subscribe(
                { Boolean isValid ->
                    submit.setEnabled(isValid)
                })

        ViewObservable
                .clicks(submit)
                .subscribe(
                { _ ->
                    submit()
                })
    }

    def submit() {
        Toast.makeText(this, "submit!", Toast.LENGTH_SHORT).show()
    }
}
