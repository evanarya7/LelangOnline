package com.defalt.lelangonline.ui.details;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.defalt.lelangonline.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

public class DetailsActivity extends AppCompatActivity {

    private static boolean isConnectionError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        String auctionID = intent.getStringExtra("TAG_EXTRA");

        TabAdapter adapter = new TabAdapter(getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tabs);
        FloatingActionButton fab = findViewById(R.id.fab);

        Bundle bundle = new Bundle();
        bundle.putString("TAG_AUID", auctionID);
        DetailsFragment detailsFragment = new DetailsFragment();
        HistoryFragment historyFragment = new HistoryFragment();
        detailsFragment.setArguments(bundle);
        historyFragment.setArguments(bundle);

        adapter.addFragment(detailsFragment, "Detail Barang");
        adapter.addFragment(historyFragment, "Riwayat Tawaran");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "This for bidding system", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public static boolean isIsConnectionError() {
        return isConnectionError;
    }

    public static void setIsConnectionError(boolean isConnectionError) {
        DetailsActivity.isConnectionError = isConnectionError;
    }
}
