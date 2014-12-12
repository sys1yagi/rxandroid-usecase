package com.sys1yagi.rxandroid.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import com.arasthel.swissknife.annotations.InjectView
import com.sys1yagi.rxandroid.R

public class ListViewPagingActivity extends ToolbarActivity {

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, ListViewPagingActivity.class)
        return intent
    }

    @InjectView(R.id.list_view)
    ListView listView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listview_paging)


    }
}
