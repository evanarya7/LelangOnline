package com.defalt.lelangonline.ui.items;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.defalt.lelangonline.R;
import com.defalt.lelangonline.data.items.remove.ItemRemoveTask;
import com.defalt.lelangonline.data.login.LoginRepository;
import com.defalt.lelangonline.ui.SharedFunctions;
import com.defalt.lelangonline.ui.items.edit.ItemsEditActivity;
import com.defalt.lelangonline.ui.recycle.BaseViewHolder;
import com.defalt.lelangonline.ui.recycle.ProgressHolder;

import java.util.List;

public class ItemsByUserAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;

    private final Activity mActivity;
    private List<Item> itemList;

    ItemsByUserAdapter(Activity mActivity, List<Item> itemList) {
        this.mActivity = mActivity;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ItemViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_items_by_user_card, parent, false), mActivity);
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

        ItemViewHolder(View view, final Activity mActivity) {
            super(view);
            title = view.findViewById(R.id.title);
            price = view.findViewById(R.id.price);
            thumbnail = view.findViewById(R.id.thumbnail);

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    String[] menus = {mActivity.getResources().getString(R.string.action_update), mActivity.getResources().getString(R.string.action_remove)};

                    AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity);
                    dialog.setTitle(title.getText())
                            .setItems(menus, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0: {
                                            Intent intent = new Intent(mActivity, ItemsEditActivity.class);
                                            intent.putExtra("TAG_EXTRA", thisItem.getItemID());
                                            mActivity.startActivity(intent);
                                            break;
                                        }
                                        case 1: {
                                            AlertDialog.Builder deleteDialog = new AlertDialog.Builder(mActivity);
                                            deleteDialog.setTitle(R.string.alert_remove_confirm_title)
                                                    .setMessage(R.string.alert_remove_confirm_desc)
                                                    .setIcon(R.drawable.ic_warning_black_24dp)
                                                    .setNegativeButton(R.string.alert_disagree, null)
                                                    .setPositiveButton(R.string.alert_agree, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            new ItemRemoveTask(new ItemRemoveTask.RemoveUI(mActivity)).execute(thisItem.getItemID(), LoginRepository.getLoggedInUser().getToken());
                                                        }
                                                    }).show();
                                            break;
                                        }
                                    }
                                }
                            }).show();
                    return true;
                }
            });
        }

        protected void clear() {
        }

        public void onBind(int position) {
            super.onBind(position);
            thisItem = itemList.get(position);

            title.setText(thisItem.getItemName());
            price.setText(SharedFunctions.formatRupiah(thisItem.getItemValue()));

            thumbnail.setImageDrawable(null);
            if (!thisItem.getItemImg().equals("null")) {
                String IMAGE_URL = "https://dev.projectlab.co.id/mit/1317003/images/items/";
                Glide.with(mActivity).load(IMAGE_URL + thisItem.getItemImg()).into(thumbnail);
            } else {
                Glide.with(mActivity).load(R.drawable.placeholder_image).into(thumbnail);
            }
        }
    }

}

