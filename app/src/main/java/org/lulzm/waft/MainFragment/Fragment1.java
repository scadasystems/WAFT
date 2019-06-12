package org.lulzm.waft.MainFragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.hbb20.CountryCodePicker;
import com.makeramen.roundedimageview.RoundedImageView;

import org.lulzm.waft.R;

public class Fragment1 extends Fragment {

    ImageView qr_icon, nav_icon, money_icon, chat_icon, sos1_main, sos2_main;
    RoundedImageView notice1, notice2;
    CountryCodePicker countryCodePicker;
    ImageButton btn_sos;

    /* sos 다이어로그 */
    Dialog dialog_sos;


    @SuppressLint("SetJavaScriptEnabled")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //  View view = inflater.inflate(R.layout.fragment1, container);
        View view = inflater.inflate(R.layout.fragment1, container, false);

        dialog_sos = new Dialog(getActivity());

        qr_icon = view.findViewById(R.id.qr_icon);
        nav_icon = view.findViewById(R.id.nav_icon);
        money_icon = view.findViewById(R.id.money_icon);
        chat_icon = view.findViewById(R.id.chat_icon);
        notice1 = view.findViewById(R.id.notic);
        notice2 = view.findViewById(R.id.notic2);
        countryCodePicker = view.findViewById(R.id.country_code_picker);
        btn_sos = view.findViewById(R.id.btn_sos);

        /* 나라 코드 저장 */
        SharedPreferences preferences = getActivity().getSharedPreferences("pref_countryCode", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        String pref_countryCode = preferences.getString("country_code", "");
        countryCodePicker.setCountryForNameCode(pref_countryCode);

        countryCodePicker.setOnCountryChangeListener(() -> {
            String country_code = countryCodePicker.getSelectedCountryNameCode();
            editor.putString("country_code", country_code);
            editor.apply();

            String pref_countryCode_set = preferences.getString("country_code", "");
            Log.d("나라코드", pref_countryCode_set);
        });

        /* sos 버튼 이벤트 처리 */
        btn_sos.setOnClickListener(v -> {
            dialog_sos.setContentView(R.layout.emergency_popup);
            CountryCodePicker country_popup_name = dialog_sos.findViewById(R.id.country_popup_name);
            TextView tv_close = dialog_sos.findViewById(R.id.txtclose);
            TextView tv_police = dialog_sos.findViewById(R.id.police_number);
            TextView tv_amb = dialog_sos.findViewById(R.id.ambulance_number);
            TextView tv_fire = dialog_sos.findViewById(R.id.fire_number);

            SharedPreferences preferences2 = getActivity().getSharedPreferences("pref_countryCode", Context.MODE_PRIVATE);
            String pref_countryCode_popUp_set = preferences2.getString("country_code", "");
            country_popup_name.setCountryForNameCode(pref_countryCode_popUp_set);



            tv_close.setOnClickListener(v1 -> dialog_sos.dismiss());
            dialog_sos.show();
        });

        Glide.with(this).load(R.drawable.qr_bt).into(qr_icon);
        Glide.with(this).load(R.drawable.nav_bt).into(nav_icon);
        Glide.with(this).load(R.drawable.money_blue2).into(money_icon);
        Glide.with(this).load(R.drawable.chat_bt).into(chat_icon);
        Glide.with(this).load(R.drawable.service_safeinfo).into(notice1);
        Glide.with(this).load(R.drawable.service_passportinfo).into(notice2);

        return view;
    }
}
