package ua.stopfan.bookshare.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.ButtonRectangle;
import com.parse.GetDataCallback;
import com.parse.ParseException;

import java.util.ArrayList;

import ua.stopfan.bookshare.Activities.BookActivity;
import ua.stopfan.bookshare.Library.Book;
import ua.stopfan.bookshare.R;

/**
 * Created by stopfan on 1/12/15.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private ArrayList<Book> books;
    private boolean isHeader;
    private Context context;

    public RecyclerViewAdapter(ArrayList<Book> books, Context context, boolean isHeader) {
        this.books = books;
        this.context = context;
        this.isHeader = isHeader;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_item, parent, false);
            return new VHItem(item);
        } else if (viewType == TYPE_HEADER) {
            if (isHeader) {
                View header = LayoutInflater.from(parent.getContext()).inflate(R.layout.header, parent, false);
                return new VHHeader(header);
            } else {
                View empty = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_card, parent, false);
                return new EmptyHeader(empty);
            }
        }

        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {


        if (holder instanceof VHItem) {
            Book book = getItem(position);
            String bookName = book.getBookName();
            if(bookName.length() > 14) {
                bookName = bookName.substring(0, 14);
                bookName += "...";
            }
            ((VHItem) holder).title.setText(bookName);
            ((VHItem) holder).subTitle.setText(book.getAuthorName());
            if (book.getImage() != null)
                book.getImage().getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] bytes, ParseException e) {
                        if(e == null) {
                            ((VHItem) holder).imageView.setImageBitmap(getRoundedCornerBitmap(decodeSampledBitmapFromResource(bytes, 100, 155)));
                        }
                    }
                });
        } else if (holder instanceof VHHeader) {

        }
    }

    private Drawable getScaledImage(Drawable image) {
        if(image != null) {
            Bitmap src = ((BitmapDrawable) image).getBitmap();
            Bitmap scaled = Bitmap.createScaledBitmap(src, 416, 620, false);
            Drawable temp = new BitmapDrawable(scaled);
            return temp;
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return books.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    private Book getItem(int position) {
        return books.get(position - 1);
    }

    private void delete(String record) {
        int position = 0;
        isHeader = false;
        notifyItemRemoved(position);
    }

    class VHItem extends RecyclerView.ViewHolder {
        TextView title;
        TextView subTitle;
        ImageView imageView;
        ButtonFlat reviewButton;
        ButtonRectangle buttonDelete;
        public VHItem(final View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.text);
            subTitle = (TextView) itemView.findViewById(R.id.sub_text);
            reviewButton = (ButtonFlat) itemView.findViewById(R.id.review);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);

            reviewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent reviewIntent = new Intent(context, BookActivity.class);
                    reviewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    reviewIntent.putExtra("BookId", books.get(getPosition() - 1).getBookId());
                    context.startActivity(reviewIntent);
                }
            });
        }
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

    public Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = 2;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    class VHHeader extends RecyclerView.ViewHolder {
        TextView noButton;
        DeleteButtonListener listener;
        TextView yesButton;
        public VHHeader(View itemView) {
            super(itemView);
            //noButton = (TextView) itemView.findViewById(R.id.no);
            //yesButton = (TextView) itemView.findViewById(R.id.yes);
           // listener = new DeleteButtonListener();
//            noButton.setOnClickListener(listener);
           // yesButton.setOnClickListener(new View.OnClickListener() {
              //  @Override
               // public void onClick(View v) {
              //      Toast.makeText(context, "YES", Toast.LENGTH_LONG).show();
              //  }
          //  });
        }
    }

    class EmptyHeader extends RecyclerView.ViewHolder {
        public EmptyHeader(View view) {
            super(view);
        }
    }

    private class DeleteButtonListener implements View.OnClickListener {
        private String record;

        @Override
        public void onClick(View v) {
            delete(record);
        }

        public void setRecord(String record) {
            this.record = record;
        }
    }
}

