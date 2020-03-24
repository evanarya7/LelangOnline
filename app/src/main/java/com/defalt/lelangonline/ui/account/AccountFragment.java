package com.defalt.lelangonline.ui.account;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.defalt.lelangonline.R;
import com.defalt.lelangonline.data.PreferencesManager;
import com.defalt.lelangonline.data.login.LoginRepository;
import com.facebook.shimmer.ShimmerFrameLayout;

public class AccountFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private ShimmerFrameLayout mShimmerViewContainer;
    private SwipeRefreshLayout swipeRefresh;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_account, container, false);

        mShimmerViewContainer = root.findViewById(R.id.shimmer_view_container);
        mShimmerViewContainer.startShimmer();

        swipeRefresh = root.findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);

        if (LoginRepository.isLoggedIn()) {
            TextView mTextView = root.findViewById(R.id.profile_name);
            mTextView.setText(PreferencesManager.instance().fetchValueString("username"));

            mShimmerViewContainer.stopShimmer();
            mShimmerViewContainer.setVisibility(View.GONE);
            swipeRefresh.setVisibility(View.VISIBLE);
        }

        return root;
    }

    @Override
    public void onRefresh() {
        mShimmerViewContainer.startShimmer();
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        swipeRefresh.setVisibility(View.GONE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefresh.setRefreshing(false);

                mShimmerViewContainer.setVisibility(View.GONE);
                swipeRefresh.setVisibility(View.VISIBLE);
                mShimmerViewContainer.stopShimmer();
            }
        }, 1000);
    }
}