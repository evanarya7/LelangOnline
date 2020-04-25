package com.defalt.lelangonline.data.account.edit;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.defalt.lelangonline.data.RestApi;
import com.defalt.lelangonline.ui.SharedFunctions;
import com.defalt.lelangonline.ui.account.edit.ProfileEditActivity;
import com.defalt.lelangonline.ui.items.edit.ItemsEditActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileByIDTask extends AsyncTask<String, Void, Void> {
    private int success;
    private ProfileEditActivity.ProfileEditUI profileEditUI;

    public ProfileByIDTask(ProfileEditActivity.ProfileEditUI profileEditUI) {
        this.profileEditUI = profileEditUI;
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
                            if (name.equals("null")) { name = ""; }
                            String email = json.getString("email");
                            if (email.equals("null")) { email = ""; }
                            String phone = json.getString("phone");
                            if (phone.equals("null")) { phone = ""; }
                            String image = json.getString("image");

                            profileEditUI.updateEditText(name, email, phone, image);
                        } else {
                            profileEditUI.showConnError();
                        }
                    } else {
                        profileEditUI.showConnError();
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                    profileEditUI.showConnError();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                t.printStackTrace();
                profileEditUI.showConnError();
            }
        });

        return null;
    }
}
