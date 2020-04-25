package com.defalt.lelangonline.ui.details;

import android.content.Context;
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
import com.defalt.lelangonline.ui.recycle.BaseViewHolder;
import com.defalt.lelangonline.ui.recycle.ProgressHolder;

import java.util.List;

public class BidAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;
    private Context mContext;

    private List<Bid> bidList;

    BidAdapter(List<Bid> bidList, Context mContext) {
        this.bidList = bidList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new BidViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_details_history_card, parent, false), mContext);
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
            return position == bidList.size() - 1 ? VIEW_TYPE_LOADING : VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        return bidList == null ? 0 : bidList.size();
    }

    public void addItems(List<Bid> postBid) {
        bidList.addAll(postBid);
        notifyDataSetChanged();
    }

    public void addLoading() {
        isLoaderVisible = true;
        bidList.add(new Bid());
        notifyItemInserted(bidList.size() - 1);
    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = bidList.size() - 1;
        Bid item = getBid(position);
        if (item != null) {
            bidList.remove(position);
            notifyItemRemoved(position);
        }
    }

    void clear() {
        bidList.clear();
        notifyDataSetChanged();
    }

    private Bid getBid(int position) {
        return bidList.get(position);
    }

    class BidViewHolder extends BaseViewHolder {
        private TextView time, price, username;
        private ImageView thumbnail;
        private Context mContext;

        BidViewHolder(View view, Context mContext) {
            super(view);
            time = view.findViewById(R.id.time);
            price = view.findViewById(R.id.price);
            username = view.findViewById(R.id.username);
            thumbnail = view.findViewById(R.id.thumbnail);
            this.mContext = mContext;
        }

        protected void clear() { }

        public void onBind(int position) {
            super.onBind(position);
            Bid thisBid = bidList.get(position);

            time.setText(thisBid.getBidTime().toString());
            price.setText(SharedFunctions.formatRupiah(thisBid.getBidPrice()));
            username.setText(thisBid.getUserName());

            thumbnail.setImageDrawable(null);
            if (!thisBid.getUserImage().equals("null")) {
                String IMAGE_URL = "https://dev.projectlab.co.id/mit/1317003/images/profile/";
                Glide.with(mContext).load(IMAGE_URL + thisBid.getUserImage()).into(thumbnail);
            } else {
                Glide.with(mContext).load(R.drawable.placeholder_image).into(thumbnail);
            }
        }
    }

}

