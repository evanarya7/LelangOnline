package com.defalt.lelangonline.data.details;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.defalt.lelangonline.data.RestApi;
import com.defalt.lelangonline.ui.SharedFunctions;
import com.defalt.lelangonline.ui.details.Details;
import com.defalt.lelangonline.ui.details.DetailsActivity;
import com.defalt.lelangonline.ui.details.DetailsFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsItemTask extends AsyncTask<String, Void, Void> {
    private int success;
    private Details details;

    private DetailsFragment.DetailsItemUI detailsItemUI;

    public DetailsItemTask(DetailsFragment.DetailsItemUI detailsItemUI) {
        this.detailsItemUI = detailsItemUI;
    }

    protected Void doInBackground(String... args) {
        RestApi server = SharedFunctions.getRetrofit().create(RestApi.class);
        RequestBody auctionID = RequestBody.create(MediaType.parse("text/plain"), args[0]);

        Call<ResponseBody> req = server.getDetailsItem(auctionID);

        req.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    if (response.body() != null) {
                        JSONObject json = new JSONObject(response.body().string());
                        success = json.getInt("success");

                        if (success == 1) {
                            details = new Details();
                            details.setItemName(json.getString("itemName"));
                            details.setItemDesc(json.getString("itemDesc"));
                            details.setItemCategory(json.getString("itemCat"));
                            details.setItemImg(json.getString("itemImg"));
                            details.setItemInitPrice(json.getDouble("itemValue"));
                            details.setItemStartPrice(json.getDouble("priceStart"));
                            details.setItemLimitPrice(json.getDouble("priceLimit"));
                            details.setItemLikeCount(json.getInt("favCount"));
                            details.setBidCount(json.getInt("bidCount"));
                            details.setTimeStart(SharedFunctions.parseDate(json.getString("auctionStart")));
                            details.setTimeEnd(SharedFunctions.parseDate(json.getString("auctionEnd")));
                            details.setTimeServer(SharedFunctions.parseDate(json.getString("serverTime")));

                            executeSuccess();
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

    private void executeSuccess() {
        detailsItemUI.updateUI(details);
        detailsItemUI.setRefreshing(false);
    }

    private void executeError() {
        if (!DetailsActivity.isIsConnectionError()) {
            DetailsActivity.setIsConnectionError(true);
            detailsItemUI.setRefreshing(false);
            detailsItemUI.showError();
        }
    }
}
