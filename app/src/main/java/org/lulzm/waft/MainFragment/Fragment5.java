package org.lulzm.waft.MainFragment;

import android.content.ComponentName;
import android.content.Context;
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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import org.lulzm.waft.R;

public class Fragment5 extends Fragment {

    public interface OnThemeChangeListener {
        void onThemeChanged(boolean isDarkMode);
    }

    OnThemeChangeListener mListener;

    SharedPref sharedPref;
    private Switch myswitch;
    FragmentTransaction transaction;
    private Class fragmentClass;
    private Fragment fragment = null;
    private boolean isTransactionSafe;
    private boolean isTransactionPending;
    //    언어변경
    LinearLayout language_setting;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        sharedPref = new SharedPref(getContext());
        if (sharedPref.loadNightModeState() == true) {
            getActivity().setTheme(R.style.darktheme);
        } else {
            getActivity().setTheme(R.style.AppTheme);
        }

        //  View view = inflater.inflate(R.layout.fragment1, container);

        View view = inflater.inflate(R.layout.fragment5, container, false);

        language_setting = view.findViewById(R.id.language_setting);

        language_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$LanguageAndInputSettingsActivity"));
                startActivity(intent);
            }
        });


        myswitch = view.findViewById(R.id.myswitch);
        if (sharedPref.loadNightModeState() == true) {
            myswitch.setChecked(true);
        }

        myswitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mListener.onThemeChanged(isChecked);
            if (isChecked) {
                sharedPref.setNightModeState(true);
//                 restartApp();
            } else {
                sharedPref.setNightModeState(false);
//                 restartApp();
            }
        });

        /* 언어변경 */
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String language = prefs.getString("language", "");
        Toast.makeText(getActivity(), language, Toast.LENGTH_SHORT).show();
//        Locale languageToLoad = new Locale(language);
//        Locale.setDefault(languageToLoad);

        changeLang = view.findViewById(R.id.changeLang);
        changeLang.setOnClickListener(v -> {

            locale = getResources().getConfiguration().locale;
            SharedPreferences.Editor edit = prefs.edit();
            Intent i = getActivity().getIntent();

            assert language != null;
            if (language.equals("ko")) { // 현재 언어가 한국어 일때
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
            } else if (language.equals("en")) { // 현재 언어가 영어 일때
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

        // setListAdapter(new MenuListAdapter(R.layout.row_menu_action_item, getActivity(), MenuActionItem.values()));
        return view;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        mListener = (OnThemeChangeListener) context;
    }

   /* public void onResume() {
        super.onResume();
        isTransactionSafe = true;
    }

    public void onPause() {
        super.onPause();
        isTransactionSafe = false;
    }
*/
    /*public void restartApp() {
        if (isTransactionSafe) {
            fragmentClass = Fragment5.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.flContent, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else {
            isTransactionPending = true;
        }
    }*/

}
