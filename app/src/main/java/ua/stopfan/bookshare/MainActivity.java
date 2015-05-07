package ua.stopfan.bookshare;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import ua.stopfan.bookshare.Activities.AddBookActivity;
import ua.stopfan.bookshare.Activities.LoginActivity;
import ua.stopfan.bookshare.Activities.SearchActivity;
import ua.stopfan.bookshare.Activities.SettingsActivity;
import ua.stopfan.bookshare.Fragments.ForSaleListFragment;
import ua.stopfan.bookshare.Fragments.GiftsListFragment;
import ua.stopfan.bookshare.Fragments.LoginFragment;
import ua.stopfan.bookshare.Fragments.MainPagerFragment;
import ua.stopfan.bookshare.Fragments.WishListFragment;
import ua.stopfan.bookshare.UserInterface.DrawerActivity;
import ua.stopfan.bookshare.UserInterface.widgets.SlidingTabLayout;

import com.facebook.AccessToken;
import com.facebook.appevents.AppEventsLogger;
import com.gc.materialdesign.views.ButtonFloat;
import com.gc.materialdesign.widgets.SnackBar;

import java.util.List;


public class MainActivity extends DrawerActivity {

    public final static String FILE_NAME = "filename";

    private Toolbar mToolbar;
    private ViewPager mPager;
    private String[] mPagerItems = null;
    private Animation slideIn;
    private Animation slideOut;

    private ActionBar bar = null;

    private ButtonFloat buttonFloat;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO:facebook component
        setUserInterface();
    }

    void setUserInterface() {
        setContentView(R.layout.activity_main);

        setToolbar();
        buttonFloat = (ButtonFloat) findViewById(R.id.buttonFloat);
        /*
        Here we run methods from DrawerActivity class to set some User Interface things
         */
        super.setUpNavDrawer();
        super.setSelectedNavDrawerItem(NAVIGATION_DRAWER_LIBRARY);
        setViewPager();
        setSnackBar();
    }

    void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mToolbar = toolbar;

        bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }
    }

    void setViewPager() {

        mPagerItems = new String[] {
                getResources().getString(R.string.my_books),
                getResources().getString(R.string.wish_list),
                getResources().getString(R.string.want_to_present),
                getResources().getString(R.string.want_to_sell)
        };

        MainPagerAdapter mPagerAdapter = new MainPagerAdapter(getFragmentManager());
        mPager = (ViewPager) findViewById(R.id.view_pager);
        mPager.setAdapter(mPagerAdapter);
        mPager.setOffscreenPageLimit(4);

        final SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        slidingTabLayout.setCustomTabView(R.layout.tab_indicator, android.R.id.text2);
        slidingTabLayout.setDistributeEvenly(false);
        slidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.material_amber_400));
        slidingTabLayout.setViewPager(mPager);

        ButtonFloat buttonFloat = (ButtonFloat) findViewById(R.id.buttonFloat);
        buttonFloat.setBackgroundColor(getResources().getColor(R.color.material_pink_400));
        buttonFloat.setDrawableIcon(getResources().getDrawable(R.drawable.ic_add_white_24dp));
    }

    public void setSnackBar() {

        buttonFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // snackBar.show();
                startActivity(new Intent(getApplicationContext(), AddBookActivity.class));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if(AccessToken.getCurrentAccessToken() == null)
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        AppEventsLogger.activateApp(getApplicationContext());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(AccessToken.getCurrentAccessToken() == null)
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                break;

            case R.id.action_search:
                //startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                Toast.makeText(getApplicationContext(), "Search activity", Toast.LENGTH_SHORT).show();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    class MainPagerAdapter extends FragmentPagerAdapter {

        public MainPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            Fragment frag = null;
            switch (position) {
                case 0:
                    frag = new MainPagerFragment();
                    break;
                case 1:
                    frag = new WishListFragment();
                    break;
                case 2:
                    frag = new GiftsListFragment();
                    break;
                case 3:
                    frag = new ForSaleListFragment();
            }

            return frag;
        }
        @Override
        public int getCount() {
            return 4;
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return mPagerItems[position];
        }
    }

}