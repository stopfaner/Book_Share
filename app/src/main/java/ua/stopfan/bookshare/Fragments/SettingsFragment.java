package ua.stopfan.bookshare.Fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import ua.stopfan.bookshare.R;

/**
 * Created by stopfan on 1/18/15.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.main_preferences);
    }
}
