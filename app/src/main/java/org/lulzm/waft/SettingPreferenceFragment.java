package org.lulzm.waft;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.*;
import android.support.annotation.Nullable;
import android.widget.BaseAdapter;

public class SettingPreferenceFragment extends PreferenceFragment {

    SharedPreferences prefs;

    ListPreference keywordSoundPreference;
    PreferenceScreen keywordScreen;
    EditTextPreference user_name, user_email;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //settings_preference 연결
        addPreferencesFromResource(R.xml.settings_preference);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        keywordSoundPreference = (ListPreference) findPreference("keyword_sound_list");
        keywordScreen = (PreferenceScreen) findPreference("keyword_screen");

        user_name = (EditTextPreference) findPreference("user_name");
        user_email = (EditTextPreference) findPreference("user_email");

        // 이름 초기화
        if (prefs.getString("user_name", "").equals("")) {
            user_name.setSummary("이름을 입력해 주세요.");
        } else {
            user_name.setSummary(user_name.getText());
        }
        //이름 입력
        user_name.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String name = newValue.toString();
                if (!name.equals("")) {
                    prefs.edit().putString("user_name", name).apply();
                    user_name.setSummary(name);
                } else {
                    user_name.setSummary("이름을 입력해 주세요.");
                }
                return true;
            }
        });

        //이메일 초기화
        if (prefs.getString("user_email", "").equals("")) {
            user_email.setSummary("이메일을 입력해 주세요.");
        } else {
            user_email.setSummary(user_email.getText());
        }
        // 이메일 입력
        user_email.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String email = newValue.toString();
                if (!email.equals("")) {
                    prefs.edit().putString("user_email", email).apply();
                    user_email.setSummary(email);
                } else {
                    user_email.setSummary("이메일을 입력해 주세요.");
                }
                return true;
            }
        });

//        prefs.registerOnSharedPreferenceChangeListener(prefListener);

    }// onCreate

    SharedPreferences.OnSharedPreferenceChangeListener prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            if (key.equals("keyword_sound_list")) {
                keywordSoundPreference.setSummary(prefs.getString("keyword_sound_list", "카톡"));
            }

            if (key.equals("keyword")) {

                if (prefs.getBoolean("keyword", false)) {
                    keywordScreen.setSummary("사용");

                } else {
                    keywordScreen.setSummary("사용안함");
                }

                //2뎁스 PreferenceScreen 내부에서 발생한 환경설정 내용을 2뎁스 PreferenceScreen에 적용하기 위한 소스
                ((BaseAdapter) getPreferenceScreen().getRootAdapter()).notifyDataSetChanged();
            }

        }
    };

}
