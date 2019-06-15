package org.lulzm.waft.MainFragment

import android.content.Context.MODE_PRIVATE
import android.content.res.Configuration
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import org.lulzm.waft.R
import java.util.*

@Suppress("DEPRECATION")
class SettingFragment : Fragment() {

    // Theme 스위치
    private var myswitch: Switch? = null
    // 언어변경
    private var language_setting: LinearLayout? = null
     private var locale: Locale? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Light/ Dark 테마 값 저장
        val sharedPreferences =
            Objects.requireNonNull<FragmentActivity>(activity).getSharedPreferences("change_theme", MODE_PRIVATE)
        if (sharedPreferences.getBoolean("dark_theme", false)) {
            activity!!.setTheme(R.style.darktheme)
        } else {
            activity!!.setTheme(R.style.AppTheme)
        }
        val view = inflater.inflate(R.layout.fragment_setting, container, false)
        /* 언어변경 */
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val language = prefs.getString("language", "")

        language_setting = view.findViewById(R.id.language_setting)
        // 새로고침
        val i = activity!!.intent
        // 언어 변경 클릭 이벤트
        language_setting?.setOnClickListener {
            locale = resources.configuration.locale
            val edit = prefs.edit()
            assert(language != null)
            if (language == "ko" || language == "한국어") { // 현재 언어가 한국어 일때
                locale = Locale.ENGLISH
                val config = Configuration()
                config.locale = locale
                resources.updateConfiguration(config, resources.displayMetrics)
                // 변경된 언어 값 저장
                edit.putString("language", "English")
                edit.apply()
                // 새로고침
                activity!!.finish()
                startActivity(i)
            } else if (language == "en" || language == "English") { // 현재 언어가 영어 일때
                locale = Locale.KOREA
                val config = Configuration()
                config.locale = locale
                resources.updateConfiguration(config, resources.displayMetrics)
                // 변경된 언어 값 저장
                edit.putString("language", "ko")
                edit.apply()
                // 새로고침
                activity!!.finish()
                startActivity(i)
            }
        }

        // 테마변경 클릭 이벤트 및 값 전달
        myswitch = view.findViewById(R.id.myswitch)
        myswitch!!.isChecked = sharedPreferences.getBoolean("dark_theme", false).toString() == "true"
        myswitch!!.setOnCheckedChangeListener { _, isChecked ->
            val editor = sharedPreferences.edit()
            editor.putBoolean("dark_theme", isChecked)
            editor.apply()
            Toast.makeText(
                activity,
                sharedPreferences.getBoolean("dark_theme", false).toString(),
                Toast.LENGTH_SHORT
            ).show()
            handleDarkMode(sharedPreferences.getBoolean("dark_theme", false))
        }
        return view
    }

    private fun handleDarkMode(active: Boolean) {
        // 테마변경 클릭시 앱 새로고침
        val i = activity!!.intent
        if (active) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            // 새로고침
            activity!!.finish()
            startActivity(i)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            // 새로고침
            activity!!.finish()
            startActivity(i)
        }
    }
}
