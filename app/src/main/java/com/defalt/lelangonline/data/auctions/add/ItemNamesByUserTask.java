package com.defalt.lelangonline.data.auctions.add;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.defalt.lelangonline.data.RestApi;
import com.defalt.lelangonline.ui.SharedFunctions;
import com.defalt.lelangonline.ui.auctions.add.AuctionsAddActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemNamesByUserTask extends AsyncTask<String, Void, Void> {
    private int success;

    private AuctionsAddActivity.AuctionsAddUI auctionsAddUI;
    private List<String> nameList = new ArrayList<>();
    private List<String> valueList = new ArrayList<>();

    public ItemNamesByUserTask(AuctionsAddActivity.AuctionsAddUI auctionsAddUI) {
        this.auctionsAddUI = auctionsAddUI;
    }

    protected Void doInBackground(String ... args) {
        RestApi server = SharedFunctions.getRetrofit().create(RestApi.class);
        RequestBody userToken = RequestBody.create(MediaType.parse("text/plain"), args[0]);

        Call<ResponseBody> req = server.getItemNamesByToken(userToken);

        req.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    if (response.body() != null) {
                        JSONObject json = new JSONObject(response.body().string());
                        success = json.getInt("success");

                        if (success == 1) {
                            JSONArray names = json.getJSONArray("names");
                            JSONArray values = json.getJSONArray("values");

                            for (int i = 0; i < names.length(); i++) {
                                nameList.add(names.getString(i));
                            }

                            for (int i = 0; i < values.length(); i++) {
                                valueList.add(SharedFunctions.formatRupiah(values.getDouble(i)));
                            }

                            auctionsAddUI.updateSpinner(nameList, valueList);
                        } else if (success == -1) {
                            auctionsAddUI.showEmptyItems();
                        } else if (success == 0) {
                            auctionsAddUI.showConnError();
                        }
                    } else {
                        auctionsAddUI.showConnError();
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                    auctionsAddUI.showConnError();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                t.printStackTrace();
                auctionsAddUI.showConnError();
            }
        });

        return null;
    }

}
