package org.lulzm.waft.ChatHome;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
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
import com.google.firebase.database.ServerValue;
import org.lulzm.waft.ChatAdapter.TabsPagerAdapter;
import org.lulzm.waft.ChatFriends.FriendsActivity;
import org.lulzm.waft.ChatSearch.SearchActivity;
import org.lulzm.waft.MainActivity;
import org.lulzm.waft.R;

public class ChatMainActivity extends AppCompatActivity {

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_main_activity);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            String user_uID = mAuth.getCurrentUser().getUid();

            userDatabaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("users").child(user_uID);
        }


        /**
         * Tabs >> Viewpager for MainActivity
         */
        mViewPager = findViewById(R.id.tabs_pager);
        mTabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mTabsPagerAdapter);

        mTabLayout = findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);
        //setupTabIcons();

        /**
         * Set Home Activity Toolbar Name
         */
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        //getSupportActionBar().setTitle("uMe");

    } // ending onCreate

    private void setupTabIcons() {
        //mTabLayout.getTabAt(0).setText("CHATS");
        //mTabLayout.getTabAt(1).setText("REQUESTS");
        //mTabLayout.getTabAt(2).setText("FRIENDS");
    }

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

    @Override
    protected void onStop() {
        super.onStop();
        // Unregister Connectivity Broadcast receiver
        //unregisterReceiver(connectivityReceiver);

        // google kore aro jana lagbe, bug aache ekhane
//        if (currentUser != null){
//            userDatabaseReference.child("active_now").setValue(ServerValue.TIMESTAMP);
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // from onStop
        if (currentUser != null){
            userDatabaseReference.child("active_now").setValue(ServerValue.TIMESTAMP);
        }
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
        if (item.getItemId() == R.id.menu_search){
            Intent intent =  new Intent(ChatMainActivity.this, SearchActivity.class);
            startActivity(intent);
        }
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
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()){

            } else {
                Snackbar snackbar = Snackbar
                        .make(mViewPager, "No internet connection! ", Snackbar.LENGTH_LONG)
                        .setAction("Go settings", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent=new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                                startActivity(intent);
                            }
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


    // This method is used to detect back button
    @Override
    public void onBackPressed() {
        if(TIME_LIMIT + backPressed > System.currentTimeMillis()){
            Intent intent_home = new Intent(ChatMainActivity.this, MainActivity.class);
            startActivity(intent_home);
            finish();
            overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
        }
        else {
            Toast.makeText(getApplicationContext(), getString(R.string.press_back_main), Toast.LENGTH_SHORT).show();
        }
        backPressed = System.currentTimeMillis();
    } //End Back button press for exit...


}