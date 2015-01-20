package ua.stopfan.bookshare;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.TouchInterceptionFrameLayout;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.listeners.EventListener;

import java.util.ArrayList;

import ua.stopfan.bookshare.Activities.BookActivity;
import ua.stopfan.bookshare.Activities.NewBookActivity;
import ua.stopfan.bookshare.Activities.SettingsActivity;
import ua.stopfan.bookshare.Fragments.ForSaleListFragment;
import ua.stopfan.bookshare.Fragments.GiftsListFragment;
import ua.stopfan.bookshare.Fragments.MainPagerFragment;
import ua.stopfan.bookshare.Fragments.WishListFragment;
import ua.stopfan.bookshare.Library.Book;
import ua.stopfan.bookshare.UserInterface.DrawerActivity;
import ua.stopfan.bookshare.UserInterface.widgets.FabView;
import ua.stopfan.bookshare.UserInterface.widgets.SlidingTabLayout;
import ua.stopfan.bookshare.Utilities.Connectivity;


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
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    /*
        instructions = getPreferences(MODE_PRIVATE);
        boolean moreTimes = instructions.getBoolean(instructionStr, false);
        if (moreTimes) {
            Log.d("TAG", "Yes");
            SharedPreferences.Editor ed = instructions.edit();
            ed.putBoolean(instructionStr, false);
            ed.commit();
            startActivity(new Intent(getApplicationContext(),
                    InstructionsActivity.class));
        }

       if(firstTime) {
            ed.putBoolean(instructionStr, false);
            ed.commit();
            startActivity(new Intent(getApplicationContext(),
                    InstructionsActivity.class));
        }

recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(books, getApplicationContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(itemAnimator);

        books = new ArrayList<>();
        populateRecView();
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
        super.onNavDrawerItemClicked(NAVIGATION_DRAWER_LIBRARY);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        mPagerItems = new String[] {
                getResources().getString(R.string.my_books),
                getResources().getString(R.string.wish_list),
                getResources().getString(R.string.want_to_present),
                getResources().getString(R.string.want_to_sell)
        };

        ViewCompat.setElevation(findViewById(R.id.la), getResources().getDimension(R.dimen.toolbar_elevation));


        MainPagerAdapter mPagerAdapter = new MainPagerAdapter(getFragmentManager());
        mPager = (ViewPager) findViewById(R.id.view_pager);
        mPager.setAdapter(mPagerAdapter);
        // Padding for ViewPager must be set outside the ViewPager itself
        // because with padding, EdgeEffect of ViewPager become strange

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        slidingTabLayout.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);
        slidingTabLayout.setDistributeEvenly(false);
        slidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.material_amber_400));
        slidingTabLayout.setViewPager(mPager);
        if (slidingTabLayout != null) {
            slidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

                @Override
                public void onPageSelected(int position) {}

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

                if (Connectivity.isConnected(getApplicationContext())) {
                    Intent intent = new Intent(getApplicationContext(), NewBookActivity.class);
                    startActivity(intent);
                } else
                    showSnackBarAnimation();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
                startActivity(new Intent(this, BookActivity.class));
                break;


            /*case android.R.id.home:
                //Toast.makeText(getApplicationContext(), "Home pressed", Toast.LENGTH_SHORT).show();
                break;*/

        }

        return super.onOptionsItemSelected(item);
    }

    private void populateRecView() {
        books.add(new Book("Hundred years of solitude", "Gabriel Garcia Marquez", getResources().getDrawable(R.drawable.hundred)));
        books.add(new Book("Fifty Shades of Grey", "E. L. James", getResources().getDrawable(R.drawable.shades)));
        books.add(new Book("Steve Jobs","Walter Isaacson", getResources().getDrawable(R.drawable.steve)));
        books.add(new Book("Fahrenheit 451", "Ray Bradbury", getResources().getDrawable(R.drawable.fahrenheit451)));
    }

    private void showSnackBarAnimation() {
        Snackbar.with(getApplicationContext())
                .eventListener(new EventListener() {
                    @Override
                    public void onShow(Snackbar snackbar) {
                        fabView.startAnimation(slideIn);
                    }

                    @Override
                    public void onShown(Snackbar snackbar) {

                    }

                    @Override
                    public void onDismiss(Snackbar snackbar) {
                        fabView.startAnimation(slideOut);
                    }

                    @Override
                    public void onDismissed(Snackbar snackbar) {

                    }
                }).duration(Snackbar.SnackbarDuration.LENGTH_SHORT)
                .swipeToDismiss(true)
                .text("No network connection")
                .show(this);
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

}