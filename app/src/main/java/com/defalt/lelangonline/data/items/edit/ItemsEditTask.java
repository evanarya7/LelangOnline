package com.defalt.lelangonline.data.items.edit;

import android.net.Uri;
import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.defalt.lelangonline.data.RestApi;
import com.defalt.lelangonline.ui.SharedFunctions;
import com.defalt.lelangonline.ui.items.edit.ItemsEditActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemsEditTask extends AsyncTask<RequestBody, Void, Void> {
    private int success;
    private boolean isImageEmpty;
    private Uri mCropImageUri;
    private boolean isImageChange;
    private ItemsEditActivity.ItemsEditUI itemsEditUI;

    public ItemsEditTask(boolean isImageEmpty, Uri mCropImageUri, boolean isImageChange, ItemsEditActivity.ItemsEditUI itemsEditUI) {
        this.isImageEmpty = isImageEmpty;
        this.mCropImageUri = mCropImageUri;
        this.isImageChange = isImageChange;
        this.itemsEditUI = itemsEditUI;
    }

    protected final Void doInBackground(RequestBody... args) {
        RestApi server = SharedFunctions.getRetrofit().create(RestApi.class);
        Call<ResponseBody> req;
        if (this.isImageChange && !this.isImageEmpty) {
            File file = new File(Objects.requireNonNull(mCropImageUri.getPath()));
            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part itemImage = MultipartBody.Part.createFormData("upload", file.getName(), reqFile);

            req = server.updateItemWithImage(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], itemImage);
        } else {
            req = server.updateItemNoImage(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7]);
        }

        req.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    if (response.body() != null) {
                        JSONObject json = new JSONObject(response.body().string());
                        success = json.getInt("success");
                        int imgSuccess = json.getInt("imgSuccess");

                        if (success == 0) {
                            itemsEditUI.showConnErrorThenRetry();
                        } else {
                            if (success == 1 && imgSuccess == 0) {
                                itemsEditUI.updateUIAfterUpload(0);
                            } else if (success == 1 && (imgSuccess == 1 || imgSuccess == -1)) {
                                itemsEditUI.updateUIAfterUpload(1);
                            }
                        }
                    } else {
                        itemsEditUI.showConnErrorThenRetry();
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                    itemsEditUI.showConnErrorThenRetry();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                t.printStackTrace();
                itemsEditUI.showConnErrorThenRetry();
            }
        });

        return null;
    }
}
