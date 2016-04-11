package com.icedcap.itbookfinder.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.icedcap.itbookfinder.R;
import com.icedcap.itbookfinder.ui.fragments.SettingsFragment;

/**
 * Created by shuqi on 16-4-3.
 */
public class SettingsActivity extends AppCompatActivity {
    private Fragment mFragment;

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, SettingsActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magic);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (mFragment == null) {
            mFragment = new SettingsFragment();
            getFragmentManager().beginTransaction().add(R.id.container, mFragment).commit();
        }

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.transition_left_in, R.anim.transition_right_out);
    }
}
