package ua.stopfan.bookshare.Activities;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ua.stopfan.bookshare.Adapters.FriendsAdapter;
import ua.stopfan.bookshare.Adapters.RecyclerViewAdapter;
import ua.stopfan.bookshare.Fragments.ForSaleListFragment;
import ua.stopfan.bookshare.Library.Book;
import ua.stopfan.bookshare.Library.Friend;
import ua.stopfan.bookshare.R;
import ua.stopfan.bookshare.UserInterface.widgets.BezelImageView;
import ua.stopfan.bookshare.Utilities.Connectivity;
import ua.stopfan.bookshare.Utilities.Constants;

public class FriendsListActivity extends ActionBarActivity {

    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private FriendsAdapter mRecyclerviewAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private Activity activity = this;

    private ArrayList<Friend> friends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        toolbar = (Toolbar) findViewById(R.id.toolbar_friends);
        mRecyclerView = (RecyclerView) findViewById(R.id.friends);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        friends = new ArrayList<>();
        populateFiends();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friends_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    public void populateFiends() {

        GraphRequest request = new GraphRequest().newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse graphResponse) {
                        JSONArray array= object.optJSONObject("taggable_friends").optJSONArray("data");
                        int count = array.length();
                        for(int i = 0; i < count; i++) {
                            JSONObject friend = array.optJSONObject(i);
                            friends.add(new Friend(friend.optString("name"),
                                    friend.optString("id"),
                                    friend.optJSONObject("picture").optJSONObject("data").optString("url")));
                        }

                        mRecyclerviewAdapter = new FriendsAdapter(friends, getApplicationContext());
                        mRecyclerView.setAdapter(mRecyclerviewAdapter);
                        mLinearLayoutManager = new LinearLayoutManager(activity);
                        mRecyclerView.setAdapter(mRecyclerviewAdapter);
                        mRecyclerView.setLayoutManager(mLinearLayoutManager);
                        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "taggable_friends");
        request.setParameters(parameters);
        request.executeAsync();
    }

}
