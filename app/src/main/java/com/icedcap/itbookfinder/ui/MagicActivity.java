package com.icedcap.itbookfinder.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.icedcap.itbookfinder.R;

/**
 * Created by shuqi on 16-4-4.
 */
public class MagicActivity extends AppCompatActivity {
    private static Fragment mFragment;
    public static final String EXTRA_KEY = "keyword";

    public static void start(Activity activity, Fragment fragment) {
        Intent intent = new Intent(activity, MagicActivity.class);
        mFragment = fragment;
        activity.startActivity(intent);
    }

    public static void start(Activity activity, String key, Fragment fragment) {
        Intent intent = new Intent(activity, MagicActivity.class);
        intent.putExtra(EXTRA_KEY, key);
        mFragment = fragment;
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magic);
        if (null == savedInstanceState) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, mFragment).commit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        overridePendingTransition(R.anim.transition_left_in, R.anim.transition_right_out);
    }
}
