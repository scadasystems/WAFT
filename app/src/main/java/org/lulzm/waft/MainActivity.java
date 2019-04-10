package org.lulzm.waft;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RecycleListAdapter.RecyclerViewClickListener {

    private static final int TIME_LIMIT = 1500;
    private static long backPressed;

    Toolbar toolbar;

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference userDatabaseReference;
    public FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            String user_uID = mAuth.getCurrentUser().getUid();

            userDatabaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("users").child(user_uID);
        }

        // 상태표시줄 색상 변경
        View view = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (view != null) {
                // 23 버전 이상일 때 상태바 하얀 색상, 회색 아이콘
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                getWindow().setStatusBarColor(Color.parseColor("#f2f2f2"));
            }
        }else if (Build.VERSION.SDK_INT >= 21) {
            // 21 버전 이상일 때 상태바 검은 색상, 흰색 아이콘
            getWindow().setStatusBarColor(Color.BLACK);
        }

        /*리사이클러뷰 선언 및 화면 설정*/
        RecyclerView recyclerView = findViewById(R.id.recycle_View);
        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        /*cardView 아이템 이름 변경 및 추가*/
        List<CardItem> dataList = new ArrayList<>();
        dataList.add(new CardItem( "MY QR"));
        dataList.add(new CardItem( "안내"));
        dataList.add(new CardItem( "환전소"));
        dataList.add(new CardItem( "채팅방"));
        dataList.add(new CardItem( "대사관 SOS"));
        dataList.add(new CardItem( "SOS"));

        /*리사이클러뷰 어뎁터 연결*/
        RecycleListAdapter adapter = new RecycleListAdapter(dataList);
        recyclerView.setAdapter(adapter);

        adapter.setOnClickListener(this);

        /* firebase */
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            String user_uID = mAuth.getCurrentUser().getUid();

            userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(user_uID);
        }

        /* tool bar */
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // tool bar custom
        SlidingRootNav slidingRootNavBuilder = new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(toolbar)
                .withMenuLayout(R.layout.sliding_root_nav)
                .withDragDistance(200)
                .withRootViewScale(0.6f)
                .inject();

        // findbyid
        TextView tv_name = (TextView) findViewById(R.id.textView10);
        TextView tv_id = (TextView) findViewById(R.id.tv_mbID);
        TextView tv_email = (TextView) findViewById(R.id.tv_mbmail);
        TextView tv_country = (TextView) findViewById(R.id.tv_mbnt);
        TextView tv_job = (TextView) findViewById(R.id.tv_mbjob);
        tv_name.setText("이름 이벤트");
    } // end onCreate

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        //checking logging, if not login redirect to Login ACTIVITY
        if (currentUser == null){
            logOutUser(); // Return to Login activity
        }
        if (currentUser != null){
            userDatabaseReference.child("active_now").setValue("true");
        }
    } // end onStart

    private void logOutUser() {
        String action;
        Intent loginIntent = new Intent(MainActivity.this, LoginSignUpActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }


    /*아이템 클릭 이벤트*/
    @Override
    public void onItemClicked(int position) {
        Toast.makeText(this, "" + position, Toast.LENGTH_SHORT).show();
    }

    public void btnLogout(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view_logout = LayoutInflater.from(MainActivity.this).inflate(R.layout.logout_dialog, null);

        ImageButton imageButton = view_logout.findViewById(R.id.logoutImg);
        imageButton.setImageResource(R.drawable.logout);
        builder.setCancelable(true);

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.setPositiveButton("로그아웃 하기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (currentUser != null) {
                    userDatabaseReference.child("active_now").setValue(ServerValue.TIMESTAMP);
                }
                mAuth.signOut();
                logOutUser();
            }
        });
        builder.setView(view_logout);
        builder.show();
    } // end btnLogout

    // This method is used to detect back button
    @Override
    public void onBackPressed() {
        if(TIME_LIMIT + backPressed > System.currentTimeMillis()){
            super.onBackPressed();
            //Toast.makeText(getApplicationContext(), "Exited", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplicationContext(), "뒤로 버튼을 누르면 어플이 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
        backPressed = System.currentTimeMillis();
    } //End Back button press for exit...

}
