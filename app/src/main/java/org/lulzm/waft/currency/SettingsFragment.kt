package org.lulzm.waft.currency

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.preference.ListPreference
import android.preference.Preference
import android.preference.PreferenceManager
import android.preference.PreferenceScreen
import org.lulzm.waft.R

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
 * Date : 2019-05-12 012
 * Time : 오후 9:52
 * GitHub : https://github.com/scadasystems
 * E-mail : redsmurf@lulzm.org
 */
class SettingsFragment : android.preference.PreferenceFragment(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences)

        val preferences = PreferenceManager.getDefaultSharedPreferences(activity)

        val preference = findPreference(Main.PREF_DIGITS) as ListPreference

        // Set summary to be the user-description for the selected value
        preference.summary = preference.entry
    }

    // on Resume
    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences
            .registerOnSharedPreferenceChangeListener(this)
    }

    // on Pause
    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences
            .unregisterOnSharedPreferenceChangeListener(this)
    }

    // On preference tree click
    override fun onPreferenceTreeClick(
        preferenceScreen: PreferenceScreen,
        preference: Preference
    ): Boolean {
        val result = super.onPreferenceTreeClick(preferenceScreen, preference)

        // Set home as up
        if (preference is PreferenceScreen) {
            val dialog = preference.dialog
            val actionBar = dialog.actionBar
            actionBar!!.setDisplayHomeAsUpEnabled(false)
        }

        return result
    }

    // On shared preference changed
    override fun onSharedPreferenceChanged(
        preferences: SharedPreferences,
        key: String
    ) {
        if (key == Main.PREF_DIGITS) {
            val preference = findPreference(key) as ListPreference

            // Set summary to be the user-description for the selected value
            preference.summary = preference.entry
        }

        if (key == Main.PREF_DARK) {
            if (Build.VERSION.SDK_INT != Main.VERSION_M)
                activity.recreate()
        }
    }
}
