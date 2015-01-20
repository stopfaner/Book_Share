package ua.stopfan.bookshare.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ua.stopfan.bookshare.Adapters.RecyclerViewAdapter;
import ua.stopfan.bookshare.Library.Book;
import ua.stopfan.bookshare.MainActivity;
import ua.stopfan.bookshare.R;

/**
 * Created by stopfan on 1/18/15.
 */
public class MainPagerFragment extends Fragment implements MainActivity.Populatable {

    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mRecyclerviewAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private ArrayList<Book> books;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recycler_view_fragment, container, false);
        books = new ArrayList<>();
        populateItems();
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mRecyclerviewAdapter = new RecyclerViewAdapter(books, getActivity().getApplicationContext(), true);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setAdapter(mRecyclerviewAdapter);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    public void populateItems() {
        books.add(new Book("Hundred years of solitude", "Gabriel Garcia Marquez", getResources().getDrawable(R.drawable.hundred)));
        books.add(new Book("Fifty Shades of Grey", "E. L. James", getResources().getDrawable(R.drawable.shades)));
        books.add(new Book("Steve Jobs","Walter Isaacson", getResources().getDrawable(R.drawable.steve)));
        books.add(new Book("Fahrenheit 451", "Ray Bradbury", getResources().getDrawable(R.drawable.fahrenheit451)));
    }

}
