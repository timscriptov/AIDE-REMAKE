package com.trunksku.asmobile.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.trunksku.asmobile.R;

public class SettingFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_settings);
    }
}