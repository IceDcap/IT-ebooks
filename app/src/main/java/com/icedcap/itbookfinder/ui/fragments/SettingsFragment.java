package com.icedcap.itbookfinder.ui.fragments;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.util.Log;

import com.icedcap.itbookfinder.R;
import com.icedcap.itbookfinder.help.SharedPreferencesHelper;
import com.icedcap.itbookfinder.persistence.Constants;

/**
 * Created by shuqi on 16-4-3.
 */
public class SettingsFragment extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private static final String KEY_ABOUT = "about";
    private static final String KEY_AUTO_CACHE_IN_WIFI = "auto_cache_in_wifi";
    private static final String KEY_HOMEPAGE_LAYOUT = "homepage_layout";
    private static final String KEY_LICENSE = "license";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != savedInstanceState) {
            return;
        }
        addPreferencesFromResource(R.xml.settings);
        SwitchPreference switchPreference = (SwitchPreference) findPreference(KEY_AUTO_CACHE_IN_WIFI);
        switchPreference.setChecked(SharedPreferencesHelper.getAutoCache(getActivity()));
        switchPreference.setOnPreferenceChangeListener(this);

        ListPreference listPreference = (ListPreference) findPreference(KEY_HOMEPAGE_LAYOUT);
        final String currentValue = SharedPreferencesHelper.getHomepageLayout(getActivity());
        listPreference.setValue(currentValue);
        setListPreferencesSummary(listPreference, currentValue);
        listPreference.setOnPreferenceChangeListener(this);

        Preference about = findPreference(KEY_ABOUT);
        try {
            final String version = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
            about.setSummary(getActivity().getString(R.string.current_version, version));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        about.setOnPreferenceClickListener(this);
        findPreference(KEY_LICENSE).setOnPreferenceClickListener(this);


    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.getKey().equals(KEY_AUTO_CACHE_IN_WIFI)) {
            setAutoCachePreferences(newValue);
        } else if (preference.getKey().equals(KEY_HOMEPAGE_LAYOUT)) {
            setHomepageLayout((ListPreference) preference, newValue);
        }
        return true;
    }


    @Override
    public boolean onPreferenceClick(Preference preference) {
        return false;
    }

    private void setAutoCachePreferences(Object newValue) {
        SharedPreferencesHelper.setAutoCache(getActivity(), (boolean) newValue);
    }

    private void setHomepageLayout(ListPreference preference, Object newValue) {
        final String value = newValue.toString();
        setListPreferencesSummary(preference, value);
        SharedPreferencesHelper.setHomepageLayout(getActivity(), value);

        Intent broadcast = new Intent(Constants.BROADCAST_ACTION);
        broadcast.putExtra(Constants.BROADCAST_ACTION, Integer.decode(value));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, broadcast, PendingIntent.FLAG_ONE_SHOT);
        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    private void setListPreferencesSummary(ListPreference preferences, String value) {
        if (value.equals("1")) {
            preferences.setSummary(R.string.homepage_linear);
        } else if (value.equals("2")) {
            preferences.setSummary(R.string.homepage_grid);
        }
    }
}
