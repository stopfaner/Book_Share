package ua.stopfan.bookshare.Activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * Created by stopfan on 1/12/15.
 */
public class LoginActivity extends FragmentActivity {


    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
      //  AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        //AppEventsLogger.deactivateApp(this);
    }
}
