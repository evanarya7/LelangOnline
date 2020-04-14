package com.defalt.lelangonline.ui.items;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.defalt.lelangonline.R;
import com.defalt.lelangonline.data.items.ItemsByUserTask;
import com.defalt.lelangonline.data.login.LoginRepository;
import com.defalt.lelangonline.ui.recycle.PaginationListener;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;

import static com.defalt.lelangonline.ui.recycle.PaginationListener.PAGE_START;

public class ItemsByUserActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ItemsByUserAdapter adapter;
    private ShimmerFrameLayout mShimmerViewContainer;
    private ItemsByUserUI itemsByUserUI;
    private int totalPage = 6;
    private int currentPage = PAGE_START;
    private static boolean isLastPage = false;
    private static boolean isLoading = false;
    private static int itemCount = 0;
    private static boolean isConnectionError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_by_user);

        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);
        mShimmerViewContainer.startShimmer();

        SwipeRefreshLayout swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);

        RecyclerView mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        adapter = new ItemsByUserAdapter(this, new ArrayList<Item>());
        mRecyclerView.setAdapter(adapter);

        itemsByUserUI = new ItemsByUserUI(mShimmerViewContainer, mRecyclerView, swipeRefresh, this);

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
    }

    @Override
    public void onRefresh() {
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmer();

        itemCount = 0;
        currentPage = PAGE_START;
        isLastPage = false;
        adapter.clear();
        ItemsByUserActivity.setIsConnectionError(false);
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
        ItemsByUserActivity.setIsConnectionError(false);
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

    public static boolean isConnectionError() {
        return isConnectionError;
    }

    public static void setIsConnectionError(boolean isConnectionError) {
        ItemsByUserActivity.isConnectionError = isConnectionError;
    }

    private void prepareData() {
        new ItemsByUserTask(adapter, new ArrayList<Item>(), currentPage, totalPage, itemsByUserUI).execute(String.valueOf(totalPage), String.valueOf(itemCount), LoginRepository.getLoggedInUser().getToken());
    }

    public static class ItemsByUserUI {
        private final ShimmerFrameLayout mShimmerViewContainer;
        private final RecyclerView mRecyclerView;
        private final SwipeRefreshLayout swipeRefresh;
        private final Context CONTEXT;

        ItemsByUserUI(ShimmerFrameLayout mShimmerViewContainer, RecyclerView mRecyclerView ,SwipeRefreshLayout swipeRefresh, Context context) {
            this.mShimmerViewContainer = mShimmerViewContainer;
            this.swipeRefresh = swipeRefresh;
            this.mRecyclerView = mRecyclerView;
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
            alertDialog.setTitle(R.string.alert_conn_title)
                    .setMessage(R.string.alert_conn_desc)
                    .setIcon(R.drawable.ic_error_black_24dp)
                    .setPositiveButton(CONTEXT.getString(R.string.alert_agree), null)
                    .show();
        }
    }
}
