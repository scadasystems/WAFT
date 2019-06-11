package org.lulzm.waft.MainFragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.*;
import androidx.annotation.Nullable;
import org.lulzm.waft.R;

import java.util.Locale;

public class SettingPreferenceFragment extends PreferenceFragment {

    PreferenceScreen prefscreen;
    SharedPreferences prefs;
    ListPreference preflist;
    Preference prefnotic;
    SwitchPreference prefblack;
    Preference preflag;
    Locale locale;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting_ference);

        prefblack = (SwitchPreference) findPreference("pref_black");
        preflag = findPreference("pref_lag");
        prefnotic = findPreference("pref_ntic");

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // 언어변경 이벤트
        preflag.setOnPreferenceClickListener(preference -> {
            locale = getResources().getConfiguration().locale;
            SharedPreferences.Editor edit = prefs.edit();
            Intent i = getActivity().getIntent();

            if (locale.getLanguage().equals("ko")) { // 현재 언어가 한국어 일때
                locale = Locale.ENGLISH;
                Configuration config = new Configuration();
                config.locale = locale;
                getResources().updateConfiguration(config, getResources().getDisplayMetrics());
                // 변경된 언어 값 저장
                edit.putString("language", "en");
                edit.apply();
                // 새로고침
                getActivity().finish();
                startActivity(i);
            } else if (locale.getLanguage().equals("en")) { // 현재 언어가 영어 일때
                locale = Locale.KOREA;
                Configuration config = new Configuration();
                config.locale = locale;
                getResources().updateConfiguration(config, getResources().getDisplayMetrics());
                // 변경된 언어 값 저장
                edit.putString("language", "ko");
                edit.apply();
                // 새로고침
                getActivity().finish();
                startActivity(i);
            }


//            Intent intent = new Intent();
//            intent.setComponent( new ComponentName("com.android.settings","com.android.settings.Settings$LanguageAndInputSettingsActivity" ));
//            startActivity(intent);

            return false;
        });
    }


}
