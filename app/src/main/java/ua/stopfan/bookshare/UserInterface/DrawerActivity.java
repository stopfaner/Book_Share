package ua.stopfan.bookshare.UserInterface;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.LruCache;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;

import ua.stopfan.bookshare.Activities.FavouriteActivity;
import ua.stopfan.bookshare.Activities.FriendsListActivity;
import ua.stopfan.bookshare.Activities.LoginActivity;
import ua.stopfan.bookshare.Activities.MapActivity;
import ua.stopfan.bookshare.Activities.SettingsActivity;
import ua.stopfan.bookshare.MainActivity;
import ua.stopfan.bookshare.R;
import ua.stopfan.bookshare.UserInterface.widgets.BezelImageView;
import ua.stopfan.bookshare.Utilities.Constants;
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
            R.drawable.ic_people,
            R.drawable.ic_point,
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

    private Activity thisActivity;
    //views
    private TextView mTextName;
    private TextView mTextEmail;
    private ImageView mImageCover;
    private BezelImageView icon;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisActivity = this;
        mHandler = new Handler();

        setShakeListener();
        setNavDrawerProfile();
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
            }
        });
    }

    void setNavDrawerProfile() {
        GraphRequest request = new GraphRequest().newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse graphResponse) {
                        mTextName = (TextView) findViewById(R.id.profile_name_text);
                        mTextEmail = (TextView) findViewById(R.id.profile_email_text);
                        mImageCover = (ImageView) findViewById(R.id.profile_cover_image);
                        icon = (BezelImageView) findViewById(R.id.profile_image);
                        if (object != null) {
                            String userId = object.optString(Constants.ID);
                            int count = object.optJSONObject("taggable_friends").optJSONArray("data").length();
                            addIdParse(userId, count);
                            String path = object.
                                    optJSONObject(Constants.COVER).optString(Constants.SOURCE);
                            String name = object.optString(Constants.NAME);
                            String email = object.optString(Constants.EMAIL);
                            String picturePath = object.optJSONObject("picture").optJSONObject("data").optString("url");


                            mTextName.setText(name);
                            mTextEmail.setText(email);
                            Picasso.with(getApplicationContext()).load(picturePath).into(icon);
                            Picasso.with(getApplicationContext()).load(path).into(mImageCover);
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, cover, name, email, picture, taggable_friends");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void addIdParse(final String userId, final int count) {
        ParseQuery<ParseObject> query = new ParseQuery<>(Constants.USERS_PARSE);
        query.whereEqualTo("UserId", userId);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e != null) {
                    if(e.getCode() == ParseException.OBJECT_NOT_FOUND) {
                        ParseObject object = new ParseObject(Constants.USERS_PARSE);
                        object.put(Constants.USER_ID, userId);
                        object.put(Constants.BOOKS_COUNT, 0);
                        object.put(Constants.FRIENDS_COUNT, count);
                        object.saveInBackground();
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(shakeListener, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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

    protected void setSelectedNavDrawerItem(int itemId) {
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
                //startActivity(new Intent(getApplicationContext(), FavouriteActivity.class));
                //finish();
                Toast.makeText(getApplicationContext(), "Favourite", Toast.LENGTH_LONG).show();
                break;
            case NAVIGATION_DRAWER_EXCHANGE:
                //intent = new Intent(this, UIUtils.getMapActivityClass(this));
                //startActivity(intent);
                //finish();
                Toast.makeText(getApplicationContext(), "Exchange", Toast.LENGTH_LONG).show();
                break;
            case NAVIGATION_DRAWER_FRIENDS:
                startActivity(new Intent(getApplicationContext(), FriendsListActivity.class));
                finish();
                break;

            case NAVIGATION_DRAWER_MEETS:
                //startActivity(new Intent(getApplicationContext(), MapActivity.class));
                //finish();
                Toast.makeText(getApplicationContext(), "Favourite", Toast.LENGTH_LONG).show();
                break;

            case NAVIGATION_DRAWER_SETTINGS:
                Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(settingsIntent);
                break;
            case NAVIGATION_DRAWER_HELP:
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
                                LoginManager.getInstance().logOut();
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            }
                        }).show();
                break;
        }
    }


}