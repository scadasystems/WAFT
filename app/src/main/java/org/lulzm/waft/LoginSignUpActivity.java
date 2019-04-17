package org.lulzm.waft;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.iid.FirebaseInstanceId;
import xyz.hasnat.sweettoast.SweetToast;

import java.util.Timer;
import java.util.TimerTask;

public class LoginSignUpActivity extends AppCompatActivity {
    private Context myContext = LoginSignUpActivity.this;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sign_up);

        // 상태표시줄 색상 변경
        View view = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (view != null) {
                // 23 버전 이상일 때 상태바 하얀 색상, 회색 아이콘
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                getWindow().setStatusBarColor(Color.parseColor("#f2f2f2"));
            }
        } else if (Build.VERSION.SDK_INT >= 21) {
            // 21 버전 이상일 때 상태바 검은 색상, 흰색 아이콘
            getWindow().setStatusBarColor(Color.BLACK);
        }

        // firebase
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");

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
                String confirmPassword = confirmPass.getText().toString();

                if (params.weight == 4.25) {
                    // sign up logic in here

                    registerAccount(emailSign, passSign, confirmPassword);

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
        progressDialog = new ProgressDialog(myContext);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = email_login.getText().toString();
                String password = pass_login.getText().toString();

                if (params2.weight == 4.25) {
                    // login logic in here

                    loginUserAccount(email, password);

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

    // login logic in here
    private void loginUserAccount(String email, String password) {
        //just validation
        if (TextUtils.isEmpty(email)) {
            SweetToast.error(this, "이메일을 입력하세요.");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            SweetToast.error(this, "이메일 형식이 올바르지 않습니다.");
        } else if (TextUtils.isEmpty(password)) {
            SweetToast.error(this, "패스워드를 입력하세요.");
        } else if (password.length() < 6) {
            SweetToast.error(this, "패스워드는 6자리 이상으로\n 만들어야 합니다.");
        } else {
            //progress bar
            progressDialog.setMessage("로그인 중입니다...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);

            // after validation checking, log in user a/c
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                // these lines for taking DEVICE TOKEN for sending device to device notification
                                String userUID = mAuth.getCurrentUser().getUid();
                                String userDeviceToken = FirebaseInstanceId.getInstance().getToken();
                                userDatabaseReference.child(userUID).child("device_token").setValue(userDeviceToken)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                checkVerifiedEmail();
                                            }
                                        });

                            } else {
                                SweetToast.error(LoginSignUpActivity.this, "이메일과 패스워드가 옳지 않습니다. 다시 한번 확인해주세요.");
                            }

                            progressDialog.dismiss();

                        }
                    });
        }
    }


    // signup logic in here
    private void registerAccount(String email, String password, String confirmPassword) {

        if (TextUtils.isEmpty(email)) {
            SweetToast.error(myContext, "이메일을 입력하세요.");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            SweetToast.error(myContext, "이메일 형식이 올바르지 않습니다.");
        } else if (TextUtils.isEmpty(password)) {
            SweetToast.error(myContext, "패스워드를 입력하세요.");
        } else if (password.length() < 6) {
            SweetToast.error(myContext, "패스워드는 6자리 이상으로\n 만들어야 합니다.");
        } else if (TextUtils.isEmpty(confirmPassword)) {
            SweetToast.warning(myContext, "패스워드를 재입력해주세요.");
        } else if (!confirmPassword.equals(password)) {
            SweetToast.error(myContext, "패스워드가 일치하지 않습니다.");
        } else {
            // create user
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String deviceToken = FirebaseInstanceId.getInstance().getToken();
                                String name = "WAFT user";

                                // get and link storage
                                String current_userID = mAuth.getCurrentUser().getUid();
                                storeDefaultDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(current_userID);

                                storeDefaultDatabaseReference.child("user_name").setValue(name);
                                storeDefaultDatabaseReference.child("verified").setValue("false");
                                storeDefaultDatabaseReference.child("search_name").setValue(name.toLowerCase());
                                storeDefaultDatabaseReference.child("user_email").setValue(email);
                                storeDefaultDatabaseReference.child("user_nickname").setValue("WAFT 유저");
                                storeDefaultDatabaseReference.child("user_gender").setValue("");
                                storeDefaultDatabaseReference.child("user_country").setValue("KR");
                                storeDefaultDatabaseReference.child("created_at").setValue(ServerValue.TIMESTAMP);
                                storeDefaultDatabaseReference.child("user_status").setValue("Hi, I'm new WAFT user");
                                storeDefaultDatabaseReference.child("user_image").setValue("default_image"); // Original image
                                storeDefaultDatabaseReference.child("device_token").setValue(deviceToken);
                                storeDefaultDatabaseReference.child("user_thumb_image").setValue("default_image")
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                // SENDING VERIFICATION EMAIL TO THE REGISTERED USER'S EMAIL
                                                user = mAuth.getCurrentUser();
                                                if (user != null) {
                                                    user.sendEmailVerification()
                                                            .addOnCompleteListener(task11 -> {
                                                                if (task11.isSuccessful()) {

                                                                    registerSuccessPopUp();

                                                                    // LAUNCH activity after certain time period
                                                                    new Timer().schedule(new TimerTask() {
                                                                        public void run() {
                                                                            LoginSignUpActivity.this.runOnUiThread(() -> {
                                                                                mAuth.signOut();

                                                                                Intent mainIntent = new Intent(myContext, LoginSignUpActivity.class);
                                                                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                                startActivity(mainIntent);
                                                                                finish();
                                                                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                                                                SweetToast.info(myContext, "이메일을 인증하셔야 합니다.");
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
                                SweetToast.error(myContext, "에러 : " + message);
                            }
                            progressDialog.dismiss();
                        }
                    });
            //config progressbar
            progressDialog.setTitle("회원가입 중입니다.");
            progressDialog.setMessage("잠시만 기다려주세요.....");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);
        }
    }

    private int inDp(int dp) {

        Resources resources = getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int px = (int) (dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    /*
     *  회원가입 성공 후 팝업 띄움
     * */
    private void registerSuccessPopUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginSignUpActivity.this);
        View view = LayoutInflater.from(LoginSignUpActivity.this).inflate(R.layout.register_success_popup, null);

        builder.setCancelable(false);

        builder.setView(view);
        builder.show();
    }

    /*
     *  이메일 인증 확인
     * */
    private void checkVerifiedEmail() {
        user = mAuth.getCurrentUser();
        boolean isVerified = false;
        if (user != null) {
            isVerified = user.isEmailVerified();
        }
        if (isVerified) {
            String UID = mAuth.getCurrentUser().getUid();
            userDatabaseReference.child(UID).child("verified").setValue("true");

            Intent intent_login = new Intent(LoginSignUpActivity.this, MainActivity.class);
            intent_login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent_login);
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        } else {
            SweetToast.info(LoginSignUpActivity.this, "이메일을 인증하지 않았습니다.\n어플 사용을 위해 인증해주세요.");
            mAuth.signOut();
        }
    }
}
