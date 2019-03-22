package org.lulzm.waft;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    private android.widget.EditText edtname;
    private android.widget.EditText edtpw;
    private android.widget.EditText edtrepw;
    private android.widget.EditText edtemail;
    private android.widget.EditText edtjop;
    private android.widget.EditText edtaddress;
    private android.support.v7.widget.AppCompatButton btnsignup;
    private android.widget.TextView tvlogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        this.tvlogin = (TextView) findViewById(R.id.tv_login);
        this.btnsignup = (AppCompatButton) findViewById(R.id.btn_signup);
        this.edtname = (EditText) findViewById(R.id.edt_name);
        this.edtpw = (EditText) findViewById(R.id.edt_pw);
        this.edtrepw = (EditText) findViewById(R.id.edt_repw);
        this.edtemail = (EditText) findViewById(R.id.edt_email);
        this.edtjop = (EditText) findViewById(R.id.edt_jop);
        this.edtaddress = (EditText) findViewById(R.id.edt_address);

        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        btnsignup.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String name = edtname.getText().toString();
        String pw = edtpw.getText().toString();
        String repw = edtrepw.getText().toString();
        String email = edtemail.getText().toString();
        String jop = edtjop.getText().toString();
        String country = edtaddress.getText().toString();

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
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        btnsignup.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = edtname.getText().toString();
        String pw = edtpw.getText().toString();
        String repw = edtrepw.getText().toString();
        String email = edtemail.getText().toString();
        String jop = edtjop.getText().toString();
        String address = edtaddress.getText().toString();
        //name
        if (name.isEmpty() || name.length() < 2) {
            edtname.setError("2글자 이상 입력해 주십시오");
            valid = false;
        } else {
            edtname.setError(null);
        }
        //pw
        if (pw.isEmpty() || pw.length() < 8 || pw.length() > 10) {
            edtpw.setError("영숫자 8 ~ 10자 사이");
            valid = false;
        } else {
            edtpw.setError(null);
        }
        //repw
        if (repw.isEmpty() || repw.length() < 8 || repw.length() > 10 || !(repw.equals(jop))) {
            edtrepw.setError("비밀번호가 맞지 않습니다");
            valid = false;
        } else {
            edtrepw.setError(null);
        }
        //email
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtemail.setError("올바른 이메일 주소를 입력하십시오");
            valid = false;
        } else {
            edtemail.setError(null);
        }
        // jop
        if (jop.isEmpty() || jop.length() < 2) {
            edtjop.setError("2글자 이상 입력해 주십시오");
            valid = false;
        } else {
            edtjop.setError(null);
        }
        //Address
        if (address.isEmpty()) {
            edtaddress.setError("올바른 국적를 입력하십시오");
            valid = false;
        } else {
            edtaddress.setError(null);
        }



        return valid;
    }
}
