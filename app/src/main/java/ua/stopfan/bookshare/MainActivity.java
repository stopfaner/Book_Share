package ua.stopfan.bookshare;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.view.CardView;
import ua.stopfan.bookshare.Adapters.TestAdapter;
import ua.stopfan.bookshare.UserInterface.FabView;
import ua.stopfan.bookshare.Utilities.ShakeListener;

public class MainActivity extends ActionBarActivity {

    // private Toolbar toolbar;
    private ActionBar actionBar = null;

    private Toolbar mToolbar;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ViewGroup mDrawerItemsContainer;

    private View[] mDrawerViews = null;
    private Handler mHandler;
    private static ArrayList<Integer> mNavigationDrawerItems = new ArrayList<Integer>();

    private SensorManager mSensorManager;
    private Sensor sensor;
    private ShakeListener shakeListener;

    //Navigation drawer string titles
    private static int[] NAVIGATION_DRAWER_STRING_ITEMS = new int[]{
        R.string.naw_drawer_my_books,
        R.string.naw_drawer_favourite,
        R.string.naw_drawer_book_exchange,
        R.string.naw_drawer_friend_books,
        R.string.naw_drawer_settings,
        R.string.naw_drawer_help
    };

    //Navigation drawer icon id
    private static int[] NAVIGATION_DRAWER_ICON_ID = new int[] {
        R.drawable.ic_book_grey600_36dp,
        R.drawable.ic_favorite_grey600_36dp,
        R.drawable.ic_swap_vert_circle_grey600_36dp,
        R.drawable.ic_group_grey600_24dp,
        R.drawable.ic_settings_grey600_36dp,
        R.drawable.ic_help_grey600_36dp
    };

    //Navigation drawer items
    protected static final int NAVIGATION_DRAWER_LIBRARY = 0;
    protected static final int NAVIGATION_DRAWER_FAVOURITE = 1;
    protected static final int NAVIGATION_DRAWER_EXCHANGE = 2;
    protected static final int NAVIGATION_DRAWER_FRIENDS = 3;
    protected static final int NAVIGATION_DRAWER_SETTINGS = 4;
    protected static final int NAVIGATION_DRAWER_HELP = 5;
    protected static final int NAVIGATION_DRAWER_INVALID = -1;
    protected static final int NAVIGATION_DRAWER_SEPARATOR = -2;

    private static final int NAVIGATION_DRAWER_LAUBCH_DELAY = 250;

    ListView listView;
    String[] arr = {"Hello", "New", "now"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        listView = (ListView) findViewById(R.id.listView);
        TestAdapter testAdapter = new TestAdapter(getApplicationContext(), arr);
        View v = getLayoutInflater().inflate(R.layout.header, null);
        listView.setVerticalScrollBarEnabled(false);
        listView.addHeaderView(v);
        listView.setAdapter(testAdapter);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        mHandler = new Handler();
        setUpNavDrawer();

        onNavDrawerItemClicked(NAVIGATION_DRAWER_LIBRARY);
        FabView fabView = (FabView)findViewById(R.id.fab_main);
        fabView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        shakeListener = new ShakeListener();
        shakeListener.setOnShakeListener(new ShakeListener.OnShakeListener() {
            @Override
            public void onShake(int count) {

                Toast.makeText(getApplicationContext(), "Shake", Toast.LENGTH_SHORT).show();
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


    private void setUpNavDrawer() {
        //Getting NavDrawer self item
        int selfItem = getSelfNavDrawerItem();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        //Checking drawerLayout is not null
        if (mDrawerLayout == null) {

            Toast.makeText(getApplicationContext(), "NULL", Toast.LENGTH_SHORT).show();
            return;
        }

        //Initializing Nav Drawer Toggle
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                mToolbar,
                R.string.drawer_opened,
                R.string.drawer_closed) {
            public void onDrawerClosed(View view) {

                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {

                invalidateOptionsMenu();
            }

        };

        //Adding items to  Nav Drawer
        fillNavDrawerItemsArray();

        //Adding "hamburger" icon to Toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //Adding Nav Drawer Listener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        //Syncs Toggle
        mDrawerToggle.syncState();

    }

    private void fillNavDrawerItemsArray() {

        mNavigationDrawerItems.clear();

        mNavigationDrawerItems.add(NAVIGATION_DRAWER_LIBRARY);
        mNavigationDrawerItems.add(NAVIGATION_DRAWER_FAVOURITE);
        mNavigationDrawerItems.add(NAVIGATION_DRAWER_EXCHANGE);
        mNavigationDrawerItems.add(NAVIGATION_DRAWER_FRIENDS);
        mNavigationDrawerItems.add(NAVIGATION_DRAWER_SEPARATOR);
        mNavigationDrawerItems.add(NAVIGATION_DRAWER_SETTINGS);
        mNavigationDrawerItems.add(NAVIGATION_DRAWER_HELP);

        setUpNavigationDrawerItems();
    }

    private void setUpNavigationDrawerItems() {

        mDrawerItemsContainer = (ViewGroup) findViewById(R.id.navdrawer_items_list);

        if(mDrawerItemsContainer == null) {
            return;
        }

        mDrawerViews = new View[mNavigationDrawerItems.size()];

        int i = 0;
        for(int id : mNavigationDrawerItems){
            mDrawerViews[i] = makeNavDrawerItem(id, mDrawerItemsContainer);
            mDrawerItemsContainer.addView(mDrawerViews[i]);
            ++i;
        }

    }

    //Method is used to create new Nav Drawer items
    private View makeNavDrawerItem(final int itemId, ViewGroup container) {
        boolean selected = getSelfNavDrawerItem() == itemId;
        int layoutToInflate = 0;
        if (itemId == NAVIGATION_DRAWER_SEPARATOR) {
            layoutToInflate = R.layout.navigation_drawer_separator;
        } else {
            layoutToInflate = R.layout.navigation_drawer_item;
        }
        View view = getLayoutInflater().inflate(layoutToInflate, container, false);

        if (isSeparator(itemId)) {
            // we are done
            setAccessibilityIgnore(view);
            return view;
        }

        ImageView iconView = (ImageView) view.findViewById(R.id.icon);
        TextView titleView = (TextView) view.findViewById(R.id.title);
        int iconId = itemId >= 0 && itemId < NAVIGATION_DRAWER_ICON_ID.length ?
                NAVIGATION_DRAWER_ICON_ID[itemId] : 0;
        int titleId = itemId >= 0 && itemId < NAVIGATION_DRAWER_STRING_ITEMS.length ?
                NAVIGATION_DRAWER_STRING_ITEMS[itemId] : 0;

        // set icon and text
        iconView.setVisibility(iconId > 0 ? View.VISIBLE : View.GONE);
        if (iconId > 0) {
            iconView.setImageResource(iconId);
        }
        titleView.setText(getString(titleId));

        formatNavDrawerItem(view, itemId, selected);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavDrawerItemClicked(itemId);
            }
        });

        return view;
    }

    private void formatNavDrawerItem(View view, int itemId, boolean selected) {
        if (isSeparator(itemId)) {
            // not applicable
            return;
        }

        ImageView iconView = (ImageView) view.findViewById(R.id.icon);
        TextView titleView = (TextView) view.findViewById(R.id.title);

        // configure its appearance according to whether or not it's selected
        titleView.setTextColor(selected ?
                getResources().getColor(R.color.navdrawer_text_color_selected) :
                getResources().getColor(R.color.navdrawer_text_color));
        iconView.setColorFilter(selected ?
                getResources().getColor(R.color.navdrawer_icon_tint_selected) :
                getResources().getColor(R.color.navdrawer_icon_tint));
    }

    private void setSelectedNavDrawerItem(int itemId) {
        if (mDrawerViews != null) {
            for (int i = 0; i < mDrawerViews.length; i++) {
                if (i < mNavigationDrawerItems.size()) {
                    int thisItemId = mNavigationDrawerItems.get(i);
                    formatNavDrawerItem(mDrawerViews[i], thisItemId, itemId == thisItemId);
                }
            }
        }
    }

    public static void setAccessibilityIgnore(View view) {
        view.setClickable(false);
        view.setFocusable(false);
        view.setContentDescription("");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        }
    }

    //Return "true" if item is separator
    private boolean isSeparator(int itemId) {
        return itemId == NAVIGATION_DRAWER_SEPARATOR;
    }

    //Return Self Nav Drawer item
    protected int getSelfNavDrawerItem() { return NAVIGATION_DRAWER_INVALID; }

    private void onNavDrawerItemClicked(final int itemId) {
        if (itemId == getSelfNavDrawerItem()) {
            mDrawerLayout.closeDrawer(Gravity.START);
            return;
        }

        // launch the target Activity after a short delay, to allow the close animation to play
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                goToNavDrawerItem(itemId);
            }
        }, NAVIGATION_DRAWER_LAUBCH_DELAY);

        // change the active item on the list so the user can see the item changed
        setSelectedNavDrawerItem(itemId);
        // fade out the main content
            /*View mainContent = findViewById(R.id.main_content);
            if (mainContent != null) {
                mainContent.animate().alpha(0).setDuration(MAIN_CONTENT_FADEOUT_DURATION);
            }*/


        mDrawerLayout.closeDrawer(Gravity.START);
    }

    //Going to new Activities via Nav Drawer
    private void goToNavDrawerItem(int item) {
        //Intent intent;
        switch (item) {
            case NAVIGATION_DRAWER_LIBRARY:
                //intent = new Intent(this, MyScheduleActivity.class);
                //startActivity(intent);
                //finish();
                break;
            case NAVIGATION_DRAWER_FAVOURITE:
                //intent = new Intent(this, BrowseSessionsActivity.class);
                //startActivity(intent);
                //finish();
                Toast.makeText(getApplicationContext(), "Tasks", Toast.LENGTH_SHORT).show();
                break;
            case NAVIGATION_DRAWER_EXCHANGE:
                //intent = new Intent(this, UIUtils.getMapActivityClass(this));
                //startActivity(intent);
                //finish();
                Toast.makeText(getApplicationContext(), "Lessons", Toast.LENGTH_SHORT).show();
                break;
            case NAVIGATION_DRAWER_FRIENDS:
                //intent = new Intent(this, SocialActivity.class);
                //startActivity(intent);
                //finish();
                Toast.makeText(getApplicationContext(), "Notes", Toast.LENGTH_SHORT).show();
                break;
            case NAVIGATION_DRAWER_SETTINGS:
                //intent = new Intent(this, SettingsActivity.class);
                //startActivity(intent);
                Toast.makeText(getApplicationContext(), "Settings", Toast.LENGTH_SHORT).show();
                break;
            case NAVIGATION_DRAWER_HELP:
                //intent = new Intent(this, VideoLibraryActivity.class);
                //startActivity(intent);
                //finish();
                Toast.makeText(getApplicationContext(), "Feedback", Toast.LENGTH_SHORT).show();
                break;
        }
    }

}