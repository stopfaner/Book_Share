package ua.stopfan.bookshare.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;

import ua.stopfan.bookshare.Adapters.BaseActivity;
import ua.stopfan.bookshare.MainActivity;
import ua.stopfan.bookshare.R;
import ua.stopfan.bookshare.UserInterface.widgets.ExpandableTextView;

/**
 * Created by stopfan on 1/10/15.
 */
public class BookActivity extends BaseActivity implements ObservableScrollViewCallbacks {

    private static final float MAX_TEXT_SCALE_DELTA = 0.3f;
    private static final boolean TOOLBAR_IS_STICKY = true;

    private Toolbar mToolbar;
    private ImageView mImageView;
    private View mOverlayView;
    private ObservableScrollView mScrollView;
    private TextView mTitleView;
    private View mFab;
    private int mActionBarSize;
    private int mFlexibleSpaceShowFabOffset;
    private int mFlexibleSpaceImageHeight;
    private int mFabMargin;
    private int mToolbarColor;
    private boolean mFabIsShown;
    int counter=0;
    private float toolbaAlpha;

    private Bitmap src;
    private Bitmap bitmap;
    private Drawable back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        setSupportActionBar((Toolbar) findViewById(R.id.mtoolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFlexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        mFlexibleSpaceShowFabOffset = getResources().getDimensionPixelSize(R.dimen.flexible_space_show_fab_offset);
        mActionBarSize = getActionBarSize();
        String yourText = "The phenomenal bestseller about Apple co-founder Steve Jobs from the author of the acclaimed biographies of Benjamin Franklin and Albert Einstein." +
                "Based on more than forty interviews with Jobs conducted over two years—as well as interviews with more than 100 family members, friends, adversaries, competitors, and colleagues—Walter Isaacson set down the riveting story of the roller-coaster life and searingly intense personality of a creative entrepreneur whose passion for perfection and ferocious drive revolutionized six industries: personal computers, animated movies, music, phones, tablet computing, and digital publishing. Isaacson’s portrait touched hundreds of thousands of readers.\n";

        ExpandableTextView expandableTextView = (ExpandableTextView) findViewById(R.id.lorem_ipsum);
        expandableTextView.setText(yourText);

        Intent intent = getIntent();
        String color = intent.getStringExtra("Color");
        int back = intent.getIntExtra("Images", R.drawable.cc);
        View overlay = (View) findViewById(R.id.overlay);
        if (color.equals("Grey")) {

            mToolbarColor = getResources().getColor(R.color.material_grey_500);
            overlay.setBackgroundResource(R.color.material_grey_500);
        } else {
            overlay.setBackgroundResource(R.color.material_pink_400);
            mToolbarColor = getResources().getColor(R.color.material_pink_400);
        }

        mToolbar =(Toolbar) findViewById(R.id.mtoolbar);

        if (!TOOLBAR_IS_STICKY) {
            mToolbar.setBackgroundColor(Color.TRANSPARENT);
        }
        mImageView =(ImageView) findViewById(R.id.toolbar_image);
        mImageView.setImageResource(back);
        mOverlayView = findViewById(R.id.overlay);
        mScrollView = (ObservableScrollView) findViewById(R.id.scroll);
        mScrollView.setScrollViewCallbacks(this);
        mTitleView = (TextView) findViewById(R.id.title);
        mTitleView.setText(getTitle());
        mTitleView.setTextColor(getResources().getColor(R.color.body_text_1));
        setTitle(null);
        mFab = findViewById(R.id.fab);

        mFabMargin = getResources().getDimensionPixelSize(R.dimen.margin_standard);

        ScrollUtils.addOnGlobalLayoutListener(mScrollView, new Runnable() {
            @Override
            public void run() {
                mScrollView.scrollTo(0, 1);
                // If you'd like to start from scrollY == 0, don't write like this:
                //mScrollView.scrollTo(0, 0);
                // The initial scrollY is 0, so it won't invoke onScrollChanged().
                // To do this, use the following:
                //onScrollChanged(0, false, false);

                // You can also achieve it with the following codes.
                // This causes scroll change from 1 to 0.
                //mScrollView.scrollTo(0, 1);
                //mScrollView.scrollTo(0, 0);
            }
        });
        onScrollChanged(0, false, true);
        setFabAlpha(38);
        setBitmap();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_book, menu);
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

            case android.R.id.home:
                startActivity(new Intent(this, MainActivity.class));
                break;

            /*case android.R.id.home:
                //Toast.makeText(getApplicationContext(), "Home pressed", Toast.LENGTH_SHORT).show();
                break;*/

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        // Translate overlay and image
        float flexibleRange = mFlexibleSpaceImageHeight - mActionBarSize;
        int minOverlayTransitionY = mActionBarSize - mOverlayView.getHeight();
        ViewHelper.setTranslationY(mOverlayView, ScrollUtils.getFloat(-scrollY, minOverlayTransitionY, 0));
        ViewHelper.setTranslationY(mImageView, ScrollUtils.getFloat(-scrollY / 2, minOverlayTransitionY, 0));

        // Change alpha of overlay
        toolbaAlpha = ScrollUtils.getFloat((float) scrollY /(3 * flexibleRange), 0, 1);
        ViewHelper.setAlpha(mOverlayView, 0f);
        // Scale title text
        float scale = 1 + ScrollUtils.getFloat((flexibleRange - scrollY) /flexibleRange, 0, MAX_TEXT_SCALE_DELTA);
        ViewHelper.setPivotX(mTitleView, 0);
        ViewHelper.setPivotY(mTitleView, 0);
        ViewHelper.setScaleX(mTitleView, scale);
        ViewHelper.setScaleY(mTitleView, scale);

        Log.d("SCROLL", String.valueOf(scrollY));
        // Translate title text
        int maxTitleTranslationY = (int) (mFlexibleSpaceImageHeight - mTitleView.getHeight() * scale);
        int maxTitleTranslationX = 20;
        int titleTranslationY = maxTitleTranslationY - scrollY;
        int titleTranslationX;
        if(scrollY <=277) {
            titleTranslationX = maxTitleTranslationX + (int) ((float) scrollY / 7);
        } else
            titleTranslationX = 59;
        if (TOOLBAR_IS_STICKY) {
            titleTranslationY = Math.max(0, titleTranslationY);
            titleTranslationX = Math.max(0, titleTranslationX);
        }
        ViewHelper.setTranslationY(mTitleView, titleTranslationY);
        ViewHelper.setTranslationX(mTitleView, titleTranslationX);
        // Translate FAB
        int maxFabTranslationY = mFlexibleSpaceImageHeight - mFab.getHeight() / 2;
        float fabTranslationY = ScrollUtils.getFloat(
                -scrollY + mFlexibleSpaceImageHeight - mFab.getHeight() / 2,
                mActionBarSize - mFab.getHeight() / 2,
                maxFabTranslationY);
        ViewHelper.setTranslationX(mFab, mOverlayView.getWidth() - 20 - mFab.getWidth());
        ViewHelper.setTranslationY(mFab, fabTranslationY);

        // Show/hide FAB
        setFabAlpha((int) ViewHelper.getTranslationY(mFab));
        if (TOOLBAR_IS_STICKY) {
            // Change alpha of toolbar background
            if (-scrollY + mFlexibleSpaceImageHeight <= mActionBarSize) {
                mToolbar.setBackground(back);
            } else {
                mToolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, mToolbarColor));
            }
        } else {
            // Translate Toolbar
            if (scrollY < mFlexibleSpaceImageHeight) {
                ViewHelper.setTranslationY(mToolbar, 0);
            } else {
                ViewHelper.setTranslationY(mToolbar, -scrollY);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        counter = 0;
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }

    private void setFabAlpha(int scroll) {
        float opacityRange = (float)1/(314 - 38)*(scroll-38);
        mFab.setAlpha(opacityRange);
        if(opacityRange < 0.05) {
            mFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {}
            });
        } else {
            mFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Favourite", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setBitmap() {

        int screenWidth = getResources().getDisplayMetrics().widthPixels;

        src = BitmapFactory.decodeResource(getApplicationContext().getResources(),
        R.drawable.shades);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getApplicationContext().getResources(),
                                    R.drawable.cc, options);
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inSampleSize = 1;
        options.inScaled = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.drawable.cc, options);
        Matrix matrix = new Matrix();
        matrix.postScale(((float)screenWidth)/src.getWidth(),
                             ((float)screenWidth)/src.getWidth());
        Bitmap resizedBitmap = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        bitmap = Bitmap.createBitmap(
                resizedBitmap,
                0,
                resizedBitmap.getHeight()/2 - mActionBarSize/2,
                resizedBitmap.getWidth(),
                mActionBarSize
        );

        float opacity = ScrollUtils.getFloat((float) 276 /(3 * (360-84)), 0, 1);
        Log.d("TAG", String.valueOf(opacity));

        back = new BitmapDrawable(getResources(), bitmap);
    }
}
