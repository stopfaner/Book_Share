package ua.stopfan.bookshare.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AppEventsLogger;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.listeners.OnLoginListener;

import ua.stopfan.bookshare.Fragments.MainFragment;
import ua.stopfan.bookshare.MainActivity;
import ua.stopfan.bookshare.R;

/**
 * Created by stopfan on 1/12/15.
 */
public class LoginActivity extends FragmentActivity {

    private SimpleFacebook mSimpleFacebook;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mSimpleFacebook = SimpleFacebook.getInstance(this);
        if (mSimpleFacebook.isLogin()) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
        else
            mSimpleFacebook.login(new OnLoginListener() {
                @Override
                public void onLogin() {
                    Toast.makeText(getApplicationContext(), "Logined", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }

                @Override
                public void onNotAcceptingPermissions(Permission.Type type) {

                }

                @Override
                public void onThinking() {

                }

                @Override
                public void onException(Throwable throwable) {

                }

                @Override
                public void onFail(String s) {

                }
            });
    }

    @Override
    public void onResume() {
        super.onResume();
        mSimpleFacebook = SimpleFacebook.getInstance(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mSimpleFacebook.onActivityResult(this, requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

}
