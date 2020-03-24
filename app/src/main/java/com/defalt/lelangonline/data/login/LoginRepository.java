package com.defalt.lelangonline.data.login;

import androidx.lifecycle.MutableLiveData;

import com.defalt.lelangonline.R;
import com.defalt.lelangonline.data.PreferencesManager;
import com.defalt.lelangonline.data.login.model.LoggedInUser;
import com.defalt.lelangonline.ui.login.LoggedInUserView;
import com.defalt.lelangonline.ui.login.LoginResult;
import com.defalt.lelangonline.ui.login.LoginViewModel;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {

    private static volatile LoginRepository instance;

    private static LoginDataSource dataSource;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private static LoggedInUser user = null;

    // private constructor : singleton access
    private LoginRepository(LoginDataSource dataSource) {
        LoginRepository.dataSource = dataSource;
    }

    public static LoginRepository getInstance(LoginDataSource dataSource) {
        if (instance == null) {
            instance = new LoginRepository(dataSource);
        }
        return instance;
    }

    public static boolean isLoggedIn() {
        return user != null;
    }

    public static void logout() {
        new LoginDataSource().logout(user);
        user = null;
        PreferencesManager.instance().clear();
    }

    public static void setLoggedInUser(LoggedInUser user) {
        LoginRepository.user = user;
        PreferencesManager.instance().storeValueString("token", user.getToken());
        PreferencesManager.instance().storeValueString("username", user.getDisplayName());
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    public static LoggedInUser getLoggedInUser() {
        return user;
    }

    public static void login(String token, String name, MutableLiveData<LoginResult> loginResult, LoginViewModel loginViewModel) {
        // handle login
        Result<LoggedInUser> result = dataSource.login(token, name);
        if (result instanceof Result.Success) {
            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
            setLoggedInUser(data);
            loginResult.setValue(new LoginResult(new LoggedInUserView(data.getDisplayName())));
        } else {
            loginResult.setValue(new LoginResult(R.string.login_failed));
        }
        loginViewModel.setLoginResult(loginResult);
    }
}
