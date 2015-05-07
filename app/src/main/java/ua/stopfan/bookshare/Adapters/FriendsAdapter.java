package ua.stopfan.bookshare.Adapters;

import android.content.Context;
import android.content.Intent;
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

import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.ButtonRectangle;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ua.stopfan.bookshare.Activities.BookActivity;
import ua.stopfan.bookshare.Library.Book;
import ua.stopfan.bookshare.Library.Friend;
import ua.stopfan.bookshare.R;
import ua.stopfan.bookshare.UserInterface.widgets.BezelImageView;

/**
 * Created by denys on 4/23/15.
 */
public class FriendsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private static final int TYPE_ITEM = 1;
    private ArrayList<Friend> friends;
    private Context context;

    public FriendsAdapter(ArrayList<Friend> friends, Context context) {
        this.friends = friends;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_list_item, parent, false);
        return new VHItem(item);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof VHItem) {
            Friend friend = getItem(position);
            ((VHItem) holder).name.setText(friend.getName());
            ((VHItem) holder).count.setText("Books count: 0");
            if (friend.getImage() != null) {
                Picasso.with(context).load(friend.getImage()).into(((VHItem) holder).icon);
            }
        }
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM;
    }
    private Friend getItem(int position) {
        return friends.get(position);
    }


    class VHItem extends RecyclerView.ViewHolder {
        TextView name;
        TextView count;
        BezelImageView icon;
        public VHItem(final View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name_view);
            count = (TextView) itemView.findViewById(R.id.count_view);
            icon = (BezelImageView) itemView.findViewById(R.id.people_icon);
        }
    }
}