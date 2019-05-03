package org.lulzm.waft;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.slidingpanelayout.widget.SlidingPaneLayout;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import okhttp3.OkHttpClient;
import org.lulzm.waft.ChatHome.ChatMainActivity;
import org.lulzm.waft.MainFragment.*;

public class MainActivity extends AppCompatActivity {

    private static final int TIME_LIMIT = 1500;
    private static long backPressed;
    //menu
    private boolean isTransactionSafe;
    private boolean isTransactionPending;
    FrameLayout flContent;
    Fragment fragment = null;
    Class fragmentClass;
    public static Handler HomeFragmentHandler;
    SlidingPaneLayout sliding_pane;

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference userDatabaseReference;
    public FirebaseUser currentUser;
    private StorageReference mProfileImgStorageRef;
    private StorageReference thumb_image_ref;

    // webview
    private WebView mWebView;
    private String safeInfoURL = "http://www.0404.go.kr/m/dev/main.do";
    private ConstraintLayout visible_main;

    @SuppressLint({"HandlerLeak"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_main);

        new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();

        //menu
        flContent = findViewById(R.id.flContent);
        sliding_pane = findViewById(R.id.sliding_pane);
        sliding_pane.setSliderFadeColor(getResources().getColor(android.R.color.transparent));
        fragmentClass = Fragment1.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (MenuFragment.fragmentMenuBinding != null) {
            MenuFragment.fragmentMenuBinding.lilPost.performClick();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
        transaction.replace(R.id.flContent, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

        //menu 아이콘 클릭시 넘어가는 화면 링크
        HomeFragmentHandler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0: {

                        if (isTransactionSafe) {
                            fragmentClass = Fragment1.class;
                            try {
                                fragment = (Fragment) fragmentClass.newInstance();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction transaction = fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
                            transaction.replace(R.id.flContent, fragment);
                            transaction.addToBackStack(null);
                            transaction.commit();
                        } else {
                            isTransactionPending = true;
                        }
                        break;
                    }
                    case 1: {

                        if (isTransactionSafe) {
                            fragmentClass = Fragment2.class;
                            try {
                                fragment = (Fragment) fragmentClass.newInstance();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction transaction = fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
                            transaction.replace(R.id.flContent, fragment);
                            transaction.addToBackStack(null);
                            transaction.commit();
                        } else {
                            isTransactionPending = true;
                        }
                        break;
                    }
                    case 2: {
                        Intent intent_chat = new Intent(MainActivity.this, MapsActivity.class);
                        startActivity(intent_chat);
//                        if (isTransactionSafe) {
//                            fragmentClass = Fragment3.class;
//                            try {
//                                fragment = (Fragment) fragmentClass.newInstance();
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                            FragmentManager fragmentManager = getSupportFragmentManager();
//                            FragmentTransaction transaction = fragmentManager.beginTransaction();
//                            transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
//                            transaction.replace(R.id.flContent, fragment);
//                            transaction.addToBackStack(null);
//                            transaction.commit();
//                        } else {
//                            isTransactionPending = true;
//                        }
                        break;
                    }
                    case 3: {
                        if (isTransactionSafe) {
                            fragmentClass = Fragment4.class;
                            try {
                                fragment = (Fragment) fragmentClass.newInstance();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction transaction = fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
                            transaction.replace(R.id.flContent, fragment);
                            transaction.addToBackStack(null);
                            transaction.commit();
                        } else {
                            isTransactionPending = true;
                        }
                        break;
                    }
                    // chat
                    case 4: {
                        Intent intent_chat = new Intent(MainActivity.this, ChatMainActivity.class);
                        startActivity(intent_chat);
//                        if (isTransactionSafe) {
//                            fragmentClass = Fragment5.class;
//                            try {
//                                fragment = (Fragment) fragmentClass.newInstance();
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                            FragmentManager fragmentManager = getSupportFragmentManager();
//                            FragmentTransaction transaction = fragmentManager.beginTransaction();
//                            transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
//                            transaction.replace(R.id.flContent, fragment);
//                            transaction.addToBackStack(null);
//                            transaction.commit();
//                        } else {
//                            isTransactionPending = true;
//                        }
                        break;
                    }
                    case 5: {
                        if (isTransactionSafe) {
                            fragmentClass = Fragment6.class;
                            try {
                                fragment = (Fragment) fragmentClass.newInstance();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction transaction = fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
                            transaction.replace(R.id.flContent, fragment);
                            transaction.addToBackStack(null);
                            transaction.commit();
                        } else {
                            isTransactionPending = true;
                        }
                        break;
                    }
                    case 6: {
                        if (isTransactionSafe) {
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

                        } else {
                            isTransactionPending = true;
                        }
                        break;
                    }
                    case 10:

                    default:
                        if (isTransactionSafe) {
                            fragmentClass = Fragment1.class;
                            try {
                                fragment = (Fragment) fragmentClass.newInstance();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction transaction = fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
                            transaction.replace(R.id.flContent, fragment);
                            transaction.addToBackStack(null);
                            transaction.commit();
                        } else {
                            isTransactionPending = true;
                        }
                        break;
                }
            }
        };

        // FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String user_uID = mAuth.getCurrentUser().getUid();
            userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(user_uID);
            mProfileImgStorageRef = FirebaseStorage.getInstance().getReference().child("profile_image");
            thumb_image_ref = FirebaseStorage.getInstance().getReference().child("thumb_image");
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


    public void onPostResume() {
        super.onPostResume();
        isTransactionSafe = true;
    }


    public void onPause() {
        super.onPause();
        isTransactionSafe = false;
    }

    // This method is used to detect back button
    @Override
    public void onBackPressed() {
        if (TIME_LIMIT + backPressed > System.currentTimeMillis()) {
            super.onBackPressed();
            //Toast.makeText(getApplicationContext(), "Exited", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Back 버튼을 한번 더 누르면 어플이 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
        backPressed = System.currentTimeMillis();
    } //End Back button press for exit...

    // 공지사항 1
    public void btnMoveSafeInfo(View view) {
        if (isTransactionSafe) {
            fragmentClass = MainWebview.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
            transaction.replace(R.id.flContent, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else {
            isTransactionPending = true;
        }
    }

//    public void btnProfile(View view) {
//        Intent intent_profile = new Intent(MainActivity.this, ProfileActivity.class);
//        startActivity(intent_profile);
//        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
//    }

}
