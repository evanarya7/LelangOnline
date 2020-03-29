package com.defalt.lelangonline.ui.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.defalt.lelangonline.R;
import com.defalt.lelangonline.ui.SharedFunctions;
import com.defalt.lelangonline.ui.details.DetailsActivity;
import com.defalt.lelangonline.ui.recycle.BaseViewHolder;
import com.defalt.lelangonline.ui.recycle.ProgressHolder;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TopAuctionAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;

    private final Context mContext;
    private List<TopAuction> topAuctionList;

    TopAuctionAdapter(Context mContext, List<TopAuction> topAuctionList) {
        this.mContext = mContext;
        this.topAuctionList = topAuctionList;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new TopAuctionViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_home_card, parent, false), mContext);
            case VIEW_TYPE_LOADING:
                return new ProgressHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleview_loading, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (isLoaderVisible) {
            return position == topAuctionList.size() - 1 ? VIEW_TYPE_LOADING : VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        return topAuctionList == null ? 0 : topAuctionList.size();
    }

    public void addItems(List<TopAuction> postTopAuction) {
        topAuctionList.addAll(postTopAuction);
        notifyDataSetChanged();
    }

    public void addLoading() {
        isLoaderVisible = true;
        topAuctionList.add(new TopAuction());
        notifyItemInserted(topAuctionList.size() - 1);
    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = topAuctionList.size() - 1;
        TopAuction item = getAuction(position);
        if (item != null) {
            topAuctionList.remove(position);
            notifyItemRemoved(position);
        }
    }

    void clear() {
        topAuctionList.clear();
        notifyDataSetChanged();
    }

    private TopAuction getAuction(int position) {
        return topAuctionList.get(position);
    }

    class TopAuctionViewHolder extends BaseViewHolder {
        private TopAuction thisTopAuction;
        private TextView title, priceInit, priceStart, timerText, timer;
        private ImageView thumbnail;

        TopAuctionViewHolder(View view, final Context mContext) {
            super(view);
            title = view.findViewById(R.id.title);
            priceInit = view.findViewById(R.id.priceInit);
            priceStart = view.findViewById(R.id.priceStart);
            timerText = view.findViewById(R.id.timerText);
            timer = view.findViewById(R.id.timer);
            thumbnail = view.findViewById(R.id.thumbnail);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, DetailsActivity.class);
                    intent.putExtra("TAG_EXTRA", thisTopAuction.getAuctionID());
                    mContext.startActivity(intent);
                }
            });
        }

        protected void clear() { }

        public void onBind(int position) {
            super.onBind(position);
            thisTopAuction = topAuctionList.get(position);

            final long currentTimestamp = thisTopAuction.getServerTime().getTime();
            final long startDiff = thisTopAuction.getAuctionStart().getTime() - currentTimestamp;
            final long endDiff = thisTopAuction.getAuctionEnd().getTime() - currentTimestamp;

            title.setText(thisTopAuction.getItemName());
            priceInit.setText(SharedFunctions.formatRupiah(thisTopAuction.getItemValue()));
            priceInit.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            priceStart.setText(SharedFunctions.formatRupiah(thisTopAuction.getPriceStart()));

            thumbnail.setImageDrawable(null);
            if (!thisTopAuction.getItemImg().equals("null")) {
                String IMAGE_URL = "https://dev.projectlab.co.id/mit/1317003/images/items/";
                Glide.with(mContext).load(IMAGE_URL + thisTopAuction.getItemImg()).into(thumbnail);
            } else {
                Glide.with(mContext).load(R.drawable.placeholder_image).into(thumbnail);
            }

            // Auction has not started
            if (startDiff > 0) {
                timerText.setText(R.string.card_auction_start);
                new CountDownTimer(startDiff, 1000) {
                    public void onTick(long millisUntilFinished) {
                        String timeLeft = String.format(Locale.getDefault(), "%02d:%02d:%02d",
                                TimeUnit.MILLISECONDS.toHours(millisUntilFinished) % 60,
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60);
                        timer.setText(timeLeft);
                    }

                    public void onFinish() {
                        // Auction has started but not ended yet
                        timerText.setText(R.string.card_auction_end);
                        new CountDownTimer(endDiff, 1000) {
                            public void onTick(long millisUntilFinished) {
                                String timeLeft = String.format(Locale.getDefault(), "%02d:%02d:%02d",
                                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished) % 60,
                                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
                                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60);
                                timer.setText(timeLeft);
                            }

                            public void onFinish() {
                                // Auction has ended
                                timerText.setText(R.string.card_auction_stop);
                                timer.setText("");
                            }
                        }.start();
                    }
                }.start();

                // Auction has started but not ended yet
            } else if (endDiff > 0) {
                timerText.setText(R.string.card_auction_end);
                new CountDownTimer(endDiff, 1000) {
                    public void onTick(long millisUntilFinished) {
                        String timeLeft = String.format(Locale.getDefault(), "%02d:%02d:%02d",
                                TimeUnit.MILLISECONDS.toHours(millisUntilFinished) % 60,
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60);
                        timer.setText(timeLeft);
                    }

                    public void onFinish() {
                        // Auction has ended
                        timerText.setText(R.string.card_auction_stop);
                        timer.setText("");
                    }
                }.start();

                // Auction has ended
            } else {
                timerText.setText(R.string.card_auction_stop);
                timer.setText("");
            }
        }
    }
}