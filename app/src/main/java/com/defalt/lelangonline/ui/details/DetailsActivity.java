package com.defalt.lelangonline.ui.details;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.defalt.lelangonline.R;
import com.defalt.lelangonline.data.details.BidTask;
import com.defalt.lelangonline.data.details.DetailsTask;
import com.defalt.lelangonline.data.login.LoginRepository;
import com.defalt.lelangonline.ui.SharedFunctions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;

import me.abhinay.input.CurrencyEditText;

public class DetailsActivity extends AppCompatActivity {

    private static boolean isDetailsLoading = true;
    private static boolean isHistoryLoading = true;
    private static boolean isConnectionError;
    private static Double priceStart;
    private static Double priceLimit;
    private static Double highestBid;
    private static Double myBid;
    private SwipeRefreshLayout swipeRefresh;
    private TextView lastBidAmount;
    private CurrencyEditText bidAmountEditText;
    private Button bidButton;
    private String auctionID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        auctionID = intent.getStringExtra("TAG_EXTRA");

        TabAdapter adapter = new TabAdapter(getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tabs);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float v, int i1) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                enableDisableSwipeRefresh(state == ViewPager.SCROLL_STATE_IDLE);
            }
        });

        Bundle bundle = new Bundle();
        bundle.putString("TAG_AUID", auctionID);
        final DetailsFragment detailsFragment = new DetailsFragment();
        final HistoryFragment historyFragment = new HistoryFragment();
        detailsFragment.setArguments(bundle);
        historyFragment.setArguments(bundle);

        adapter.addFragment(detailsFragment, "Detail Barang");
        adapter.addFragment(historyFragment, "Riwayat Tawaran");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                prepareData();
                detailsFragment.onRefresh();
                historyFragment.onRefresh();
            }
        });

        lastBidAmount = findViewById(R.id.lastBidAmount);
        bidAmountEditText = SharedFunctions.setEditTextCurrency((CurrencyEditText) findViewById(R.id.bidAmount));
        bidButton = findViewById(R.id.bidButton);
        bidButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBid();
            }
        });

        LinearLayout layoutBottomSheet = findViewById(R.id.bottom_sheet);
        BottomSheetBehavior sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        final ImageView sheetCaret = findViewById(R.id.sheet_arrow);

        sheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        sheetCaret.setImageDrawable(getDrawable(R.drawable.ic_expand_more_black_24dp));
                        break;
                    }
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        sheetCaret.setImageDrawable(getDrawable(R.drawable.ic_expand_less_black_24dp));
                        break;
                    }
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

        prepareData();
    }

    private void prepareData() {
        new DetailsTask(new DetailsUI(lastBidAmount, bidAmountEditText, bidButton, this)).execute(auctionID, LoginRepository.getLoggedInUser().getToken());
    }

    private void checkBid() {
        Double bidAmount = bidAmountEditText.getCleanDoubleValue();
        if (bidAmount >= priceStart) {
            if (bidAmount > highestBid) {
                if (bidAmount <= priceLimit) {
                    new BidTask(new DetailsUI(this)).execute(auctionID, String.valueOf(bidAmount), LoginRepository.getLoggedInUser().getToken());
                } else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                    alertDialog.setTitle(R.string.alert_bid_invalid_title)
                            .setMessage(R.string.alert_bid_invalid_2_desc)
                            .setIcon(R.drawable.ic_error_black_24dp)
                            .setPositiveButton(R.string.alert_agree, null)
                            .show();
                }
            } else {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle(R.string.alert_bid_invalid_title)
                        .setMessage(R.string.alert_bid_invalid_1_desc)
                        .setIcon(R.drawable.ic_error_black_24dp)
                        .setPositiveButton(R.string.alert_agree, null)
                        .show();
            }
        } else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle(R.string.alert_bid_invalid_title)
                    .setMessage(R.string.alert_bid_invalid_0_desc)
                    .setIcon(R.drawable.ic_error_black_24dp)
                    .setPositiveButton(R.string.alert_agree, null)
                    .show();
        }

    }

    public static boolean isIsDetailsLoading() {
        return isDetailsLoading;
    }

    public static void setIsDetailsLoading(boolean isDetailsLoading) {
        DetailsActivity.isDetailsLoading = isDetailsLoading;
    }

    public static boolean isIsHistoryLoading() {
        return isHistoryLoading;
    }

    public static void setIsHistoryLoading(boolean isHistoryLoading) {
        DetailsActivity.isHistoryLoading = isHistoryLoading;
    }

    public static boolean isIsConnectionError() {
        return isConnectionError;
    }

    public static void setIsConnectionError(boolean isConnectionError) {
        DetailsActivity.isConnectionError = isConnectionError;
    }

    public static Double getPriceStart() {
        return priceStart;
    }

    public static void setPriceStart(Double priceStart) {
        DetailsActivity.priceStart = priceStart;
    }

    public static Double getPriceLimit() {
        return priceLimit;
    }

    public static void setPriceLimit(Double priceLimit) {
        DetailsActivity.priceLimit = priceLimit;
    }

    public static Double getHighestBid() {
        return highestBid;
    }

    public static void setHighestBid(Double highestBid) {
        DetailsActivity.highestBid = highestBid;
    }

    public static Double getMyBid() {
        return myBid;
    }

    public static void setMyBid(Double myBid) {
        DetailsActivity.myBid = myBid;
    }

    private void enableDisableSwipeRefresh(boolean enable) {
        if (swipeRefresh != null) {
            swipeRefresh.setEnabled(enable);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    public static class DetailsUI {
        private TextView lastBidAmount;
        private CurrencyEditText bidAmountEditText;
        private Button bidButton;
        private Activity mActivity;

        DetailsUI(TextView lastBidAmount, CurrencyEditText bidAmountEditText, Button bidButton, Activity mActivity) {
            this.lastBidAmount = lastBidAmount;
            this.bidAmountEditText = bidAmountEditText;
            this.bidButton = bidButton;
            this.mActivity = mActivity;
        }

        DetailsUI(Activity mActivity) {
            this.mActivity = mActivity;
        }

        public void unlockBid() {
            bidAmountEditText.setEnabled(true);
            bidAmountEditText.setHint(R.string.auction_bid_amount_hint);
            bidAmountEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, mActivity.getDrawable(R.drawable.ic_gavel_black_24dp), null);
            bidButton.setEnabled(true);
        }

        public void lockBid() {
            bidAmountEditText.setEnabled(false);
            bidAmountEditText.setHint(R.string.auction_bid_amount_hint_locked);
            bidAmountEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, mActivity.getDrawable(R.drawable.ic_lock_black_24dp), null);
            bidButton.setEnabled(false);
        }

        public void lockOwner() {
            bidAmountEditText.setEnabled(false);
            bidAmountEditText.setHint(R.string.auction_bid_amount_hint_owner);
            bidAmountEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, mActivity.getDrawable(R.drawable.ic_lock_black_24dp), null);
            bidButton.setEnabled(false);
        }

        public void updateUI(Double myBid) {
            lastBidAmount.setText(SharedFunctions.formatRupiah(myBid));
        }

        public void showSuccess() {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mActivity);
            alertDialog.setTitle(R.string.alert_post_success_title)
                    .setMessage(R.string.alert_post_success_bid_desc)
                    .setIcon(R.drawable.ic_check_circle_black_24dp)
                    .setCancelable(false)
                    .setPositiveButton(mActivity.getString(R.string.alert_agree), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mActivity.recreate();
                        }
                    }).show();
        }

        public void showError() {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mActivity);
            alertDialog.setTitle(R.string.alert_conn_title)
                    .setMessage(R.string.alert_conn_desc)
                    .setIcon(R.drawable.ic_error_black_24dp)
                    .setPositiveButton(mActivity.getString(R.string.alert_agree), null)
                    .show();
        }
    }
}
