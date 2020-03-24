package com.defalt.lelangonline.ui.splash;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.defalt.lelangonline.MainActivity;
import com.defalt.lelangonline.R;
import com.defalt.lelangonline.data.PreferencesManager;
import com.defalt.lelangonline.data.login.LoginRepository;
import com.defalt.lelangonline.data.login.model.LoggedInUser;
import com.defalt.lelangonline.data.splash.SplashTask;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final Context context = getApplicationContext();
        PreferencesManager.instance(context);

        final String token = PreferencesManager.instance().fetchValueString("token");
        final String username = PreferencesManager.instance().fetchValueString("username");

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (token != null && username != null) {
                    try {
                        int success = new SplashTask().execute(token).get(30000, TimeUnit.MILLISECONDS);

                        if (success == 1) {
                            LoginRepository.setLoggedInUser(new LoggedInUser(token, username));
                        } else if (success == 2) {
                            PreferencesManager.instance().clear();
                        }
                    } catch (ExecutionException | InterruptedException | TimeoutException e) {
                        e.printStackTrace();

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                        alertDialog.setTitle(R.string.alert_conn_title);
                        alertDialog.setMessage(R.string.alert_conn_desc);
                        alertDialog.setIcon(R.drawable.ic_error_black_24dp);
                        alertDialog.setPositiveButton(context.getString(R.string.alert_agree), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                        alertDialog.show();
                    }
                }

                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }).start();
    }
}
