package org.lulzm.waft.Currency;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import org.lulzm.waft.R;

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
 * Date : 2019-05-12 012                                        
 * Time : 오후 9:52                                       
 * GitHub : https://github.com/scadasystems              
 * E-mail : redsmurf@lulzm.org                           
 *********************************************************/
public class SettingsFragment extends android.preference.PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String pref_pre_screen = "pref_pre_screen";
    private static final String pref_update_category = "pref_update_category";
    private static final String pref_wifi = "pref_wifi";
    private static final String pref_roaming = "pref_roaming";
    private static final String pref_select_category = "pref_select_category";
    private static final String pref_select = "pref_select";
    private static final String pref_numbers_category = "pref_numbers_category";
    private static final String pref_digits = "pref_digits";
    private static final String pref_chart_category = "pref_chart_category";
    private static final String pref_fill = "pref_fill";

    private PreferenceScreen screen;
    private android.preference.PreferenceCategory preferenceCategory_update, preferenceCategory_select, preferenceCategory_numbers, preferenceCategory_chart;
    private android.preference.CheckBoxPreference checkBoxPreference_wifi, checkBoxPreference_roaming, checkBoxPreference_select, checkBoxPreference_fill;
    private ListPreference listPreference_digits;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        screen = getPreferenceScreen();

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());

        ListPreference preference =
                (ListPreference) findPreference(Main.PREF_DIGITS);

        // Set summary to be the user-description for the selected value
        preference.setSummary(preference.getEntry());
    }

    // on Resume
    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    // on Pause
    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    // On preference tree click
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
                                         Preference preference) {
        boolean result =
                super.onPreferenceTreeClick(preferenceScreen, preference);

        // Set home as up
        if (preference instanceof PreferenceScreen) {
            Dialog dialog = ((PreferenceScreen) preference).getDialog();
            ActionBar actionBar = dialog.getActionBar();
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        return result;
    }

    // On shared preference changed
    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences,
                                          String key) {
        if (key.equals(Main.PREF_DIGITS)) {
            ListPreference preference = (ListPreference) findPreference(key);

            // Set summary to be the user-description for the selected value
            preference.setSummary(preference.getEntry());
        }

        if (key.equals(Main.PREF_DARK)) {
            if (Build.VERSION.SDK_INT != Main.VERSION_M)
                getActivity().recreate();
        }
    }
}
