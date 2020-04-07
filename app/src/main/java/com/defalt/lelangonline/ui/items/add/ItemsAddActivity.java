package com.defalt.lelangonline.ui.items.add;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.custom.SimpleCustomValidation;
import com.bumptech.glide.Glide;
import com.defalt.lelangonline.R;
import com.defalt.lelangonline.data.RestApi;
import com.defalt.lelangonline.data.login.LoginRepository;
import com.defalt.lelangonline.ui.SharedFunctions;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import me.abhinay.input.CurrencyEditText;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemsAddActivity extends AppCompatActivity {
    private boolean isImageEmpty = true;
    private Uri mCropImageUri;

    private Activity mActivity;
    private Context mContext;
    private EditText nameEditText;
    private EditText descEditText;
    private EditText categoryEditText;
    private CurrencyEditText valueEditText;
    private ImageView thumbImageView;
    private CardView progressBarCard;
    private AwesomeValidation mAwesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mActivity = ItemsAddActivity.this;
        mContext = getApplicationContext();

        nameEditText = findViewById(R.id.name);
        categoryEditText = findViewById(R.id.category);
        valueEditText = SharedFunctions.setEditTextCurrency((CurrencyEditText) findViewById(R.id.value));
        descEditText = findViewById(R.id.description);
        thumbImageView = findViewById(R.id.thumbnail);
        progressBarCard = findViewById(R.id.progressCard);

        SimpleCustomValidation validationEmpty = new SimpleCustomValidation() {
            @Override
            public boolean compare(String input) {
                return input.length() > 0;
            }
        };

        mAwesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        mAwesomeValidation.addValidation(mActivity, R.id.name, validationEmpty, R.string.form_invalid_item_name);
        mAwesomeValidation.addValidation(mActivity, R.id.category, validationEmpty, R.string.form_invalid_item_cat);
        mAwesomeValidation.addValidation(mActivity, R.id.value, validationEmpty, R.string.form_invalid_item_val);
        mAwesomeValidation.addValidation(mActivity, R.id.description, validationEmpty, R.string.form_invalid_item_desc);

        enableImageAdd();
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
                imgDialog.setTitle(R.string.item_post_img_hint);
                imgDialog.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Hapus")) {
                            thumbImageView.setImageResource(R.drawable.placeholder_image);
                            isImageEmpty = true;
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
                .setAspectRatio(16,9)
                .setRequestedSize(800, 450, CropImageView.RequestSizeOptions.RESIZE_INSIDE)
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
                    isImageEmpty = false;
                    break;
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            if (mAwesomeValidation.validate()) {
                uploadToServer();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void uploadToServer() {
        progressBarCard.setVisibility(View.VISIBLE);

        SharedFunctions.disableEditText(nameEditText);
        SharedFunctions.disableEditText(descEditText);
        SharedFunctions.disableEditText(categoryEditText);
        SharedFunctions.disableEditText(valueEditText);
        disableImage();

        RestApi server = SharedFunctions.getRetrofit().create(RestApi.class);
        RequestBody itemName = RequestBody.create(MediaType.parse("text/plain"), nameEditText.getText().toString());
        RequestBody itemDesc = RequestBody.create(MediaType.parse("text/plain"), descEditText.getText().toString());
        RequestBody itemCat = RequestBody.create(MediaType.parse("text/plain"), categoryEditText.getText().toString());
        RequestBody itemVal = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(valueEditText.getCleanIntValue()));
        RequestBody isImageEmptyBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(this.isImageEmpty));
        RequestBody userToken = RequestBody.create(MediaType.parse("text/plain"), LoginRepository.getLoggedInUser().getToken());

        Call<ResponseBody> req;
        if (!this.isImageEmpty) {
            File file = new File(Objects.requireNonNull(mCropImageUri.getPath()));
            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part itemImage = MultipartBody.Part.createFormData("upload", file.getName(), reqFile);

            req = server.postItemWithImage(itemName, itemDesc, itemCat, itemVal, isImageEmptyBody, userToken, itemImage);
        } else {
            req = server.postItemNoImage(itemName, itemDesc, itemCat, itemVal, isImageEmptyBody, userToken);
        }

        req.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject json = new JSONObject(Objects.requireNonNull(response.body()).string());
                    int success = json.getInt("success");
                    int imgSuccess = json.getInt("imgSuccess");

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(mActivity);
                    if (success == 0) {
                        showConnError();
                    } else {
                        alertDialog.setPositiveButton(mContext.getString(R.string.alert_agree), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                        if (success == 1 && imgSuccess == 0) {
                            alertDialog.setTitle(R.string.alert_post_success_title)
                                    .setMessage(R.string.alert_post_success_item_0_desc)
                                    .setIcon(R.drawable.ic_check_circle_black_24dp);
                        } else if (success == 1 && (imgSuccess == 1 || imgSuccess == -1)) {
                            alertDialog.setTitle(R.string.alert_post_success_title)
                                    .setMessage(R.string.alert_post_success_item_1_desc)
                                    .setIcon(R.drawable.ic_check_circle_black_24dp);
                        }
                    }
                    progressBarCard.setVisibility(View.GONE);
                    alertDialog.setCancelable(false)
                            .show();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                t.printStackTrace();
                showConnError();
            }
        });
    }

    private void showConnError() {
        progressBarCard.setVisibility(View.GONE);

        SharedFunctions.enableEditText(nameEditText);
        SharedFunctions.enableEditText(descEditText);
        SharedFunctions.enableEditText(categoryEditText);
        SharedFunctions.enableEditText(valueEditText);
        if (isImageEmpty) {
            enableImageAdd();
        } else {
            enableImageRemove();
        }

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mActivity);
        alertDialog.setTitle(R.string.alert_conn_title)
                .setMessage(R.string.alert_conn_desc)
                .setIcon(R.drawable.ic_error_black_24dp)
                .setPositiveButton(mContext.getString(R.string.alert_agree), null)
                .show();
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

}
