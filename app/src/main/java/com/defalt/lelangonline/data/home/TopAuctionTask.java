package com.defalt.lelangonline.data.home;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.defalt.lelangonline.MainActivity;
import com.defalt.lelangonline.data.RestApi;
import com.defalt.lelangonline.ui.SharedFunctions;
import com.defalt.lelangonline.ui.home.HomeFragment;
import com.defalt.lelangonline.ui.home.TopAuction;
import com.defalt.lelangonline.ui.home.TopAuctionAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.defalt.lelangonline.ui.recycle.PaginationListener.PAGE_START;

public class TopAuctionTask extends AsyncTask<Integer, Void, Void> {
    private int success;

    private TopAuctionAdapter adapter;
    private List<TopAuction> topAuctionList;
    private int currentPage;
    private int totalPage;
    private HomeFragment.HomeUI homeUI;

    public TopAuctionTask(TopAuctionAdapter adapter, List<TopAuction> topAuctionList, int currentPage, int totalPage, HomeFragment.HomeUI homeUI) {
        this.adapter = adapter;
        this.topAuctionList = topAuctionList;
        this.currentPage = currentPage;
        this.totalPage = totalPage;
        this.homeUI = homeUI;
    }

    protected Void doInBackground(Integer... args) {
        RestApi server = SharedFunctions.getRetrofit().create(RestApi.class);
        RequestBody desiredCount = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(args[0]));
        RequestBody dataOffset = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(args[1]));

        Call<ResponseBody> req = server.getTopAuction(desiredCount, dataOffset);

        req.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    if (response.body() != null) {
                        JSONObject json = new JSONObject(response.body().string());
                        success = json.getInt("success");

                        if (success == 1) {
                            Timestamp serverTime = SharedFunctions.parseDate(json.getString("serverTime"));
                            JSONArray items = json.getJSONArray("auctions");

                            for (int i = 0; i < items.length(); i++) {
                                JSONObject c = items.getJSONObject(i);
                                HomeFragment.setItemCount(HomeFragment.getItemCount() + 1);

                                String auctionID = c.getString("auctionID");
                                Timestamp auctionStart = SharedFunctions.parseDate(c.getString("auctionStart"));
                                Timestamp auctionEnd = SharedFunctions.parseDate(c.getString("auctionEnd"));
                                String itemName = c.getString("itemName");
                                Double itemValue = c.getDouble("itemValue");
                                Double priceStart = c.getDouble("priceStart");
                                String itemImg = c.getString("itemImg");
                                int favCount = c.getInt("favCount");

                                TopAuction au = new TopAuction(auctionID, auctionStart, auctionEnd, itemName, itemValue, priceStart, itemImg, favCount, serverTime);
                                topAuctionList.add(au);
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
        adapter.addItems(topAuctionList);
        homeUI.setRefreshing(false);
    }

    private void executeSuccess() {
        if (topAuctionList.size() < totalPage) {
            HomeFragment.setLastPage(true);
        } else {
            adapter.addLoading();
        }
        homeUI.updateUI();
    }

    private void executeEmpty() {
        HomeFragment.setLastPage(true);
        homeUI.updateUI();
    }

    private void executeError() {
        HomeFragment.setLastPage(true);
        if (!MainActivity.isConnectionError()) {
            homeUI.showError();
            MainActivity.setIsConnectionError(true);
        }
    }

    private void endExecute() {
        HomeFragment.setLoading(false);
    }

}