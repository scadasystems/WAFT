package org.lulzm.waft;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.slidingpanelayout.widget.SlidingPaneLayout;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.hbb20.CountryCodePicker;

import org.lulzm.waft.ChatHome.ChatMainActivity;
import org.lulzm.waft.Currency.Main;
import org.lulzm.waft.MainFragment.Fragment1;
import org.lulzm.waft.MainFragment.Fragment5;
import org.lulzm.waft.MainFragment.FragmentQRMain;
import org.lulzm.waft.MainFragment.MainWebview;
import org.lulzm.waft.MainFragment.SharedPref;
import org.lulzm.waft.ProfileSetting.ProfileActivity;

import java.util.Locale;

import okhttp3.OkHttpClient;
import xyz.hasnat.sweettoast.SweetToast;

public class MainActivity extends AppCompatActivity implements Fragment5.OnThemeChangeListener {

    Dialog dialog_sos;
    private static final int TIME_LIMIT = 1500;
    private static long backPressed;
    //menu
    private boolean isTransactionSafe;
    private boolean isTransactionPending;

    private FrameLayout flContent;
    private Fragment fragment = null;
    private Class fragmentClass;
    public static Handler HomeFragmentHandler;
    private SlidingPaneLayout sliding_pane;

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference userDatabaseReference;
    public FirebaseUser currentUser;

    // 툴바
    AppBarLayout appBarLayout;
    SharedPref sharedPref;

    @SuppressLint({"HandlerLeak"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState() == true) {
            setTheme(R.style.darktheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_main);
        dialog_sos = new Dialog(this);

        new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();

        // FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String user_uID = mAuth.getCurrentUser().getUid();
            userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(user_uID);
        }

        // 현재 기본설정 언어값을 넘겨줌
        Locale locale = getResources().getConfiguration().locale;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("language", locale.getLanguage());
        edit.putString("lang", locale.getLanguage());
        edit.apply();

        // 상태표시줄 색상 변경
        View view = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 23 버전 이상일 때 상태바 하얀 색상, 회색 아이콘
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.parseColor("#f2f2f2"));
        } else if (Build.VERSION.SDK_INT >= 21) {
            // 21 버전 이상일 때 상태바 검은 색상, 흰색 아이콘
            getWindow().setStatusBarColor(Color.BLACK);
        }

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
                    // currency_main
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
                            transaction.detach(fragment).attach(fragment).commit();
                            sliding_pane.closePane();
                        } else {
                            isTransactionPending = true;
                        }
                        break;
                    }
                    // profile
                    case 1: {
                        Intent intent_profile = new Intent(MainActivity.this, ProfileActivity.class);
                        startActivity(intent_profile);
                        break;
                    }
                    // setting
                    case 2: {
                        if (isTransactionSafe) {
                            fragmentClass = Fragment5.class;
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
                            sliding_pane.closePane();
                        } else {
                            isTransactionPending = true;
                        }
                        break;
                    }
                    // sos
                    case 3: {
                        dialog_sos.setContentView(R.layout.emergency_popup);
                        dialog_sos.getWindow().setBackgroundDrawable(new ColorDrawable(0));

                        CountryCodePicker country_popup_name = dialog_sos.findViewById(R.id.country_popup_name);
                        ImageButton tv_close = dialog_sos.findViewById(R.id.txtclose);
                        TextView tv_police = dialog_sos.findViewById(R.id.police_number);
                        TextView tv_amb = dialog_sos.findViewById(R.id.ambulance_number);
                        TextView tv_fire = dialog_sos.findViewById(R.id.fire_number);

                        SharedPreferences preferences2 = getSharedPreferences("pref_countryCode", Context.MODE_PRIVATE);
                        country_popup_name.setDefaultCountryUsingNameCode("KR");

                        String pref_countryCode_popUp_set = preferences2.getString("country_code", "");
                        country_popup_name.setCountryForNameCode(pref_countryCode_popUp_set);
                        tv_close.setOnClickListener(v -> dialog_sos.dismiss());
                        dialog_sos.show();
                        break;
                    }
                    // logout
                    case 4: {
                        // todo 로그아웃 다이얼로그에 radius 넣어야함.
                        if (isTransactionSafe) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            View view_logout = LayoutInflater.from(MainActivity.this).inflate(R.layout.logout_dialog, null);

                            ImageButton imageButton = view_logout.findViewById(R.id.logoutImg);
                            imageButton.setImageResource(R.drawable.logout);

                            builder.setCancelable(true);
                            builder.setNegativeButton(getString(R.string.logout_cancel), (dialog, which) -> dialog.cancel());
                            builder.setPositiveButton(getString(R.string.logout_success), (dialog, which) -> {
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
                            sliding_pane.closePane();
                        } else {
                            isTransactionPending = true;
                        }
                        break;
                }
            }
        };
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
        } else {
            SweetToast.info(this, "한번 더 누르면 종료됩니다.");
        }
        backPressed = System.currentTimeMillis();
    } //End Back button press for exit...

    // QR코드
    public void btnqr(View view) {
        if (isTransactionSafe) {
            fragmentClass = FragmentQRMain.class;
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

    // 네비게이션
    public void btnnav(View view) {
        Intent intent_nav = new Intent(this, MapsActivity.class);
        startActivity(intent_nav);
    }

    // 환전소
    public void btnmoney(View view) {
        Intent intent_currency = new Intent(this, Main.class);
        startActivity(intent_currency);
    }

    // 채팅방
    public void btnchatting(View view) {
        Intent intent_chat = new Intent(this, ChatMainActivity.class);
        startActivity(intent_chat);
    }

    // 국가별 기본정보
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

            // webview fragment 로 데이터 보내기.
            Bundle bundle = new Bundle(1);  // 1개의 데이터만
            bundle.putString("webURL", "http://www.0404.go.kr/m/dev/country.do");
            fragment.setArguments(bundle);
        } else {
            isTransactionPending = true;
        }
    }

    // 영사서비스/비자
    public void btnMovePassportInfo(View view) {
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

            Bundle bundle = new Bundle(1);
            bundle.putString("webURL", "http://www.0404.go.kr/m/consulate/consul_apo.jsp");
            fragment.setArguments(bundle);
        } else {
            isTransactionPending = true;
        }
    }

    @Override
    public void onThemeChanged(boolean isDarkMode) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.flContent, new Fragment5());
        transaction.addToBackStack(null);
        transaction.commit();
    }
}