package com.sys1yagi.rxandroid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import com.arasthel.swissknife.annotations.InjectView
import com.sys1yagi.rxandroid.activities.ToolbarActivity
import com.sys1yagi.rxandroid.observables.HttpRequestObservable
import groovy.transform.CompileStatic
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.parser.Parser
import rx.Observable
import rx.android.observables.AndroidObservable
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Func1
import rx.schedulers.Schedulers

@CompileStatic
public class RssParseActivity extends ToolbarActivity {

    public static final String ARGS_URL = "url"

    public static Intent createIntent(Context context, String url) {
        Intent intent = new Intent(context, RssParseActivity.class)

        intent.putExtra(ARGS_URL, url)

        return intent
    }

    @InjectView(R.id.list_view)
    ListView listView

    @InjectView(R.id.progress_bar)
    ProgressBar progressBar

    @InjectView(R.id.error_text)
    TextView errorText

    String url

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rss_parse)
        url = getIntent().getStringExtra(ARGS_URL)

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1);
        listView.setAdapter(adapter)

        AndroidObservable.bindActivity(this,
                Observable.create(HttpRequestObservable.get(url))
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
        ).map({ String xml ->
            Document document = Jsoup.parse(xml, "", Parser.xmlParser())
            document.select("item").collect({ Element child ->
                child.select("title").first().text()
            })
        } as Func1<String, List<String>>)
                .subscribe(
                { List<String> titles ->
                    progressBar.setVisibility(View.GONE)
                    listView.setVisibility(View.VISIBLE)
                    adapter.addAll(titles)
                },
                { Throwable error ->
                    progressBar.setVisibility(View.GONE)
                    errorText.setVisibility(View.VISIBLE)
                    errorText.setText(error.getMessage())
                })
    }
}
