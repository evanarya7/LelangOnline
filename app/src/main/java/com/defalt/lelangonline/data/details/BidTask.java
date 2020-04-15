package com.defalt.lelangonline.data.details;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.defalt.lelangonline.data.RestApi;
import com.defalt.lelangonline.ui.SharedFunctions;
import com.defalt.lelangonline.ui.details.DetailsActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BidTask extends AsyncTask<String, Void, Void> {
    private int success;
    private DetailsActivity.DetailsUI detailsUI;

    public BidTask(DetailsActivity.DetailsUI detailsUI) {
        this.detailsUI = detailsUI;
    }

    protected Void doInBackground(String... args) {
        RestApi server = SharedFunctions.getRetrofit().create(RestApi.class);
        RequestBody auctionID = RequestBody.create(MediaType.parse("text/plain"), args[0]);
        RequestBody bidAmount = RequestBody.create(MediaType.parse("text/plain"), args[1]);
        RequestBody token = RequestBody.create(MediaType.parse("text/plain"), args[2]);

        Call<ResponseBody> req = server.postBid(auctionID, bidAmount, token);

        req.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    if (response.body() != null) {
                        JSONObject json = new JSONObject(response.body().string());
                        success = json.getInt("success");

                        if (success == 1) {
                            detailsUI.showSuccess();
                        }
                    } else {
                        detailsUI.showError();
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();

                    detailsUI.showError();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t){
                t.printStackTrace();

                detailsUI.showError();
            }
        });

        return null;
    }
}
