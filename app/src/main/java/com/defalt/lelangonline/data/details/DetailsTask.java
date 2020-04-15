package com.defalt.lelangonline.data.details;

import android.os.AsyncTask;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;

import com.defalt.lelangonline.data.RestApi;
import com.defalt.lelangonline.ui.SharedFunctions;
import com.defalt.lelangonline.ui.details.DetailsActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Timestamp;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsTask extends AsyncTask<String, Void, Void> {
    private int success;
    private CountDownTimer countDownTimer;
    private DetailsActivity.DetailsUI detailsUI;

    public DetailsTask(DetailsActivity.DetailsUI detailsUI) {
        this.detailsUI = detailsUI;
    }

    protected Void doInBackground(String... args) {
        RestApi server = SharedFunctions.getRetrofit().create(RestApi.class);
        RequestBody auctionID = RequestBody.create(MediaType.parse("text/plain"), args[0]);
        RequestBody token = RequestBody.create(MediaType.parse("text/plain"), args[1]);

        Call<ResponseBody> req = server.getDetails(auctionID, token);

        req.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    if (response.body() != null) {
                        JSONObject json = new JSONObject(response.body().string());
                        success = json.getInt("success");

                        if (success == 1) {
                            Double priceStart = json.getDouble("priceStart");
                            Double priceLimit = json.getDouble("priceLimit");
                            Double highestBid = json.getDouble("highestBid");
                            Double myBid = json.getDouble("myBid");
                            Timestamp timeStart = SharedFunctions.parseDate(json.getString("timeStart"));
                            Timestamp timeEnd = SharedFunctions.parseDate(json.getString("timeEnd"));
                            Timestamp serverTime = SharedFunctions.parseDate(json.getString("serverTime"));

                            DetailsActivity.setPriceStart(priceStart);
                            DetailsActivity.setPriceLimit(priceLimit);
                            DetailsActivity.setHighestBid(highestBid);
                            DetailsActivity.setMyBid(myBid);
                            detailsUI.updateUI(myBid);

                            final long currentTimestamp = serverTime.getTime();
                            final long startDiff = timeStart.getTime() - currentTimestamp;
                            final long endDiff = timeEnd.getTime() - currentTimestamp;

                            if (countDownTimer != null) {
                                countDownTimer.cancel();
                            }

                            // Auction has not started
                            if (startDiff > 0) {
                                detailsUI.lockBid();
                                countDownTimer = new CountDownTimer(startDiff, 1000) {
                                    @Override
                                    public void onTick(long millisUntilFinished) { }

                                    public void onFinish() {
                                        // Auction has started
                                        detailsUI.unlockBid();
                                        countDownTimer = new CountDownTimer(endDiff, 1000) {
                                            @Override
                                            public void onTick(long millisUntilFinished) { }

                                            public void onFinish() {
                                                // Auction has ended
                                                detailsUI.lockBid();
                                            }
                                        };
                                        countDownTimer.start();
                                    }
                                };
                                countDownTimer.start();

                            // Auction has started but not ended yet
                            } else if (endDiff > 0) {
                                detailsUI.unlockBid();
                                countDownTimer = new CountDownTimer(endDiff, 1000) {
                                    @Override
                                    public void onTick(long millisUntilFinished) { }

                                    public void onFinish() {
                                        // Auction has ended
                                        detailsUI.lockBid();
                                    }
                                };
                                countDownTimer.start();

                            // Auction has ended
                            } else {
                                detailsUI.lockBid();
                            }
                        } else if (success == 2) {
                            detailsUI.lockOwner();
                        }
                    } else {
                        detailsUI.lockBid();
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();

                    detailsUI.lockBid();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                t.printStackTrace();

                detailsUI.lockBid();
            }
        });

        return null;
    }
}
