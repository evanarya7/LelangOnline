package com.defalt.lelangonline.data.register;

import android.os.AsyncTask;

import com.defalt.lelangonline.data.JSONParser;
import com.defalt.lelangonline.ui.register.RegisterViewModel;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RegisterTask extends AsyncTask<String, Void, Void> {

    private static Boolean isSuccess = false;
    private static String message;

    private String fullName;
    private RegisterViewModel registerViewModel;

    public RegisterTask(RegisterViewModel registerViewModel) {
        this.registerViewModel = registerViewModel;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    protected Void doInBackground(String... args) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("name", args[0]));
        params.add(new BasicNameValuePair("email", args[1]));
        params.add(new BasicNameValuePair("password", args[2]));

        String url = "https://dev.projectlab.co.id/mit/1317003/create_user.php";
        JSONParser jsonParser = new JSONParser();
        JSONObject json = jsonParser.makeHttpRequest(url, "POST", params);

        if (json != null) {
            try {
                int success = json.getInt("success");
                message = json.getString("message");

                if (success == 1) {
                    isSuccess = true;
                    fullName = args[0];
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    protected void onPostExecute(Void file_url) {
        registerViewModel.registerEnd(isSuccess, message, fullName);
    }

}
