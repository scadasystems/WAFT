package org.lulzm.waft.MainFragment;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.*;
import android.widget.Toast;
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
        String l = prefs.getString("country", "없음");
        Toast.makeText(getActivity(), l, Toast.LENGTH_SHORT).show();

        preflag.setOnPreferenceClickListener(preference -> {
            locale = getResources().getConfiguration().locale;
            SharedPreferences.Editor edit = prefs.edit();

            Intent i = getActivity().getIntent();

            if (locale.getLanguage().equals("ko")) {
                locale = Locale.ENGLISH;
                Configuration config = new Configuration();
                config.locale = locale;
                getResources().updateConfiguration(config, getResources().getDisplayMetrics());
                Toast.makeText(getActivity(), locale.toString(), Toast.LENGTH_SHORT).show();
                edit.putString("country", locale.getLanguage());
                getActivity().finish();
                startActivity(i);
            } else if (locale.getLanguage().equals("en")) {
                locale = Locale.KOREA;
                Configuration config = new Configuration();
                config.locale = locale;
                getResources().updateConfiguration(config, getResources().getDisplayMetrics());
                edit.putString("country", locale.getLanguage());


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
