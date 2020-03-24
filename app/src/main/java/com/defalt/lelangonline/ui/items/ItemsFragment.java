package com.defalt.lelangonline.ui.items;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.defalt.lelangonline.MainActivity;
import com.defalt.lelangonline.R;
import com.defalt.lelangonline.data.items.ItemsTask;
import com.defalt.lelangonline.ui.recycle.PaginationListener;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;

import static com.defalt.lelangonline.ui.recycle.PaginationListener.PAGE_START;

public class ItemsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private ItemAdapter adapter;
    private ShimmerFrameLayout mShimmerViewContainer;
    private ItemsUI itemsUI;
    private int totalPage = 6;
    private int currentPage = PAGE_START;
    private static boolean isLastPage = false;
    private static boolean isLoading = false;
    private static int itemCount = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_items, container, false);

        mShimmerViewContainer = root.findViewById(R.id.shimmer_view_container);
        mShimmerViewContainer.startShimmer();

        SwipeRefreshLayout swipeRefresh = root.findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);

        RecyclerView mRecyclerView = root.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);

        adapter = new ItemAdapter(getContext(), new ArrayList<Item>());
        mRecyclerView.setAdapter(adapter);

        itemsUI = new ItemsUI(mShimmerViewContainer, mRecyclerView, swipeRefresh, getContext());

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
        MainActivity.setIsConnectionError(false);
        prepareData();
    }

    @Override
    public void onStop() {
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmer();

        itemCount = 0;
        currentPage = PAGE_START;
        isLastPage = false;
        adapter.clear();
        MainActivity.setIsConnectionError(false);
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
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
        new ItemsTask(adapter, new ArrayList<Item>(), currentPage, totalPage, itemsUI).execute(totalPage, itemCount);
    }

    public static class ItemsUI {
        private ShimmerFrameLayout mShimmerViewContainer;
        private RecyclerView mRecyclerView;
        private SwipeRefreshLayout swipeRefresh;
        private final Context CONTEXT;

        ItemsUI(ShimmerFrameLayout mShimmerViewContainer, RecyclerView mRecyclerView, SwipeRefreshLayout swipeRefresh, Context context) {
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