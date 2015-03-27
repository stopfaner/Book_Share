package ua.stopfan.bookshare.Activities.NewBook;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.widget.LoginButton;

import ua.stopfan.bookshare.R;
import ua.stopfan.bookshare.UserInterface.widgets.FabView;
import ua.stopfan.bookshare.UserInterface.widgets.SlidingTabLayout;


public class NewBookActivity extends ActionBarActivity {

    private Toolbar mToolbar;
    private FabView mFabView;
    private ViewPager mPager;
    private LoginButton button;

    private final static String[] pages = {
            "     Add from existing    ",
            "     Add totally new     "
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_new_book);

            mToolbar = (Toolbar) findViewById(R.id.new_book_toolbar);
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);

            NewBookPagerAdapter adapter = new NewBookPagerAdapter(getFragmentManager());
            mPager = (ViewPager) findViewById(R.id.new_book_pager);
            mPager.setAdapter(adapter);
            mPager.setOffscreenPageLimit(2);
            final SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.new_book_tabs);
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

            mFabView = (FabView) findViewById(R.id.fab_add);
            mFabView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_book, menu);
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
            case R.id.new_book_settings:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    class NewBookPagerAdapter extends FragmentPagerAdapter {
        public NewBookPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = new AddExistingFragment();
                    break;
                case 1:
                    fragment = new AddTotallyNewFragment();
            }
            return fragment;
        }

        @Override
        public int getCount() {return 2;}

        @Override
        public CharSequence getPageTitle(int position) {
            return pages[position];
        }
    }
}
