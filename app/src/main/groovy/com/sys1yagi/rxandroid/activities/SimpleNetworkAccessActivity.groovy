package com.sys1yagi.rxandroid.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.ActionBarActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.arasthel.swissknife.SwissKnife
import com.arasthel.swissknife.annotations.InjectView
import com.sys1yagi.rxandroid.R
import com.sys1yagi.rxandroid.observables.HttpRequestObservable
import rx.android.observables.AndroidObservable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

public class SimpleNetworkAccessActivity extends ActionBarActivity {

    public static final String ARGS_URL = "url"

    public static Intent createIntent(Context context, String url) {
        Intent intent = new Intent(context, SimpleNetworkAccessActivity.class)

        intent.putExtra(ARGS_URL, url)

        return intent
    }

    @InjectView(R.id.toolbar)
    Toolbar toolbar

    @InjectView(R.id.text_content)
    TextView content

    @InjectView(R.id.progress_bar)
    ProgressBar progressBar

    @InjectView(R.id.error_text)
    TextView errorText

    String url

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_network_access);
        SwissKnife.inject(this)
        setSupportActionBar(toolbar)

        url = getIntent().getStringExtra(ARGS_URL)

        AndroidObservable.bindActivity(this,
                rx.Observable.create(HttpRequestObservable.get(url))
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
        )
                .subscribe(
                { String html ->
                    progressBar.setVisibility(View.GONE)
                    content.setVisibility(View.VISIBLE)
                    content.setText(html)
                },
                { Throwable error ->
                    progressBar.setVisibility(View.GONE)
                    errorText.setVisibility(View.VISIBLE)
                    errorText.setText(error.getMessage())
                })
    }
}
