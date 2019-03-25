package org.lulzm.waft;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.hbb20.CountryCodePicker;
import java.util.Iterator;

public class SignupActivity extends AppCompatActivity {
    private android.widget.EditText edtname;
    private android.widget.EditText edtpassword;
    private android.widget.EditText edtre_password;
    private android.widget.EditText edtid;
    private android.widget.EditText edtjob;
    private android.support.v7.widget.AppCompatButton btnsignup;
    private android.widget.TextView tvlogin;
    private CountryCodePicker ccp;

    // FireBase
    private DatabaseReference mSignupReference;
    private ValueEventListener checkRegister = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
            while (child.hasNext()) {
                if (edtid.getText().toString().equals(child.next().getKey())) {
                    Toast.makeText(getApplicationContext(), "이미 사용중인 아이디 입니다. 다른 아이디를 써주세요.", Toast.LENGTH_LONG).show();
                    mSignupReference.removeEventListener(this);
                    return;
                }
            }
            makeNewId();
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    };

    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        // firebase
        mSignupReference = FirebaseDatabase.getInstance().getReference("users");

        this.tvlogin = (TextView) findViewById(R.id.tv_login);
        this.btnsignup = (AppCompatButton) findViewById(R.id.btn_signup);
        this.edtname = (EditText) findViewById(R.id.edt_name);
        this.edtpassword = (EditText) findViewById(R.id.edt_password);
        this.edtre_password = (EditText) findViewById(R.id.edt_repassword);
        this.edtid = (EditText) findViewById(R.id.edt_id);
        this.edtjob = (EditText) findViewById(R.id.edt_job);
        this.ccp = (CountryCodePicker) findViewById(R.id.ccp);
        // 패스워드 길이
        TextInputLayout pw_input_layout = (TextInputLayout) findViewById(R.id.pw_input_layout);
        TextInputLayout repw_input_layout = (TextInputLayout) findViewById(R.id.repw_input_layout);
        pw_input_layout.setCounterEnabled(true);
        pw_input_layout.setCounterMaxLength(15);
        repw_input_layout.setCounterEnabled(true);
        repw_input_layout.setCounterMaxLength(15);

        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSignupReference.addListenerForSingleValueEvent(checkRegister);
                signup();
            }
        });

        tvlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }
    void makeNewId() {
        String name = edtname.getText().toString();
        String id = edtid.getText().toString();
        String password = edtpassword.getText().toString();
        String job = edtjob.getText().toString();
        String country = ccp.getSelectedCountryName();

        mSignupReference.child(id).child("name").setValue(name);
        mSignupReference.child(id).child("id").setValue(id);
        mSignupReference.child(id).child("password").setValue(password);
        mSignupReference.child(id).child("job").setValue(job);
        mSignupReference.child(id).child("country").setValue(country);
    }

    public void signup() {
        if (!validate()) {
            onSignupFailed();
            return;
        }

        btnsignup.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("회원가입 중입니다...");
        progressDialog.show();

        // TODO: Implement your own signup logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    public void onSignupSuccess() {
        btnsignup.setEnabled(true);
        setResult(RESULT_OK, null);
        Intent i = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "회원가입을 실패했습니다.", Toast.LENGTH_LONG).show();
        Intent i = new Intent(getApplicationContext(), SignupActivity.class);
        btnsignup.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = edtname.getText().toString();
        String password = edtpassword.getText().toString();
        String repassword = edtre_password.getText().toString();
        String id = edtid.getText().toString();
        String job = edtjob.getText().toString();

        //name
        if (name.isEmpty() || name.length() < 2) {
            edtname.setError("2글자 이상 입력해 주십시오");
            valid = false;
        } else {
            edtname.setError(null);
        }
        //id
        if (id.isEmpty() || id.length() < 5) {
            edtid.setError("5글자 이상 입력해 주십시오");
            valid = false;
        } else {
            edtid.setError(null);
        }
        //password
        if (password.isEmpty() || password.length() < 8 || password.length() > 12) {
            edtpassword.setError("영숫자 8 ~ 12자 사이");
            valid = false;
        } else {
            edtpassword.setError(null);
        }
        //re-password
        if (repassword.isEmpty() || repassword.length() < 8 || repassword.length() > 12 || !(repassword.equals(password))) {
            edtre_password.setError("비밀번호가 맞지 않습니다");
            valid = false;
        } else {
            edtre_password.setError(null);
        }
        // job
        if (job.isEmpty() || job.length() < 2) {
            edtjob.setError("2글자 이상 입력해 주십시오");
            valid = false;
        } else {
            edtjob.setError(null);
        }

        return valid;
    }
}
