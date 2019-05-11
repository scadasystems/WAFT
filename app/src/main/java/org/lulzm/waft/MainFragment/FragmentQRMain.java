package org.lulzm.waft.MainFragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import org.lulzm.waft.R;
import xyz.hasnat.sweettoast.SweetToast;

import java.io.*;
import java.util.Random;

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
    private AlertDialog dialog;
    private ByteArrayOutputStream bytearrayoutputstream;
    private File file;
    private FileOutputStream fileoutputstream;

    // firebase
    DatabaseReference getUserDatabaseReference;
    FirebaseAuth mAuth;

    public static FragmentQRMain newInstance() {
        return new FragmentQRMain();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        //you can set the title for your toolbar here for different fragments different titles
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
                String search_name = dataSnapshot.child("search_name").getValue().toString();

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
                    900, 900, null);
        } catch (IllegalArgumentException Illegalargumentexception) {
            return null;
        } catch (WriterException e) {
            e.printStackTrace();
        }

        int bitMatrixWidth = bitMatrix.getWidth();
        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];
        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {
                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.oil) :
                        getResources().getColor(R.color.trans);
            }
        }
        Bitmap.Config config;
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 900, 0, 0, bitMatrixWidth, bitMatrixHeight);

        return bitmap;
    }

    public Bitmap mergeBitmaps(Bitmap logo, Bitmap qrcode) {
        Bitmap combined = Bitmap.createBitmap(qrcode.getWidth(), qrcode.getHeight(), qrcode.getConfig());
        Canvas canvas = new Canvas(combined);
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();
        canvas.drawBitmap(qrcode, new Matrix(), null);

        Bitmap resizeLogo = Bitmap.createScaledBitmap(logo, canvasWidth / 4, canvasHeight / 4, true);
        int centreX = (canvasWidth - resizeLogo.getWidth()) / 2;
        int centreY = (canvasHeight - resizeLogo.getHeight()) / 2;
        canvas.drawBitmap(resizeLogo, centreX, centreY, null);
        return combined;
    }

    private String saveImageLocally(ImageView iv) {
        iv.buildDrawingCache();

        Bitmap bmp = iv.getDrawingCache();

        File storageLocal = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM); //context.getExternalFilesDir(null);

        Random random = new Random();
        int n = 10000;
        String path;
        n = random.nextInt(n);
        String name = "Image-" + n + ".png";

        File file = new File(storageLocal, name);

        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
        Toast.makeText(getActivity(), "Saved successfully in " + path + "/" + name, Toast.LENGTH_LONG).show();

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

    private static void scanFile(Context context, Uri imageUri) {
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(imageUri);
        context.sendBroadcast(scanIntent);

    }

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
                Bitmap bitmap = ((BitmapDrawable) qr_image.getDrawable()).getBitmap();
                try {
                    File file = new File(getActivity().getCacheDir(), "QR_image.png");
                    FileOutputStream fOut = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                    file.setReadable(true, false);
                    final Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                    intent.setType("image/png");
                    startActivity(Intent.createChooser(intent, "Share image via"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void setQRScannerFragment(Fragment child) {
        FragmentTransaction childFt = getChildFragmentManager().beginTransaction();

        if (!child.isAdded()) {
            childFt.replace(R.id.fl_qrscanner, child);
            childFt.addToBackStack(null);
            childFt.commit();
        }
    }
}
