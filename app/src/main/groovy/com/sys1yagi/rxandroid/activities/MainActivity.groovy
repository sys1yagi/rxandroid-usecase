package com.sys1yagi.rxandroid.activities

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.ActionBarActivity
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.arasthel.swissknife.SwissKnife
import com.arasthel.swissknife.annotations.InjectView
import com.sys1yagi.rxandroid.MenuListFragment
import com.sys1yagi.rxandroid.R
import groovy.transform.CompileStatic

@CompileStatic
public class MainActivity extends ActionBarActivity {

    @InjectView(R.id.toolbar)
    Toolbar toolbar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        SwissKnife.inject(this)
        setSupportActionBar(toolbar)

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, MenuListFragment.newInstance())
                    .commit()
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu)
        return true
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId()

        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}
