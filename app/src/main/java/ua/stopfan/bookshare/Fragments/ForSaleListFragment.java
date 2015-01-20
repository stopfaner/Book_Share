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
 * Created by stopfan on 1/21/15.
 */
public class ForSaleListFragment extends Fragment implements MainActivity.Populatable {

    private ArrayList<Book> saleBooks = null;
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mAdapter;
    private LinearLayoutManager manager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                                        Bundle savedInstanceState) {
        View forSaleList = inflater.inflate(R.layout.forsale_fragment, container, false);

        mRecyclerView = (RecyclerView) forSaleList.findViewById(R.id.forsale_recycler_view);
        saleBooks = new ArrayList<Book>();
        populateItems();
        return forSaleList;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter = new RecyclerViewAdapter(saleBooks, false);
        manager = new LinearLayoutManager(getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    public void populateItems() {
        saleBooks.add(new Book("Fifty Shades of Grey", "E. L. James", getResources().getDrawable(R.drawable.shades)));
        saleBooks.add(new Book("Steve Jobs","Walter Isaacson", getResources().getDrawable(R.drawable.steve)));
    }

}
