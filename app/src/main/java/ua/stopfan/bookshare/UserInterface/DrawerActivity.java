package ua.stopfan.bookshare.UserInterface;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.SessionDefaultAudience;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;
import com.sromku.simple.fb.entities.Account;
import com.sromku.simple.fb.entities.Page;
import com.sromku.simple.fb.entities.Profile;
import com.sromku.simple.fb.listeners.OnAccountsListener;
import com.sromku.simple.fb.listeners.OnLogoutListener;
import com.sromku.simple.fb.listeners.OnProfileListener;
import com.sromku.simple.fb.utils.Attributes;
import com.sromku.simple.fb.utils.PictureAttributes;
import com.sromku.simple.fb.utils.Utils;

import org.w3c.dom.Text;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import ua.stopfan.bookshare.Activities.MapActivity;
import ua.stopfan.bookshare.Activities.SettingsActivity;
import ua.stopfan.bookshare.MainActivity;
import ua.stopfan.bookshare.R;
import ua.stopfan.bookshare.UserInterface.widgets.BezelImageView;
import ua.stopfan.bookshare.Utilities.ShakeListener;

/**
 * Created by stopfan on 1/18/15.
 */
public abstract class DrawerActivity extends ActionBarActivity{

    //Navigation drawer items
    protected static final int NAVIGATION_DRAWER_LIBRARY = 0;
    protected static final int NAVIGATION_DRAWER_FAVOURITE = 1;
    protected static final int NAVIGATION_DRAWER_EXCHANGE = 2;
    protected static final int NAVIGATION_DRAWER_FRIENDS = 3;
    protected static final int NAVIGATION_DRAWER_MEETS = 4;
    protected static final int NAVIGATION_DRAWER_SETTINGS = 5;
    protected static final int NAVIGATION_DRAWER_HELP = 6;
    protected static final int NAVIGATION_DRAWER_LOG_OUT = 7;
    protected static final int NAVIGATION_DRAWER_INVALID = -1;
    protected static final int NAVIGATION_DRAWER_SEPARATOR = -2;
    protected static final int NAVIGATION_DRAWER_EMPTY_ITEM = -3;

    //Navigation drawer string titles
    private static int[] NAVIGATION_DRAWER_STRING_ITEMS = new int[]{
            R.string.naw_drawer_my_books,
            R.string.naw_drawer_favourite,
            R.string.naw_drawer_book_exchange,
            R.string.naw_drawer_friend_books,
            R.string.naw_drawer_meet,
            R.string.naw_drawer_settings,
            R.string.naw_drawer_help,
            R.string.naw_drawer_exit
    };

    //Navigation drawer icon id
    private static int[] NAVIGATION_DRAWER_ICON_ID = new int[] {
            R.drawable.ic_book,
            R.drawable.ic_favorite_grey,
            R.drawable.ic_swap,
            R.drawable.ic_group,
            R.drawable.ic_place,
            R.drawable.ic_settings,
            R.drawable.ic_help,
            R.drawable.ic_exit
    };

    private static final int NAVIGATION_DRAWER_LAUBCH_DELAY = 250;

    private Toolbar mToolbar;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ViewGroup mDrawerItemsContainer;

    private View[] mDrawerViews = null;
    private Handler mHandler;
    private static ArrayList<Integer> mNavigationDrawerItems = new ArrayList<>();

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeListener shakeListener;

    private SimpleFacebook mSimpleFacebook;
    private String name;
    //views
    private TextView t;
    private ImageView box;
    private BezelImageView icon;

    private static final String LOG_TAG = "Drawer Activity logging";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setShakeListener();
        Permission[] permissions = new Permission[] {
                Permission.PUBLIC_PROFILE,
                Permission.PUBLISH_ACTION,
                Permission.EMAIL,
                Permission.USER_ABOUT_ME
        };

        SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
                .setAppId(getResources().getString(R.string.app_id))
                .setNamespace(getResources().getString(R.string.namespace))
                .setPermissions(permissions)
                .setDefaultAudience(SessionDefaultAudience.FRIENDS)
                .setAskForAllPermissionsAtOnce(false)
                .build();

        SimpleFacebook.setConfiguration(configuration);
        mHandler = new Handler();
        mSimpleFacebook = SimpleFacebook.getInstance(this);
        OnProfileListener onProfileListener = new OnProfileListener() {
            @Override
            public void onComplete(Profile response) {
                t = (TextView)findViewById(R.id.profile_email_text);
                box = (ImageView)findViewById(R.id.profile_cover_image);
                t.setText(response.getEmail());
                ImageLoader loader = new ImageLoader(getApplicationContext());
                loader.loadImage(response.getPicture(), box);
                Log.d("Request", "Complete");
            }

        };
        PictureAttributes pictureAttributes = Attributes.createPictureAttributes();
        pictureAttributes.setHeight(145);
        pictureAttributes.setWidth(280);
        pictureAttributes.setType(PictureAttributes.PictureType.SQUARE);
        Profile.Properties properties = new Profile.Properties.Builder()
                .add(Profile.Properties.ID)
                .add(Profile.Properties.NAME)
                .add(Profile.Properties.EMAIL)
                .add(Profile.Properties.PICTURE, pictureAttributes)
                .build();
        pictureAttributes.setHeight(40);
        pictureAttributes.setWidth(40);
        Profile.Properties icon_prop = new Profile.Properties.Builder()
                .add(Profile.Properties.PICTURE, pictureAttributes)
                .build();

    }

    private void setShakeListener() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        shakeListener = new ShakeListener();
        shakeListener.setOnShakeListener(new ShakeListener.OnShakeListener() {
            @Override
            public void onShake(int count) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"bookshare@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "Bug Report");
                i.putExtra(Intent.EXTRA_TEXT, "body of email");
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(DrawerActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(DrawerActivity.this, "Sent.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mSimpleFacebook = SimpleFacebook.getInstance(this);

        mSensorManager.registerListener(shakeListener, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mSimpleFacebook.onActivityResult(this, requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(shakeListener);
        super.onPause();
    }

    protected void setUpNavDrawer() {
        //Getting NavDrawer self item
        int selfItem = getSelfNavDrawerItem();
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
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
        mNavigationDrawerItems.add(NAVIGATION_DRAWER_MEETS);
        mNavigationDrawerItems.add(NAVIGATION_DRAWER_EMPTY_ITEM);
        mNavigationDrawerItems.add(NAVIGATION_DRAWER_EMPTY_ITEM);
        mNavigationDrawerItems.add(NAVIGATION_DRAWER_EMPTY_ITEM);
        mNavigationDrawerItems.add(NAVIGATION_DRAWER_SEPARATOR);
        mNavigationDrawerItems.add(NAVIGATION_DRAWER_SETTINGS);
        mNavigationDrawerItems.add(NAVIGATION_DRAWER_HELP);
        mNavigationDrawerItems.add(NAVIGATION_DRAWER_LOG_OUT);

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
        } else if (itemId == NAVIGATION_DRAWER_EMPTY_ITEM){
            layoutToInflate = R.layout.empty;
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

        if (selected)
            view.setBackgroundResource(R.color.material_grey_200);
        else
            view.setBackgroundResource(R.drawable.drawer_item_pressed);

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
        return itemId == NAVIGATION_DRAWER_SEPARATOR || itemId == NAVIGATION_DRAWER_EMPTY_ITEM;
    }

    //Return Self Nav Drawer item
    protected int getSelfNavDrawerItem() { return NAVIGATION_DRAWER_INVALID; }

    protected void onNavDrawerItemClicked(final int itemId) {
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
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
            case NAVIGATION_DRAWER_FAVOURITE:
                //intent = new Intent(this, BrowseSessionsActivity.class);
                //startActivity(intent);
                //finish();
                break;
            case NAVIGATION_DRAWER_EXCHANGE:
                //intent = new Intent(this, UIUtils.getMapActivityClass(this));
                //startActivity(intent);
                //finish();
                break;
            case NAVIGATION_DRAWER_FRIENDS:
                //intent = new Intent(this, SocialActivity.class);
                //startActivity(intent);
                //finish();
                break;

            case NAVIGATION_DRAWER_MEETS:
                startActivity(new Intent(getApplicationContext(), MapActivity.class));
                break;

            case NAVIGATION_DRAWER_SETTINGS:
                Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(settingsIntent);
                break;
            case NAVIGATION_DRAWER_HELP:
                //intent = new Intent(this, VideoLibraryActivity.class);
                //startActivity(intent);
                //finish();
                break;

            case NAVIGATION_DRAWER_LOG_OUT:
                new MaterialDialog.Builder(this)
                        .title(R.string.log_out_title)
                        .content(R.string.log_out_content)
                        .positiveText(R.string.log_out_agree)
                        .negativeText(R.string.log_out_disagree)
                        .callback(new MaterialDialog.Callback() {
                            @Override
                            public void onNegative(MaterialDialog materialDialog) {

                            }

                            @Override
                            public void onPositive(MaterialDialog materialDialog) {
                                logout();
                            }
                        }).show();
                break;
        }
    }

    protected void logout() {
        mSimpleFacebook.logout(new OnLogoutListener() {
            @Override
            public void onLogout() {
                Toast.makeText(getApplicationContext(), "Logged out", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onThinking() {

            }

            @Override
            public void onException(Throwable throwable) {

            }

            @Override
            public void onFail(String s) {

            }
        });
    }
}
