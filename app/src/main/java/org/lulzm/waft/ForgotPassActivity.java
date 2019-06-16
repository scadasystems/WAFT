package org.lulzm.waft;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.firebase.auth.FirebaseAuth;
import xyz.hasnat.sweettoast.SweetToast;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class ForgotPassActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private EditText forgotEmail;
    private Button resetPassButton;

    private FirebaseAuth auth;

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
        setContentView(R.layout.activity_forgot_pass);

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

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.reset_password));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        auth = FirebaseAuth.getInstance();

        forgotEmail = findViewById(R.id.forgotEmail);
        resetPassButton = findViewById(R.id.resetPassButton);
        resetPassButton.setOnClickListener(v -> {
            String email = forgotEmail.getText().toString();
            if(TextUtils.isEmpty(email)){
                SweetToast.error(ForgotPassActivity.this, getString(R.string.input_email));
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                SweetToast.error(ForgotPassActivity.this,getString(R.string.correct_email_format));
            } else {
                // send email to reset password
                auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        emailSentSuccessPopUp();

                        // LAUNCH activity after certain time period
                        new Timer().schedule(new TimerTask(){
                            public void run() {
                                ForgotPassActivity.this.runOnUiThread(() -> {
                                    auth.signOut();
                                    Intent mainIntent =  new Intent(ForgotPassActivity.this, LoginSignUpActivity.class);
                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(mainIntent);
                                    finish();
                                    SweetToast.info(ForgotPassActivity.this, getString(R.string.check_email));

                                });
                            }
                        }, 8000);
                    }
                }).addOnFailureListener(e ->
                        SweetToast.error(ForgotPassActivity.this, "Oops! Error: "+e.getMessage())
                );
            }
        });
    }

    private void emailSentSuccessPopUp() {
        // Custom Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPassActivity.this);
        View view = LayoutInflater.from(ForgotPassActivity.this).inflate(R.layout.register_success_popup, null);
        TextView successMessage = view.findViewById(R.id.successMessage);
        successMessage.setText(getString(R.string.success_forgot_password_message));
        builder.setCancelable(true);

        builder.setView(view);
        builder.show();
    }

    // edittext clearfocus
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }
}
