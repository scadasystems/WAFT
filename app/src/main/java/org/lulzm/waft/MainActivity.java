package org.lulzm.waft;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;
import de.hdodenhof.circleimageview.CircleImageView;
import org.lulzm.waft.ChatHome.ChatMainActivity;
import org.lulzm.waft.ProfileSetting.ProfileActivity;
import xyz.hasnat.sweettoast.SweetToast;

public class MainActivity extends AppCompatActivity {

    private static final int TIME_LIMIT = 1500;
    private static long backPressed;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    Toolbar toolbar;

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference userDatabaseReference;
    public FirebaseUser currentUser;
    private StorageReference mProfileImgStorageRef;
    private StorageReference thumb_image_ref;

    // for glide exception
    RequestManager mGlideRequestManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        // Mainboard instantiate
        Mainboard mainboard = new Mainboard();
        fragmentTransaction.replace(R.id.container, mainboard);
        fragmentTransaction.commit();

        // glide
        mGlideRequestManager = Glide.with(getApplicationContext());


        // FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String user_uID = mAuth.getCurrentUser().getUid();
            userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(user_uID);
            mProfileImgStorageRef = FirebaseStorage.getInstance().getReference().child("profile_image");
        }

        // 상태표시줄 색상 변경
        View view = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (view != null) {
                // 23 버전 이상일 때 상태바 하얀 색상, 회색 아이콘
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                getWindow().setStatusBarColor(Color.parseColor("#f2f2f2"));
            }
        } else if (Build.VERSION.SDK_INT >= 21) {
            // 21 버전 이상일 때 상태바 검은 색상, 흰색 아이콘
            getWindow().setStatusBarColor(Color.BLACK);
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
                .withRootViewScale(0.5f)
                .inject();

        // findbyid
        CircleImageView user_image = findViewById(R.id.user_image);
        TextView tv_name = findViewById(R.id.tv_nickName);
        TextView tv_status = findViewById(R.id.tv_status);

        userDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String image = dataSnapshot.child("user_image").getValue().toString();
                String nickName = dataSnapshot.child("user_name").getValue().toString();
                String status = dataSnapshot.child("user_status").getValue().toString();

                view.post(() -> mGlideRequestManager
                        .load(image)
                        .error(R.drawable.default_profile_image)
                        .into(user_image));
                tv_name.setText(nickName);
                tv_status.setText(status);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    } // end onCreate

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        //checking logging, if not login redirect to Login ACTIVITY
        if (currentUser == null) {
            logOutUser(); // Return to Login activity
        }
        if (currentUser != null) {
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


    public void btnLogout(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view_logout = LayoutInflater.from(MainActivity.this).inflate(R.layout.logout_dialog, null);

        ImageButton imageButton = view_logout.findViewById(R.id.logoutImg);
        imageButton.setImageResource(R.drawable.logout);
        builder.setCancelable(true);

        builder.setNegativeButton("취소", (dialog, which) -> dialog.cancel());

        builder.setPositiveButton("로그아웃 하기", (dialog, which) -> {
            if (currentUser != null) {
                userDatabaseReference.child("active_now").setValue(ServerValue.TIMESTAMP);
            }
            mAuth.signOut();
            logOutUser();
        });
        builder.setView(view_logout);
        builder.show();
    } // end btnLogout

    // This method is used to detect back button
    @Override
    public void onBackPressed() {
        if (TIME_LIMIT + backPressed > System.currentTimeMillis()) {
            super.onBackPressed();
            //Toast.makeText(getApplicationContext(), "Exited", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "뒤로 버튼을 누르면 어플이 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
        backPressed = System.currentTimeMillis();
    } //End Back button press for exit...

    public void btnProfile(View view) {
        Intent intent_profile = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(intent_profile);
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }

    public void move_QR(View view) {
        SweetToast.success(MainActivity.this, "QR 테스트");
    }

    public void move_CHAT(View view) {
        Intent intent_chat = new Intent(MainActivity.this, ChatMainActivity.class);
        startActivity(intent_chat);
        finish();
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }
}
