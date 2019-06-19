package org.lulzm.waft;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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
import org.lulzm.waft.chatHome.ChatMainActivity;
import org.lulzm.waft.currency.Main;
import org.lulzm.waft.mainFragment.Fragment1;
import org.lulzm.waft.mainFragment.Fragment5;
import org.lulzm.waft.mainFragment.FragmentQRMain;
import org.lulzm.waft.mainFragment.MainWebview;
import org.lulzm.waft.profileSetting.ProfileActivity;
import org.lulzm.waft.sosAdapter.ApiService;
import org.lulzm.waft.sosAdapter.Datum;
import org.lulzm.waft.sosAdapter.RetroClient;
import org.lulzm.waft.sosAdapter.SosList;
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
        // 다크모드 적용
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
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String language = prefs.getString("language", "");
        Configuration config = new Configuration();
        config.setLocale(Locale.forLanguageTag(language));
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
//        Locale locale = getResources().getConfiguration().locale;
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        SharedPreferences.Editor edit = prefs.edit();
//        edit.putString("language", locale.getLanguage());
//        edit.putString("lang", locale.getLanguage());
//        edit.apply();

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
        fragmentClass = Fragment1.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (MenuFragment.Companion.getFragmentMenuBinding() != null) {
            MenuFragment.Companion.getFragmentMenuBinding().lilPost.performClick();
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
                        ApiService api = RetroClient.INSTANCE.getApiService();
                        Call<SosList> call = api.getMyJSON();

                        call.clone().enqueue(new retrofit2.Callback<SosList>() {
                            @Override
                            public void onResponse(Call<SosList> call, Response<SosList> response) {

                                progressDialog.show();

                                if (response.isSuccessful()) {
                                    datumList = response.body().getData();

                                    for (Datum datum : datumList) {
                                        if (datum.getCountry().getIsoCode().equals(pref_countryCode_popUp_set)) {

                                            String num_police = datum.getPolice().getAll().get(0);
                                            String num_ambulance = datum.getAmbulance().getAll().get(0);
                                            String num_fire = datum.getFire().getAll().get(0);
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
            if (fragmentClass == Fragment1.class) {
                finish();
            } else if (fragmentClass == MainWebview.class) {
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
            } else if (fragmentClass == FragmentQRMain.class) {
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
                super.onBackPressed();
            }
        } else {
            SweetToast.info(this, getString(R.string.on_more_time_backpress));
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
        // 다크모드 가져오기
        SharedPreferences sharedPreferences = getSharedPreferences("change_theme", MODE_PRIVATE);
        /* 언어변경 */
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String language = prefs.getString("language", "");
        assert language != null;

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

            Bundle bundle = new Bundle(1);  // 1개의 데이터만
            // 언어변경시 한국=외교부 해외안전여행, 미국=해외안전여행
            if (language.equals("ko") || language.equals("한국어")) {
                if (sharedPreferences.getBoolean("dark_theme", false)) {
                    bundle.putString("webURL", "http://www.0404.go.kr/m/dev/country.do");
                } else {
                    bundle.putString("webURL", "http://www.0404.go.kr/m/dev/country.do");
                }
            } else if (language.equals("en") || language.equals("English")) {
                if (sharedPreferences.getBoolean("dark_theme", false)) {
                    bundle.putString("webURL", "https://travel.state.gov/content/travel/en/traveladvisories/traveladvisories.html/");
                } else {
                    Configuration config = new Configuration();
                    config.setLocale(Locale.forLanguageTag(language));
                    getResources().updateConfiguration(config, getResources().getDisplayMetrics());
                    bundle.putString("webURL", "https://travel.state.gov/content/travel/en/traveladvisories/traveladvisories.html/");
                }
            }
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