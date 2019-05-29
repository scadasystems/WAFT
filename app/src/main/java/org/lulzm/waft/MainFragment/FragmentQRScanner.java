package org.lulzm.waft.MainFragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import info.androidhive.barcode.BarcodeReader;
import info.androidhive.barcode.camera.CameraSource;
import org.lulzm.waft.ChatProfile.ChatProfileActivity;
import org.lulzm.waft.R;
import xyz.hasnat.sweettoast.SweetToast;

import java.util.List;

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
 * Date : 2019-05-11 011                                        
 * Time : 오후 1:12                                       
 * GitHub : https://github.com/scadasystems              
 * E-mail : redsmurf@lulzm.org                           
 *********************************************************/
public class FragmentQRScanner extends Fragment implements BarcodeReader.BarcodeReaderListener {

    private BarcodeReader barcodeReader;

    public static FragmentQRScanner newInstance() {
        return new FragmentQRScanner();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr_scanner, container, false);

        barcodeReader = (BarcodeReader) getChildFragmentManager().findFragmentById(R.id.qrcode_fragment);
        barcodeReader.setListener(this);

        return view;
    }

    @Override
    public void onScanned(Barcode barcode) {
        barcodeReader.playBeep();

        getActivity().runOnUiThread(() -> {
            SweetToast.success(getActivity(), "QRcode: " + barcode.rawValue);
            String action;
            Intent intent = new Intent(getActivity(), ChatProfileActivity.class);
//            Intent i = new Intent(getActivity(), ChatProfileActivity.class);
//            i.putExtra("visitUserId", barcode);
//            startActivity(i);
        });
    }

    @Override
    public void onScannedMultiple(List<Barcode> barcodes) {
        String codes = "";
        for (Barcode barcode : barcodes) {
            codes += barcode.displayValue + ", ";
        }

        final String finalCodes = codes;
        getActivity().runOnUiThread(() -> SweetToast.info(getActivity(), "QRcodes: " + finalCodes));
    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {

    }

    @Override
    public void onScanError(String errorMessage) {
        SweetToast.error(getActivity(), "onScanError: " + errorMessage);
    }

    @Override
    public void onCameraPermissionDenied() {
        SweetToast.error(getActivity(), "카메라 권한을 허용해주세요.");
    }
}
