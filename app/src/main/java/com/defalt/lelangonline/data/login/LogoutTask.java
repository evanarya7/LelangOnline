package com.defalt.lelangonline.data.login;

import android.os.AsyncTask;

import com.defalt.lelangonline.data.JSONParser;
import com.defalt.lelangonline.data.login.model.LoggedInUser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

class LogoutTask extends AsyncTask<LoggedInUser, Void, Integer> {

    private int success;

    protected Integer doInBackground(LoggedInUser... args) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("token", args[0].getToken()));

        String url = "https://dev.projectlab.co.id/mit/1317003/user_logout.php";
        JSONParser jsonParser = new JSONParser();
        JSONObject json = jsonParser.makeHttpRequest(url, "POST", params);

        if (json != null) {
            try {
                success = json.getInt("success");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return success;
    }
}
