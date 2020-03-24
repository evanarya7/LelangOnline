package com.defalt.lelangonline.ui.register;

import androidx.annotation.Nullable;

import com.defalt.lelangonline.ui.login.LoggedInUserView;

class RegisterResult {
    @Nullable
    private LoggedInUserView success;
    @Nullable
    private String error;

    RegisterResult(@Nullable String error) {
        this.error = "Register failed : " + error;
    }

    RegisterResult(@Nullable LoggedInUserView success) {
        this.success = success;
    }

    @Nullable
    LoggedInUserView getSuccess() {
        return success;
    }

    @Nullable
    String getError() {
        return error;
    }
}
