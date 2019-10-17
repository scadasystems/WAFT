package org.lulzm.waft.mainFragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.lulzm.waft.BuildConfig;
import org.lulzm.waft.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

import xyz.hasnat.sweettoast.SweetToast;

/*********************************************************
 *   $$\                  $$\             $$\      $$\
 *   $$ |                 $$ |            $$$\    $$$ |
 *   $$ |      $$\   $$\  $$ | $$$$$$$$\  $$$$\  $$$$ |
 *   $$ |      $$ |  $$ | $$ | \____$$  | $$ \$\$$ $$ |
 *   $$ |      $$ |  $$ | $$ |   $$$$ _/  $$  \$$  $$ |
 *   $$ |      $$ |  $$ | $$ |  $$  _/    $$ | $  /$$ |
 *   $$$$$$$$  \$$$$$$$ | $$ | $$$$$$$$\  $$ | \_/ $$ |
 *   \_______| \______/   \__| \________| \__|     \__|
 *
 * Project : WAFT
 * Created by Android Studio
 * Developer : Lulz_M
 * Date : 2019-05-08 008
 * Time : 오후 2:03
 * GitHub : https://github.com/scadasystems
 * E-mail : redsmurf@lulzm.org
 *********************************************************/
public class FragmentQRMain extends Fragment implements View.OnClickListener {

    private TextView qr_title, qr_subtitle;
    private ImageView qr_image;
    private Button btnScanner, btnProfile, btnSave, btnShare;
    private FrameLayout fl_qrscanner;

    private static final int REQUEST_CODE_PERMISSIONS = 100;

    // firebase
    DatabaseReference getUserDatabaseReference;
    FirebaseAuth mAuth;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 23) {
            int permissionCheck = ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr, container, false);

        btnScanner = view.findViewById(R.id.btnScanner);
        btnScanner.setOnClickListener(this);
        btnProfile = view.findViewById(R.id.btnProfile);
        btnProfile.setOnClickListener(this);
        btnSave = view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);
        btnShare = view.findViewById(R.id.btnShare);
        btnShare.setOnClickListener(this);

        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Generator");

        qr_image = view.findViewById(R.id.qrImage);
        fl_qrscanner = view.findViewById(R.id.fl_qrscanner);
        qr_title = view.findViewById(R.id.qr_title);
        qr_subtitle = view.findViewById(R.id.tv_QRAddMe);

        // firebase
        mAuth = FirebaseAuth.getInstance();
        String user_id = mAuth.getCurrentUser().getUid();
        getUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(user_id);

        getUserDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String search_name = mAuth.getCurrentUser().getUid();
                Bitmap logo = BitmapFactory.decodeResource(view.getResources(), R.drawable.waft_icon);
                Bitmap merge = mergeBitmaps(logo, GenerateQRCodeBitmap(search_name));
                qr_image.setImageBitmap(merge);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private Bitmap GenerateQRCodeBitmap(String data) {
        BitMatrix bitMatrix = null;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    data, BarcodeFormat.QR_CODE,
                    600, 600, null);
        } catch (IllegalArgumentException Illegalargumentexception) {
            return null;
        } catch (WriterException e) {
            e.printStackTrace();
        }

        int bitMatrixWidth = bitMatrix.getWidth();
        int bitMatrixHeight = bitMatrix.getHeight();

        // 다크모드 적용
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("change_theme", getActivity().MODE_PRIVATE);

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];
        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            if (sharedPreferences.getBoolean("dark_theme", false)) {
                for (int x = 0; x < bitMatrixWidth; x++) {
                    pixels[offset + x] = bitMatrix.get(x, y) ?
                            getResources().getColor(R.color.white) :
                            getResources().getColor(R.color.background_color);
                }
            } else {
                for (int x = 0; x < bitMatrixWidth; x++) {
                    pixels[offset + x] = bitMatrix.get(x, y) ?
                            getResources().getColor(R.color.black) :
                            getResources().getColor(R.color.white);
                }
            }
        }
        Bitmap.Config config;
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 600, 0, 0, bitMatrixWidth, bitMatrixHeight);

        return bitmap;
    }

    public Bitmap mergeBitmaps(Bitmap logo, Bitmap qrcode) {
        Bitmap combined = Bitmap.createBitmap(qrcode.getWidth(), qrcode.getHeight(), qrcode.getConfig());
        Canvas canvas = new Canvas(combined);
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();
        canvas.drawBitmap(qrcode, new Matrix(), null);

        Bitmap resizeLogo = Bitmap.createScaledBitmap(logo, canvasWidth / 5, canvasHeight / 5, true);
        int centreX = (canvasWidth - resizeLogo.getWidth()) / 2;
        int centreY = (canvasHeight - resizeLogo.getHeight()) / 2;
        canvas.drawBitmap(resizeLogo, centreX, centreY, null);
        return combined;
    }

    private String saveImageLocally(ImageView iv) {
        iv.buildDrawingCache();

        Bitmap bmp = iv.getDrawingCache();

        File storageLocal = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

        String name = "myQR(WAFT).png";

        File file = new File(storageLocal, name);

        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
        SweetToast.success(getActivity(), getString(R.string.success_save) + path + "/" + name);

        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            scanFile(getActivity(), Uri.fromFile(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "true";
    }

    // 클릭 이벤트들들
    @Override
    public void onClick(View v) {
        Fragment fragment;
        switch (v.getId()) {
            case R.id.btnScanner:
                qr_title.setVisibility(View.INVISIBLE);
                qr_image.setVisibility(View.INVISIBLE);
                fl_qrscanner.setVisibility(View.VISIBLE);
                qr_subtitle.setText(getString(R.string.scan_qr_and_add_friends));
                fragment = FragmentQRScanner.newInstance();
                setQRScannerFragment(fragment);
                break;
            case R.id.btnProfile:
                fl_qrscanner.setVisibility(View.GONE);
                qr_title.setVisibility(View.VISIBLE);
                qr_image.setVisibility(View.VISIBLE);
                qr_subtitle.setText(getString(R.string.using_qr_add_me));
                break;
            case R.id.btnSave:
                saveImageLocally(qr_image);
                break;
            case R.id.btnShare:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
                intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(Objects.requireNonNull(getContext()), BuildConfig.APPLICATION_ID, new File(filePath, "myQR(WAFT).png")));
                Intent chooser = Intent.createChooser(intent, getString(R.string.share));
                startActivity(chooser);

                break;
        }
    }

    private static void scanFile(Context context, Uri imageUri) {
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(imageUri);
        context.sendBroadcast(scanIntent);
    }

    // QRscanner 프레그먼트 바꾸기
    private void setQRScannerFragment(Fragment child) {
        FragmentTransaction childFt = getChildFragmentManager().beginTransaction();

        if (!child.isAdded()) {
            childFt.replace(R.id.fl_qrscanner, child);
            childFt.addToBackStack(null);
            childFt.commit();
        }
    }

    // 권한 체크
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_PERMISSIONS:
                // 위치 권한 허용X
                if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSIONS);
                    SweetToast.error(getActivity(), getString(R.string.need_permission));
                }
        }
    }

}
