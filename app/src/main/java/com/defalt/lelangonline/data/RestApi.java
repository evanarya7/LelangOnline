package com.defalt.lelangonline.data;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by NIAN on 2/8/2017.
 */

public interface RestApi {
    @Multipart
    @POST("/dev/mit/1317003/create_item.php")
    Call<ResponseBody> postItem(@Part("itemName") RequestBody itemName, @Part("itemDesc") RequestBody itemDesc, @Part("itemCat") RequestBody itemCat, @Part("itemVal") RequestBody itemVal, @Part("isImageEmpty") RequestBody isImageEmpty, @Part("userToken") RequestBody userToken);

    @Multipart
    @POST("/dev/mit/1317003/create_item.php")
    Call<ResponseBody> postItemImage(@Part("itemName") RequestBody itemName, @Part("itemDesc") RequestBody itemDesc, @Part("itemCat") RequestBody itemCat, @Part("itemVal") RequestBody itemVal, @Part("isImageEmpty") RequestBody isImageEmpty, @Part("userToken") RequestBody userToken, @Part MultipartBody.Part image);

    @Multipart
    @POST("/dev/mit/1317003/up_profile.php")
    Call<ResponseBody> postProfileImage(@Part MultipartBody.Part image, @Part("name") RequestBody name);
}