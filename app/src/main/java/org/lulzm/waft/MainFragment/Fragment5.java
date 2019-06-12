package org.lulzm.waft.MainFragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import org.lulzm.waft.R;

import java.util.Objects;
import java.util.zip.Inflater;

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

    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        sharedPref = new SharedPref(getContext());
        if (sharedPref.loadNightModeState() == true) {
            getActivity().setTheme(R.style.darktheme);
        } else {
            getActivity().setTheme(R.style.AppTheme);
        }

        //  View view = inflater.inflate(R.layout.fragment1, container);

         View view= inflater.inflate(R.layout.fragment5, container, false);


         myswitch = view.findViewById(R.id.myswitch);
         if (sharedPref.loadNightModeState() == true){
            myswitch.setChecked(true);
        }

         myswitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
             mListener.onThemeChanged(isChecked);
             if (isChecked){
                 sharedPref.setNightModeState(true);
//                 restartApp();
             }
             else {
                 sharedPref.setNightModeState(false);
//                 restartApp();
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
