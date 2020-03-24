package com.defalt.lelangonline.ui.logout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.defalt.lelangonline.R;
import com.defalt.lelangonline.data.login.LoginRepository;
import com.defalt.lelangonline.ui.splash.SplashActivity;

public class LogoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        LoginRepository.logout();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                startActivity(intent);
                finish();
            }
        },1000);
    }
}
