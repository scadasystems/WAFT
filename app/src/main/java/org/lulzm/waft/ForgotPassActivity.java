package org.lulzm.waft;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import xyz.hasnat.sweettoast.SweetToast;

import java.util.Timer;
import java.util.TimerTask;

public class ForgotPassActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private EditText forgotEmail;
    private Button resetPassButton;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Reset Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        auth = FirebaseAuth.getInstance();

        forgotEmail = findViewById(R.id.forgotEmail);
        resetPassButton = findViewById(R.id.resetPassButton);
        resetPassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = forgotEmail.getText().toString();
                if(TextUtils.isEmpty(email)){
                    SweetToast.error(ForgotPassActivity.this, "이메일을 입력해주세요.");
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    SweetToast.error(ForgotPassActivity.this,"올바른 이메일 형식을 입력해주세요.");
                } else {
                    // send email to reset password
                    auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                emailSentSuccessPopUp();

                                // LAUNCH activity after certain time period
                                new Timer().schedule(new TimerTask(){
                                    public void run() {
                                        ForgotPassActivity.this.runOnUiThread(new Runnable() {
                                            public void run() {
                                                auth.signOut();
                                                Intent mainIntent =  new Intent(ForgotPassActivity.this, LoginSignUpActivity.class);
                                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(mainIntent);
                                                finish();
                                                SweetToast.info(ForgotPassActivity.this, "이메일을 확인해주세요.");

                                            }
                                        });
                                    }
                                }, 8000);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            SweetToast.error(ForgotPassActivity.this, "Oops!! "+e.getMessage());
                        }
                    });
                }
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
}
