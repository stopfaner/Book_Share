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
public class WishListFragment extends Fragment  implements MainActivity.Populatable {

    private ArrayList<Book> wishListArray = null;
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                                Bundle savedInstanceState) {
        View favouriteView = inflater.inflate(R.layout.wish_list_fragment, container, false);
        mRecyclerView = (RecyclerView) favouriteView.findViewById(R.id.wish_recycler_view);
        wishListArray = new ArrayList<>();
        populateItems();
        return favouriteView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new RecyclerViewAdapter(wishListArray, getActivity().getApplicationContext(), false);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    public void populateItems() {
        wishListArray.add(new Book("Hundred years of solitude", "Gabriel Garcia Marquez", getResources().getDrawable(R.drawable.hundred)));
        wishListArray.add(new Book("Fifty Shades of Grey", "E. L. James", getResources().getDrawable(R.drawable.shades)));
        wishListArray.add(new Book("Steve Jobs","Walter Isaacson", getResources().getDrawable(R.drawable.steve)));
    }

}
