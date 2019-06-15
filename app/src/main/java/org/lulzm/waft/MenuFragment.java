package org.lulzm.waft;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import org.lulzm.waft.databinding.FragmentMenuBinding;

import java.util.Objects;

import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;

import static android.content.Context.MODE_PRIVATE;

public class MenuFragment extends Fragment {


    @SuppressLint("StaticFieldLeak")
    public static FragmentMenuBinding fragmentMenuBinding;

    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentMenuBinding = FragmentMenuBinding.inflate(inflater, container, false);
//        getActivity().getActionBar().hide();

        return fragmentMenuBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("change_theme", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("dark_theme", false)) {
            getActivity().setTheme(R.style.darktheme);
        } else {
            getActivity().setTheme(R.style.AppTheme);
        }
        super.onViewCreated(view, savedInstanceState);

        fragmentMenuBinding.lilPost.setOnClickListener(view1 -> {
            if(MainActivity.HomeFragmentHandler != null)
            {
                MainActivity.HomeFragmentHandler.sendEmptyMessage(0);
            }

            Glide.with(MenuFragment.this).load(R.drawable.home_blue).into(fragmentMenuBinding.iconPost);
            Glide.with(MenuFragment.this).load(R.drawable.profile).into(fragmentMenuBinding.iconAccounts);
            Glide.with(MenuFragment.this).load(R.drawable.settings).into(fragmentMenuBinding.iconHistory);
            Glide.with(MenuFragment.this).load(R.drawable.sos1).into(fragmentMenuBinding.iconShareus);
            Glide.with(MenuFragment.this).load(R.drawable.logout).into(fragmentMenuBinding.iconLogout);

            fragmentMenuBinding.textPost.setSelected(true);
            fragmentMenuBinding.textAccounts.setSelected(false);
            fragmentMenuBinding.textHistory.setSelected(false);
            fragmentMenuBinding.textShareus.setSelected(false);
            fragmentMenuBinding.textLogout.setSelected(false);

        });

        fragmentMenuBinding.lilAccounts.setOnClickListener(view2 -> {
            if(MainActivity.HomeFragmentHandler != null)
            {
                MainActivity.HomeFragmentHandler.sendEmptyMessage(1);
            }

            Glide.with(MenuFragment.this).load(R.drawable.home).into(fragmentMenuBinding.iconPost);
            Glide.with(MenuFragment.this).load(R.drawable.profile_blue).into(fragmentMenuBinding.iconAccounts);
            Glide.with(MenuFragment.this).load(R.drawable.settings).into(fragmentMenuBinding.iconHistory);
            Glide.with(MenuFragment.this).load(R.drawable.sos1).into(fragmentMenuBinding.iconShareus);
            Glide.with(MenuFragment.this).load(R.drawable.logout).into(fragmentMenuBinding.iconLogout);

            fragmentMenuBinding.textPost.setSelected(false);
            fragmentMenuBinding.textAccounts.setSelected(true);
            fragmentMenuBinding.textHistory.setSelected(false);
            fragmentMenuBinding.textShareus.setSelected(false);
            fragmentMenuBinding.textLogout.setSelected(false);


        });
        fragmentMenuBinding.lilHistory.setOnClickListener(view3 -> {
            if(MainActivity.HomeFragmentHandler != null)
            {
                MainActivity.HomeFragmentHandler.sendEmptyMessage(2);
            }

            Glide.with(MenuFragment.this).load(R.drawable.home).into(fragmentMenuBinding.iconPost);
            Glide.with(MenuFragment.this).load(R.drawable.profile).into(fragmentMenuBinding.iconAccounts);
            Glide.with(MenuFragment.this).load(R.drawable.settings_blue).into(fragmentMenuBinding.iconHistory);
            Glide.with(MenuFragment.this).load(R.drawable.sos1).into(fragmentMenuBinding.iconShareus);
            Glide.with(MenuFragment.this).load(R.drawable.logout).into(fragmentMenuBinding.iconLogout);

            fragmentMenuBinding.textPost.setSelected(false);
            fragmentMenuBinding.textAccounts.setSelected(false);
            fragmentMenuBinding.textHistory.setSelected(true);
            fragmentMenuBinding.textShareus.setSelected(false);
            fragmentMenuBinding.textLogout.setSelected(false);

        });

        fragmentMenuBinding.lilShareus.setOnClickListener(view4 -> {
            if(MainActivity.HomeFragmentHandler != null)
            {
                MainActivity.HomeFragmentHandler.sendEmptyMessage(3);
            }
            Glide.with(MenuFragment.this).load(R.drawable.home).into(fragmentMenuBinding.iconPost);
            Glide.with(MenuFragment.this).load(R.drawable.profile).into(fragmentMenuBinding.iconAccounts);
            Glide.with(MenuFragment.this).load(R.drawable.settings).into(fragmentMenuBinding.iconHistory);
            Glide.with(MenuFragment.this).load(R.drawable.sos1).into(fragmentMenuBinding.iconShareus);
            Glide.with(MenuFragment.this).load(R.drawable.logout).into(fragmentMenuBinding.iconLogout);

            fragmentMenuBinding.textPost.setSelected(false);
            fragmentMenuBinding.textAccounts.setSelected(false);
            fragmentMenuBinding.textHistory.setSelected(false);
            fragmentMenuBinding.textShareus.setSelected(true);
            fragmentMenuBinding.textLogout.setSelected(false);

        });

        fragmentMenuBinding.lilLogout.setOnClickListener(view5 -> {
            if(MainActivity.HomeFragmentHandler != null)
            {
                MainActivity.HomeFragmentHandler.sendEmptyMessage(4);
            }
            Glide.with(MenuFragment.this).load(R.drawable.home).into(fragmentMenuBinding.iconPost);
            Glide.with(MenuFragment.this).load(R.drawable.profile).into(fragmentMenuBinding.iconAccounts);
            Glide.with(MenuFragment.this).load(R.drawable.settings).into(fragmentMenuBinding.iconHistory);
            Glide.with(MenuFragment.this).load(R.drawable.sos1).into(fragmentMenuBinding.iconShareus);
            Glide.with(MenuFragment.this).load(R.drawable.logout).into(fragmentMenuBinding.iconLogout);

            fragmentMenuBinding.textPost.setSelected(false);
            fragmentMenuBinding.textAccounts.setSelected(false);
            fragmentMenuBinding.textHistory.setSelected(false);
            fragmentMenuBinding.textShareus.setSelected(false);
            fragmentMenuBinding.textLogout.setSelected(true);


        });
    }
}
