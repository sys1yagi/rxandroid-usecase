package com.sys1yagi.rxandroid.activities

import android.support.v7.app.ActionBarActivity
import android.support.v7.widget.Toolbar
import com.arasthel.swissknife.SwissKnife
import com.sys1yagi.rxandroid.R

class ToolbarActivity extends ActionBarActivity {

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID)
        SwissKnife.inject(this)
        Toolbar toolbar = findViewById(R.id.toolbar)
        if (toolbar != null) {
            setSupportActionBar(toolbar)
        }
    }
}
