package org.lulzm.waft.MainFragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.hbb20.CountryCodePicker
import com.makeramen.roundedimageview.RoundedImageView
import org.lulzm.waft.R
import org.lulzm.waft.SosAdapter.ApiService
import org.lulzm.waft.SosAdapter.Datum
import org.lulzm.waft.SosAdapter.RetroClient
import org.lulzm.waft.SosAdapter.SosList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class MainFragment : Fragment() {

    // 메인 카드뷰 아이콘
    internal var qr_icon: ImageView
    internal var nav_icon: ImageView
    internal var money_icon: ImageView
    internal var chat_icon: ImageView
    internal var notice1: RoundedImageView
    internal var notice2: RoundedImageView
    internal var countryCodePicker: CountryCodePicker
    internal var btn_sos: ImageButton

    /* sos 다이어로그 */
    internal var dialog_sos: Dialog

    /* Sos parsing */
    private var datumList: ArrayList<Datum>? = null
    
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.main_fragment, container, false)

        dialog_sos = Dialog(activity!!)
        qr_icon = view.findViewById(R.id.qr_icon)
        nav_icon = view.findViewById(R.id.nav_icon)
        money_icon = view.findViewById(R.id.money_icon)
        chat_icon = view.findViewById(R.id.chat_icon)
        notice1 = view.findViewById(R.id.notic)
        notice2 = view.findViewById(R.id.notic2)
        countryCodePicker = view.findViewById(R.id.country_code_picker)
        btn_sos = view.findViewById(R.id.btn_sos)

        /* 언어변경 */
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val language = prefs.getString("language", "")!!
// country code picker 언어 변경
        if (language == "ko" || language == "한국어") {
            countryCodePicker.changeDefaultLanguage(CountryCodePicker.Language.KOREAN)

        } else if (language == "en" || language == "English") {
            countryCodePicker.changeDefaultLanguage(CountryCodePicker.Language.ENGLISH)
        }

        /* 나라 코드 저장 */
        val preferences = activity!!.getSharedPreferences("pref_countryCode", Context.MODE_PRIVATE)
        val editor = preferences.edit()

        val pref_countryCode = preferences.getString("country_code", "")
        countryCodePicker.setCountryForNameCode(pref_countryCode)

        countryCodePicker.setOnCountryChangeListener {
            val country_code = countryCodePicker.selectedCountryNameCode
            editor.putString("country_code", country_code)
            editor.apply()

            val pref_countryCode_set = preferences.getString("country_code", "")
            Log.d("나라코드", pref_countryCode_set)
        }

        /* Sos parsing */
        val api = RetroClient.getApiService()
        val call = api.myJSON

        /* sos 버튼 이벤트 처리 */
        btn_sos.setOnClickListener { v ->
            dialog_sos.setContentView(R.layout.emergency_popup)
            dialog_sos.window!!.setBackgroundDrawable(ColorDrawable(0))
            // 나라코드
            val country_popup_name = dialog_sos.findViewById<CountryCodePicker>(R.id.country_popup_name)
            // 닫기버튼
            val tv_close = dialog_sos.findViewById<ImageButton>(R.id.txtclose)
            // 각 전화번호
            val tv_police = dialog_sos.findViewById<TextView>(R.id.police_number)
            val tv_amb = dialog_sos.findViewById<TextView>(R.id.ambulance_number)
            val tv_fire = dialog_sos.findViewById<TextView>(R.id.fire_number)
            // 전화걸기
            val call_police = dialog_sos.findViewById<ImageButton>(R.id.call_police)
            val call_amb = dialog_sos.findViewById<ImageButton>(R.id.call_ambulance)
            val call_fire = dialog_sos.findViewById<ImageButton>(R.id.call_fire)

            // country code picker 언어 변경
            if (language == "ko" || language == "한국어") {
                country_popup_name.changeDefaultLanguage(CountryCodePicker.Language.KOREAN)

            } else if (language == "en" || language == "English") {
                country_popup_name.changeDefaultLanguage(CountryCodePicker.Language.ENGLISH)
            }

            // 나라코드 가져오기
            val preferences2 = activity!!.getSharedPreferences("pref_countryCode", Context.MODE_PRIVATE)
            val pref_countryCode_popUp_set = preferences2.getString("country_code", "")
            country_popup_name.setCountryForNameCode(pref_countryCode_popUp_set)

            // call.enquere 는 중복처리가 안되는 거 같아서 clone을 씀.
            call.clone().enqueue(object : Callback<SosList> {
                override fun onResponse(call: Call<SosList>, response: Response<SosList>) {
                    if (response.isSuccessful) {
                        datumList = response.body()!!.data
                        for (datum in datumList!!) {
                            if (datum.country.isoCode == pref_countryCode_popUp_set) {
                                val num_police = datum.police.all[0].trim { it <= ' ' }
                                val num_ambulance = datum.ambulance.all[0].trim { it <= ' ' }
                                val num_fire = datum.fire.all[0].trim { it <= ' ' }
                                // 각 전화번호
                                tv_police.text = num_police
                                tv_amb.text = num_ambulance
                                tv_fire.text = num_fire
                                // 경찰 전화걸기
                                call_police.setOnClickListener { v12 ->
                                    val intent_police = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$num_police"))
                                    startActivity(intent_police)
                                }
                                // 응급 전화걸기
                                call_amb.setOnClickListener { v13 ->
                                    val intent_ambulance = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$num_ambulance"))
                                    startActivity(intent_ambulance)
                                }
                                // 소방 전화걸기
                                call_fire.setOnClickListener { v14 ->
                                    val intent_fire = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$num_fire"))
                                    startActivity(intent_fire)
                                }
                                break
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<SosList>, t: Throwable) {}
            })
            // 팝업 닫기 버튼
            tv_close.setOnClickListener { v1 -> dialog_sos.dismiss() }
            dialog_sos.show()
        }
        // 각 아이콘 및 공지사항 이미지 Glide 사용하여 최적화
        Glide.with(this).load(R.drawable.qr_bt).into(qr_icon)
        Glide.with(this).load(R.drawable.nav_bt).into(nav_icon)
        Glide.with(this).load(R.drawable.money_blue2).into(money_icon)
        Glide.with(this).load(R.drawable.chat_bt).into(chat_icon)
        Glide.with(this).load(R.drawable.service_safeinfo).placeholder(R.drawable.service_safeinfo).override(500)
            .into(notice1)
        Glide.with(this).load(R.drawable.service_passportinfo).placeholder(R.drawable.service_passportinfo)
            .override(500).into(notice2)
        return view
    }
}
