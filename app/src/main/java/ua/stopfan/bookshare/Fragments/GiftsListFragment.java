package ua.stopfan.bookshare.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ua.stopfan.bookshare.Adapters.RecyclerViewAdapter;
import ua.stopfan.bookshare.Library.Book;
import ua.stopfan.bookshare.MainActivity;
import ua.stopfan.bookshare.R;

/**
 * Created by stopfan on 1/19/15.
 */
public class GiftsListFragment extends Fragment implements MainActivity.Populatable {

    private ArrayList<Book> giftBooks = null;
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mAdapter;
    private LinearLayoutManager manager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                                        Bundle savedInstanceState) {
        View giftView = inflater.inflate(R.layout.gift_fragment, container, false);

        mRecyclerView = (RecyclerView) giftView.findViewById(R.id.gift_recycler);
        giftBooks = new ArrayList<Book>();
        populateItems();
        return giftView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new RecyclerViewAdapter(giftBooks, false);
        manager = new LinearLayoutManager(getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    public void populateItems() {
        giftBooks.add(new Book("Fifty Shades of Grey", "E. L. James", getResources().getDrawable(R.drawable.shades)));
        giftBooks.add(new Book("Steve Jobs","Walter Isaacson", getResources().getDrawable(R.drawable.steve)));
        giftBooks.add(new Book("Fahrenheit 451", "Ray Bradbury", getResources().getDrawable(R.drawable.fahrenheit451)));
    }
}
