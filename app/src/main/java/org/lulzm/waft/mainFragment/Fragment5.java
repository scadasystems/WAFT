package org.lulzm.waft.mainFragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import org.lulzm.waft.R;

import java.util.Locale;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class Fragment5 extends Fragment {

    private Switch myswitch;

    // 언어변경
    LinearLayout language_setting;
    Locale locale;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("change_theme", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("dark_theme", false)) {
            getActivity().setTheme(R.style.darktheme);
        } else {
            getActivity().setTheme(R.style.AppTheme);
        }

        View view = inflater.inflate(R.layout.fragment5, container, false);

        /* 언어변경 */
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String language = prefs.getString("language", "");

        // 새로고침
        Intent i = getActivity().getIntent();

        language_setting = view.findViewById(R.id.language_setting);
        language_setting.setOnClickListener(v -> {

            locale = getResources().getConfiguration().locale;
            SharedPreferences.Editor edit = prefs.edit();

            assert language != null;
            if (language.equals("ko") || language.equals("한국어")) { // 현재 언어가 한국어 일때
                locale = Locale.ENGLISH;
                Configuration config = new Configuration();
                config.locale = locale;
                getResources().updateConfiguration(config, getResources().getDisplayMetrics());
                // 변경된 언어 값 저장
                edit.putString("language", "English");
                edit.apply();
                // 새로고침
                getActivity().finish();
                startActivity(i);
            } else if (language.equals("en") || language.equals("English")) { // 현재 언어가 영어 일때
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
        });

        myswitch = view.findViewById(R.id.myswitch);

        if (String.valueOf(sharedPreferences.getBoolean("dark_theme", false)).equals("true")) {
            myswitch.setChecked(true);
        } else {
            myswitch.setChecked(false);
        }

        myswitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("dark_theme", isChecked);
            editor.apply();
            Toast.makeText(getActivity(), String.valueOf(sharedPreferences.getBoolean("dark_theme", false)), Toast.LENGTH_SHORT).show();
            handleDarkMode(sharedPreferences.getBoolean("dark_theme", false));
        });
        return view;
    }

    private void handleDarkMode(boolean active) {
        // 새로고침
        Intent i = getActivity().getIntent();
        if (active) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            // 새로고침
            getActivity().finish();
            startActivity(i);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            // 새로고침
            getActivity().finish();
            startActivity(i);
        }
    }
}
