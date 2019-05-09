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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import org.lulzm.waft.R;

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
public class FragmentQRMain extends Fragment {

    private EditText etTen;
    private EditText etSoLuong;
    private EditText etTinhTrang;
    private EditText etNguoiGiu;

    private ImageView imageView;
    private Button btnCreate;
    private Button btnSave;
    private Button btnShare;
    private Button btnReset;

    private AlertDialog dialog;
    View view;
    ByteArrayOutputStream bytearrayoutputstream;
    File file;
    FileOutputStream fileoutputstream;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qr, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Generator");

        etTen = getView().findViewById(R.id.edt_txt);

        btnCreate = getView().findViewById(R.id.btnCreate);
        btnSave = getView().findViewById(R.id.btnSave);
        btnShare = getView().findViewById(R.id.btnShare);

        imageView = getView().findViewById(R.id.imageView);

        String text = "lulzm";

        Bitmap logo = BitmapFactory.decodeResource(view.getResources(), R.drawable.waft_icon);
        Bitmap merge = mergeBitmaps(logo, GenerateQRCodeBitmap(text));

        imageView.setImageBitmap(merge);

        btnSave.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            saveImageLocally(imageView);
        });

        btnShare.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            Bitmap bitmap =  ((BitmapDrawable) imageView.getDrawable()).getBitmap();
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
        });
    }

    private Bitmap GenerateQRCodeBitmap(String data) {
        BitMatrix bitMatrix = null;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    data, BarcodeFormat.DATA_MATRIX.QR_CODE,
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
                        getResources().getColor(R.color.hot_pink) :
                        getResources().getColor(R.color.white);
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

}
