package com.defalt.lelangonline.data.login;

import com.defalt.lelangonline.data.login.model.LoggedInUser;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String token, String name) {
        LoggedInUser user = new LoggedInUser(token, name);
        if (user.getToken() != null) {
            return new Result.Success<>(user);
        } else {
            return new Result.Error(new Exception("Error logging in"));
        }
    }

    void logout(LoggedInUser user) { new LogoutTask().execute(user); }
}
