package ua.stopfan.bookshare.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import ua.stopfan.bookshare.Adapters.RecyclerViewAdapter;
import ua.stopfan.bookshare.Library.Book;
import ua.stopfan.bookshare.MainActivity;
import ua.stopfan.bookshare.R;
import ua.stopfan.bookshare.Utilities.Connectivity;

/**
 * Created by stopfan on 1/21/15.
 */
public class ForSaleListFragment extends Fragment  {

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
        saleBooks = new ArrayList<>();
        //populateItems();
        fillListWithParseObjects();
        return forSaleList;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        manager = new LinearLayoutManager(getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    void fillListWithParseObjects() {
        if (Connectivity.isConnected(getActivity().getApplicationContext()) && saleBooks.isEmpty()) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Books");
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    if (e == null) {
                        for (ParseObject object : parseObjects) {
                            object.pinInBackground();
                            Book book = new Book(object.getString("Name"),
                                    object.getString("AuthorsNames"),
                                    object.getParseFile("Image"),
                                    object.getInt("bookId"));
                            Log.d(ForSaleListFragment.class.getName(), object.getString("Name") + object.getString("AuthorsNames"));
                            saleBooks.add(book);
                        }
                    } else {
                        Log.d(ForSaleListFragment.class.getName(), "Error while downloading from parse");
                    }
                    mAdapter = new RecyclerViewAdapter(saleBooks, getActivity().getApplicationContext(), false);
                    mRecyclerView.setAdapter(mAdapter);
                    Log.d("TAG", "Count " + saleBooks.size());
                }
            });
        } else {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Books");
            query.fromLocalDatastore();
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    if (e == null) {
                        for (int i = parseObjects.size() - 1; i >= 0; i--) {
                            Book book = new Book(parseObjects.get(i).getString("Name"),
                                                parseObjects.get(i).getString("AuthorsNames"),
                                                    parseObjects.get(i).getParseFile("Image"),
                                                        parseObjects.get(i).getInt("bookId"));
                            Log.d(ForSaleListFragment.class.getName(), parseObjects.get(i).getString("Name") + parseObjects.get(i).getString("AuthorsNames"));
                            saleBooks.add(book);
                        }
                    } else {
                        Log.d(ForSaleListFragment.class.getName(), "Error while downloading from parse");
                    }
                    mAdapter = new RecyclerViewAdapter(saleBooks, getActivity().getApplicationContext(), false);
                    mRecyclerView.setAdapter(mAdapter);
                    Log.d("TAG", "Count " + saleBooks.size());
                }
            });
        }
    }

}
