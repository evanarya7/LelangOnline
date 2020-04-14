package com.defalt.lelangonline.ui.details;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.defalt.lelangonline.R;
import com.defalt.lelangonline.data.details.DetailsTask;
import com.defalt.lelangonline.ui.SharedFunctions;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DetailsFragment extends Fragment {

    private ShimmerFrameLayout mShimmerViewContainer;
    private ScrollView mScrollView;
    private String auctionID;
    private ImageView mThumbnail;
    private TextView mBidCount;
    private TextView mTimerText;
    private TextView mTimer;
    private TextView mItemName;
    private TextView mItemPriceInit;
    private TextView mItemPriceStart;
    private TextView mItemDesc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_details_item, container, false);

        mShimmerViewContainer = root.findViewById(R.id.shimmer_view_container);
        mShimmerViewContainer.startShimmer();

        mScrollView = root.findViewById(R.id.container);
        mThumbnail = root.findViewById(R.id.thumbnail);
        mBidCount = root.findViewById(R.id.bidCount);
        mTimerText = root.findViewById(R.id.timerText);
        mTimer = root.findViewById(R.id.timer);
        mItemName = root.findViewById(R.id.itemName);
        mItemPriceInit = root.findViewById(R.id.itemPrice);
        mItemPriceStart = root.findViewById(R.id.itemStart);
        mItemDesc = root.findViewById(R.id.itemDescription);
        auctionID = getArguments() != null ? getArguments().getString("TAG_AUID") : null;

        prepareData();

        return root;
    }

    private void prepareData() {
        new DetailsTask(new DetailsUI(mShimmerViewContainer, mScrollView,
                mThumbnail, mBidCount, mTimerText, mTimer, mItemName, mItemPriceInit, mItemPriceStart, mItemDesc, getActivity()))
                .execute(auctionID);
    }

    public static class DetailsUI {
        private ShimmerFrameLayout mShimmerViewContainer;
        private ScrollView mScrollView;
        private ImageView mThumbnail;
        private TextView mBidCount;
        private TextView mTimerText;
        private TextView mTimer;
        private TextView mItemName;
        private TextView mItemPriceInit;
        private TextView mItemPriceStart;
        private TextView mItemDesc;
        private Context mContext;

        DetailsUI(ShimmerFrameLayout mShimmerViewContainer, ScrollView mScrollView,
                  ImageView mThumbnail, TextView mBidCount, TextView mTimerText, TextView mTimer, TextView mItemName,
                  TextView mItemPriceInit, TextView mItemPriceStart, TextView mItemDesc, Context mContext) {
            this.mShimmerViewContainer = mShimmerViewContainer;
            this.mScrollView = mScrollView;
            this.mThumbnail = mThumbnail;
            this.mBidCount = mBidCount;
            this.mTimerText = mTimerText;
            this.mTimer = mTimer;
            this.mItemName = mItemName;
            this.mItemPriceInit = mItemPriceInit;
            this.mItemPriceStart = mItemPriceStart;
            this.mItemDesc = mItemDesc;
            this.mContext = mContext;
        }

        public void updateUI(Details details) {
            mBidCount.setText(String.valueOf(details.getBidCount()));
            mItemName.setText(details.getItemName());
            mItemPriceInit.setText(SharedFunctions.formatRupiah(details.getItemInitPrice()));
            mItemPriceInit.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            mItemPriceStart.setText(SharedFunctions.formatRupiah(details.getItemStartPrice()));
            mItemDesc.setText(details.getItemDesc());

            mThumbnail.setImageDrawable(null);
            if (!details.getItemImg().equals("null")) {
                String IMAGE_URL = "https://dev.projectlab.co.id/mit/1317003/images/items/";
                Glide.with(mContext).load(IMAGE_URL + details.getItemImg()).into(mThumbnail);
            } else {
                Glide.with(mContext).load(R.drawable.placeholder_image).into(mThumbnail);
            }

            final long currentTimestamp = details.getTimeServer().getTime();
            final long startDiff = details.getTimeStart().getTime() - currentTimestamp;
            final long endDiff = details.getTimeEnd().getTime() - currentTimestamp;

            // Auction has not started
            if (startDiff > 0) {
                mTimerText.setText(R.string.card_details_start);
                new CountDownTimer(startDiff, 1000) {
                    public void onTick(long millisUntilFinished) {
                        String timeLeft = String.format(Locale.getDefault(), "%02d:%02d:%02d",
                                TimeUnit.MILLISECONDS.toHours(millisUntilFinished) % 60,
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60);
                        mTimer.setText(timeLeft);
                    }

                    public void onFinish() {
                        // Auction has started but not ended yet
                        mTimerText.setText(R.string.card_details_end);
                        new CountDownTimer(endDiff, 1000) {
                            public void onTick(long millisUntilFinished) {
                                String timeLeft = String.format(Locale.getDefault(), "%02d:%02d:%02d",
                                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished) % 60,
                                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
                                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60);
                                mTimer.setText(timeLeft);
                            }

                            public void onFinish() {
                                // Auction has ended
                                mTimerText.setText("");
                                mTimer.setText(R.string.card_details_stop);
                            }
                        }.start();
                    }
                }.start();

                // Auction has started but not ended yet
            } else if (endDiff > 0) {
                mTimerText.setText(R.string.card_details_end);
                new CountDownTimer(endDiff, 1000) {
                    public void onTick(long millisUntilFinished) {
                        String timeLeft = String.format(Locale.getDefault(), "%02d:%02d:%02d",
                                TimeUnit.MILLISECONDS.toHours(millisUntilFinished) % 60,
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60);
                        mTimer.setText(timeLeft);
                    }

                    public void onFinish() {
                        // Auction has ended
                        mTimerText.setText("");
                        mTimer.setText(R.string.card_details_stop);
                    }
                }.start();

                // Auction has ended
            } else {
                mTimerText.setText("");
                mTimer.setText(R.string.card_details_stop);
            }

            mShimmerViewContainer.stopShimmer();
            mShimmerViewContainer.setVisibility(View.GONE);
            mScrollView.setVisibility(View.VISIBLE);
        }
    }
}
