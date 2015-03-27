package ua.stopfan.bookshare.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import fr.castorflex.android.circularprogressbar.CircularProgressDrawable;
import ua.stopfan.bookshare.Adapters.RecyclerViewAdapter;
import ua.stopfan.bookshare.Library.Book;
import ua.stopfan.bookshare.MainActivity;
import ua.stopfan.bookshare.R;
import ua.stopfan.bookshare.Utilities.Connectivity;
import ua.stopfan.bookshare.Utilities.Constants;

/**
 * Created by stopfan on 1/18/15.
 */
public class MainPagerFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mRecyclerviewAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private SwipeRefreshLayout refreshLayout;
    private FloatingActionButton fab;

    private ArrayList<Book> books;
    private  CircularProgressBar cpd;

    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity().getApplicationContext();

        View rootView = inflater.inflate(R.layout.recycler_view_fragment, container, false);
        cpd= (CircularProgressBar) rootView.findViewById(R.id.cpd);


        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorScheme(Constants.SWIPE_AMBER, Constants.SWIPE_BLUE,
                Constants.SWIPE_INDIGO, Constants.SWIPE_GREEN, Constants.SWIPE_LIGHT_GREEN);

        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CircularProgressDrawable)cpd.getIndeterminateDrawable()).start();
            }
        });
        fab.attachToRecyclerView(mRecyclerView);

        books = new ArrayList<>();
        populateItems(false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setAdapter(mRecyclerviewAdapter);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onRefresh() {

        refreshLayout.setRefreshing(true);
        if (!Connectivity.isConnected(getActivity().getApplicationContext()))
            runSnackBar();
        ParseObject.unpinAllInBackground("Books");
        books = new ArrayList<>();

        populateItems(true);

        refreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(false);
            }
        }, 3000);

    }

    public void populateItems(final boolean refreshed) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Books");
        Context context = mContext;
        if (Connectivity.isConnected(context) && Connectivity.isConnectedFast(context) || refreshed) {
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
                            books.add(book);
                        }
                        cpd.progressiveStop();
                    } else {
                        runSnackBar();
                    }
                }
            });
        } else {
            query.fromLocalDatastore();
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    if(e == null) {
                        for (int i = parseObjects.size() - 1; i >= 0; i--) {
                            Book book = new Book(parseObjects.get(i).getString("Name"),
                                    parseObjects.get(i).getString("AuthorsNames"),
                                    parseObjects.get(i).getParseFile("Image"),
                                    parseObjects.get(i).getInt("bookId"));
                            books.add(book);
                        }
                        cpd.progressiveStop();
                    } else {
                        runSnackBar();
                    }
                }
            });
        }

        mRecyclerviewAdapter = new RecyclerViewAdapter(books, getActivity().getApplicationContext(), true);
        mRecyclerView.setAdapter(mRecyclerviewAdapter);
        Log.d("TAG", "Count " + books.size());
    }

    private void deleteList() {

    }

    private void runSnackBar() {
        SnackbarManager.show(
                Snackbar.with(getActivity().getApplicationContext())
                        .text("Interner error")
                        .actionColor(Constants.SWIPE_AMBER)
                        .actionLabel("Try again")
                        .actionListener(new ActionClickListener() {
                            @Override
                            public void onActionClicked(Snackbar snackbar) {

                            }
                        }),
                    getActivity());
    }

}
