package ua.stopfan.bookshare;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.afollestad.materialdialogs.ThemeSingleton;
import com.facebook.UiLifecycleHelper;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.listeners.EventListener;

import java.util.ArrayList;

import ua.stopfan.bookshare.Activities.LoginActivity;
import ua.stopfan.bookshare.Activities.NewBookActivity;
import ua.stopfan.bookshare.Activities.SettingsActivity;
import ua.stopfan.bookshare.Fragments.ForSaleListFragment;
import ua.stopfan.bookshare.Fragments.GiftsListFragment;
import ua.stopfan.bookshare.Fragments.MainPagerFragment;
import ua.stopfan.bookshare.Fragments.WishListFragment;
import ua.stopfan.bookshare.Library.Book;
import ua.stopfan.bookshare.UserInterface.DrawerActivity;
import ua.stopfan.bookshare.UserInterface.widgets.ColorChooserDialog;
import ua.stopfan.bookshare.UserInterface.widgets.FabView;
import ua.stopfan.bookshare.UserInterface.widgets.SlidingTabLayout;
import ua.stopfan.bookshare.Utilities.Connectivity;

import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Profile;
import com.sromku.simple.fb.listeners.OnProfileListener;


public class MainActivity extends DrawerActivity {

    private Toolbar mToolbar;
    private ArrayList<Book> books;
    private ViewPager mPager;
    private String[] mPagerItems = null;

    private SharedPreferences instructions;
    private String instructionStr = "INSTRUCTION";
    private Animation slideIn;
    private Animation slideOut;

    private FabView fabView;
    private ActionBar bar = null;
    protected static final String TAG = MainActivity.class.getName();
    private SimpleFacebook mSimpleFacebook;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO:facebook component
/*
        mSimpleFacebook = SimpleFacebook.getInstance(this);
        if (!mSimpleFacebook.isLogin()) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
*/
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mToolbar = toolbar;

        bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }
        super.setUpNavDrawer();
        //super.onNavDrawerItemClicked(NAVIGATION_DRAWER_LIBRARY);

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
        if (slidingTabLayout != null) {
            slidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

                @Override
                public void onPageSelected(int position) {
                }

                @Override
                public void onPageScrollStateChanged(int state) {}
            });
        }
        setSnackBar();
    }


    public void setSnackBar() {
        slideIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in);
        slideIn.setFillAfter(true);

        slideOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out);
        slideOut.setFillAfter(true);

        fabView = (FabView)findViewById(R.id.fab_main);
        fabView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*
                if (Connectivity.isConnected(getApplicationContext())) {
                    Intent intent = new Intent(getApplicationContext(), NewBookActivity.class);
                    startActivity(intent);
                } else
                    showSnackBarAnimation();*/
                showCustomColorChooser();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mSimpleFacebook = SimpleFacebook.getInstance(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mSimpleFacebook.onActivityResult(this, requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) MainActivity.this.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(MainActivity.this.getComponentName()));
        }
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


            /*case android.R.id.home:
                //Toast.makeText(getApplicationContext(), "Home pressed", Toast.LENGTH_SHORT).show();
                break;*/

        }

        return super.onOptionsItemSelected(item);
    }

    class MainPagerAdapter extends FragmentPagerAdapter {

        public MainPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            Log.d("TAG", "Fragment num " + position);
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

    public interface Populatable {

        public void populateItems();
    }

    private void showCustomColorChooser() {

    }
}