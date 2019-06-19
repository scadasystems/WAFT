package org.lulzm.waft.profileSetting;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.lulzm.waft.R;

import xyz.hasnat.sweettoast.SweetToast;

/*********************************************************
 *   $$\                  $$\             $$\      $$\   
 *   $$ |                 $$ |            $$$\    $$$ |  
 *   $$ |      $$\   $$\  $$ | $$$$$$$$\  $$$$\  $$$$ |  
 *   $$ |      $$ |  $$ | $$ | \____$$  | $$ \$\$$ $$ | 
 *   $$ |      $$ |  $$ | $$ |   $$$$ _/  $$  \$$  $$ |  
 *   $$ |      $$ |  $$ | $$ |  $$  _/    $$ | $  /$$ |  
 *   $$$$$$$$  \$$$$$$$ | $$ | $$$$$$$$\  $$ | \_/ $$ |  
 *   \_______| \______/   \__| \________| \__|     \__|  
 *
 * Project : WAFT                             
 * Created by Android Studio                           
 * Developer : Lulz_M                                    
 * Date : 2019-04-16                                        
 * Time : 오후 9:58                                       
 * GitHub : https://github.com/scadasystems              
 * E-mail : redsmurf@lulzm.org                           
 *********************************************************/
public class StatusUpdateActivity extends AppCompatActivity {

    private static final String TAG = "StatusUpdateActivity";

    private Toolbar mToolbar;
    private EditText status_from_input;
    private ProgressDialog progressDialog;

    private DatabaseReference statusDatabaseReference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // 태마 변경
        SharedPreferences sharedPreferences = getSharedPreferences("change_theme", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("dark_theme", false)) {
            setTheme(R.style.darktheme);
        } else {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_update);

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


        mAuth = FirebaseAuth.getInstance();
        String user_id = mAuth.getCurrentUser().getUid();
        statusDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(user_id);

        status_from_input = findViewById(R.id.input_status);
        progressDialog = new ProgressDialog(this);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        mToolbar.setNavigationOnClickListener(v -> {
            Log.d(TAG, "onClick : navigating back to 'SettingsActivity.class' ");
            finish();
        });

        String previousStatus = getIntent().getExtras().get("ex_status").toString();
        status_from_input.setText(previousStatus);
        status_from_input.setSelection(status_from_input.getText().length());
    } // end onCreate

    // tool bar Status update done- menu button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.update_status_done_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.status_update_done) {
            String new_status = status_from_input.getText().toString();
            changeProfileStatus(new_status);
        }
        return true;
    }

    private void changeProfileStatus(String new_status) {
        if (TextUtils.isEmpty(new_status)) {
            SweetToast.warning(getApplicationContext(), "상태 메세지를 입력하세요.");
        } else {
            progressDialog.setMessage("잠시만 기다려주세요...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);

            statusDatabaseReference.child("user_status").setValue(new_status)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            finish();
                        } else {
                            SweetToast.warning(getApplicationContext(), "에러: 상태 메세지를 업데이트 못했습니다.");
                        }
                    });
        }
    }

    // editText clearFocus
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

}
