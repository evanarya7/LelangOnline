package com.defalt.lelangonline.data.items.edit;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.defalt.lelangonline.data.RestApi;
import com.defalt.lelangonline.ui.SharedFunctions;
import com.defalt.lelangonline.ui.items.edit.ItemsEditActivity;

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

public class ItemByIDTask extends AsyncTask<String, Void, Void> {
    private int success;
    private ItemsEditActivity.ItemsEditUI itemsEditUI;

    public ItemByIDTask(ItemsEditActivity.ItemsEditUI itemsEditUI) {
        this.itemsEditUI = itemsEditUI;
    }

    protected Void doInBackground(String... args) {
        RestApi server = SharedFunctions.getRetrofit().create(RestApi.class);
        RequestBody itemID = RequestBody.create(MediaType.parse("text/plain"), args[0]);

        Call<ResponseBody> req = server.getItemByID(itemID);

        req.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    if (response.body() != null) {
                        JSONObject json = new JSONObject(response.body().string());
                        success = json.getInt("success");

                        if (success == 1) {
                            itemsEditUI.updateEditText(json.getString("itemName"), json.getString("itemCat"), json.getDouble("itemValue"), json.getString("itemDesc"), json.getString("itemImg"));
                        } else {
                            itemsEditUI.showConnError();
                        }
                    } else {
                        itemsEditUI.showConnError();
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                    itemsEditUI.showConnError();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                t.printStackTrace();
                itemsEditUI.showConnError();
            }
        });

        return null;
    }
}
