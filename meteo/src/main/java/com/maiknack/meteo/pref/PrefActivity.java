package com.maiknack.meteo.pref;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.maiknack.meteo.R;

public class PrefActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref);
    }
}
