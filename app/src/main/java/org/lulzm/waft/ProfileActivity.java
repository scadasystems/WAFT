package org.lulzm.waft;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.iid.FirebaseInstanceId;
import com.hbb20.CountryCodePicker;
import de.hdodenhof.circleimageview.CircleImageView;
import xyz.hasnat.sweettoast.SweetToast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class ProfileActivity extends AppCompatActivity {
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_ALBUM = 2;
    private static final int CROP_FROM_IMAGE = 3;

    private Context myContext = ProfileActivity.this;

    private Uri mImageCaptureUri;
    private String absoultePath;


    EditText edt_name, edt_uid, edt_email, edt_job;
    CountryCodePicker ccp;
    RadioGroup rg_gender;
    RadioButton rb_male, rb_female;
    Button btn_save, btn_cancel;
    ImageView img_imgSetUp, img_profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        edt_name = findViewById(R.id.edt_name);
        edt_uid = findViewById(R.id.edt_uid);
        edt_email = findViewById(R.id.edt_email);
        edt_job = findViewById(R.id.edt_job);
        ccp = findViewById(R.id.ccp);
        btn_save = findViewById(R.id.btn_save);
        btn_cancel = findViewById(R.id.btn_cancel);
        img_imgSetUp = findViewById(R.id.img_imgSetUp);
        img_profileImage = findViewById(R.id.img_profileImage);

        View view = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (view != null) {
                // 23 버전 이상일 때 상태바 하얀 색상, 회색 아이콘
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                getWindow().setStatusBarColor(Color.parseColor("#f2f2f2"));
            }
        }else if (Build.VERSION.SDK_INT >= 21) {
            // 21 버전 이상일 때 상태바 검은 색상, 흰색 아이콘
            getWindow().setStatusBarColor(Color.BLACK);
        }

        // 취소, 저장 버튼
        btn_cancel.setOnClickListener(v -> {
            finish();
        });
        btn_save.setOnClickListener(v -> {
//            Intent saveIntent = new Intent(getApplicationContext(), MainActivity.class);
            registerSuccessPopUp();
//            startActivity(saveIntent);
//            finish();
        });

        // 라디오 그룹
        rg_gender = findViewById(R.id.rg_gender);
        rg_gender.setOnCheckedChangeListener(radioGroupButtonChangeListener);

        // 카메라 이미지 클릭 이벤트
        img_imgSetUp.setOnClickListener(v -> alert());

    }

     //  회원정보 수정 성공 후 팝업 띄움
    private void registerSuccessPopUp() {
        SweetToast.success(myContext, "수정완료");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case PICK_FROM_ALBUM :
                {
                    assert data != null;
                    mImageCaptureUri = data.getData();
                Log.d("SmartWheel", mImageCaptureUri.getPath().toString());
            }
            case PICK_FROM_CAMERA :
                {
                // 이미지를 가져와 리사이즈
                // 이미지 크롭 어플리케이션 호출
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");
                //CROP 할 이미지를 200*200 크기로 저장
                intent.putExtra("outputX", 200);
                intent.putExtra("outputY", 200);
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, CROP_FROM_IMAGE);

                break;
            }
            case CROP_FROM_IMAGE :
                {
                //크롭된 이미지 받음 , 부가적인 작업 이후 임시 파일 삭제
                    if(resultCode != RESULT_OK) {
                        return;
                    }
                    final Bundle extras = data.getExtras();

                    //CROP 된 이미지 경로
                    String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SmartWeel/"
                            + System.currentTimeMillis() + ".jpg";

                    if(extras != null) {
                        Bitmap photo = extras.getParcelable("data"); //CROP 된 Bitmap
                        img_profileImage.setImageBitmap(photo);

                        storeCropImage(photo, filePath);

                        absoultePath = filePath;
                        break;
                    }
                    //임시파일 삭제
                    File f = new File(mImageCaptureUri.getPath());
                    if(f.exists()) {
                        f.delete();
                    }
            }
        }
    }

    // 카메라 이미지 클릭 이벤트(이미지 변경)
    public void alert() {
        DialogInterface.OnClickListener cameraListener = (dialog, which) -> doTakePhotoAction();
        DialogInterface.OnClickListener albumListener = (dialog, which) -> doTakeAlbumAction();
        DialogInterface.OnClickListener cancelListener = (dialog, which) -> dialog.dismiss();

        new AlertDialog.Builder(this)
                .setTitle("업로드할 이미지 선택")
                .setPositiveButton("사진촬영", cameraListener)
                .setNegativeButton("앨범선택", albumListener)
                .setNeutralButton("취소", cancelListener)
                .show();
    }
    // Bitmap을 저장하는 부분

    private void storeCropImage(Bitmap bitmap, String filePath) {
        //Smartwheel 폴더를 생성하여 이미지 저장
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SmartWheel";
        File directory_SmartWheel = new File(dirPath);

        if(!directory_SmartWheel.exists()) { // SmartWheel 폴더가 없으면
            directory_SmartWheel.mkdir();
        }
        File copyFile = new File(filePath);
        BufferedOutputStream out;

        try {
            copyFile.createNewFile();
            out = new BufferedOutputStream(new FileOutputStream(copyFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            //sendBroadcast를 통해 CROP 된 사진을 앨범에 보이도록 갱신.
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(copyFile)));
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 카메라 사진 촬영
    public void doTakePhotoAction() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 임시로 사용할 파일 경로
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
            mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));
            cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
            startActivityForResult(cameraIntent, PICK_FROM_CAMERA);
        }
    }

    // 앨범에서 이미지 가져오기
    public void doTakeAlbumAction() {
        Intent albumIntent = new Intent(Intent.ACTION_PICK);
        albumIntent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(albumIntent, PICK_FROM_ALBUM);
    }

    // 라디오 버튼 선택
    RadioGroup.OnCheckedChangeListener radioGroupButtonChangeListener = (radioGroup, i) -> {
        if (i == R.id.rb_male) {
            Toast.makeText(getApplicationContext(), "라디오 그룹 남자버튼 눌렸습니다.", Toast.LENGTH_SHORT).show();
        } else if (i == R.id.rb_female) {
            Toast.makeText(getApplicationContext(), "라디오 그룹 여자버튼 눌렸습니다.", Toast.LENGTH_SHORT).show();
        }
    };

}
