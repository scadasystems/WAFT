package org.lulzm.waft.MainFragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import org.lulzm.waft.SosAdapter.ApiService;
import org.lulzm.waft.SosAdapter.Datum;
import org.lulzm.waft.SosAdapter.RetroClient;
import org.lulzm.waft.SosAdapter.SosList;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment1 extends Fragment {

    ImageView qr_icon, nav_icon, money_icon, chat_icon, sos1_main, sos2_main;
    RoundedImageView notice1, notice2;
    CountryCodePicker countryCodePicker;
    ImageButton btn_sos;

    /* sos 다이어로그 */
    Dialog dialog_sos;

    /* Sos parsing */
    private ArrayList<Datum> datumList;


    @SuppressLint({"SetJavaScriptEnabled", "ResourceType"})
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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

        // 다크모드 가져오기
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("change_theme", getActivity().MODE_PRIVATE);

        /* 언어변경 */
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String language = prefs.getString("language", "");
        assert language != null;
        // country code picker 언어 변경
        if (language.equals("ko") || language.equals("한국어")) {
            // 다크모드
            if (sharedPreferences.getBoolean("dark_theme", false)) {
                Glide.with(this).load(R.drawable.notice_countrys_info_dark).placeholder(R.drawable.notice_countrys_info_dark).override(500).into(notice1);
                Glide.with(this).load(R.drawable.notice_countrys_visa_dark).placeholder(R.drawable.notice_countrys_visa_dark).override(500).into(notice2);
            } else {
                Glide.with(this).load(R.drawable.notice_countrys_info).placeholder(R.drawable.notice_countrys_info).override(500).into(notice1);
                Glide.with(this).load(R.drawable.notice_countrys_visa).placeholder(R.drawable.notice_countrys_visa).override(500).into(notice2);
            }
            countryCodePicker.changeDefaultLanguage(CountryCodePicker.Language.KOREAN);
        } else if (language.equals("en") || language.equals("English")) {
            if (sharedPreferences.getBoolean("dark_theme", false)) {
                Glide.with(this).load(R.drawable.notice_countrys_info_eng_dark).placeholder(R.drawable.notice_countrys_info_eng_dark).override(500).into(notice1);
                Glide.with(this).load(R.drawable.notice_countrys_visa_eng_dark).placeholder(R.drawable.notice_countrys_visa_eng_dark).override(500).into(notice2);
            } else {
                Glide.with(this).load(R.drawable.notice_countrys_info_eng).placeholder(R.drawable.notice_countrys_info_eng).override(500).into(notice1);
                Glide.with(this).load(R.drawable.notice_countrys_visa_eng).placeholder(R.drawable.notice_countrys_visa_eng).override(500).into(notice2);
            }

            countryCodePicker.changeDefaultLanguage(CountryCodePicker.Language.ENGLISH);
        }

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

        /* Sos parsing */
        ApiService api = RetroClient.getApiService();
        Call<SosList> call = api.getMyJSON();

        /* sos 버튼 이벤트 처리 */
        btn_sos.setOnClickListener(v -> {
            dialog_sos.setContentView(R.layout.emergency_popup);
            dialog_sos.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            // 나라코드
            CountryCodePicker country_popup_name = dialog_sos.findViewById(R.id.country_popup_name);
            // 닫기버튼
            ImageButton tv_close = dialog_sos.findViewById(R.id.txtclose);
            // 각 전화번호
            TextView tv_police = dialog_sos.findViewById(R.id.police_number);
            TextView tv_amb = dialog_sos.findViewById(R.id.ambulance_number);
            TextView tv_fire = dialog_sos.findViewById(R.id.fire_number);
            // 전화걸기
            ImageButton call_police = dialog_sos.findViewById(R.id.call_police);
            ImageButton call_amb = dialog_sos.findViewById(R.id.call_ambulance);
            ImageButton call_fire = dialog_sos.findViewById(R.id.call_fire);

            // country code picker 언어 변경
            if (language.equals("ko") || language.equals("한국어")) {
                country_popup_name.changeDefaultLanguage(CountryCodePicker.Language.KOREAN);

            } else if (language.equals("en") || language.equals("English")) {
                country_popup_name.changeDefaultLanguage(CountryCodePicker.Language.ENGLISH);
            }

            // 나라코드 가져오기
            SharedPreferences preferences2 = getActivity().getSharedPreferences("pref_countryCode", Context.MODE_PRIVATE);
            String pref_countryCode_popUp_set = preferences2.getString("country_code", "");
            country_popup_name.setCountryForNameCode(pref_countryCode_popUp_set);

            // call.enquere 는 중복처리가 안되는 거 같아서 clone을 씀.
            call.clone().enqueue(new Callback<SosList>() {
                @Override
                public void onResponse(Call<SosList> call, Response<SosList> response) {
                    if (response.isSuccessful()) {
                        datumList = response.body().getData();

//                        SharedPreferences preferences3 = getActivity().getSharedPreferences("pref_country")

                        for (Datum datum : datumList) {
                            if (datum.getCountry().getISOCode().equals(pref_countryCode_popUp_set)) {
                                String num_police = datum.getPolice().getAll().get(0);
                                String num_ambulance = datum.getAmbulance().getAll().get(0);
                                String num_fire = datum.getFire().getAll().get(0);

                                tv_police.setText(num_police);
                                tv_amb.setText(num_ambulance);
                                tv_fire.setText(num_fire);

                                // 각 전화번호

                                // 경찰 전화걸기
                                call_police.setOnClickListener(v12 -> {
                                    Intent intent_police = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + num_police));
                                    startActivity(intent_police);
                                });
                                // 응급 전화걸기
                                call_amb.setOnClickListener(v13 -> {
                                    Intent intent_ambulance = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + num_ambulance));
                                    startActivity(intent_ambulance);
                                });
                                // 소방 전화걸기
                                call_fire.setOnClickListener(v14 -> {
                                    Intent intent_fire = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + num_fire));
                                    startActivity(intent_fire);
                                });

                                break;
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<SosList> call, Throwable t) {

                }
            });
            // 팝업 닫기 버튼
            tv_close.setOnClickListener(v1 -> dialog_sos.dismiss());
            dialog_sos.show();
        });

        Glide.with(this).load(R.drawable.qr_bt).into(qr_icon);
        Glide.with(this).load(R.drawable.nav_bt).into(nav_icon);
        Glide.with(this).load(R.drawable.money_blue2).into(money_icon);
        Glide.with(this).load(R.drawable.chat_bt).into(chat_icon);


        return view;
    }
}
