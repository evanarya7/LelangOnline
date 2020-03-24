package com.defalt.lelangonline.ui.register;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.defalt.lelangonline.R;
import com.defalt.lelangonline.ui.login.LoggedInUserView;

public class RegisterViewModel extends ViewModel {

    private MutableLiveData<RegisterFormState> registerFormState = new MutableLiveData<>();
    private MutableLiveData<RegisterResult> registerResult = new MutableLiveData<>();

    LiveData<RegisterFormState> getRegisterFormState() {
        return registerFormState;
    }

    LiveData<RegisterResult> getRegisterResult() {
        return registerResult;
    }

    public void registerDataChanged(String fullName, String email, String password) {
        if (!isFullNameValid(fullName)) {
            registerFormState.setValue(new RegisterFormState(R.string.invalid_fullName, null, null));
        } else if (!isEmailValid(email)) {
            registerFormState.setValue(new RegisterFormState(null, R.string.invalid_email, null));
        } else if (!isPasswordValid(password)) {
            registerFormState.setValue(new RegisterFormState(null, null, R.string.invalid_password));
        } else {
            registerFormState.setValue(new RegisterFormState(true));
        }
    }

    private boolean isFullNameValid(String fullName) {
        return fullName != null && fullName.trim().length() >= 1;
    }

    private boolean isEmailValid(String email) {
        if (email.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        } else {
            return false;
        }
    }

    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() >= 8;
    }

    public void registerEnd(Boolean isSuccess, String message, String fullName) {
        if (isSuccess) {
            registerResult.setValue(new RegisterResult(new LoggedInUserView(fullName)));
        } else {
            registerResult.setValue(new RegisterResult(message));
        }
    }
}
