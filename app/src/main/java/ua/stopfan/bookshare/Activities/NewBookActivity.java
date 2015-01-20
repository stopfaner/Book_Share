package ua.stopfan.bookshare.Activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import ua.stopfan.bookshare.R;
import ua.stopfan.bookshare.UserInterface.widgets.FabView;


public class NewBookActivity extends ActionBarActivity {

    private Toolbar mToolbar;
    private FabView mFabView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_book);

        mToolbar = (Toolbar) findViewById(R.id.add_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle(R.string.add_activity_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mFabView = (FabView) findViewById(R.id.fab_add);
        mFabView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_book, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.new_book_settings:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
