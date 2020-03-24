package com.defalt.lelangonline.data.splash;

import android.os.AsyncTask;

import com.defalt.lelangonline.data.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SplashTask extends AsyncTask<String, Void, Integer> {

    protected Integer doInBackground(String... args) {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("token", args[0]));

        String url = "https://dev.projectlab.co.id/mit/1317003/user_exp.php";
        JSONParser jsonParser = new JSONParser();
        JSONObject json = jsonParser.makeHttpRequest(url, "POST", params);

        if (json != null) {
            try {
                return json.getInt("success");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return 0;
    }
}
