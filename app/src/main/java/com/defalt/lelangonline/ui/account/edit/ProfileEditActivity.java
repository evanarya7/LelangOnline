package com.defalt.lelangonline.ui.account.edit;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.basgeekball.awesomevalidation.utility.custom.SimpleCustomValidation;
import com.bumptech.glide.Glide;
import com.defalt.lelangonline.R;
import com.defalt.lelangonline.data.account.edit.ProfileByIDTask;
import com.defalt.lelangonline.data.account.edit.ProfileEditTask;
import com.defalt.lelangonline.data.login.LoginRepository;
import com.defalt.lelangonline.ui.SharedFunctions;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class ProfileEditActivity extends AppCompatActivity {
    private Activity mActivity;
    private Context mContext;

    private ShimmerFrameLayout mShimmerViewContainer;
    private ScrollView mScrollView;
    private ProfileEditUI profileEditUI;

    private EditText nameEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private EditText passwordEditText;
    private EditText passwordNewEditText;
    private EditText passwordConfEditText;
    private ImageView thumbImageView;
    private Switch passwordChangeSwitch;
    private View overlay;
    private CardView progressBarCard;
    private AwesomeValidation mAwesomeValidation;

    private static boolean isLoading = true;
    private static boolean isImageEmpty = true;
    private Uri mCropImageUri;
    private boolean isImageChange;
    private boolean isPasswordChange;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);
        mShimmerViewContainer.startShimmer();

        mScrollView = findViewById(R.id.container);

        mActivity = ProfileEditActivity.this;
        mContext = getApplicationContext();
        profileEditUI = new ProfileEditUI();

        nameEditText = findViewById(R.id.name);
        emailEditText = findViewById(R.id.email);
        phoneEditText = findViewById(R.id.phone);
        passwordEditText = findViewById(R.id.password);
        passwordNewEditText = findViewById(R.id.password_new);
        passwordConfEditText = findViewById(R.id.password_new_conf);
        thumbImageView = findViewById(R.id.thumbnail);
        passwordChangeSwitch = findViewById(R.id.change_pass);
        overlay = findViewById(R.id.overlay);
        progressBarCard = findViewById(R.id.progressCard);

        final SimpleCustomValidation validationEmpty = new SimpleCustomValidation() {
            @Override
            public boolean compare(String input) {
                return input.length() > 0;
            }
        };

        final SimpleCustomValidation validationPassword = new SimpleCustomValidation() {
            @Override
            public boolean compare(String input) {
                return input.length() >= 8;
            }
        };

        mAwesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        mAwesomeValidation.addValidation(mActivity, R.id.name, "[a-zA-Z\\s]+", R.string.form_invalid_username);
        mAwesomeValidation.addValidation(mActivity, R.id.name, validationEmpty, R.string.form_invalid_username_empty);
        mAwesomeValidation.addValidation(mActivity, R.id.email, android.util.Patterns.EMAIL_ADDRESS, R.string.form_invalid_email);
        mAwesomeValidation.addValidation(mActivity, R.id.email, validationEmpty, R.string.form_invalid_email_empty);
        mAwesomeValidation.addValidation(mActivity, R.id.phone, RegexTemplate.TELEPHONE, R.string.form_invalid_phone);
        mAwesomeValidation.addValidation(mActivity, R.id.phone, validationEmpty, R.string.form_invalid_phone_empty);
        mAwesomeValidation.addValidation(mActivity, R.id.password, validationPassword, R.string.form_invalid_password);
        mAwesomeValidation.addValidation(mActivity, R.id.password_new, validationPassword, R.string.form_invalid_password);
        mAwesomeValidation.addValidation(mActivity, R.id.password_new_conf, R.id.password_new, R.string.form_invalid_password);

        passwordChangeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setPasswordChange(true);
                    passwordNewEditText.setVisibility(View.VISIBLE);
                    passwordConfEditText.setVisibility(View.VISIBLE);
                } else {
                    setPasswordChange(false);
                    passwordNewEditText.setVisibility(View.GONE);
                    passwordConfEditText.setVisibility(View.GONE);
                }
            }
        });

        new ProfileByIDTask(profileEditUI).execute(LoginRepository.getLoggedInUser().getToken());
    }

    private void enableImageAdd() {
        thumbImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CropImage.isExplicitCameraPermissionRequired(mContext)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE);
                    }
                } else {
                    CropImage.startPickImageActivity(mActivity);
                }
            }
        });
    }

    private void enableImageRemove() {
        thumbImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] options = {"Hapus"};
                final AlertDialog.Builder imgDialog = new AlertDialog.Builder(mActivity);
                imgDialog.setTitle(R.string.account_post_image_hint);
                imgDialog.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Hapus")) {
                            thumbImageView.setImageResource(R.drawable.placeholder_image);
                            isImageChange = true;
                            setIsImageEmpty(true);
                            mCropImageUri = null;
                            enableImageAdd();
                        }
                    }
                });
                imgDialog.show();
            }
        });
    }

    private void disableImage() {
        thumbImageView.setOnClickListener(null);
    }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setFixAspectRatio(true)
                .setAspectRatio(1, 1)
                .setRequestedSize(500, 500, CropImageView.RequestSizeOptions.RESIZE_INSIDE)
                .start(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case (CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE): {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    CropImage.startPickImageActivity(this);
                } else {
                    Toast.makeText(this, "Batal, izin kamera tidak diberikan", Toast.LENGTH_LONG).show();
                }
                break;
            }
            case (CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE): {
                if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCropImageActivity(mCropImageUri);
                } else {
                    Toast.makeText(this, "Batal, izin penyimpanan tidak diberikan", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case (CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE): {
                    Uri imageUri = CropImage.getPickImageResultUri(this, data);

                    if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                        mCropImageUri = imageUri;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
                        }
                    } else {
                        startCropImageActivity(imageUri);
                    }
                    break;
                }
                case (CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE): {
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    mCropImageUri = result.getUri();
                    Glide.with(mActivity).load(mCropImageUri).into(thumbImageView);
                    enableImageRemove();
                    isImageChange = true;
                    setIsImageEmpty(false);
                    break;
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            if (!isLoading()) {
                if (mAwesomeValidation.validate()) {
                    uploadToServer();
                }
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void uploadToServer() {
        setIsLoading(true);
        overlay.setVisibility(View.VISIBLE);
        progressBarCard.setVisibility(View.VISIBLE);

        disableImage();
        SharedFunctions.disableEditText(nameEditText);
        SharedFunctions.disableEditText(phoneEditText);
        SharedFunctions.disableEditText(passwordEditText);
        SharedFunctions.disableSwitch(passwordChangeSwitch);
        SharedFunctions.disableEditText(passwordNewEditText);
        SharedFunctions.disableEditText(passwordConfEditText);

        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), nameEditText.getText().toString());
        RequestBody phone = RequestBody.create(MediaType.parse("text/plain"), phoneEditText.getText().toString());
        RequestBody oldPassword = RequestBody.create(MediaType.parse("text/plain"), new String(Hex.encodeHex(DigestUtils.sha256(passwordEditText.getText().toString()))));
        RequestBody newPassword = RequestBody.create(MediaType.parse("text/plain"), new String(Hex.encodeHex(DigestUtils.sha256(passwordConfEditText.getText().toString()))));
        RequestBody isPasswordChange = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(isPasswordChange()));
        RequestBody isImageEmpty = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(isImageEmpty()));
        RequestBody isImageChange = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(this.isImageChange));
        RequestBody userToken = RequestBody.create(MediaType.parse("text/plain"), LoginRepository.getLoggedInUser().getToken());

        new ProfileEditTask(isImageEmpty(), mCropImageUri, this.isImageChange, profileEditUI).execute(name, phone, oldPassword, newPassword, isPasswordChange, isImageEmpty, isImageChange, userToken);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    public static boolean isLoading() {
        return isLoading;
    }

    public static void setIsLoading(boolean isLoading) {
        ProfileEditActivity.isLoading = isLoading;
    }

    public static boolean isImageEmpty() {
        return isImageEmpty;
    }

    public static void setIsImageEmpty(boolean isImageEmpty) {
        ProfileEditActivity.isImageEmpty = isImageEmpty;
    }

    public boolean isPasswordChange() {
        return isPasswordChange;
    }

    public void setPasswordChange(boolean passwordChange) {
        isPasswordChange = passwordChange;
    }

    public class ProfileEditUI {
        ProfileEditUI() { }

        public void updateEditText(String name, String email, String phone, String image) {
            nameEditText.setText(name);
            emailEditText.setText(email);
            phoneEditText.setText(phone);
            thumbImageView.setImageDrawable(null);
            if (!image.equals("null")) {
                String IMAGE_URL = "https://dev.projectlab.co.id/mit/1317003/images/profile/";
                Glide.with(mContext).load(IMAGE_URL + image).into(thumbImageView);
                setIsImageEmpty(false);
                enableImageRemove();
            } else {
                Glide.with(mContext).load(R.drawable.placeholder_image).into(thumbImageView);
                setIsImageEmpty(true);
                enableImageAdd();
            }
            updateUI();
        }

        private void updateUI() {
            mShimmerViewContainer.stopShimmer();
            mShimmerViewContainer.setVisibility(View.GONE);
            mScrollView.setVisibility(View.VISIBLE);
            setIsLoading(false);
        }

        public void showConnError() {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mActivity);
            alertDialog.setTitle(R.string.alert_conn_title)
                    .setMessage(R.string.alert_conn_desc)
                    .setIcon(R.drawable.ic_error_black_24dp)
                    .setCancelable(false)
                    .setPositiveButton(mActivity.getString(R.string.alert_agree), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mActivity.finish();
                        }
                    })
                    .show();
        }

        public void updateUIAfterUpload(int endType) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mActivity);
            if (endType == 0) {
                alertDialog.setMessage(R.string.alert_update_success_account_0_desc);
            } else {
                alertDialog.setMessage(R.string.alert_update_success_account_1_desc);
            }
            alertDialog.setTitle(R.string.alert_update_success_title)
                    .setIcon(R.drawable.ic_check_circle_black_24dp)
                    .setCancelable(false)
                    .setPositiveButton(mActivity.getString(R.string.alert_agree), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mActivity.finish();
                        }
                    })
                    .show();
        }

        public void showConnErrorThenRetry() {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mActivity);
            alertDialog.setTitle(R.string.alert_conn_title)
                    .setMessage(R.string.alert_conn_desc)
                    .setIcon(R.drawable.ic_error_black_24dp)
                    .setCancelable(false)
                    .setPositiveButton(mActivity.getString(R.string.alert_agree), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            openFormLock();
                        }
                    }).show();
        }

        public void showPassErrorThenRetry() {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mActivity);
            alertDialog.setTitle(R.string.alert_pass_invalid_title)
                    .setMessage(R.string.alert_pass_invalid_desc)
                    .setIcon(R.drawable.ic_error_black_24dp)
                    .setCancelable(false)
                    .setPositiveButton(mActivity.getString(R.string.alert_agree), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            openFormLock();
                        }
                    }).show();
        }

        private void openFormLock() {
            if (isImageEmpty()) {
                enableImageAdd();
            } else {
                enableImageRemove();
            }
            SharedFunctions.enableEditText(nameEditText);
            SharedFunctions.enableEditText(phoneEditText);
            SharedFunctions.enableEditText(passwordEditText);
            SharedFunctions.enableSwitch(passwordChangeSwitch);
            SharedFunctions.enableEditText(passwordNewEditText);
            SharedFunctions.enableEditText(passwordConfEditText);

            overlay.setVisibility(View.GONE);
            progressBarCard.setVisibility(View.GONE);
            setIsLoading(false);
        }
    }
}
