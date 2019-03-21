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
    private android.widget.EditText edtaddress;
    private android.widget.EditText edtemail;
    private android.widget.EditText edtmobile;
    private android.widget.EditText edtpassword;
    private android.widget.EditText edtreEnterPassword;
    private android.support.v7.widget.AppCompatButton btnsignup;
    private android.widget.TextView tvlogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        this.tvlogin = (TextView) findViewById(R.id.tv_login);
        this.btnsignup = (AppCompatButton) findViewById(R.id.btn_signup);
        this.edtreEnterPassword = (EditText) findViewById(R.id.edt_reEnterPassword);
        this.edtpassword = (EditText) findViewById(R.id.edt_password);
        this.edtmobile = (EditText) findViewById(R.id.edt_mobile);
        this.edtemail = (EditText) findViewById(R.id.edt_email);
        this.edtaddress = (EditText) findViewById(R.id.edt_address);
        this.edtname = (EditText) findViewById(R.id.edt_name);

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
        String address = edtaddress.getText().toString();
        String email = edtemail.getText().toString();
        String mobile = edtmobile.getText().toString();
        String password = edtpassword.getText().toString();
        String reEnterPassword = edtreEnterPassword.getText().toString();

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
        String address = edtaddress.getText().toString();
        String email = edtemail.getText().toString();
        String mobile = edtmobile.getText().toString();
        String password = edtpassword.getText().toString();
        String reEnterPassword = edtreEnterPassword.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            edtname.setError("at least 3 characters");
            valid = false;
        } else {
            edtname.setError(null);
        }

        if (address.isEmpty()) {
            edtaddress.setError("Enter Valid Address");
            valid = false;
        } else {
            edtaddress.setError(null);
        }


        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtemail.setError("enter a valid email address");
            valid = false;
        } else {
            edtemail.setError(null);
        }

        if (mobile.isEmpty() || mobile.length()!=10) {
            edtmobile.setError("Enter Valid Mobile Number");
            valid = false;
        } else {
            edtmobile.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            edtpassword.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            edtpassword.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            edtreEnterPassword.setError("Password Do not match");
            valid = false;
        } else {
            edtreEnterPassword.setError(null);
        }

        return valid;
    }
}
