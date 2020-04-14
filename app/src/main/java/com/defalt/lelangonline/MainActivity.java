package com.defalt.lelangonline;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.defalt.lelangonline.data.login.LoginRepository;
import com.defalt.lelangonline.ui.account.AccountFragment;
import com.defalt.lelangonline.ui.auctions.AuctionsByUserActivity;
import com.defalt.lelangonline.ui.auctions.add.AuctionsAddActivity;
import com.defalt.lelangonline.ui.home.HomeFragment;
import com.defalt.lelangonline.ui.items.ItemsByUserActivity;
import com.defalt.lelangonline.ui.items.ItemsFragment;
import com.defalt.lelangonline.ui.items.add.ItemsAddActivity;
import com.defalt.lelangonline.ui.logout.LogoutActivity;
import com.defalt.lelangonline.ui.register.RegisterActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private final FragmentManager FM = getSupportFragmentManager();
    private Fragment homeFragment = new HomeFragment();
    private Fragment itemsFragment = new ItemsFragment();
    //private Fragment favoriteFragment = new FavoriteFragment();
    private Fragment accountFragment = new AccountFragment();
    private Fragment fragmentActive = homeFragment;
    private BottomNavigationView bn;
    private int navActive;

    private static boolean isConnectionError;
    private boolean isLoggedIn = LoginRepository.isLoggedIn();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bn = findViewById(R.id.nav_view);
        FM.beginTransaction().add(R.id.container, homeFragment, "1").commit();
        FM.beginTransaction().add(R.id.container, itemsFragment, "2").hide(itemsFragment).commit();
        //FM.beginTransaction().add(R.id.container, favoriteFragment, "3").hide(favoriteFragment).commit();
        FM.beginTransaction().add(R.id.container, accountFragment, "4").hide(accountFragment).commit();

        bn.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.navigation_home:
                FM.beginTransaction().hide(fragmentActive).show(homeFragment).commit();
                fragmentActive = homeFragment;
                navActive = R.id.navigation_home;
                return true;

            case R.id.navigation_items:
                FM.beginTransaction().hide(fragmentActive).show(itemsFragment).commit();
                fragmentActive = itemsFragment;
                navActive = R.id.navigation_items;
                return true;

//            case R.id.navigation_favorite:
//                FM.beginTransaction().hide(fragmentActive).show(favoriteFragment).commit();
//                fragmentActive = favoriteFragment;
//                navActive = R.id.navigation_favorite;
//                return true;
//
            case R.id.navigation_account:
                if (!isLoggedIn) {
                    Intent intent = new Intent(this, RegisterActivity.class);
                    startActivity(intent);
                    bn.setSelectedItemId(navActive);
                } else {
                    FM.beginTransaction().hide(fragmentActive).show(accountFragment).commit();
                    fragmentActive = accountFragment;
                    navActive = R.id.navigation_account;
                    return true;
                }
        }
        return false;
    }

    public void startAddItemActivity(View view) {
        Intent intent = new Intent(this, ItemsAddActivity.class);
        startActivity(intent);
    }

    public void startItemsByUserActivity(View view) {
        Intent intent = new Intent(this, ItemsByUserActivity.class);
        startActivity(intent);
    }

    public void startAddAuctionActivity(View view) {
        Intent intent = new Intent(this, AuctionsAddActivity.class);
        startActivity(intent);
    }

    public void startAuctionByUserActivity(View view) {
        Intent intent = new Intent(this, AuctionsByUserActivity.class);
        startActivity(intent);
    }

    public void startLogout(View view) {
        AlertDialog.Builder logoutDialog = new AlertDialog.Builder(this);
        logoutDialog.setTitle(R.string.action_sign_out)
                .setMessage(R.string.alert_confirm_sign_out)
                .setNegativeButton(getApplicationContext().getString(R.string.alert_disagree), null)
                .setPositiveButton(getApplicationContext().getString(R.string.alert_agree), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MainActivity.this, LogoutActivity.class);
                startActivity(intent);
                finish();
            }
        }).show();
    }

    public static boolean isConnectionError() {
        return isConnectionError;
    }

    public static void setIsConnectionError(boolean isConnectionError) {
        MainActivity.isConnectionError = isConnectionError;
    }
}
