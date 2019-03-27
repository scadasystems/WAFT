package org.lulzm.waft;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.widget.BaseAdapter;

public class SettingPreferenceFragment extends PreferenceFragment {
    
        SharedPreferences prefs;

        ListPreference soundPreference;
        ListPreference keywordSoundPreference;
        PreferenceScreen keywordScreen;


        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.settings_preference);
            soundPreference = (ListPreference)findPreference("sound_list");
            keywordSoundPreference = (ListPreference)findPreference("keyword_sound_list");
            keywordScreen = (PreferenceScreen)findPreference("keyword_screen");

            prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

            if(!prefs.getString("sound_list", "").equals("")){
                soundPreference.setSummary(prefs.getString("sound_list", "카톡"));
            }

            if(!prefs.getString("keyword_sound_list", "").equals("")){
                keywordSoundPreference.setSummary(prefs.getString("keyword_sound_list", "카톡"));
            }

            if(prefs.getBoolean("keyword", false)){
                keywordScreen.setSummary("사용");
            }

            prefs.registerOnSharedPreferenceChangeListener(prefListener);

        }// onCreate

        SharedPreferences.OnSharedPreferenceChangeListener prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if(key.equals("sound_list")){
                    soundPreference.setSummary(prefs.getString("sound_list", "카톡"));
                }

                if(key.equals("keyword_sound_list")){
                    keywordSoundPreference.setSummary(prefs.getString("keyword_sound_list", "카톡"));
                }

                if(key.equals("keyword")){

                    if(prefs.getBoolean("keyword", false)){
                        keywordScreen.setSummary("사용");

                    }else{
                        keywordScreen.setSummary("사용안함");
                    }

                    //2뎁스 PreferenceScreen 내부에서 발생한 환경설정 내용을 2뎁스 PreferenceScreen에 적용하기 위한 소스
                    ((BaseAdapter)getPreferenceScreen().getRootAdapter()).notifyDataSetChanged();
                }

            }
        };

    }
