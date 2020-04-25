package com.defalt.lelangonline.data;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RestApi {
    // SPLASH ACTIVITY
    @Multipart
    @POST("/dev/mit/1317003/user_exp.php")
    Call<ResponseBody> checkToken(
            @Part("token") RequestBody token);

    // HOME FRAGMENT
    @Multipart
    @POST("/dev/mit/1317003/get_auction_best.php")
    Call<ResponseBody> getTopAuction(
            @Part("desiredCount") RequestBody desiredCount,
            @Part("dataOffset") RequestBody dataOffset);

    // DETAILS ACTIVITY
    @Multipart
    @POST("/dev/mit/1317003/get_details.php")
    Call<ResponseBody> getDetails(
            @Part("auctionID") RequestBody auctionID,
            @Part("token") RequestBody token);

    @Multipart
    @POST("/dev/mit/1317003/get_details_item.php")
    Call<ResponseBody> getDetailsItem(
            @Part("auctionID") RequestBody auctionID);

    @Multipart
    @POST("/dev/mit/1317003/get_details_history.php")
    Call<ResponseBody> getDetailsHistory(
            @Part("desiredCount") RequestBody desiredCount,
            @Part("dataOffset") RequestBody dataOffset,
            @Part("auctionID") RequestBody auctionID);

    @Multipart
    @POST("/dev/mit/1317003/create_bid.php")
    Call<ResponseBody> postBid(
            @Part("auctionID") RequestBody auctionID,
            @Part("bidAmount") RequestBody bidAmount,
            @Part("token") RequestBody token);

    // ITEMS FRAGMENT
    @Multipart
    @POST("/dev/mit/1317003/get_items.php")
    Call<ResponseBody> getItems(
            @Part("desiredCount") RequestBody desiredCount,
            @Part("dataOffset") RequestBody dataOffset);

    @Multipart
    @POST("/dev/mit/1317003/get_items_search.php")
    Call<ResponseBody> getItemsSearch(
            @Part("desiredCount") RequestBody desiredCount,
            @Part("dataOffset") RequestBody dataOffset,
            @Part("query") RequestBody query);

    // AUCTION BY ITEM ACTIVITY
    @Multipart
    @POST("/dev/mit/1317003/get_auction_by_item.php")
    Call<ResponseBody> getAuctionsByItem(
            @Part("desiredCount") RequestBody desiredCount,
            @Part("dataOffset") RequestBody dataOffset,
            @Part("itemID") RequestBody itemID);

    // ACCOUNT FRAGMENT
    @Multipart
    @POST("/dev/mit/1317003/get_profile_by_uid.php")
    Call<ResponseBody> getProfileByToken(
            @Part("token") RequestBody token);

    // PROFILE EDIT ACTIVITY
    @Multipart
    @POST("/dev/mit/1317003/update_profile.php")
    Call<ResponseBody> updateUserWithImage(
            @Part("name") RequestBody name,
            @Part("phone") RequestBody phone,
            @Part("oldPassword") RequestBody oldPassword,
            @Part("newPassword") RequestBody newPassword,
            @Part("isPasswordChange") RequestBody isPasswordChange,
            @Part("isImageEmpty") RequestBody isImageEmpty,
            @Part("isImageChange") RequestBody isImageChange,
            @Part("token") RequestBody token,
            @Part MultipartBody.Part image);

    @Multipart
    @POST("/dev/mit/1317003/update_profile.php")
    Call<ResponseBody> updateUserNoImage(
            @Part("name") RequestBody name,
            @Part("phone") RequestBody phone,
            @Part("oldPassword") RequestBody oldPassword,
            @Part("newPassword") RequestBody newPassword,
            @Part("isPasswordChange") RequestBody isPasswordChange,
            @Part("isImageEmpty") RequestBody isImageEmpty,
            @Part("isImageChange") RequestBody isImageChange,
            @Part("token") RequestBody token);

    // ADD ITEM ACTIVITY
    @Multipart
    @POST("/dev/mit/1317003/create_item.php")
    Call<ResponseBody> postItemWithImage(
            @Part("itemName") RequestBody itemName,
            @Part("itemDesc") RequestBody itemDesc,
            @Part("itemCat") RequestBody itemCat,
            @Part("itemVal") RequestBody itemVal,
            @Part("isImageEmpty") RequestBody isImageEmpty,
            @Part("token") RequestBody token,
            @Part MultipartBody.Part image);

    @Multipart
    @POST("/dev/mit/1317003/create_item.php")
    Call<ResponseBody> postItemNoImage(
            @Part("itemName") RequestBody itemName,
            @Part("itemDesc") RequestBody itemDesc,
            @Part("itemCat") RequestBody itemCat,
            @Part("itemVal") RequestBody itemVal,
            @Part("isImageEmpty") RequestBody isImageEmpty,
            @Part("token") RequestBody token);

    // ITEM BY USER ACTIVITY
    @Multipart
    @POST("/dev/mit/1317003/get_items_by_uid.php")
    Call<ResponseBody> getItemsByUser(
            @Part("desiredCount") RequestBody desiredCount,
            @Part("dataOffset") RequestBody dataOffset,
            @Part("token") RequestBody token);

    @Multipart
    @POST("/dev/mit/1317003/remove_item.php")
    Call<ResponseBody> removeItem(
            @Part("itemID") RequestBody itemID,
            @Part("token") RequestBody token);

    // ITEM EDIT ACTIVITY
    @Multipart
    @POST("/dev/mit/1317003/get_item_by_id.php")
    Call<ResponseBody> getItemByID(
            @Part("itemID") RequestBody itemID);

    @Multipart
    @POST("/dev/mit/1317003/update_item.php")
    Call<ResponseBody> updateItemWithImage(
            @Part("itemID") RequestBody itemID,
            @Part("itemName") RequestBody itemName,
            @Part("itemDesc") RequestBody itemDesc,
            @Part("itemCat") RequestBody itemCat,
            @Part("itemVal") RequestBody itemVal,
            @Part("isImageEmpty") RequestBody isImageEmpty,
            @Part("isImageChange") RequestBody isImageChange,
            @Part("token") RequestBody token,
            @Part MultipartBody.Part image);

    @Multipart
    @POST("/dev/mit/1317003/update_item.php")
    Call<ResponseBody> updateItemNoImage(
            @Part("itemID") RequestBody itemID,
            @Part("itemName") RequestBody itemName,
            @Part("itemDesc") RequestBody itemDesc,
            @Part("itemCat") RequestBody itemCat,
            @Part("itemVal") RequestBody itemVal,
            @Part("isImageEmpty") RequestBody isImageEmpty,
            @Part("isImageChange") RequestBody isImageChange,
            @Part("token") RequestBody token);

    // ADD AUCTION ACTIVITY
    @Multipart
    @POST("/dev/mit/1317003/get_item_names_by_uid.php")
    Call<ResponseBody> getItemNamesByToken(
            @Part("token") RequestBody token);

    @Multipart
    @POST("/dev/mit/1317003/create_auction.php")
    Call<ResponseBody> postAuction(
            @Part("itemID") RequestBody itemID,
            @Part("initPrice") RequestBody initPrice,
            @Part("limitPrice") RequestBody limitPrice,
            @Part("auctionStart") RequestBody auctionStart,
            @Part("auctionEnd") RequestBody auctionEnd,
            @Part("token") RequestBody token);

    // AUCTION BY USER ACTIVITY
    @Multipart
    @POST("/dev/mit/1317003/get_auction_by_uid.php")
    Call<ResponseBody> getAuctionsByUser(
            @Part("desiredCount") RequestBody desiredCount,
            @Part("dataOffset") RequestBody dataOffset,
            @Part("token") RequestBody token);
}