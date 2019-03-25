package org.lulzm.waft;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.*;

import java.util.Iterator;

public class LoginActivity extends AppCompatActivity {
    private static final int REQUEST_SIGNUP = 0;

    private android.widget.EditText edtid;
    private android.widget.EditText edtpassword;
    private android.support.v7.widget.AppCompatButton btnlogin;
    private android.widget.TextView tvsignup;

    // firebase
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // firebase check
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        this.tvsignup = (TextView) findViewById(R.id.tv_signup);
        this.btnlogin = (AppCompatButton) findViewById(R.id.btn_login);
        this.edtpassword = (EditText) findViewById(R.id.edt_password);
        this.edtid = (EditText) findViewById(R.id.edt_id);
        // 패스워드 카운터
        TextInputLayout pw_inputLayout = (TextInputLayout) findViewById(R.id.pw_input_layout);
        pw_inputLayout.setCounterEnabled(true);
        pw_inputLayout.setCounterMaxLength(15);

//        btnlogin.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                login();
//            }
//        });

        tvsignup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
        // firebase login
        databaseReference.child(edtid.getText().toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    final String id = userSnapshot.child("id").getValue(String.class);
                    final String pw = userSnapshot.child("password").getValue(String.class);

                    btnlogin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (edtid.getText().toString().equals(id) && edtpassword.getText().toString().equals(pw)) {
                                Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_SHORT).show();
                                final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                                        R.style.AppTheme_Dark_Dialog);
                                progressDialog.setIndeterminate(true);
                                progressDialog.setMessage("로그인 중입니다...");
                                progressDialog.show();

                                String id = edtid.getText().toString();
                                String password = edtpassword.getText().toString();

                                new android.os.Handler().postDelayed(
                                        new Runnable() {
                                            public void run() {
                                                // On complete call either onLoginSuccess or onLoginFailed
                                                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                                startActivity(i);
                                                finish();
                                                // onLoginFailed();
                                                progressDialog.dismiss();
                                            }
                                        }, 3000);
                            } else {
                                Toast.makeText(getApplicationContext(), "실패!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void login() {

        if (!validate()) {
//            onLoginFailed();
            return;
        }

        btnlogin.setEnabled(false);

//        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
//                R.style.AppTheme_Dark_Dialog);
//        progressDialog.setIndeterminate(true);
//        progressDialog.setMessage("로그인 중입니다...");
//        progressDialog.show();
//
//        String id = edtid.getText().toString();
//        String password = edtpassword.getText().toString();
//
//        new android.os.Handler().postDelayed(
//                new Runnable() {
//                    public void run() {
//                        // On complete call either onLoginSuccess or onLoginFailed
//                        onLoginSuccess();
//                        // onLoginFailed();
//                        progressDialog.dismiss();
//                    }
//                }, 3000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

//    public void onLoginSuccess() {
//        btnlogin.setEnabled(true);
//        finish();
//    }

//    public void onLoginFailed() {
//        Toast.makeText(getBaseContext(), "로그인을 실패했습니다.", Toast.LENGTH_LONG).show();
//
//        btnlogin.setEnabled(true);
//    }

    public boolean validate() {
        boolean valid = true;

        String id = edtid.getText().toString();
        String password = edtpassword.getText().toString();

        if (id.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(id).matches()) {
            edtid.setError("유효한 ID 주소를 입력하세요.");
            valid = false;
        } else {
            edtid.setError(null);
        }

        if (password.isEmpty() || password.length() < 8 || password.length() > 15) {
            edtpassword.setError("패스워드는 8 ~ 15자 사이로 입력하세요.");
            valid = false;
        } else {
            edtpassword.setError(null);
        }

        return valid;
    }
}