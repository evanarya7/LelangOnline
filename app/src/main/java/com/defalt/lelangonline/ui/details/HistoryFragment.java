package com.defalt.lelangonline.ui.details;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.defalt.lelangonline.R;
import com.defalt.lelangonline.data.details.HistoryTask;
import com.defalt.lelangonline.ui.recycle.PaginationListener;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;

import static com.defalt.lelangonline.ui.recycle.PaginationListener.PAGE_START;

public class HistoryFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private BidAdapter adapter;
    private ShimmerFrameLayout mShimmerViewContainer;
    private HistoryUI historyUI;
    private String auctionID;
    private int totalPage = 10;
    private int currentPage = PAGE_START;
    private static boolean isLastPage = false;
    private static boolean isLoading = false;
    private static int itemCount = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_details_history, container, false);

        mShimmerViewContainer = root.findViewById(R.id.shimmer_view_container);
        mShimmerViewContainer.startShimmer();

        SwipeRefreshLayout swipeRefresh = root.findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);

        RecyclerView mRecyclerView = root.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);

        adapter = new BidAdapter(new ArrayList<Bid>());
        mRecyclerView.setAdapter(adapter);

        historyUI = new HistoryUI(mShimmerViewContainer, mRecyclerView, swipeRefresh, getContext());
        auctionID = getArguments() != null ? getArguments().getString("TAG_AUID") : null;
        prepareData();

        mRecyclerView.addOnScrollListener(new PaginationListener(layoutManager, totalPage) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage++;
                prepareData();
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        return root;
    }

    @Override
    public void onRefresh() {
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmer();

        itemCount = 0;
        currentPage = PAGE_START;
        isLastPage = false;
        adapter.clear();
        DetailsActivity.setIsConnectionError(false);
        prepareData();
    }

    public static void setLastPage(boolean lastPage) {
        isLastPage = lastPage;
    }

    public static void setLoading(boolean loading) {
        isLoading = loading;
    }

    public static int getItemCount() {
        return itemCount;
    }

    public static void setItemCount(int newItemCount) {
        itemCount = newItemCount;
    }

    private void prepareData() {
        new HistoryTask(adapter, new ArrayList<Bid>(), currentPage, totalPage, historyUI).execute(auctionID, String.valueOf(totalPage), String.valueOf(itemCount));
    }

    public static class HistoryUI {
        private final ShimmerFrameLayout mShimmerViewContainer;
        private final RecyclerView mRecyclerView;
        private final SwipeRefreshLayout swipeRefresh;
        private final Context CONTEXT;

        HistoryUI(ShimmerFrameLayout mShimmerViewContainer, RecyclerView mRecyclerView, SwipeRefreshLayout swipeRefresh, Context context) {
            this.mShimmerViewContainer = mShimmerViewContainer;
            this.mRecyclerView = mRecyclerView;
            this.swipeRefresh = swipeRefresh;
            this.CONTEXT = context;
        }

        public void setRefreshing(boolean refreshingState) {
            swipeRefresh.setRefreshing(refreshingState);
        }

        public void updateUI() {
            mShimmerViewContainer.stopShimmer();
            mShimmerViewContainer.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }

        public void showError() {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(CONTEXT);
            alertDialog.setTitle(R.string.alert_conn_title);
            alertDialog.setMessage(R.string.alert_conn_desc);
            alertDialog.setIcon(R.drawable.ic_error_black_24dp);
            alertDialog.setPositiveButton(CONTEXT.getString(R.string.alert_agree), null);
            alertDialog.show();
        }
    }
}
