package ua.stopfan.bookshare.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.ImageView;

import ua.stopfan.bookshare.Fragments.InstructionPageFragment;
import ua.stopfan.bookshare.MainActivity;
import ua.stopfan.bookshare.R;

/**
 * Created by stopfan on 1/13/15.
 */
public class InstructionsActivity extends FragmentActivity {

    static final String TAG = "Instructions Activity";
    static final int PAGE_COUNT = 5;

    private ViewPager pager;
    private PagerAdapter pagerAdapter;
    private ImageView[] views;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        pager = (ViewPager) findViewById(R.id.pager);
        findPagesViews();
        views[0].setImageResource(R.drawable.dot);
        pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d(TAG, "onPageSelected, position = " + position);
            }

            @Override
            public void onPageSelected(int position) {

                        for(int i = 0; i < PAGE_COUNT; i++) {
                            if (i != position)
                                views[i].setImageResource(R.drawable.dotfree);
                            else
                                views[i].setImageResource(R.drawable.dot);
                        }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



    }

    private void findPagesViews() {
        views = new ImageView[5];
        views[0] = (ImageView) findViewById(R.id.image_dot_1);
        views[1] = (ImageView) findViewById(R.id.image_dot_2);
        views[2] = (ImageView) findViewById(R.id.image_dot_3);
        views[3] = (ImageView) findViewById(R.id.image_dot_4);
        views[4] = (ImageView) findViewById(R.id.image_dot_5);
    }


    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return InstructionPageFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

    }

}
