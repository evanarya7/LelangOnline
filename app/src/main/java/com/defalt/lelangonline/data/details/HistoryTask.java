package com.defalt.lelangonline.data.details;

import android.os.AsyncTask;

import com.defalt.lelangonline.data.JSONParser;
import com.defalt.lelangonline.ui.details.Bid;
import com.defalt.lelangonline.ui.details.BidAdapter;
import com.defalt.lelangonline.ui.details.DetailsActivity;
import com.defalt.lelangonline.ui.details.HistoryFragment;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("auctionID", args[0]));
        params.add(new BasicNameValuePair("desiredCount", args[1]));
        params.add(new BasicNameValuePair("dataOffset", args[2]));

        String url = "https://dev.projectlab.co.id/mit/1317003/get_details_history.php";
        JSONParser jsonParser = new JSONParser();
        JSONObject json = jsonParser.makeHttpRequest(url, "POST", params);

        if (json != null) {
            try {
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
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    protected void onPostExecute(Void result) {
        if (currentPage != PAGE_START) {
            adapter.removeLoading();
        }
        adapter.addItems(bidList);
        historyUI.setRefreshing(false);

        if (success == 1) {
            if (bidList.size() < totalPage) {
                HistoryFragment.setLastPage(true);
            } else {
                adapter.addLoading();
            }
            historyUI.updateUI();
        } else if (success == -1) {
            HistoryFragment.setLastPage(true);
            historyUI.updateUI();
        } else if (success == 0) {
            HistoryFragment.setLastPage(true);
            if (!DetailsActivity.isIsConnectionError()) {
                historyUI.showError();
                DetailsActivity.setIsConnectionError(true);
            }
        }

        HistoryFragment.setLoading(false);
    }

}
