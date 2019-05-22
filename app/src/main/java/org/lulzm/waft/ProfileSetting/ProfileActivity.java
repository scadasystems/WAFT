package org.lulzm.waft.ProfileSetting;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import org.lulzm.waft.R;
import xyz.hasnat.sweettoast.SweetToast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class ProfileActivity extends AppCompatActivity {
    Toolbar toolbar;

    private EditText display_name, user_nickname, display_email;
    private TextView updateMsg, display_nickname, display_status, recheckGender;
    private CountryCodePicker countryCodePicker;
    private RadioButton rb_male, rb_female;
    private Button btn_save, btn_cancel;
    private ImageView edit_photo_icon, edit_status;
    private CircleImageView profile_settings_image;

    private DatabaseReference getUserDatabaseReference;
    private FirebaseAuth mAuth;
    private StorageReference mProfileImgStorageRef;
    private StorageReference thumb_image_ref;

    private final static int GALLERY_PICK_CODE = 1;
    Bitmap thumb_Bitmap = null;

    private ProgressDialog progressDialog;
    private String selectedGender = "", profile_download_url, profile_thumb_download_url;

    // for glide error -> You cannot start a load for a destroyed activity
    public RequestManager mGlideRequestManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // glide
        mGlideRequestManager = Glide.with(getApplicationContext());

        // firebase
        mAuth = FirebaseAuth.getInstance();
        String user_id = mAuth.getCurrentUser().getUid();
        getUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(user_id);
        getUserDatabaseReference.keepSynced(true); // for offline

        mProfileImgStorageRef = FirebaseStorage.getInstance().getReference().child("profile_image");
        thumb_image_ref = FirebaseStorage.getInstance().getReference().child("thumb_image");

        profile_settings_image = findViewById(R.id.img_profileImage);
        edit_photo_icon = findViewById(R.id.editPhotoIcon);
        edit_status = findViewById(R.id.editStatus);
        display_status = findViewById(R.id.tv_status);
        display_name = findViewById(R.id.edt_name);
        user_nickname = findViewById(R.id.edt_uid);
        display_nickname = findViewById(R.id.tv_nickName);
        display_email = findViewById(R.id.edt_email);
        countryCodePicker = findViewById(R.id.ccp);

        recheckGender = findViewById(R.id.recheckGender);
        recheckGender.setVisibility(View.VISIBLE);

        rb_male = findViewById(R.id.rb_male);
        rb_female = findViewById(R.id.rb_female);

        btn_save = findViewById(R.id.btn_save);
        btn_cancel = findViewById(R.id.btn_cancel);

        updateMsg = findViewById(R.id.updatedMsg);

        // 수정불가
        display_email.setFocusable(false);
        display_email.setClickable(false);

        // 상태표시줄
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

        progressDialog = new ProgressDialog(this);

        /** 엔터키 막음 */
        display_nickname.setOnKeyListener((v, keyCode, event) -> {
            if(keyCode == event.KEYCODE_ENTER)
            {
                return true;
            }
            return false;
        });

        getUserDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("user_name").getValue().toString();
                String nickname = dataSnapshot.child("user_nickname").getValue().toString();
                String status = dataSnapshot.child("user_status").getValue().toString();
                String email = dataSnapshot.child("user_email").getValue().toString();
                String country = dataSnapshot.child("user_country").getValue().toString();
                String gender = dataSnapshot.child("user_gender").getValue().toString();
                final String image = dataSnapshot.child("user_image").getValue().toString();
                String thumbImage = dataSnapshot.child("user_thumb_image").getValue().toString();
                // 상태메세지
                display_status.setText(status);
                // 이름
                display_name.setText(name);
                display_name.setSelection(display_name.getText().length());
                // 닉네임
                display_nickname.setText(nickname);
                user_nickname.setText(nickname);
                user_nickname.setSelection(user_nickname.getText().length());
                // 이메일
                display_email.setText(email);
                // 국가
                countryCodePicker.setCountryForNameCode(country);
                countryCodePicker.getSelectedCountryName();
                // QR



                // default image for new user
                if (!image.equals("default_image")) {
                    view.post(() -> mGlideRequestManager
                            .load(image)
                            .placeholder(R.drawable.default_profile_image)
                            .error(R.drawable.default_profile_image)
                            .into(profile_settings_image));
                }

                if (gender.equals("Male")) {
                    rb_male.setChecked(true);
                } else if (gender.equals("Female")) {
                    rb_female.setChecked(true);
                } else return;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        /** Change profile photo from gallery */
        edit_photo_icon.setOnClickListener(v -> {
            // open gallery
            Intent galleryIntent = new Intent();
            galleryIntent.setType("image/*");
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(galleryIntent, GALLERY_PICK_CODE);
        });

        /** edit information */
        btn_save.setOnClickListener(v -> {
            String uName = display_name.getText().toString();
            String uNickname = user_nickname.getText().toString();
            String uCountry = countryCodePicker.getSelectedCountryNameCode();

            saveInformation(uName, uNickname, uCountry, selectedGender);
        });

        /** cancel event */
        btn_cancel.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
        });

        /** edit status */
        edit_status.setOnClickListener(v -> {
            String previous_status = display_status.getText().toString();
            Intent intent_statusUpdate = new Intent(ProfileActivity.this, StatusUpdateActivity.class);
            intent_statusUpdate.putExtra("ex_status", previous_status);
            startActivity(intent_statusUpdate);
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        });

        // hide keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    } // end onCreate

    // Gender Radio button
    public void selectedGenderRB(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.rb_male:
                if (checked) {
                    selectedGender = "남";
                    recheckGender.setVisibility(View.GONE);
                    break;
                }

            case R.id.rb_female:
                if (checked) {
                    selectedGender = "여";
                    recheckGender.setVisibility(View.GONE);
                    break;
                }
        }
    }

    // User informations SAVE
    private void saveInformation(String uName, String uNickname, String uCountry, String uGender) {
        if (uGender.length() < 1) {     // 성별 체크를 안할 시
            recheckGender.setTextColor(Color.RED);
        } else if (TextUtils.isEmpty(uName)) {
            SweetToast.error(this, "이름을 기입해주세요");
        } else if (uName.length() < 2 || uName.length() > 40) {
            SweetToast.error(this, "이름은 2글자 이상 40글자 이하로 기입해야 합니다.");
        } else {
            getUserDatabaseReference.child("user_name").setValue(uName);
            getUserDatabaseReference.child("user_nickname").setValue(uNickname);
            getUserDatabaseReference.child("search_name").setValue(uName.toLowerCase());
            getUserDatabaseReference.child("user_country").setValue(uCountry);
            getUserDatabaseReference.child("user_gender").setValue(uGender)
                    .addOnCompleteListener(task -> {
                        updateMsg.setVisibility(View.VISIBLE);

                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                ProfileActivity.this.runOnUiThread(() -> updateMsg.setVisibility(View.GONE));
                            }
                        }, 1500);
                    })
                    .addOnFailureListener(e -> {
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 이미지 자르기
        if (requestCode == GALLERY_PICK_CODE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                progressDialog.setMessage("잠시만 기다려주세요...");
                progressDialog.show();

                final Uri resultUri = result.getUri();

                File thumb_filePath_Uri = new File(resultUri.getPath());

                final String user_id = mAuth.getCurrentUser().getUid();

                try {
                    thumb_Bitmap = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(45)
                            .compressToBitmap(thumb_filePath_Uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // 자른 이미지를 업로드하기 위한 firebase storage
                final StorageReference filePath = mProfileImgStorageRef.child(user_id + ".jpg");

                UploadTask uploadTask = filePath.putFile(resultUri);
                Task<Uri> uriTask = uploadTask.continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        SweetToast.error(ProfileActivity.this, "Profile Photo Error: " + task.getException().getMessage());
                    }
                    profile_download_url = filePath.getDownloadUrl().toString();
                    return filePath.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        SweetToast.success(ProfileActivity.this, "이미지를 업로드했습니다.");
                        // 저장된 이미지를 프로필 사진으로
                        profile_download_url = task.getResult().toString();
                        Log.i("tag", "profile url: " + profile_download_url);

                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        thumb_Bitmap.compress(Bitmap.CompressFormat.JPEG, 45, outputStream);
                        final byte[] thumb_byte = outputStream.toByteArray();

                        // 잘라낸 이미지와 압축된 이미지를 업로드하기 위한 firebase storage
                        final StorageReference thumb_filePath = thumb_image_ref.child(user_id + "jpg");
                        UploadTask thumb_uploadTask = thumb_filePath.putBytes(thumb_byte);

                        Task<Uri> thumbUriTask = thumb_uploadTask.continueWithTask(task1 -> {
                            if (!task1.isSuccessful()) {
                                SweetToast.error(ProfileActivity.this, "Thumb Image Error: " + task1.getException().getMessage());
                            }
                            profile_thumb_download_url = thumb_filePath.getDownloadUrl().toString();
                            return thumb_filePath.getDownloadUrl();
                        }).addOnCompleteListener(task12 -> {
                            profile_thumb_download_url = task12.getResult().toString();
                            Log.i("tag", "thumb url: " + profile_thumb_download_url);
                            if (task12.isSuccessful()) {
                                Log.i("tag", "thumb profile 업데이트");

                                HashMap<String, Object> update_user_data = new HashMap<>();
                                update_user_data.put("user_image", profile_download_url);
                                update_user_data.put("user_thumb_image", profile_thumb_download_url);

                                getUserDatabaseReference.updateChildren(new HashMap<String, Object>(update_user_data))
                                        .addOnSuccessListener(aVoid -> {
                                            Log.i("tag", "thumb profile 업데이트");
                                            progressDialog.dismiss();
                                        }).addOnFailureListener(e -> {
                                    Log.i("tag", "for thumb profile: " + e.getMessage());
                                    progressDialog.dismiss();
                                });
                            }
                        });
                    }

                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                SweetToast.error(ProfileActivity.this, "이미지 자르기를 실패했습니다.");
            }
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
