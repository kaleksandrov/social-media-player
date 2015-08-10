package com.kaleksandrov.smp.ui.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;

import com.kaleksandrov.smp.R;
import com.kaleksandrov.smp.application.FairPlayerApplication;

/**
 * Activity that is started when the application starts. Used for loading application resources.
 *
 * @author kaleksandrov
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                FairPlayerApplication app = (FairPlayerApplication) getApplicationContext();
                app.initResource();
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                ActivityOptionsCompat options = ActivityOptionsCompat
                        .makeSceneTransitionAnimation(SplashActivity.this);
                ActivityCompat.startActivity(SplashActivity.this, new Intent(SplashActivity.this, LibraryActivity.class), options.toBundle());
                finish();
            }

        };
        asyncTask.execute(null, null);
    }
}
