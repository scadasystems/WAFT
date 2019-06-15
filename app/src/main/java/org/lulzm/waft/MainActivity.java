package org.lulzm.waft;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.hbb20.CountryCodePicker;
import okhttp3.OkHttpClient;
import org.lulzm.waft.ChatHome.ChatMainActivity;
import org.lulzm.waft.Currency.Main;
import org.lulzm.waft.MainFragment.MainFragment;
import org.lulzm.waft.MainFragment.SettingFragment;
import org.lulzm.waft.MainFragment.FragmentQRMain;
import org.lulzm.waft.MainFragment.MainWebview;
import org.lulzm.waft.ProfileSetting.ProfileActivity;
import org.lulzm.waft.SosAdapter.ApiService;
import org.lulzm.waft.SosAdapter.Datum;
import org.lulzm.waft.SosAdapter.RetroClient;
import org.lulzm.waft.SosAdapter.SosList;
import retrofit2.Call;
import retrofit2.Response;
import xyz.hasnat.sweettoast.SweetToast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

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
    private ProgressDialog progressDialog;
    /* Sos parsing */
    private ArrayList<Datum> datumList;

    @SuppressLint({"HandlerLeak"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Light/ Dark 테마 값 받아오기
        SharedPreferences sharedPreferences = getSharedPreferences("change_theme", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("dark_theme", false)) {
            setTheme(R.style.darktheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_main);

        // sos 팝업
        dialog_sos = new Dialog(this);

        // progressbar
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage(getString(R.string.sos_loading));
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
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

        //menu
        flContent = findViewById(R.id.flContent);
        sliding_pane = findViewById(R.id.sliding_pane);
        sliding_pane.setSliderFadeColor(getResources().getColor(android.R.color.transparent));
        fragmentClass = MainFragment.class;
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
                            fragmentClass = MainFragment.class;
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
                            fragmentClass = SettingFragment.class;
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

                        // 나라코드
                        CountryCodePicker country_popup_name = dialog_sos.findViewById(R.id.country_popup_name);
                        // 닫기버튼
                        ImageButton tv_close = dialog_sos.findViewById(R.id.txtclose);
                        // 각 전화번호
                        TextView tv_police = dialog_sos.findViewById(R.id.police_number);
                        TextView tv_amb = dialog_sos.findViewById(R.id.ambulance_number);
                        TextView tv_fire = dialog_sos.findViewById(R.id.fire_number);
                        // 전화걸기
                        ImageButton call_police = dialog_sos.findViewById(R.id.call_police);
                        ImageButton call_amb = dialog_sos.findViewById(R.id.call_ambulance);
                        ImageButton call_fire = dialog_sos.findViewById(R.id.call_fire);

                        /* 언어변경 */
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        String language = prefs.getString("language", "");
                        assert language != null;
                        // country code picker 언어 변경
                        if (language.equals("ko") || language.equals("한국어")) {
                            country_popup_name.changeDefaultLanguage(CountryCodePicker.Language.KOREAN);
                        } else if (language.equals("en") || language.equals("English")) {
                            country_popup_name.changeDefaultLanguage(CountryCodePicker.Language.ENGLISH);
                        }
                        SharedPreferences preferences2 = getSharedPreferences("pref_countryCode", Context.MODE_PRIVATE);
                        country_popup_name.setDefaultCountryUsingNameCode("KR");
                        String pref_countryCode_popUp_set = preferences2.getString("country_code", "");
                        country_popup_name.setCountryForNameCode(pref_countryCode_popUp_set);
                        tv_close.setOnClickListener(v -> dialog_sos.dismiss());
                        /* Sos parsing */
                        ApiService api = RetroClient.getApiService();
                        Call<SosList> call = api.getMyJSON();
                        call.clone().enqueue(new retrofit2.Callback<SosList>() {
                            @Override
                            public void onResponse(Call<SosList> call, Response<SosList> response) {

                                progressDialog.show();

                                if (response.isSuccessful()) {
                                    datumList = response.body().getData();

                                    for (Datum datum : datumList) {
                                        if (datum.getCountry().getISOCode().equals(pref_countryCode_popUp_set)) {

                                            String num_police = datum.getPolice().getAll().get(0).trim();
                                            String num_ambulance = datum.getAmbulance().getAll().get(0).trim();
                                            String num_fire = datum.getFire().getAll().get(0).trim();
                                            // 각 전화번호
                                            tv_police.setText(num_police);
                                            tv_amb.setText(num_ambulance);
                                            tv_fire.setText(num_fire);
                                            // 경찰 전화걸기
                                            call_police.setOnClickListener(v12 -> {
                                                Intent intent_police = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + num_police));
                                                startActivity(intent_police);
                                            });
                                            // 응급 전화걸기
                                            call_amb.setOnClickListener(v13 -> {
                                                Intent intent_ambulance = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + num_ambulance));
                                                startActivity(intent_ambulance);
                                            });
                                            // 소방 전화걸기
                                            call_fire.setOnClickListener(v14 -> {
                                                Intent intent_fire = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + num_fire));
                                                startActivity(intent_fire);
                                            });
                                            progressDialog.dismiss();
                                            break;
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<SosList> call, Throwable t) {
                            }
                        });
                        // 팝업 닫기 버튼
                        tv_close.setOnClickListener(v1 -> dialog_sos.dismiss());
                        dialog_sos.show();
                        break;
                    }
                    // logout
                    case 4: {
                        // todo 로그아웃 다이얼로그에 radius 넣어야함.
                        if (isTransactionSafe) {
                            // 로그아웃 다이얼로그 커스텀
                            if (sharedPreferences.getBoolean("dark_theme", false)) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.alertDialog_dark);
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
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.alertDialog);
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
                            }
                        } else {
                            isTransactionPending = true;
                        }
                        break;
                    }
                    default:
                        if (isTransactionSafe) {
                            fragmentClass = MainFragment.class;
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

    /* 메뉴 아이콘 클릭 값 */
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
}