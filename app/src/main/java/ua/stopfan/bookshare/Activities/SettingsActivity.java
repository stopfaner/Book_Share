package ua.stopfan.bookshare.Activities;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.preference.Preference;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.afollestad.materialdialogs.ThemeSingleton;

import ua.stopfan.bookshare.Fragments.SettingsFragment;
import ua.stopfan.bookshare.R;
import ua.stopfan.bookshare.UserInterface.widgets.ColorChooserDialog;


public class SettingsActivity extends ActionBarActivity {

    static int selectedColorIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

        // use action bar here
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

}
