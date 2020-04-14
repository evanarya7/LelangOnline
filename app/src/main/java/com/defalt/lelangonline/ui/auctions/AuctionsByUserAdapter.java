package com.defalt.lelangonline.ui.auctions;

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

public class AuctionAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;

    private final Context mContext;
    private List<Auction> auctionList;

    AuctionAdapter(Context mContext, List<Auction> auctionList) {
        this.mContext = mContext;
        this.auctionList = auctionList;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new AuctionViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_auctions_by_item_card, parent, false), mContext);
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
            return position == auctionList.size() - 1 ? VIEW_TYPE_LOADING : VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        return auctionList == null ? 0 : auctionList.size();
    }

    public void addItems(List<Auction> postAuction) {
        auctionList.addAll(postAuction);
        notifyDataSetChanged();
    }

    public void addLoading() {
        isLoaderVisible = true;
        auctionList.add(new Auction());
        notifyItemInserted(auctionList.size() - 1);
    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = auctionList.size() - 1;
        Auction item = getAuction(position);
        if (item != null) {
            auctionList.remove(position);
            notifyItemRemoved(position);
        }
    }

    void clear() {
        auctionList.clear();
        notifyDataSetChanged();
    }

    private Auction getAuction(int position) {
        return auctionList.get(position);
    }

    class AuctionViewHolder extends BaseViewHolder {
        private Auction thisAuction;
        private TextView title, priceInit, priceStart, timerText, timer;
        private ImageView thumbnail;
        private CountDownTimer countDownTimer;

        AuctionViewHolder(View view, final Context mContext) {
            super(view);
            title = view.findViewById(R.id.title);
            priceInit = view.findViewById(R.id.value);
            priceStart = view.findViewById(R.id.price);
            timerText = view.findViewById(R.id.timerText);
            timer = view.findViewById(R.id.timer);
            thumbnail = view.findViewById(R.id.thumbnail);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, DetailsActivity.class);
                    intent.putExtra("TAG_EXTRA", thisAuction.getAuctionID());
                    mContext.startActivity(intent);
                }
            });
        }

        protected void clear() { }

        public void onBind(int position) {
            super.onBind(position);
            thisAuction = auctionList.get(position);

            final long currentTimestamp = thisAuction.getServerTime().getTime();
            final long startDiff = thisAuction.getAuctionStart().getTime() - currentTimestamp;
            final long endDiff = thisAuction.getAuctionEnd().getTime() - currentTimestamp;

            title.setText(thisAuction.getItemName());
            priceInit.setText(SharedFunctions.formatRupiah(thisAuction.getItemValue()));
            priceInit.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            priceStart.setText(SharedFunctions.formatRupiah(thisAuction.getPriceStart()));

            thumbnail.setImageDrawable(null);
            if (!thisAuction.getItemImg().equals("null")) {
                String IMAGE_URL = "https://dev.projectlab.co.id/mit/1317003/images/items/";
                Glide.with(mContext).load(IMAGE_URL + thisAuction.getItemImg()).into(thumbnail);
            } else {
                Glide.with(mContext).load(R.drawable.placeholder_image).into(thumbnail);
            }

            if (countDownTimer != null) {
                countDownTimer.cancel();
            }

            // Auction has not started
            if (startDiff > 0) {
                timerText.setText(R.string.card_auction_start);
                timer.setText(null);
                countDownTimer = new CountDownTimer(startDiff, 1000) {
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
                        timer.setText(null);
                        countDownTimer = new CountDownTimer(endDiff, 1000) {
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
                                timer.setText(null);
                            }
                        };
                        countDownTimer.start();
                    }
                };
                countDownTimer.start();

                // Auction has started but not ended yet
            } else if (endDiff > 0) {
                timerText.setText(R.string.card_auction_end);
                timer.setText(null);
                countDownTimer = new CountDownTimer(endDiff, 1000) {
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
                        timer.setText(null);
                    }
                };
                countDownTimer.start();

                // Auction has ended
            } else {
                timerText.setText(R.string.card_auction_stop);
                timer.setText(null);
            }
        }
    }
}
