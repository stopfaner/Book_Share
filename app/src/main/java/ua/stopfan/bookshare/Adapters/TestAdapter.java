package ua.stopfan.bookshare.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.joooonho.SelectableRoundedImageView;

import ua.stopfan.bookshare.R;
import ua.stopfan.bookshare.UserInterface.CustomImageView;

/**
 * Created by stopfan on 1/10/15.
 */
public class TestAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    String[] array;
    public TestAdapter(Context context, String[] array) {
        this.context = context;
        this.array = array;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return array.length;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return array[position];
    }

    private String getString(int position) {
        return array[position];
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if(view == null) {
            view = inflater.inflate(R.layout.cardview_item, parent, false);
        }

        ((SelectableRoundedImageView) view.findViewById(R.id.imageView)).setImageResource(R.drawable.cc);
        ((TextView) view.findViewById(R.id.text)).setText(getString(position));

        return view;
    }

}
