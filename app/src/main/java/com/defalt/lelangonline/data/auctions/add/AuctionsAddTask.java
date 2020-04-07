package com.defalt.lelangonline.data.auctions.add;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.defalt.lelangonline.data.RestApi;
import com.defalt.lelangonline.ui.SharedFunctions;
import com.defalt.lelangonline.ui.auctions.add.AuctionsAddActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuctionsAddTask extends AsyncTask<String, Void, Void> {
    private int success;
    private AuctionsAddActivity.AuctionsAddUI auctionsAddUI;

    public AuctionsAddTask(AuctionsAddActivity.AuctionsAddUI auctionsAddUI) {
        this.auctionsAddUI = auctionsAddUI;
    }

    protected Void doInBackground(String ... args) {
        RestApi server = SharedFunctions.getRetrofit().create(RestApi.class);
        RequestBody itemID = RequestBody.create(MediaType.parse("text/plain"), args[0]);
        RequestBody initPrice = RequestBody.create(MediaType.parse("text/plain"), args[1]);
        RequestBody limitPrice = RequestBody.create(MediaType.parse("text/plain"), args[2]);
        RequestBody auctionStart = RequestBody.create(MediaType.parse("text/plain"), args[3]);
        RequestBody auctionEnd = RequestBody.create(MediaType.parse("text/plain"), args[4]);

        Call<ResponseBody> req = server.postAuction(itemID, initPrice, limitPrice, auctionStart, auctionEnd);

        req.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject json = new JSONObject(Objects.requireNonNull(response.body()).string());
                    success = json.getInt("success");

                    if (success == 1) {
                        auctionsAddUI.updateUIAfterUpload();
                    } else if (success == 0) {
                        auctionsAddUI.showConnErrorThenRetry();
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                    auctionsAddUI.showConnErrorThenRetry();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                t.printStackTrace();
                auctionsAddUI.showConnErrorThenRetry();
            }
        });

        return null;
    }
}
