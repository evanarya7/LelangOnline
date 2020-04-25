package com.defalt.lelangonline.data.account;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.defalt.lelangonline.MainActivity;
import com.defalt.lelangonline.data.RestApi;
import com.defalt.lelangonline.ui.SharedFunctions;
import com.defalt.lelangonline.ui.account.AccountFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountTask extends AsyncTask<String, Void, Void> {
    private int success;
    private AccountFragment.AccountUI accountUI;

    public AccountTask(AccountFragment.AccountUI accountUI) {
        this.accountUI = accountUI;
    }

    protected Void doInBackground(String... args) {
        RestApi server = SharedFunctions.getRetrofit().create(RestApi.class);
        RequestBody token = RequestBody.create(MediaType.parse("text/plain"), args[0]);

        Call<ResponseBody> req = server.getProfileByToken(token);

        req.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    if (response.body() != null) {
                        JSONObject json = new JSONObject(response.body().string());
                        success = json.getInt("success");

                        if (success == 1) {
                            String name = json.getString("name");
                            String email = json.getString("email");
                            String phone = json.getString("phone");
                            String image = json.getString("image");

                            double completeness = 1.0;
                            if (name.equals("null")) {
                                completeness -= 0.25;
                            }
                            if (email.equals("null")) {
                                completeness -= 0.25;
                            }
                            if (phone.equals("null")) {
                                completeness -= 0.25;
                            }
                            if (image.equals("null")) {
                                completeness -= 0.25;
                            }

                            accountUI.updateUI(name, image, completeness);
                        } else {
                            executeError();
                        }
                    } else {
                        executeError();
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();

                    executeError();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                t.printStackTrace();

                executeError();
            }
        });

        return null;
    }

    private void executeError() {
        if (!MainActivity.isConnectionError()) {
            accountUI.showError();
            MainActivity.setIsConnectionError(true);
        }
    }
}
