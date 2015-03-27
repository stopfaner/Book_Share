package ua.stopfan.bookshare.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;

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
    private ImageView mItemImageView;
    private View mOverlayView;
    private ObservableScrollView mScrollView;
    private TextView mTitleView;
    private View mFab;
    private int mActionBarSize;
    private int mFlexibleSpaceShowFabOffset;
    private int mFlexibleSpaceImageHeight;
    private int mFabMargin;
    private int mToolbarColor;
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
        int bookId = intent.getIntExtra("BookId", 0);

        View overlay = findViewById(R.id.overlay);
        mToolbarColor = getResources().getColor(R.color.material_grey_500);
        overlay.setBackgroundResource(R.color.material_blue_500);


        mToolbar =(Toolbar) findViewById(R.id.mtoolbar);
        if (!TOOLBAR_IS_STICKY) {
            mToolbar.setBackgroundColor(Color.TRANSPARENT);
        }

        mImageView = (ImageView) findViewById(R.id.toolbar_image);
        mItemImageView = (ImageView) findViewById(R.id.image_item_view);
        mOverlayView = findViewById(R.id.overlay);
        mScrollView = (ObservableScrollView) findViewById(R.id.scroll);
        mScrollView.setScrollViewCallbacks(this);
        mTitleView = (TextView) findViewById(R.id.title);
        mTitleView.setText(getTitle());
        mTitleView.setTextColor(getResources().getColor(R.color.body_text_1));
        setTitle(null);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Books");
        query.fromLocalDatastore();
        query.whereEqualTo("bookId", bookId);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                parseObject.getParseFile("Image").getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] bytes, ParseException e) {
                        if(e == null) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            mImageView.setImageBitmap(bitmap);
                            mItemImageView.setImageBitmap(scaleItemBitmap(bitmap, 174, 174));
                            setBitmap(bitmap);
                        }
                    }
                });
            }
        });

        mFab = findViewById(R.id.fab);
        mFabMargin = getResources().getDimensionPixelSize(R.dimen.margin_standard);

        ScrollUtils.addOnGlobalLayoutListener(mScrollView, new Runnable() {
            @Override
            public void run() {
                mScrollView.scrollTo(0, 1);
            }
        });
        onScrollChanged(0, false, true);
        setFabAlpha(38);
        overridePendingTransition(R.anim.animation_enter, R.anim.animation_enter);
        setSlidr();
    }

    @Override
    public void onBackPressed() {
        this.finish();
        overridePendingTransition(R.anim.animation_leave, R.anim.animation_leave);
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
        toolbaAlpha = ScrollUtils.getFloat((float) scrollY /(2 * flexibleRange), 0, 1);
        ViewHelper.setAlpha(mOverlayView, toolbaAlpha);
        // Scale title text
        float scale = 1 + ScrollUtils.getFloat((flexibleRange - scrollY) /flexibleRange, 0, MAX_TEXT_SCALE_DELTA);
        ViewHelper.setPivotX(mTitleView, 0);
        ViewHelper.setPivotY(mTitleView, 0);
        ViewHelper.setScaleX(mTitleView, scale);
        ViewHelper.setScaleY(mTitleView, scale);

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

    private void setBitmap(Bitmap bitmap) {

        int screenWidth = getResources().getDisplayMetrics().widthPixels;

        src = bitmap;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inSampleSize = 1;
        options.inScaled = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
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

        back = new BitmapDrawable(getResources(), bitmap);
    }

    private Bitmap scaleItemBitmap(Bitmap bmp, int newWidth, int newHeight) {
        if (bmp.getHeight() > newHeight || bmp.getWidth() > newWidth) {
            int originalWidth = bmp.getWidth();
            int originalHeight = bmp.getHeight();
            float inSampleSize;
            if (originalWidth > originalHeight) {
                inSampleSize = (float) newWidth / originalWidth;
            } else {
                inSampleSize = (float) newHeight / originalHeight;
            }
            newWidth = Math.round(originalWidth * inSampleSize);
            newHeight = Math.round(originalHeight * inSampleSize);
            bitmap = Bitmap.createScaledBitmap(bmp, newWidth, newHeight, true);
        } else {
            bitmap = bmp;
        }
        return bitmap;
    }

    void setSlidr() {
        SlidrConfig config = new SlidrConfig.Builder()
                .position(SlidrPosition.LEFT)
                .sensitivity(1f)
                .build();

        Slidr.attach(this, config).unlock();
    }
}
