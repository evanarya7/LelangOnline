package com.defalt.lelangonline.data.details;

import android.os.AsyncTask;

import com.defalt.lelangonline.data.JSONParser;
import com.defalt.lelangonline.ui.SharedFunctions;
import com.defalt.lelangonline.ui.details.Details;
import com.defalt.lelangonline.ui.details.DetailsActivity;
import com.defalt.lelangonline.ui.details.DetailsFragment;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DetailsTask extends AsyncTask<String, Void, Void> {
    private int success;
    private Details details;

    private DetailsFragment.DetailsUI detailsUI;

    public DetailsTask(DetailsFragment.DetailsUI detailsUI) {
        this.detailsUI = detailsUI;
    }

    protected Void doInBackground(String... args) {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("auctionID", args[0]));

        String url = "https://dev.projectlab.co.id/mit/1317003/get_details.php";
        JSONParser jsonParser = new JSONParser();
        JSONObject json = jsonParser.makeHttpRequest(url, "POST", params);

        if (json != null) {
            try {
                success = json.getInt("success");

                if (success == 1) {
                    details = new Details();
                    details.setItemName(json.getString("itemName"));
                    details.setItemDesc(json.getString("itemDesc"));
                    details.setItemCategory(json.getString("itemCat"));
                    details.setItemCategory(json.getString("itemImg"));
                    details.setItemInitPrice(json.getDouble("itemValue"));
                    details.setItemStartPrice(json.getDouble("priceStart"));
                    details.setItemLimitPrice(json.getDouble("priceLimit"));
                    details.setItemLikeCount(json.getInt("favCount"));
                    details.setBidCount(json.getInt("bidCount"));
                    details.setTimeStart(SharedFunctions.parseDate(json.getString("auctionStart")));
                    details.setTimeEnd(SharedFunctions.parseDate(json.getString("auctionEnd")));
                    details.setTimeServer(SharedFunctions.parseDate(json.getString("serverTime")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    protected void onPostExecute(Void result) {
        if (success == 1) {
            detailsUI.updateUI(details);
        } else if (success == 0) {
            DetailsActivity.setIsConnectionError(true);
        }
    }

}
