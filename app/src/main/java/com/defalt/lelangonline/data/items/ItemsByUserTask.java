package com.defalt.lelangonline.data.items;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.defalt.lelangonline.data.RestApi;
import com.defalt.lelangonline.ui.SharedFunctions;
import com.defalt.lelangonline.ui.items.Item;
import com.defalt.lelangonline.ui.items.ItemsByUserAdapter;
import com.defalt.lelangonline.ui.items.ItemsByUserActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.defalt.lelangonline.ui.recycle.PaginationListener.PAGE_START;

public class ItemsByUserTask extends AsyncTask<String, Void, Void> {
    private int success;

    private ItemsByUserAdapter adapter;
    private List<Item> itemList;
    private int currentPage;
    private int totalPage;
    private ItemsByUserActivity.ItemsByUserUI itemsByUserUI;

    public ItemsByUserTask(ItemsByUserAdapter adapter, List<Item> itemList, int currentPage, int totalPage, ItemsByUserActivity.ItemsByUserUI itemsByUserUI) {
        this.adapter = adapter;
        this.itemList = itemList;
        this.currentPage = currentPage;
        this.totalPage = totalPage;
        this.itemsByUserUI = itemsByUserUI;
    }

    protected Void doInBackground(String... args) {
        RestApi server = SharedFunctions.getRetrofit().create(RestApi.class);
        RequestBody desiredCount = RequestBody.create(MediaType.parse("text/plain"), args[0]);
        RequestBody dataOffset = RequestBody.create(MediaType.parse("text/plain"), args[1]);
        RequestBody userToken = RequestBody.create(MediaType.parse("text/plain"), args[2]);

        Call<ResponseBody> req = server.getItemsByUser(desiredCount, dataOffset, userToken);

        req.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    if (response.body() != null) {
                        JSONObject json = new JSONObject(response.body().string());
                        success = json.getInt("success");

                        if (success == 1) {
                            JSONArray items = json.getJSONArray("items");

                            for (int i = 0; i < items.length(); i++) {
                                JSONObject c = items.getJSONObject(i);
                                ItemsByUserActivity.setItemCount(ItemsByUserActivity.getItemCount() + 1);

                                String itemID = c.getString("itemID");
                                String itemName = c.getString("itemName");
                                String itemCat = c.getString("itemCat");
                                Double itemValue = c.getDouble("itemValue");
                                String itemImg = c.getString("itemImg");
                                int favCount = c.getInt("favCount");

                                Item im = new Item(itemID, itemName, itemCat, itemValue, itemImg, favCount);
                                itemList.add(im);
                            }
                        }

                        postExecute();
                        if (success == 1) {
                            executeSuccess();
                        } else if (success == -1) {
                            executeEmpty();
                        } else if (success == 0) {
                            executeError();
                        }
                        endExecute();
                    } else {
                        postExecute();
                        executeError();
                        endExecute();
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();

                    postExecute();
                    executeError();
                    endExecute();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                t.printStackTrace();

                postExecute();
                executeError();
                endExecute();
            }
        });

        return null;
    }

    private void postExecute() {
        if (currentPage != PAGE_START) {
            adapter.removeLoading();
        }
        adapter.addItems(itemList);
        itemsByUserUI.setRefreshing(false);
    }

    private void executeSuccess() {
        if (itemList.size() < totalPage) {
            ItemsByUserActivity.setLastPage(true);
        } else {
            adapter.addLoading();
        }
        itemsByUserUI.updateUI();
    }

    private void executeEmpty() {
        ItemsByUserActivity.setLastPage(true);
        itemsByUserUI.updateUI();
    }

    private void executeError() {
        ItemsByUserActivity.setLastPage(true);
        if (!ItemsByUserActivity.isConnectionError()) {
            itemsByUserUI.showError();
            ItemsByUserActivity.setIsConnectionError(true);
        }
    }

    private void endExecute() {
        ItemsByUserActivity.setLoading(false);
    }

}
