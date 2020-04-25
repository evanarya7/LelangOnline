package com.defalt.lelangonline.ui.items;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.defalt.lelangonline.MainActivity;
import com.defalt.lelangonline.R;
import com.defalt.lelangonline.data.items.ItemsSearchTask;
import com.defalt.lelangonline.data.items.ItemsTask;
import com.defalt.lelangonline.ui.recycle.PaginationListener;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.Objects;

import static com.defalt.lelangonline.ui.recycle.PaginationListener.PAGE_START;

public class ItemsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private ItemsAdapter adapter;
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
        setHasOptionsMenu(true);

        mShimmerViewContainer = root.findViewById(R.id.shimmer_view_container);
        mShimmerViewContainer.startShimmer();

        SwipeRefreshLayout swipeRefresh = root.findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);

        RecyclerView mRecyclerView = root.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);

        adapter = new ItemsAdapter(getContext(), new ArrayList<Item>());
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
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.options_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

        SearchManager searchManager =
                (SearchManager) Objects.requireNonNull(this.getContext()).getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                Objects.requireNonNull(searchManager).getSearchableInfo(Objects.requireNonNull(getActivity()).getComponentName()));
        searchView.setQueryHint("Cari barang...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mShimmerViewContainer.setVisibility(View.VISIBLE);
                mShimmerViewContainer.startShimmer();

                itemCount = 0;
                currentPage = PAGE_START;
                isLastPage = false;
                adapter.clear();
                MainActivity.setIsConnectionError(false);
                prepareDataFromSearch(query);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
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

    private void prepareDataFromSearch(String query) {
        new ItemsSearchTask(adapter, new ArrayList<Item>(), currentPage, totalPage, itemsUI).execute(String.valueOf(totalPage), String.valueOf(itemCount), query);
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
            alertDialog.setTitle(R.string.alert_conn_title)
                    .setMessage(R.string.alert_conn_desc)
                    .setIcon(R.drawable.ic_error_black_24dp)
                    .setPositiveButton(CONTEXT.getString(R.string.alert_agree), null)
                    .show();
        }
    }
}