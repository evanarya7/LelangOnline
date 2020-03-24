package com.defalt.lelangonline.data.items;

import android.os.AsyncTask;

import com.defalt.lelangonline.MainActivity;
import com.defalt.lelangonline.data.JSONParser;
import com.defalt.lelangonline.ui.items.Item;
import com.defalt.lelangonline.ui.items.ItemAdapter;
import com.defalt.lelangonline.ui.items.ItemsFragment;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.defalt.lelangonline.ui.recycle.PaginationListener.PAGE_START;

public class ItemsTask extends AsyncTask<Integer, Void, Void> {
    private int success;

    private ItemAdapter adapter;
    private List<Item> itemList;
    private int currentPage;
    private int totalPage;
    private ItemsFragment.ItemsUI itemsUI;

    public ItemsTask(ItemAdapter adapter, List<Item> itemList, int currentPage, int totalPage, ItemsFragment.ItemsUI itemsUI) {
        this.adapter = adapter;
        this.itemList = itemList;
        this.currentPage = currentPage;
        this.totalPage = totalPage;
        this.itemsUI = itemsUI;
    }

    protected Void doInBackground(Integer... args) {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("desiredCount", String.valueOf(args[0])));
        params.add(new BasicNameValuePair("dataOffset", String.valueOf(args[1])));

        String url = "https://dev.projectlab.co.id/mit/1317003/get_items.php";
        JSONParser jsonParser = new JSONParser();
        JSONObject json = jsonParser.makeHttpRequest(url, "POST", params);

        if (json != null) {
            try {
                success = json.getInt("success");

                if (success == 1) {
                    JSONArray items = json.getJSONArray("items");

                    for (int i = 0; i < items.length(); i++) {
                        JSONObject c = items.getJSONObject(i);
                        ItemsFragment.setItemCount(ItemsFragment.getItemCount() + 1);

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
        adapter.addItems(itemList);
        itemsUI.setRefreshing(false);

        if (success == 1) {
            if (itemList.size() < totalPage) {
                ItemsFragment.setLastPage(true);
            } else {
                adapter.addLoading();
            }
            itemsUI.updateUI();
        } else if (success == -1) {
            ItemsFragment.setLastPage(true);
            itemsUI.updateUI();
        } else if (success == 0) {
            ItemsFragment.setLastPage(true);
            if (!MainActivity.isIsConnectionError()) {
                itemsUI.showError();
                MainActivity.setIsConnectionError(true);
            }
        }

        ItemsFragment.setLoading(false);
    }

}
