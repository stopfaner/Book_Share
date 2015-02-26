package ua.stopfan.bookshare.Fragments;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;

import com.afollestad.materialdialogs.ThemeSingleton;

import ua.stopfan.bookshare.R;
import ua.stopfan.bookshare.UserInterface.widgets.ColorChooserDialog;

/**
 * Created by stopfan on 1/18/15.
 */
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    private static int selectedColorIndex = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.main_preferences);

        PreferenceCategory pc = new PreferenceCategory(getActivity());
        pc.setTitle("New settings");
        getPreferenceScreen().addPreference(pc);
        getPreferenceScreen().setOnPreferenceClickListener(this);
        Preference colorPreference = findPreference("chooseColor");

        colorPreference.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {

        new ColorChooserDialog().show(getFragmentManager() ,selectedColorIndex, new ColorChooserDialog.Callback() {
            @Override
            public void onColorSelection(int index, int color, int darker) {
                selectedColorIndex = index;
                ThemeSingleton.get().positiveColor = color;
                ThemeSingleton.get().neutralColor = color;
                ThemeSingleton.get().negativeColor = color;
            }
        });
        preference.setSummary("Main theme color" + String .valueOf(selectedColorIndex));
        return false;
    }


}
