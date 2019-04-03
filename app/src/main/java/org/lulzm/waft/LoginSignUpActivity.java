package org.lulzm.waft;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Timer;
import java.util.TimerTask;

import xyz.hasnat.sweettoast.SweetToast;

public class LoginSignUpActivity extends AppCompatActivity {

    EditText email_login, pass_login, email_sign, pass_sign, confirmPass;
    RelativeLayout relativeLayout, relativeLayout2;
    LinearLayout mainLinear, img;
    TextView signUp, login, forgetPass;
    ImageView logo, back;
    LinearLayout.LayoutParams params, params2;
    FrameLayout.LayoutParams params3;
    FrameLayout mainFrame;
    ObjectAnimator animator2, animator1;
    TextInputLayout til1, til2, til3, til4, til5;
    private ProgressDialog progressDialog;


    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser user;


    private DatabaseReference storeDefaultDatabaseReference;
    private DatabaseReference userDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sign_up);

        // firebase
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");

        //progress
        progressDialog = new ProgressDialog(this);

        params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        params2 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        params3 = new FrameLayout.LayoutParams(inDp(50), inDp(50));

        // TextInputLayout
        til1 = (TextInputLayout) findViewById(R.id.til1);
        til2 = (TextInputLayout) findViewById(R.id.til2);
        til3 = (TextInputLayout) findViewById(R.id.til3);
        til4 = (TextInputLayout) findViewById(R.id.til4);
        til5 = (TextInputLayout) findViewById(R.id.til5);
        signUp = (TextView) findViewById(R.id.signUp);
        login = (TextView) findViewById(R.id.login);
        email_login = (EditText) findViewById(R.id.email);
        pass_login = (EditText) findViewById(R.id.pass);
        forgetPass = (TextView) findViewById(R.id.forget);
        img = (LinearLayout) findViewById(R.id.img);
        email_sign = (EditText) findViewById(R.id.email2);
        pass_sign = (EditText) findViewById(R.id.pass2);
        confirmPass = (EditText) findViewById(R.id.pass3);
        mainFrame = (FrameLayout) findViewById(R.id.mainFrame);
        back = (ImageView) findViewById(R.id.backImg);
        relativeLayout = (RelativeLayout) findViewById(R.id.relative);
        relativeLayout2 = (RelativeLayout) findViewById(R.id.relative2);
        mainLinear = (LinearLayout) findViewById(R.id.mainLinear);

        logo = new ImageView(this);
        logo.setImageResource(R.drawable.ic_account_circle_white_48dp);
        logo.setLayoutParams(params3);

        relativeLayout.post(new Runnable() {
            @Override
            public void run() {

                logo.setX((relativeLayout2.getRight() / 2));
                logo.setY(inDp(50));
                mainFrame.addView(logo);
            }
        });

        params.weight = (float) 0.75;
        params2.weight = (float) 4.25;

        mainLinear.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                mainLinear.getWindowVisibleDisplayFrame(r);
                int screenHeight = mainFrame.getRootView().getHeight();

                int keypadHeight = screenHeight - r.bottom;

                if (keypadHeight > screenHeight * 0.15) {
                    // keyboard is opened
                    if (params.weight == 4.25) {
                        animator1 = ObjectAnimator.ofFloat(back, "scaleX", (float) 1.95);
                        animator2 = ObjectAnimator.ofFloat(back, "scaleY", (float) 1.95);
                        AnimatorSet set = new AnimatorSet();
                        set.playTogether(animator1, animator2);
                        set.setDuration(1000);
                        set.start();

                    } else {
                        animator1 = ObjectAnimator.ofFloat(back, "scaleX", (float) 1.75);
                        animator2 = ObjectAnimator.ofFloat(back, "scaleY", (float) 1.75);
                        AnimatorSet set = new AnimatorSet();
                        set.playTogether(animator1, animator2);
                        set.setDuration(500);
                        set.start();
                    }
                } else {
                    // keyboard is closed
                    animator1 = ObjectAnimator.ofFloat(back, "scaleX", 3);
                    animator2 = ObjectAnimator.ofFloat(back, "scaleY", 3);
                    AnimatorSet set = new AnimatorSet();
                    set.playTogether(animator1, animator2);
                    set.setDuration(500);
                    set.start();
                }
            }
        });


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailSign = email_sign.getText().toString();
                String passSign = pass_sign.getText().toString();
                String rePass = confirmPass.getText().toString();

                if (params.weight == 4.25) {
//                    Snackbar.make(relativeLayout, "Sign Up Complete", Snackbar.LENGTH_SHORT).show();
                    if (SignValidateDate()) {
                        return;
                    } else {
                        registerAccount("WAFT 유저", emailSign, passSign);
                    }
                    return;
                }
                email_sign.setVisibility(View.VISIBLE);
                pass_sign.setVisibility(View.VISIBLE);
                confirmPass.setVisibility(View.VISIBLE);
                til3.setVisibility(View.VISIBLE);
                til4.setVisibility(View.VISIBLE);
                til5.setVisibility(View.VISIBLE);

                final ChangeBounds bounds = new ChangeBounds();
                bounds.setDuration(1000);
                bounds.addListener(new Transition.TransitionListener() {
                    @Override
                    public void onTransitionStart(Transition transition) {
                        ObjectAnimator animator1 = ObjectAnimator.ofFloat(signUp, "translationX", mainLinear.getWidth() / 2 - relativeLayout2.getWidth() / 2 - signUp.getWidth() / 2);
                        ObjectAnimator animator2 = ObjectAnimator.ofFloat(img, "translationX", -relativeLayout2.getX());
                        ObjectAnimator animator3 = ObjectAnimator.ofFloat(signUp, "rotation", 0);
                        ObjectAnimator animator4 = ObjectAnimator.ofFloat(email_login, "alpha", 1, 0);
                        ObjectAnimator animator5 = ObjectAnimator.ofFloat(LoginSignUpActivity.this.pass_login, "alpha", 1, 0);
                        ObjectAnimator animator6 = ObjectAnimator.ofFloat(forgetPass, "alpha", 1, 0);
                        ObjectAnimator animator7 = ObjectAnimator.ofFloat(login, "rotation", 90);
                        ObjectAnimator animator8 = ObjectAnimator.ofFloat(login, "y", relativeLayout2.getHeight() / 2);
                        ObjectAnimator animator9 = ObjectAnimator.ofFloat(email_sign, "alpha", 0, 1);
                        ObjectAnimator animator10 = ObjectAnimator.ofFloat(confirmPass, "alpha", 0, 1);
                        ObjectAnimator animator11 = ObjectAnimator.ofFloat(pass_sign, "alpha", 0, 1);
                        ObjectAnimator animator12 = ObjectAnimator.ofFloat(signUp, "y", login.getY());
                        ObjectAnimator animator13 = ObjectAnimator.ofFloat(back, "translationX", img.getX());
                        ObjectAnimator animator14 = ObjectAnimator.ofFloat(signUp, "scaleX", 2);
                        ObjectAnimator animator15 = ObjectAnimator.ofFloat(signUp, "scaleY", 2);
                        ObjectAnimator animator16 = ObjectAnimator.ofFloat(login, "scaleX", 1);
                        ObjectAnimator animator17 = ObjectAnimator.ofFloat(login, "scaleY", 1);
                        ObjectAnimator animator18 = ObjectAnimator.ofFloat(logo, "x", relativeLayout2.getRight() / 2 - relativeLayout.getRight());

                        AnimatorSet set = new AnimatorSet();
                        set.playTogether(animator1, animator2, animator3, animator4, animator5, animator6, animator7,
                                animator8, animator9, animator10, animator11, animator12, animator13, animator14, animator15, animator16, animator17, animator18);
                        set.setDuration(1000).start();


                    }

                    @Override
                    public void onTransitionEnd(Transition transition) {
                        email_login.setVisibility(View.INVISIBLE);
                        LoginSignUpActivity.this.pass_login.setVisibility(View.INVISIBLE);
                        forgetPass.setVisibility(View.INVISIBLE);
                        til1.setVisibility(View.INVISIBLE);
                        til2.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onTransitionCancel(Transition transition) {

                    }

                    @Override
                    public void onTransitionPause(Transition transition) {

                    }

                    @Override
                    public void onTransitionResume(Transition transition) {


                    }
                });

                TransitionManager.beginDelayedTransition(mainLinear, bounds);

                params.weight = (float) 4.25;
                params2.weight = (float) 0.75;

                relativeLayout.setLayoutParams(params);
                relativeLayout2.setLayoutParams(params2);

            }
        });
        progressDialog = new ProgressDialog(LoginSignUpActivity.this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = email_login.getText().toString();
                String password = pass_login.getText().toString();

                if (params2.weight == 4.25) {
//                    Snackbar.make(relativeLayout2, "Login Complete", Snackbar.LENGTH_SHORT).show();
                    if (LoginValidateDate()) {
                        return;
                    } else {
                        loginUserAccount(email, password);
                    }
                    return;
                }

                email_login.setVisibility(View.VISIBLE);
                LoginSignUpActivity.this.pass_login.setVisibility(View.VISIBLE);
                forgetPass.setVisibility(View.VISIBLE);
                til1.setVisibility(View.VISIBLE);
                til2.setVisibility(View.VISIBLE);


                final ChangeBounds bounds = new ChangeBounds();
                bounds.setDuration(1000);
                bounds.addListener(new Transition.TransitionListener() {
                    @Override
                    public void onTransitionStart(Transition transition) {


                        ObjectAnimator animator1 = ObjectAnimator.ofFloat(login, "translationX", mainLinear.getWidth() / 2 - relativeLayout.getWidth() / 2 - login.getWidth() / 2);
                        ObjectAnimator animator2 = ObjectAnimator.ofFloat(img, "translationX", (relativeLayout.getX()));
                        ObjectAnimator animator3 = ObjectAnimator.ofFloat(login, "rotation", 0);
                        ObjectAnimator animator4 = ObjectAnimator.ofFloat(email_login, "alpha", 0, 1);
                        ObjectAnimator animator5 = ObjectAnimator.ofFloat(LoginSignUpActivity.this.pass_login, "alpha", 0, 1);
                        ObjectAnimator animator6 = ObjectAnimator.ofFloat(forgetPass, "alpha", 0, 1);
                        ObjectAnimator animator7 = ObjectAnimator.ofFloat(signUp, "rotation", 90);
                        ObjectAnimator animator8 = ObjectAnimator.ofFloat(signUp, "y", relativeLayout.getHeight() / 2);
                        ObjectAnimator animator9 = ObjectAnimator.ofFloat(email_sign, "alpha", 1, 0);
                        ObjectAnimator animator10 = ObjectAnimator.ofFloat(confirmPass, "alpha", 1, 0);
                        ObjectAnimator animator11 = ObjectAnimator.ofFloat(pass_sign, "alpha", 1, 0);
                        ObjectAnimator animator12 = ObjectAnimator.ofFloat(login, "y", signUp.getY());
                        ObjectAnimator animator13 = ObjectAnimator.ofFloat(back, "translationX", -img.getX());
                        ObjectAnimator animator14 = ObjectAnimator.ofFloat(login, "scaleX", 2);
                        ObjectAnimator animator15 = ObjectAnimator.ofFloat(login, "scaleY", 2);
                        ObjectAnimator animator16 = ObjectAnimator.ofFloat(signUp, "scaleX", 1);
                        ObjectAnimator animator17 = ObjectAnimator.ofFloat(signUp, "scaleY", 1);
                        ObjectAnimator animator18 = ObjectAnimator.ofFloat(logo, "x", logo.getX() + relativeLayout2.getWidth());


                        AnimatorSet set = new AnimatorSet();
                        set.playTogether(animator1, animator2, animator3, animator4, animator5, animator6, animator7,
                                animator8, animator9, animator10, animator11, animator12, animator13, animator14, animator15, animator16, animator17, animator18);
                        set.setDuration(1000).start();

                    }

                    @Override
                    public void onTransitionEnd(Transition transition) {

                        email_sign.setVisibility(View.INVISIBLE);
                        pass_sign.setVisibility(View.INVISIBLE);
                        confirmPass.setVisibility(View.INVISIBLE);
                        til3.setVisibility(View.INVISIBLE);
                        til4.setVisibility(View.INVISIBLE);
                        til5.setVisibility(View.INVISIBLE);

                    }

                    @Override
                    public void onTransitionCancel(Transition transition) {

                    }

                    @Override
                    public void onTransitionPause(Transition transition) {

                    }

                    @Override
                    public void onTransitionResume(Transition transition) {

                    }
                });

                TransitionManager.beginDelayedTransition(mainLinear, bounds);

                params.weight = (float) 0.75;
                params2.weight = (float) 4.25;

                relativeLayout.setLayoutParams(params);
                relativeLayout2.setLayoutParams(params2);
            }
        });
    }

    private void loginUserAccount(String email, String password) {
        //progress bar
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userUID = mAuth.getCurrentUser().getUid();
                        String userDeiceToken = FirebaseInstanceId.getInstance().getToken();
                        userDatabaseReference.child(userUID).child("device_token").setValue(userDeiceToken)
                                .addOnSuccessListener(aVoid -> checkVerifiedEmail());
                    } else {
                        SweetToast.error(LoginSignUpActivity.this, "Your email and password may be incorrect. Please check & try again.");
                    }
                    progressDialog.dismiss();
                });
    }

    /** checking email verified or NOT */
    private void checkVerifiedEmail() {
        user = mAuth.getCurrentUser();
        boolean isVerified = false;
        if (user != null) {
            isVerified = user.isEmailVerified();
        }
        if (isVerified){
            String UID = mAuth.getCurrentUser().getUid();
            userDatabaseReference.child(UID).child("verified").setValue("true");

            Intent intent = new Intent(LoginSignUpActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            SweetToast.info(LoginSignUpActivity.this, "회원이 아닙니다.");
            mAuth.signOut();
        }
    }

    // signup logic in here
    private void registerAccount(String name, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String deviceToken = FirebaseInstanceId.getInstance().getToken();
                            String current_userID = mAuth.getCurrentUser().getUid();
                            storeDefaultDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(current_userID);


                            storeDefaultDatabaseReference.child("user_name").setValue(name);
                            storeDefaultDatabaseReference.child("verified").setValue("false");
                            storeDefaultDatabaseReference.child("search_name").setValue(name.toLowerCase());
                            storeDefaultDatabaseReference.child("user_email").setValue(email);
                            storeDefaultDatabaseReference.child("user_nickname").setValue("");
                            storeDefaultDatabaseReference.child("user_gender").setValue("");
                            storeDefaultDatabaseReference.child("created_at").setValue(ServerValue.TIMESTAMP);
                            storeDefaultDatabaseReference.child("user_status").setValue("Hi, I'm new WAFT user");
                            storeDefaultDatabaseReference.child("user_image").setValue("default_image"); // Original image
                            storeDefaultDatabaseReference.child("device_token").setValue(deviceToken);
                            storeDefaultDatabaseReference.child("user_thumb_image").setValue("default_image")
                                    .addOnCompleteListener(task12 -> {
                                        if (task12.isSuccessful()) {
                                            // SENDING VERIFICATION EMAIL TO THE REGISTERED USER'S EMAIL
                                            user = mAuth.getCurrentUser();
                                            if (user != null) {
                                                user.sendEmailVerification()
                                                        .addOnCompleteListener(task1 -> {
                                                            if (task1.isSuccessful()) {

                                                                registerSuccessPopUp();

                                                                // LAUNCH activity after certain time period
                                                                new Timer().schedule(new TimerTask() {
                                                                    public void run() {
                                                                        LoginSignUpActivity.this.runOnUiThread(() -> {
                                                                            mAuth.signOut();

                                                                            Intent mainIntent = new Intent(LoginSignUpActivity.this, LoginSignUpActivity.class);
                                                                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                            startActivity(mainIntent);
                                                                            finish();

                                                                            SweetToast.info(LoginSignUpActivity.this, "Please check your email & verify.");

                                                                        });
                                                                    }
                                                                }, 8000);


                                                            } else {
                                                                mAuth.signOut();
                                                            }
                                                        });
                                            }

                                        }
                                    });


                        } else {
                            String message = task.getException().getMessage();
                            SweetToast.error(LoginSignUpActivity.this, "Error occurred : " + message);
                        }

                        progressDialog.dismiss();

                    }
                });

    }

    private boolean LoginValidateDate() {
        boolean result = true;

        String loginEmail = email_login.getText().toString().trim();
        String loginPassword = pass_login.getText().toString().trim();

        if (loginEmail.isEmpty()) {
            til1.setError("이메일을 입력해주세요.");
            result = false;
        } else if (!(loginEmail.matches("^[a-zA-X0-9]@[a-zA-X0-9].[a-zA-X0-9]"))) {
            til1.setError("이메일 형식이 올바르지 않습니다.");
            result = false;
        } else {
            til1.setError(null);
        }
        // password
        if (loginPassword.isEmpty() || loginPassword.length() < 4) {
            til2.setError("패스워드는 4자리 이상입니다.");
            result = false;
        } else {
            til2.setError(null);
        }

        return result;
    }

    private boolean SignValidateDate() {
        boolean result = true;

        String signEmail = email_sign.getText().toString().trim();
        String signPassword = pass_sign.getText().toString().trim();
        String confirmPassword = confirmPass.getText().toString().trim();

        // email
        if (signEmail.isEmpty()) {
            til3.setError("이메일을 입력해주세요.");
            result = false;
        } else if (!(signEmail.matches("^[a-zA-X0-9]@[a-zA-X0-9].[a-zA-X0-9]"))) {
            til3.setError("이메일 형식이 올바르지 않습니다.");
            result = false;
        } else {
            til3.setError(null);
        }
        //password
        if (signPassword.isEmpty() || signPassword.length() < 4) {
            til4.setError("패스워드는 4자리 이상으로 만들어주세요.");
            result = false;
        } else {
            til4.setError(null);
        }
        // confirmPassword
        if (confirmPassword.isEmpty() || confirmPassword.length() < 4 || !(confirmPassword.equals(signPassword))) {
            til5.setError("패스워드가 맞지 않습니다.");
            result = false;
        } else {
            til5.setError(null);
        }

        return result;
    }

    private int inDp(int dp) {

        Resources resources = getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int px = (int) (dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    private void registerSuccessPopUp() {
        // Custom Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginSignUpActivity.this);
        View view = LayoutInflater.from(LoginSignUpActivity.this).inflate(R.layout.register_success_popup, null);

        //ImageButton imageButton = view.findViewById(R.id.successIcon);
        //imageButton.setImageResource(R.drawable.logout);
        builder.setCancelable(false);

        builder.setView(view);
        builder.show();
    }
}
