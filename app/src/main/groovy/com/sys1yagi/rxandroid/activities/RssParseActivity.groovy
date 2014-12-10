package com.sys1yagi.rxandroid

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import com.arasthel.swissknife.annotations.InjectView
import com.sys1yagi.rxandroid.activities.ToolbarActivity
import com.sys1yagi.rxandroid.models.Item
import com.sys1yagi.rxandroid.retrofit.AndroidArsenalService
import com.sys1yagi.rxandroid.retrofit.RssConverter
import groovy.transform.CompileStatic
import retrofit.RestAdapter
import rx.android.events.OnItemClickEvent
import rx.android.observables.AndroidObservable
import rx.android.observables.ViewObservable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

@CompileStatic
public class RssParseActivity extends ToolbarActivity {

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, RssParseActivity.class)
        return intent
    }

    @InjectView(R.id.list_view)
    ListView listView

    @InjectView(R.id.progress_bar)
    ProgressBar progressBar

    @InjectView(R.id.error_text)
    TextView errorText

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rss_parse)

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this as Context,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
        );
        listView.setAdapter(adapter)

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://feeds.feedburner.com/")
                .setConverter(new RssConverter())
                .build()

        AndroidArsenalService androidArsenalService = restAdapter.
                create(AndroidArsenalService.class);

        AndroidObservable.bindActivity(this,
                androidArsenalService.androidArsenal()
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread()))
                .subscribe(
                { List<Item> items ->
                    progressBar.setVisibility(View.GONE)
                    listView.setVisibility(View.VISIBLE)
                    items.each({ Item item ->
                        adapter.add(item.title)
                    })
                    ViewObservable.itemClicks(listView).subscribe({ OnItemClickEvent event ->
                        String url = items.get(event.position).url
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(intent)
                    })

                },
                { Throwable error ->
                    progressBar.setVisibility(View.GONE)
                    errorText.setVisibility(View.VISIBLE)
                    errorText.setText(error.getMessage())
                })
    }
}
