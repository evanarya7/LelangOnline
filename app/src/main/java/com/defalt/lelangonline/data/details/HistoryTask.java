package com.defalt.lelangonline.data.details;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.defalt.lelangonline.data.RestApi;
import com.defalt.lelangonline.ui.SharedFunctions;
import com.defalt.lelangonline.ui.details.Bid;
import com.defalt.lelangonline.ui.details.BidAdapter;
import com.defalt.lelangonline.ui.details.DetailsActivity;
import com.defalt.lelangonline.ui.details.HistoryFragment;

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

public class HistoryTask extends AsyncTask<String, Void, Void> {
    private int success;

    private BidAdapter adapter;
    private List<Bid> bidList;
    private int currentPage;
    private int totalPage;
    private HistoryFragment.HistoryUI historyUI;

    public HistoryTask(BidAdapter adapter, List<Bid> bidList, int currentPage, int totalPage, HistoryFragment.HistoryUI historyUI) {
        this.adapter = adapter;
        this.bidList = bidList;
        this.currentPage = currentPage;
        this.totalPage = totalPage;
        this.historyUI = historyUI;
    }

    protected Void doInBackground(String... args) {
        RestApi server = SharedFunctions.getRetrofit().create(RestApi.class);
        RequestBody desiredCount = RequestBody.create(MediaType.parse("text/plain"), args[0]);
        RequestBody dataOffset = RequestBody.create(MediaType.parse("text/plain"), args[1]);
        RequestBody auctionID = RequestBody.create(MediaType.parse("text/plain"), args[2]);

        Call<ResponseBody> req = server.getDetailsHistory(desiredCount, dataOffset, auctionID);

        req.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    if (response.body() != null) {
                        JSONObject json = new JSONObject(response.body().string());
                        success = json.getInt("success");

                        if (success == 1) {
                            JSONArray items = json.getJSONArray("bids");

                            for (int i = 0; i < items.length(); i++) {
                                JSONObject c = items.getJSONObject(i);

                                String userName = c.getString("userName");
                                Double bidPrice = c.getDouble("bidPrice");
                                Timestamp bidTime = Timestamp.valueOf(c.getString("bidTime"));

                                Bid im = new Bid(userName, bidPrice, bidTime);
                                bidList.add(im);
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
        adapter.addItems(bidList);
        historyUI.setRefreshing(false);
    }

    private void executeSuccess() {
        if (bidList.size() < totalPage) {
            HistoryFragment.setLastPage(true);
        } else {
            adapter.addLoading();
        }
        historyUI.updateUI();
    }

    private void executeEmpty() {
        HistoryFragment.setLastPage(true);
        historyUI.updateUI();
    }

    private void executeError() {
        HistoryFragment.setLastPage(true);
        if (!DetailsActivity.isIsConnectionError()) {
            historyUI.showError();
            DetailsActivity.setIsConnectionError(true);
        }
    }

    private void endExecute() {
        HistoryFragment.setLoading(false);
    }

}
