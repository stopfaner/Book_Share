package ua.stopfan.bookshare.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.joooonho.SelectableRoundedImageView;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {


        if (holder instanceof VHItem) {
            Book book = getItem(position);
            String bookName = book.getBookName();
            if(bookName.length() > 14) {
                bookName = bookName.substring(0, 14);
                bookName += "...";
            }
            ((VHItem) holder).title.setText(bookName);
            ((VHItem) holder).subTitle.setText(book.getAuthorName());
            ((VHItem) holder).imageView.setImageDrawable(getScaledImage(book.getImage()));
        } else if (holder instanceof VHHeader) {

        }
    }

    private Drawable getScaledImage(Drawable image) {
        Bitmap src = ((BitmapDrawable)image).getBitmap();
        Bitmap scaled = Bitmap.createScaledBitmap(src, 416, 620, false);
        Drawable temp = new BitmapDrawable(scaled);
        return temp;
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
        TextView reviewButton;
        public VHItem(final View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.text);
            subTitle = (TextView) itemView.findViewById(R.id.sub_text);
            reviewButton = (TextView) itemView.findViewById(R.id.review);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);

            reviewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent reviewIntent = new Intent(context, BookActivity.class);
                   // reviewIntent.putExtra("Title", title.getText().toString());
                    reviewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    reviewIntent.putExtra("Color", "Grey");
                    reviewIntent.putExtra("Images",R.drawable.cc);
                    context.startActivity(reviewIntent);
                }
            });
        }
    }

    class VHHeader extends RecyclerView.ViewHolder {
        TextView noButton;
        DeleteButtonListener listener;
        TextView yesButton;
        public VHHeader(View itemView) {
            super(itemView);
            noButton = (TextView) itemView.findViewById(R.id.no);
            yesButton = (TextView) itemView.findViewById(R.id.yes);
            listener = new DeleteButtonListener();
            noButton.setOnClickListener(listener);
            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "YES", Toast.LENGTH_LONG).show();
                }
            });
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

