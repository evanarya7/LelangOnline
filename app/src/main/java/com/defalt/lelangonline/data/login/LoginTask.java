package com.defalt.lelangonline.data.login;

import android.os.AsyncTask;

import androidx.lifecycle.MutableLiveData;

import com.defalt.lelangonline.data.JSONParser;
import com.defalt.lelangonline.ui.login.LoginResult;
import com.defalt.lelangonline.ui.login.LoginViewModel;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LoginTask extends AsyncTask<String, Void, Void> {

    private LoginViewModel loginViewModel;
    private MutableLiveData<LoginResult> loginResult;
    private String token;
    private String name;

    public LoginTask(LoginViewModel loginViewModel, MutableLiveData<LoginResult> loginResult) {
        this.loginViewModel = loginViewModel;
        this.loginResult = loginResult;
    }

    protected Void doInBackground(String... args) {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("email", args[0]));
        params.add(new BasicNameValuePair("password", args[1]));

        String url = "https://dev.projectlab.co.id/mit/1317003/user_login.php";
        JSONParser jsonParser = new JSONParser();
        JSONObject json = jsonParser.makeHttpRequest(url, "POST", params);

        if (json != null) {
            try {
                int success = json.getInt("success");

                if (success == 1) {
                    token = json.getString("token");
                    name = json.getString("name");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void voids) {
        LoginRepository.login(token, name, loginResult, loginViewModel);
    }
}
