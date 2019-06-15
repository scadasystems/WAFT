package org.lulzm.waft.MainFragment

import android.content.Intent
import android.os.Bundle
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.vision.barcode.Barcode
import info.androidhive.barcode.BarcodeReader
import org.lulzm.waft.ChatProfile.ChatProfileActivity
import org.lulzm.waft.R
import xyz.hasnat.sweettoast.SweetToast

/*********************************************************
 * $$\                  $$\             $$\      $$\
 * $$ |                 $$ |            $$$\    $$$ |
 * $$ |      $$\   $$\  $$ | $$$$$$$$\  $$$$\  $$$$ |
 * $$ |      $$ |  $$ | $$ | \____$$  | $$ \$\$$ $$ |
 * $$ |      $$ |  $$ | $$ |   $$$$ _/  $$  \$$  $$ |
 * $$ |      $$ |  $$ | $$ |  $$  _/    $$ | $  /$$ |
 * $$$$$$$$  \$$$$$$$ | $$ | $$$$$$$$\  $$ | \_/ $$ |
 * \_______| \______/   \__| \________| \__|     \__|
 *
 * Project : WAFT
 * Created by Android Studio
 * Developer : Lulz_M
 * Date : 2019-05-11 011
 * Time : 오후 1:12
 * GitHub : https://github.com/scadasystems
 * E-mail : redsmurf@lulzm.org
 */
class FragmentQRScanner : Fragment(), BarcodeReader.BarcodeReaderListener {

    private var barcodeReader: BarcodeReader? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_qr_scanner, container, false)

        barcodeReader =
            childFragmentManager.findFragmentById(R.id.qrcode_fragment) as BarcodeReader?
        barcodeReader!!.setListener(this)

        return view
    }

    override fun onScanned(barcode: Barcode) {
        barcodeReader!!.playBeep()

        activity!!.runOnUiThread {
            SweetToast.success(activity, "QRcode: " + barcode.rawValue)
            /* QR코드 읽을 시 이벤트 처리 */
            val intent = Intent(activity, ChatProfileActivity::class.java)


        }
    }

    override fun onScannedMultiple(barcodes: List<Barcode>) {
        var codes = ""
        for (barcode in barcodes) {
            codes += barcode.displayValue + ", "
        }

        val finalCodes = codes
        activity!!.runOnUiThread { SweetToast.info(activity, "QRcodes: $finalCodes") }
    }

    override fun onBitmapScanned(sparseArray: SparseArray<Barcode>) {

    }

    override fun onScanError(errorMessage: String) {
        SweetToast.error(activity, "onScanError: $errorMessage")
    }

    override fun onCameraPermissionDenied() {
        SweetToast.error(activity, "카메라 권한을 허용해주세요.")
    }

    companion object {

        fun newInstance(): FragmentQRScanner {
            return FragmentQRScanner()
        }
    }
}
