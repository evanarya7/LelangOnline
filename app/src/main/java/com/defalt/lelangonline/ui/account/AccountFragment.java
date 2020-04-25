package com.defalt.lelangonline.ui.account;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.defalt.lelangonline.R;
import com.defalt.lelangonline.data.account.AccountTask;
import com.defalt.lelangonline.data.login.LoginRepository;
import com.facebook.shimmer.ShimmerFrameLayout;

public class AccountFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private ShimmerFrameLayout mShimmerViewContainer;
    private SwipeRefreshLayout swipeRefresh;
    private AccountUI accountUI;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_account, container, false);

        mShimmerViewContainer = root.findViewById(R.id.shimmer_view_container);
        mShimmerViewContainer.startShimmer();

        swipeRefresh = root.findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);

        ImageView profileImageView = root.findViewById(R.id.profile_thumbnail);
        TextView nameTextView = root.findViewById(R.id.profile_name);
        TextView subTextView = root.findViewById(R.id.profile_active);
        Context mContext = getActivity();

        accountUI = new AccountUI(mShimmerViewContainer, swipeRefresh, profileImageView, nameTextView, subTextView, mContext);
        new AccountTask(accountUI).execute(LoginRepository.getLoggedInUser().getToken());

        return root;
    }

    @Override
    public void onRefresh() {
        mShimmerViewContainer.startShimmer();
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        swipeRefresh.setVisibility(View.GONE);

        new AccountTask(accountUI).execute(LoginRepository.getLoggedInUser().getToken());
    }

    public static class AccountUI {
        private ShimmerFrameLayout mShimmerViewContainer;
        private SwipeRefreshLayout swipeRefresh;
        private ImageView profileImageView;
        private TextView nameTextView;
        private TextView subTextView;
        private Context mContext;

        AccountUI(ShimmerFrameLayout mShimmerViewContainer, SwipeRefreshLayout swipeRefresh, ImageView profileImageView, TextView nameTextView, TextView subTextView, Context mContext) {
            this.mShimmerViewContainer = mShimmerViewContainer;
            this.swipeRefresh = swipeRefresh;
            this.profileImageView = profileImageView;
            this.nameTextView = nameTextView;
            this.subTextView = subTextView;
            this.mContext = mContext;
        }

        public void updateUI(String name, String image, double completeness) {
            profileImageView.setImageDrawable(null);
            if (!image.equals("null")) {
                String IMAGE_URL = "https://dev.projectlab.co.id/mit/1317003/images/profile/";
                Glide.with(mContext).load(IMAGE_URL + image).into(profileImageView);
            } else {
                Glide.with(mContext).load(R.drawable.placeholder_image).into(profileImageView);
            }

            nameTextView.setText(name);

            if (completeness == 1.0) {
                subTextView.setText(R.string.account_sub_unverified);
            } else {
                int completenessPercent = (int) (completeness * 100);
                subTextView.setText("Kelengkapan profil " + completenessPercent + "%");
            }

            endLoading();
        }

        public void showError() {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
            alertDialog.setTitle(R.string.alert_conn_title)
                    .setMessage(R.string.alert_conn_desc)
                    .setIcon(R.drawable.ic_error_black_24dp)
                    .setPositiveButton(mContext.getString(R.string.alert_agree), null)
                    .show();

            endLoading();
        }

        private void endLoading() {
            swipeRefresh.setRefreshing(false);
            mShimmerViewContainer.stopShimmer();
            mShimmerViewContainer.setVisibility(View.GONE);
            swipeRefresh.setVisibility(View.VISIBLE);
        }
    }
}