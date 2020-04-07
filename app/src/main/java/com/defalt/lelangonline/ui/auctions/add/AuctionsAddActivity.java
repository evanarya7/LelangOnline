package com.defalt.lelangonline.ui.auctions.add;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.custom.SimpleCustomValidation;
import com.defalt.lelangonline.R;
import com.defalt.lelangonline.data.auctions.add.AuctionsAddTask;
import com.defalt.lelangonline.data.auctions.add.ItemNamesByUserTask;
import com.defalt.lelangonline.data.login.LoginRepository;
import com.defalt.lelangonline.ui.SharedFunctions;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.sql.Timestamp;
import java.util.List;

import me.abhinay.input.CurrencyEditText;

public class AuctionsAddActivity extends AppCompatActivity implements View.OnClickListener {
    private Activity mActivity;
    private Context mContext;

    private Spinner itemNameSpinner;
    private CurrencyEditText initPriceEditText;
    private CurrencyEditText limitPriceEditText;
    private EditText startDateEditText;
    private EditText startTimeEditText;
    private EditText endDateEditText;
    private EditText endTimeEditText;
    private View overlay;
    private CardView progressBarCard;
    private AwesomeValidation mAwesomeValidation;

    static boolean isLoading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_auction);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ShimmerFrameLayout mShimmerViewContainer = findViewById(R.id.shimmer_view_container);
        mShimmerViewContainer.startShimmer();

        mActivity = AuctionsAddActivity.this;
        mContext = getApplicationContext();

        itemNameSpinner = findViewById(R.id.name);
        TextView itemValueTextView = findViewById(R.id.itemValue);
        AuctionsAddUI mAuctionAddUI = new AuctionsAddUI(mActivity, mShimmerViewContainer, (ScrollView) findViewById(R.id.container), itemNameSpinner, itemValueTextView);
        new ItemNamesByUserTask(mAuctionAddUI).execute(LoginRepository.getLoggedInUser().getToken());

        initPriceEditText = SharedFunctions.setEditTextCurrency((CurrencyEditText) findViewById(R.id.init_price));
        limitPriceEditText = SharedFunctions.setEditTextCurrency((CurrencyEditText) findViewById(R.id.limit_price));
        startDateEditText = findViewById(R.id.start_date);
        startTimeEditText = findViewById(R.id.start_time);
        endDateEditText = findViewById(R.id.end_date);
        endTimeEditText = findViewById(R.id.end_time);
        overlay = findViewById(R.id.overlay);
        progressBarCard = findViewById(R.id.progressCard);

        startDateEditText.setOnClickListener(this);
        startTimeEditText.setOnClickListener(this);
        endDateEditText.setOnClickListener(this);
        endTimeEditText.setOnClickListener(this);

        SimpleCustomValidation validationEmpty = new SimpleCustomValidation() {
            @Override
            public boolean compare(String input) {
                return input.length() > 0;
            }
        };

        SimpleCustomValidation validDate = new SimpleCustomValidation() {
            @Override
            public boolean compare(String s) {
                if ((startDateEditText.getText()).length() > 0 && (startTimeEditText.getText()).length() > 0 && (endDateEditText.getText()).length() > 0 && (endTimeEditText.getText()).length() > 0) {
                    Timestamp startDate = SharedFunctions.parseDate(startDateEditText.getText() + " " + startTimeEditText.getText());
                    Timestamp endDate = SharedFunctions.parseDate(endDateEditText.getText() + " " + endTimeEditText.getText());

                    long timeDiff = endDate.getTime() - startDate.getTime();
                    return timeDiff > 0;
                }

                return false;
            }
        };

        mAwesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        mAwesomeValidation.addValidation(mActivity, R.id.name, validationEmpty, R.string.form_invalid_auction_name);
        mAwesomeValidation.addValidation(mActivity, R.id.init_price, validationEmpty, R.string.form_invalid_auction_init_price);
        mAwesomeValidation.addValidation(mActivity, R.id.limit_price, validationEmpty, R.string.form_invalid_auction_limit_price);
        mAwesomeValidation.addValidation(mActivity, R.id.start_date, validationEmpty, R.string.form_invalid_auction_time_start);
        mAwesomeValidation.addValidation(mActivity, R.id.start_time, validationEmpty, R.string.form_invalid_auction_time_start);
        mAwesomeValidation.addValidation(mActivity, R.id.end_date, validationEmpty, R.string.form_invalid_auction_time_end);
        mAwesomeValidation.addValidation(mActivity, R.id.end_time, validationEmpty, R.string.form_invalid_auction_time_end);
        mAwesomeValidation.addValidation(mActivity, R.id.end_time, validDate, R.string.form_invalid_auction_time_end);
    }

    @Override
    public void onClick(View view) {
        if (view == startDateEditText) {
            SharedFunctions.showDatePicker(mActivity, startDateEditText);
        } else if (view == startTimeEditText) {
            SharedFunctions.showTimePicker(mActivity, startTimeEditText);
        } else if (view == endDateEditText) {
            SharedFunctions.showDatePicker(mActivity, endDateEditText);
        } else if (view == endTimeEditText) {
            SharedFunctions.showTimePicker(mActivity, endTimeEditText);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_auction, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            if (!isLoading) {
                if (mAwesomeValidation.validate()) {
                    uploadToServer();
                }
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void uploadToServer() {
        isLoading = true;
        overlay.setVisibility(View.VISIBLE);
        progressBarCard.setVisibility(View.VISIBLE);

        SharedFunctions.disableSpinner(itemNameSpinner);
        SharedFunctions.disableEditText(initPriceEditText);
        SharedFunctions.disableEditText(limitPriceEditText);
        SharedFunctions.disableEditText(startDateEditText);
        SharedFunctions.disableEditText(startTimeEditText);
        SharedFunctions.disableEditText(endDateEditText);
        SharedFunctions.disableEditText(endTimeEditText);

        String itemID = itemNameSpinner.getSelectedItem().toString().split("\\|")[0].substring(1).replace(" ", "");
        String initPrice = String.valueOf(initPriceEditText.getCleanIntValue());
        String limitPrice = String.valueOf(limitPriceEditText.getCleanIntValue());
        Timestamp auctionStart = SharedFunctions.parseDate(startDateEditText.getText() + " " + startTimeEditText.getText());
        Timestamp auctionEnd = SharedFunctions.parseDate(endDateEditText.getText() + " " + endTimeEditText.getText());

        AuctionsAddUI mAuctionAddUI = new AuctionsAddUI(mActivity, itemNameSpinner, initPriceEditText, limitPriceEditText, startDateEditText, startTimeEditText, endDateEditText, endTimeEditText, overlay, progressBarCard);
        new AuctionsAddTask(mAuctionAddUI).execute(itemID, initPrice, limitPrice, String.valueOf(auctionStart), String.valueOf(auctionEnd));
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

    public static class AuctionsAddUI {
        private ShimmerFrameLayout mShimmerViewContainer;
        private ScrollView mScrollView;
        private final Activity mActivity;

        private Spinner itemNameSpinner;
        private TextView itemValueTextView;
        private CurrencyEditText initPriceEditText;
        private CurrencyEditText limitPriceEditText;
        private EditText startDateEditText;
        private EditText startTimeEditText;
        private EditText endDateEditText;
        private EditText endTimeEditText;
        private View overlay;
        private CardView progressBarCard;

        AuctionsAddUI(Activity mActivity, ShimmerFrameLayout mShimmerViewContainer, ScrollView mScrollView, Spinner itemNameSpinner, TextView itemValueTextView) {
            this.mActivity = mActivity;
            this.mShimmerViewContainer = mShimmerViewContainer;
            this.mScrollView = mScrollView;
            this.itemNameSpinner = itemNameSpinner;
            this.itemValueTextView = itemValueTextView;
        }

        AuctionsAddUI(Activity mActivity, Spinner itemNameSpinner, CurrencyEditText initPriceEditText, CurrencyEditText limitPriceEditText, EditText startDateEditText, EditText startTimeEditText, EditText endDateEditText, EditText endTimeEditText, View overlay, CardView progressBarCard) {
            this.mActivity = mActivity;
            this.itemNameSpinner = itemNameSpinner;
            this.initPriceEditText = initPriceEditText;
            this.limitPriceEditText = limitPriceEditText;
            this.startDateEditText = startDateEditText;
            this.startTimeEditText = startTimeEditText;
            this.endDateEditText = endDateEditText;
            this.endTimeEditText = endTimeEditText;
            this.overlay = overlay;
            this.progressBarCard = progressBarCard;
        }

        public void updateSpinner(List<String> nameList, final List<String> valueList) {
            final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(
                    mActivity, android.R.layout.simple_spinner_item, nameList);

            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            itemNameSpinner.setAdapter(spinnerArrayAdapter);

            itemNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    itemValueTextView.setText(valueList.get(position));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) { }

            });

            updateUI();
        }

        public void showEmptyItems() {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mActivity);
            alertDialog.setTitle(R.string.alert_get_item_names_title)
                    .setMessage(R.string.alert_get_item_names_desc)
                    .setIcon(R.drawable.ic_warning_black_24dp)
                    .setCancelable(false)
                    .setPositiveButton(mActivity.getString(R.string.alert_agree), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mActivity.finish();
                        }
                    })
                    .show();
        }

        public void showConnError() {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mActivity);
            alertDialog.setTitle(R.string.alert_conn_title)
                    .setMessage(R.string.alert_conn_desc)
                    .setIcon(R.drawable.ic_error_black_24dp)
                    .setCancelable(false)
                    .setPositiveButton(mActivity.getString(R.string.alert_agree), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mActivity.finish();
                        }
                    })
                    .show();
        }

        void updateUI() {
            mShimmerViewContainer.stopShimmer();
            mShimmerViewContainer.setVisibility(View.GONE);
            mScrollView.setVisibility(View.VISIBLE);
            isLoading = false;
        }

        public void showConnErrorThenRetry() {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mActivity);
            alertDialog.setTitle(R.string.alert_conn_title)
                    .setMessage(R.string.alert_conn_desc)
                    .setIcon(R.drawable.ic_error_black_24dp)
                    .setCancelable(false)
                    .setPositiveButton(mActivity.getString(R.string.alert_agree), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SharedFunctions.enableSpinner(itemNameSpinner);
                            SharedFunctions.enableEditText(initPriceEditText);
                            SharedFunctions.enableEditText(limitPriceEditText);
                            SharedFunctions.enableEditText(startDateEditText);
                            SharedFunctions.enableEditText(startTimeEditText);
                            SharedFunctions.enableEditText(endDateEditText);
                            SharedFunctions.enableEditText(endTimeEditText);

                            isLoading = false;
                            overlay.setVisibility(View.GONE);
                            progressBarCard.setVisibility(View.GONE);
                        }
                    })
                    .show();
        }

        public void updateUIAfterUpload() {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mActivity);
            alertDialog.setTitle(R.string.alert_post_success_title)
                    .setMessage(R.string.alert_post_success_auction_desc)
                    .setIcon(R.drawable.ic_check_circle_black_24dp)
                    .setCancelable(false)
                    .setPositiveButton(mActivity.getString(R.string.alert_agree), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mActivity.finish();
                        }
                    })
                    .show();
        }
    }
}
