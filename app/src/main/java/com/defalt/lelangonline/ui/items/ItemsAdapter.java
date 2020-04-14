package com.defalt.lelangonline.ui.items;

import android.content.Context;
import android.content.Intent;
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
import com.defalt.lelangonline.ui.auctions.AuctionsActivity;
import com.defalt.lelangonline.ui.recycle.BaseViewHolder;
import com.defalt.lelangonline.ui.recycle.ProgressHolder;

import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;

    private final Context mContext;
    private List<Item> itemList;

    ItemsAdapter(Context mContext, List<Item> itemList) {
        this.mContext = mContext;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ItemViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_items_card, parent, false), mContext);
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
            return position == itemList.size() - 1 ? VIEW_TYPE_LOADING : VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }

    public void addItems(List<Item> postItem) {
        itemList.addAll(postItem);
        notifyDataSetChanged();
    }

    public void addLoading() {
        isLoaderVisible = true;
        itemList.add(new Item());
        notifyItemInserted(itemList.size() - 1);
    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = itemList.size() - 1;
        Item item = getItem(position);
        if (item != null) {
            itemList.remove(position);
            notifyItemRemoved(position);
        }
    }

    void clear() {
        itemList.clear();
        notifyDataSetChanged();
    }

    private Item getItem(int position) {
        return itemList.get(position);
    }

    class ItemViewHolder extends BaseViewHolder {
        private Item thisItem;
        private TextView title, price;
        private ImageView thumbnail;
        private Context mContext;

        ItemViewHolder(View view, final Context mContext) {
            super(view);
            this.mContext = mContext;
            title = view.findViewById(R.id.title);
            price = view.findViewById(R.id.price);
            thumbnail = view.findViewById(R.id.thumbnail);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, AuctionsActivity.class);
                    intent.putExtra("TAG_EXTRA", thisItem.getItemID());
                    mContext.startActivity(intent);
                }
            });
        }

        protected void clear() { }

        public void onBind(int position) {
            super.onBind(position);
            thisItem = itemList.get(position);

            title.setText(thisItem.getItemName());
            price.setText(SharedFunctions.formatRupiah(thisItem.getItemValue()));

            thumbnail.setImageDrawable(null);
            if (!thisItem.getItemImg().equals("null")) {
                String IMAGE_URL = "https://dev.projectlab.co.id/mit/1317003/images/items/";
                Glide.with(mContext).load(IMAGE_URL + thisItem.getItemImg()).into(thumbnail);
            } else {
                Glide.with(mContext).load(R.drawable.placeholder_image).into(thumbnail);
            }
        }
    }

}

