package org.lulzm.waft.MainFragment;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.*;
import androidx.annotation.Nullable;
import org.lulzm.waft.R;

public class SettingPreferenceFragment extends PreferenceFragment {

    PreferenceScreen prefscreen;
    SharedPreferences prefs;
    ListPreference preflist;
    Preference prefnotic;
    SwitchPreference prefblack;
    Preference preflag;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting_ference);

        prefblack = (SwitchPreference) findPreference("pref_black");
        preflag =  findPreference("pref_lag");
        prefnotic = findPreference("pref_ntic");

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        preflag.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent();
            intent.setComponent( new ComponentName("com.android.settings","com.android.settings.Settings$LanguageAndInputSettingsActivity" ));
            startActivity(intent);

            return false;
        });

    }
}
