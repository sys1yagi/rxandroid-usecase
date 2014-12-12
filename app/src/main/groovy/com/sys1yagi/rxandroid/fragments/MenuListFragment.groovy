package com.sys1yagi.rxandroid

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import com.arasthel.swissknife.SwissKnife
import com.arasthel.swissknife.annotations.InjectView
import com.sys1yagi.rxandroid.activities.FormValidation2Activity
import com.sys1yagi.rxandroid.activities.FormValidationActivity
import com.sys1yagi.rxandroid.activities.ListViewPagingActivity
import com.sys1yagi.rxandroid.activities.SimpleNetworkAccessActivity
import groovy.transform.CompileStatic
import rx.Observable
import rx.android.events.OnItemClickEvent
import rx.android.observables.ViewObservable
import rx.android.schedulers.AndroidSchedulers

@CompileStatic
class MenuListFragment extends Fragment {

    def static MenuListFragment newInstance() {
        return new MenuListFragment()
    }

    @InjectView(R.id.list_view)
    ListView listView

    @Override
    View onCreateView(LayoutInflater inflater,
            @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menulist, null)
        SwissKnife.inject(this, view)

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1);
        listView.setAdapter(adapter)

        Observable.from(
                "Simple Network Access in Activity",
                "Rss Parse in Activity",
                "Form validation 1 in Activity",
                "Form validation 2 in Activity",
                "ListView paging",
                //"View Binding",
                //"xxx in Fragment"
        )
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ item ->
            adapter.add(item)
        });
        ViewObservable.itemClicks(listView).subscribe({ OnItemClickEvent event ->
            int position = event.position
            actions[position].call(getActivity())
        })

        return view
    }

    def Closure<Void>[] actions = [
            {
                Activity activity ->
                    Intent intent = SimpleNetworkAccessActivity.createIntent(activity,
                            "https://android-arsenal.com/details/1/1170")
                    activity.startActivity(intent)

            },
            {
                Activity activity ->
                    Intent intent = RssParseActivity.createIntent(activity)
                    activity.startActivity(intent)

            },
            {
                Activity activity ->
                    Intent intent = FormValidationActivity.createIntent(activity)
                    activity.startActivity(intent)
            },
            {
                Activity activity ->
                    Intent intent = FormValidation2Activity.createIntent(activity)
                    activity.startActivity(intent)
            },
            {
                Activity activity ->
                    Intent intent = ListViewPagingActivity.createIntent(activity)
                    activity.startActivity(intent)
            }
    ]
}
