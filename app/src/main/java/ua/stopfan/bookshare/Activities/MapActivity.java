package ua.stopfan.bookshare.Activities;

import android.location.Location;
import android.support.v7.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import ua.stopfan.bookshare.R;
import ua.stopfan.bookshare.UserInterface.DrawerActivity;

/**
 * Created by stopfan on 1/28/15.
 */
public class MapActivity extends DrawerActivity implements GoogleMap.OnMapClickListener{

    private Toolbar mToolbar;
    private ActionBar bar;
    private GoogleMap googleMap;
    private MapFragment mapFragment;
    private UiSettings settings;
    private Marker marker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_map);
        setSupportActionBar(toolbar);
        bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        super.setUpNavDrawer();
        super.onNavDrawerItemClicked(NAVIGATION_DRAWER_LIBRARY);

        mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
        googleMap = mapFragment.getMap();
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMapClickListener(this);
        settings = googleMap.getUiSettings();
        settings.setMyLocationButtonEnabled(true);
        settings.setIndoorLevelPickerEnabled(true);
        settings.setAllGesturesEnabled(true);


    }

    @Override
    public void onMapClick(LatLng point) {
        googleMap.addMarker(new MarkerOptions()
            .position(point)
            .title("Clicked")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) MapActivity.this.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(MapActivity.this.getComponentName()));
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                break;


            /*case android.R.id.home:
                //Toast.makeText(getApplicationContext(), "Home pressed", Toast.LENGTH_SHORT).show();
                break;*/

        }

        return super.onOptionsItemSelected(item);
    }

}
