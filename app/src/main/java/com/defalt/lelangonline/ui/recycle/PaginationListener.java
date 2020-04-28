package com.defalt.lelangonline.ui.recycle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class PaginationListener extends RecyclerView.OnScrollListener {

  public static final int PAGE_START = 0;
  private int pageSize;

  @NonNull
  private LinearLayoutManager layoutManager;

  protected PaginationListener(@NonNull LinearLayoutManager layoutManager, int pageSize) {
    this.layoutManager = layoutManager;
    this.pageSize = pageSize;
  }

  @Override
  public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
    super.onScrolled(recyclerView, dx, dy);

    int visibleItemCount = layoutManager.getChildCount();
    int totalItemCount = layoutManager.getItemCount();
    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

    if (!isLoading() && !isLastPage()) {
      if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
          && firstVisibleItemPosition >= 0
          && totalItemCount >= pageSize) {
        loadMoreItems();
      }
    }
  }

  protected abstract void loadMoreItems();

  public abstract boolean isLastPage();

  public abstract boolean isLoading();
}