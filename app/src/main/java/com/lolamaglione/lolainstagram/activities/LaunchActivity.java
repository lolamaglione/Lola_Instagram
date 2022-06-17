package com.lolamaglione.lolainstagram.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.lolamaglione.lolainstagram.R;

/**
 * LaunchActivity creates the instagram logo launch page for the first 1 second you open the app.
 */
public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        new Handler().postDelayed(() -> goToLoginActivity(), 1000);
    }

    private void goToLoginActivity(){
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }


}