package com.defalt.lelangonline.ui.register;

import androidx.annotation.Nullable;

class RegisterFormState {
    @Nullable
    private Integer fullNameError;
    @Nullable
    private Integer emailError;
    @Nullable
    private Integer passwordError;
    private boolean isDataValid;

    RegisterFormState(@Nullable Integer fullNameError, @Nullable Integer emailError, @Nullable Integer passwordError) {
        this.fullNameError = fullNameError;
        this.emailError = emailError;
        this.passwordError = passwordError;
        this.isDataValid = false;
    }

    RegisterFormState(boolean isDataValid) {
        this.fullNameError = null;
        this.emailError = null;
        this.passwordError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    Integer getFullNameError() {
        return fullNameError;
    }

    @Nullable
    Integer getEmailError() {
        return emailError;
    }

    @Nullable
    Integer getPasswordError() {
        return passwordError;
    }

    boolean isDataValid() {
        return isDataValid;
    }
}
