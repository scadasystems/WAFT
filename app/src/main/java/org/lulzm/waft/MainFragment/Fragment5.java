package org.lulzm.waft.MainFragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

        return view;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        mListener = (OnThemeChangeListener) context;
    }


}
