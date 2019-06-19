package org.lulzm.waft.chatHome;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.lulzm.waft.chatAdapter.TabsPagerAdapter;
import org.lulzm.waft.chatFriends.FriendsActivity;
import org.lulzm.waft.chatSearch.SearchActivity;
import org.lulzm.waft.MainActivity;
import org.lulzm.waft.R;

import xyz.hasnat.sweettoast.SweetToast;

public class ChatMainActivity extends AppCompatActivity {

    // for back button
    private static final int TIME_LIMIT = 1500;
    private static long backPressed;

    private Toolbar mToolbar;

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private TabsPagerAdapter mTabsPagerAdapter;

    //Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference userDatabaseReference;
    public FirebaseUser currentUser;

    private ConnectivityReceiver connectivityReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 다크모드 적용
        SharedPreferences sharedPreferences = getSharedPreferences("change_theme", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("dark_theme", false)) {
            setTheme(R.style.darktheme);
        } else {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_main_activity);

        // 상태표시줄 색상 변경
        View view = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 23 버전 이상일 때 상태바 하얀 색상, 회색 아이콘
            if (sharedPreferences.getBoolean("dark_theme", false)) {
                getWindow().setStatusBarColor(Color.BLACK);
            } else {
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                getWindow().setStatusBarColor(Color.parseColor("#f2f2f2"));
            }
        } else if (Build.VERSION.SDK_INT >= 21) {
            // 21 버전 이상일 때 상태바 검은 색상, 흰색 아이콘
            getWindow().setStatusBarColor(Color.BLACK);
        }

        // firebase connect
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            String user_uID = mAuth.getCurrentUser().getUid();

            userDatabaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("users").child(user_uID);
        }


        /**
         * Tabs >> Viewpager for ChatMainActivity
         */
        mViewPager = findViewById(R.id.tabs_pager);
        mTabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager(), this);
        mViewPager.setAdapter(mTabsPagerAdapter);

        mTabLayout = findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);
        //setupTabIcons();

        /**
         * Set Home Activity Toolbar Name
         */
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
    } // ending onCreate

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        //checking logging, if not login redirect to Login ACTIVITY
        if (currentUser != null){
            userDatabaseReference.child("active_now").setValue("true");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Register Connectivity Broadcast receiver
        connectivityReceiver = new ConnectivityReceiver();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectivityReceiver, intentFilter);
    }

    // tool bar action menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.chat_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        // search friends
        if (item.getItemId() == R.id.menu_search){
            Intent intent =  new Intent(ChatMainActivity.this, SearchActivity.class);
            startActivity(intent);
        }
        // friends list
        if (item.getItemId() == R.id.all_friends){
            Intent intent =  new Intent(ChatMainActivity.this, FriendsActivity.class);
            startActivity(intent);
        }
        return true;
    }

    // Broadcast receiver for network checking
    public class ConnectivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            @SuppressLint("MissingPermission")
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()){

            } else {
                @SuppressLint("WrongConstant") Snackbar snackbar = Snackbar
                        .make(mViewPager, getString(R.string.internet_disconnect), Snackbar.LENGTH_LONG)
                        .setAction("Go settings", view -> {
                            Intent intent1 =new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                            startActivity(intent1);
                        });
                // Changing action button text color
                snackbar.setActionTextColor(Color.BLACK);
                // Changing message text color
                View view = snackbar.getView();
                view.setBackgroundColor(ContextCompat.getColor(ChatMainActivity.this, R.color.colorPrimary));
                TextView textView = view.findViewById(com.google.android.material.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                snackbar.show();
            }
        }
    }


    // back button event
    @Override
    public void onBackPressed() {
        if(TIME_LIMIT + backPressed > System.currentTimeMillis()){
            Intent intent_home = new Intent(ChatMainActivity.this, MainActivity.class);
            startActivity(intent_home);
            finish();
            overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
        }
        else {
            SweetToast.info(getApplicationContext(), getString(R.string.press_back_main));
        }
        backPressed = System.currentTimeMillis();
    } //End Back button


}