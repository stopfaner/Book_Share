package ua.stopfan.bookshare.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonFloat;
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

import org.w3c.dom.Text;

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
    private View mOverlayView;
    private ObservableScrollView mScrollView;
    private TextView mTitleView;
    private ButtonFloat mFab;
    private int mActionBarSize;
    private int mFlexibleSpaceShowFabOffset;
    private int mFlexibleSpaceImageHeight;
    private int mFabMargin;
    int counter=0;
    private float toolbaAlpha;

    private int bookId;

    /**
     * Views of main book information
     */
    private ImageView mImageView;
    private ImageView mItemImageView;
    private TextView mShortDescription;
    private TextView mPeopleCount;
    private TextView mLikesCount;
    private ExpandableTextView mLongDescription;
    private TextView mBookName;
    private TextView mReleaseYear;
    private TextView mLanguage;
    private TextView mPageCount;
    private View mBookColorView;
    private RelativeLayout mRatingView;
    private View round;
    private View littleRound;
    private View mToolbarOverlay;
    private TextView roundText;
    private View overlay;

    private Bitmap src;
    private Bitmap bitmap;
    private Drawable back;
    private RelativeLayout rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_book);

        setSupportActionBar((Toolbar) findViewById(R.id.mtoolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        bookId = intent.getIntExtra("BookId", 0);

        mFlexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        mFlexibleSpaceShowFabOffset = getResources().getDimensionPixelSize(R.dimen.flexible_space_show_fab_offset);
        mActionBarSize = getActionBarSize();

        findAllViewsById();
        getBookInfo();

        mToolbar =(Toolbar) findViewById(R.id.mtoolbar);
        if (!TOOLBAR_IS_STICKY) {
            mToolbar.setBackgroundColor(Color.TRANSPARENT);
        }

        mScrollView.setScrollViewCallbacks(this);
        mTitleView.setText(getTitle());
        mTitleView.setTextColor(getResources().getColor(R.color.white));
        setTitle(null);

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

    private void findAllViewsById() {

        mFab = (ButtonFloat)findViewById(R.id.fab);
        mImageView = (ImageView) findViewById(R.id.toolbar_image);
        mItemImageView = (ImageView) findViewById(R.id.image_item_view);
        mOverlayView = findViewById(R.id.overlay);
        mScrollView = (ObservableScrollView) findViewById(R.id.scroll);
        mTitleView = (TextView) findViewById(R.id.title);
        mShortDescription = (TextView) findViewById(R.id.book_item_view);
        mPeopleCount = (TextView) findViewById(R.id.textView2);
        mLikesCount = (TextView) findViewById(R.id.favorite_text);
        mLongDescription = (ExpandableTextView) findViewById(R.id.lorem_ipsum);

        mToolbarOverlay = findViewById(R.id.toolbar_overlay);
        //Description
        mBookName = (TextView) findViewById(R.id.book_name);
        mReleaseYear = (TextView) findViewById(R.id.released);
        mLanguage = (TextView) findViewById(R.id.language);
        mPageCount = (TextView) findViewById(R.id.pages_count);
        mBookColorView = findViewById(R.id.book_text_item_view);

        overlay = findViewById(R.id.overlay);
        //Rating
        mRatingView = (RelativeLayout) findViewById(R.id.rating_view);
        round = findViewById(R.id.round);
        littleRound = findViewById(R.id.little_round);
        roundText = (TextView) findViewById(R.id.text_round);

        rl = (RelativeLayout) findViewById(R.id.short_desc_view);
    }

    private void getBookInfo() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Books");
        query.fromLocalDatastore();
        mTitleView.setText("");
        query.whereEqualTo("bookId", bookId);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                parseObject.getParseFile("Image").getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] bytes, ParseException e) {
                        if(e == null) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            Bitmap scaledBitmap = decodeSampledBitmapFromResource(bytes,
                                    (rl.getHeight() * bitmap.getWidth()) / bitmap.getHeight(), rl.getHeight());
                            int averageColor = getAverageColor(scaledBitmap);
                            mImageView.setImageBitmap(bitmap);
                            overlay.setBackgroundColor(averageColor);
                            mToolbarOverlay.setBackgroundColor(averageColor);
                            mItemImageView.setImageBitmap(scaledBitmap);
                            setFavouriteButton(averageColor);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                Window window = getWindow();
                                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                                window.setStatusBarColor(averageColor);
                            }

                            mBookColorView.setBackgroundColor(averageColor);

                            mRatingView.setBackgroundColor(averageColor);

                            GradientDrawable roundShape = (GradientDrawable) littleRound.getBackground();
                            roundShape.setColor(getDarker(averageColor));

                            GradientDrawable littleRound = (GradientDrawable) round.getBackground();
                            littleRound.setColor(getBrighter(averageColor));
                            roundText.setTextColor(getBrighter(averageColor));

                            setBitmap(bitmap);
                        }
                    }
                });


                mTitleView.setText(parseObject.getString("Name"));
                mPeopleCount.setText(String.valueOf(parseObject.getInt("PeopleHave")));
                mLikesCount.setText(String.valueOf(parseObject.getInt("Likes")));
                mLongDescription.setText(parseObject.getString("LongDescription"));
                mShortDescription.setText(parseObject.getString("AuthorsNames"));

                mBookName.setText(parseObject.getString("Name"));
                mReleaseYear.setText(String.valueOf(parseObject.getInt("YearOfProduction")));
                mLanguage.setText(parseObject.getString("Language"));
                mPageCount.setText(String.valueOf(parseObject.getInt("PageCount")));
            }
        });

    }

    int getBrighter(int color) {
        float[] hsv = new float[3];
        int averageColor = color;
        Color.colorToHSV(color, hsv);
        hsv[2] = 0.2f + 0.8f * hsv[2];
        averageColor = Color.HSVToColor(hsv);
        return averageColor;
    }

    int getDarker(int color) {
        float[] hsv = new float[3];
        int averageColor = color;
        Color.colorToHSV(color, hsv);
        hsv[2] = 0.2f + 1.2f * hsv[2];
        averageColor = Color.HSVToColor(hsv);
        return averageColor;
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
                ViewHelper.setAlpha(mToolbarOverlay, 0.5f);

            } else {
                mToolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, 0));
                ViewHelper.setAlpha(mToolbarOverlay, 0f);
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


    private void setFavouriteButton(int avColor) {
        mFab.setBackgroundColor(getResources().getColor(R.color.white));
        Drawable drawable = getResources().getDrawable(R.drawable.ic_favorite_grey);

        int iColor = avColor;
        Log.d("COLOR", String.valueOf(iColor));

        int red = (iColor & 0xFF0000) / 0xFFFF;
        int green = (iColor & 0xFF00) / 0xFF;
        int blue = iColor & 0xFF;

        float[] matrix = { 0, 0, 0, 0, red
                , 0, 0, 0, 0, green
                , 0, 0, 0, 0, blue
                , 0, 0, 0, 1, 0 };
        ColorFilter colorFilter = new ColorMatrixColorFilter(matrix);
        drawable.setColorFilter(colorFilter);

        mFab.setDrawableIcon(drawable);
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

    void setSlidr() {
        SlidrConfig config = new SlidrConfig.Builder()
                .position(SlidrPosition.LEFT)
                .sensitivity(1f)
                .build();

        Slidr.attach(this, config).unlock();
    }

    public static Bitmap decodeSampledBitmapFromResource(byte[] bytes,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private int getAverageColor(Bitmap bitmap) {
        int redBucket = 0;
        int greenBucket = 0;
        int blueBucket = 0;
        int pixelCount = 0;

        for(int i = 0; i < bitmap.getHeight(); i++) {
            for (int j = 0; j < bitmap.getWidth(); j++) {
                int color = bitmap.getPixel(j, i);

                pixelCount++;
                redBucket += Color.red(color);
                greenBucket += Color.green(color);
                blueBucket += Color.blue(color);
            }
        }
        return Color.rgb(redBucket / pixelCount,
                            greenBucket/ pixelCount,
                            blueBucket / pixelCount);
    }
}
