package com.defalt.lelangonline.data.home;

import android.os.AsyncTask;

import com.defalt.lelangonline.MainActivity;
import com.defalt.lelangonline.data.JSONParser;
import com.defalt.lelangonline.ui.SharedFunctions;
import com.defalt.lelangonline.ui.home.HomeFragment;
import com.defalt.lelangonline.ui.home.TopAuction;
import com.defalt.lelangonline.ui.home.TopAuctionAdapter;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("desiredCount", String.valueOf(args[0])));
        params.add(new BasicNameValuePair("dataOffset", String.valueOf(args[1])));

        String url = "https://dev.projectlab.co.id/mit/1317003/get_auction_best.php";
        JSONParser jsonParser = new JSONParser();
        JSONObject json = jsonParser.makeHttpRequest(url, "POST", params);

        if (json != null) {
            try {
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
                        int favCount = c.getInt("favCount");

                        TopAuction au = new TopAuction(auctionID, auctionStart, auctionEnd, itemName, itemValue, priceStart, favCount, serverTime);
                        topAuctionList.add(au);
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
        adapter.addItems(topAuctionList);
        homeUI.setRefreshing(false);

        if (success == 1) {
            if (topAuctionList.size() < totalPage) {
                HomeFragment.setLastPage(true);
            } else {
                adapter.addLoading();
            }
            homeUI.updateUI();
        } else if (success == -1) {
            HomeFragment.setLastPage(true);
            homeUI.updateUI();
        } else if (success == 0) {
            HomeFragment.setLastPage(true);
            if (!MainActivity.isIsConnectionError()) {
                homeUI.showError();
                MainActivity.setIsConnectionError(true);
            }
        }

        HomeFragment.setLoading(false);
    }

}