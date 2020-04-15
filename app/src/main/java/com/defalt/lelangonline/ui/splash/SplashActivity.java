package com.defalt.lelangonline.ui.splash;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.defalt.lelangonline.MainActivity;
import com.defalt.lelangonline.R;
import com.defalt.lelangonline.data.PreferencesManager;
import com.defalt.lelangonline.data.RestApi;
import com.defalt.lelangonline.data.login.LoginRepository;
import com.defalt.lelangonline.data.login.model.LoggedInUser;
import com.defalt.lelangonline.ui.SharedFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final Activity mActivity = SplashActivity.this;
        final Context context = getApplicationContext();
        PreferencesManager.instance(context);

        final String token = PreferencesManager.instance().fetchValueString("token");
        final String username = PreferencesManager.instance().fetchValueString("username");

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (token != null && username != null) {
                    RestApi server = SharedFunctions.getRetrofit().create(RestApi.class);
                    RequestBody userToken = RequestBody.create(MediaType.parse("text/plain"), token);

                    Call<ResponseBody> req = server.checkToken(userToken);

                    req.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                            try {
                                if (response.body() != null) {
                                    JSONObject json = new JSONObject(response.body().string());
                                    int success = json.getInt("success");

                                    if (success == 1) {
                                        LoginRepository.setLoggedInUser(new LoggedInUser(token, username));
                                    } else if (success == 2) {
                                        PreferencesManager.instance().clear();
                                    }
                                    SplashUI.startApp(mActivity);
                                } else {
                                    SplashUI.showConnError(mActivity);
                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                                SplashUI.showConnError(mActivity);
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                            t.printStackTrace();
                            SplashUI.showConnError(mActivity);
                        }
                    });
                } else {
                    SplashUI.startApp(mActivity);
                }
            }
        }).start();
    }

    private static class SplashUI {
        static void startApp(Activity mActivity) {
            Intent intent = new Intent(mActivity, MainActivity.class);
            mActivity.startActivity(intent);
            mActivity.finish();
        }

        static void showConnError(final Activity mActivity) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mActivity);
            alertDialog.setTitle(R.string.alert_conn_title);
            alertDialog.setMessage(R.string.alert_conn_desc);
            alertDialog.setIcon(R.drawable.ic_error_black_24dp);
            alertDialog.setPositiveButton(mActivity.getString(R.string.alert_agree), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mActivity.finish();
                }
            });
            alertDialog.show();
        }
    }
}
