package ua.stopfan.bookshare.Fragments;

import android.app.Fragment;
import android.content.Context;
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
 * Created by stopfan on 1/19/15.
 */
public class GiftsListFragment extends Fragment {

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
        populateItems(false);
        return giftView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new RecyclerViewAdapter(giftBooks, getActivity().getApplicationContext(), false);
        manager = new LinearLayoutManager(getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    public void populateItems(boolean refreshed) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Books");
        Context context = getActivity().getApplicationContext();
        final boolean condition = Connectivity.isConnected(context) && Connectivity.isConnectedFast(context) ||
                Connectivity.isConnected(context) && giftBooks.isEmpty() ||
                refreshed;
        if(!condition)
            query.fromLocalDatastore();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if(e == null) {
                    if(condition) {
                        for (ParseObject object : parseObjects) {
                            object.pinInBackground();
                            Book book = new Book(object.getString("Name"),
                                    object.getString("AuthorsNames"),
                                    object.getParseFile("Image"),
                                    object.getInt("bookId"));
                            Log.d(ForSaleListFragment.class.getName(), object.getString("Name") + object.getString("AuthorsNames"));
                            giftBooks.add(book);
                        }
                    } else {
                        for (int i = parseObjects.size() - 1; i >= 0; i--) {
                            Book book = new Book(parseObjects.get(i).getString("Name"),
                                    parseObjects.get(i).getString("AuthorsNames"),
                                    parseObjects.get(i).getParseFile("Image"),
                                    parseObjects.get(i).getInt("bookId"));
                            giftBooks.add(book);
                        }
                    }
                } else {
                    runSnackbar();
                }
            }
        });
    }

    private void runSnackbar() {

    }
}
