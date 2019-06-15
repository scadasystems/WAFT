package org.lulzm.waft;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import org.lulzm.waft.databinding.FragmentMenuBinding;

import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class MenuFragment extends Fragment {

    @SuppressLint("StaticFieldLeak")
    // Data Binding 사용
    public static FragmentMenuBinding fragmentMenuBinding;

    // FragmentMenuBinding 연결
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentMenuBinding = FragmentMenuBinding.inflate(inflater, container, false);
        return fragmentMenuBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Light/ Dark 테마 값 받아오기
        SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("change_theme", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("dark_theme", false)) {
            getActivity().setTheme(R.style.darktheme);
        } else {
            getActivity().setTheme(R.style.AppTheme);
        }
        super.onViewCreated(view, savedInstanceState);

        // Home 아이콘 클릭 이벤트
        fragmentMenuBinding.lilPost.setOnClickListener(view1 -> {
            if (MainActivity.HomeFragmentHandler != null) {
                MainActivity.HomeFragmentHandler.sendEmptyMessage(0);
            }
            // Home 아이콘 클릭시 아이콘 색 변경 이벤트
            Glide.with(MenuFragment.this).load(R.drawable.home_blue).into(fragmentMenuBinding.iconPost);
            Glide.with(MenuFragment.this).load(R.drawable.profile).into(fragmentMenuBinding.iconAccounts);
            Glide.with(MenuFragment.this).load(R.drawable.settings).into(fragmentMenuBinding.iconHistory);
            Glide.with(MenuFragment.this).load(R.drawable.sos1).into(fragmentMenuBinding.iconShareus);
            Glide.with(MenuFragment.this).load(R.drawable.logout).into(fragmentMenuBinding.iconLogout);
            // Home 아이콘 클릭시 아이콘 색 변경 이벤트
            fragmentMenuBinding.textPost.setSelected(true);
            fragmentMenuBinding.textAccounts.setSelected(false);
            fragmentMenuBinding.textHistory.setSelected(false);
            fragmentMenuBinding.textShareus.setSelected(false);
            fragmentMenuBinding.textLogout.setSelected(false);
        });

        // Profile 아이콘 클릭 이벤트
        fragmentMenuBinding.lilAccounts.setOnClickListener(view2 -> {
            if (MainActivity.HomeFragmentHandler != null) {
                MainActivity.HomeFragmentHandler.sendEmptyMessage(1);
            }
            // Profile 아이콘 클릭시 아이콘 색 변경 이벤트
            Glide.with(MenuFragment.this).load(R.drawable.home).into(fragmentMenuBinding.iconPost);
            Glide.with(MenuFragment.this).load(R.drawable.profile_blue).into(fragmentMenuBinding.iconAccounts);
            Glide.with(MenuFragment.this).load(R.drawable.settings).into(fragmentMenuBinding.iconHistory);
            Glide.with(MenuFragment.this).load(R.drawable.sos1).into(fragmentMenuBinding.iconShareus);
            Glide.with(MenuFragment.this).load(R.drawable.logout).into(fragmentMenuBinding.iconLogout);
            // Profile 아이콘 클릭시 아이콘 색 변경 이벤트
            fragmentMenuBinding.textPost.setSelected(false);
            fragmentMenuBinding.textAccounts.setSelected(true);
            fragmentMenuBinding.textHistory.setSelected(false);
            fragmentMenuBinding.textShareus.setSelected(false);
            fragmentMenuBinding.textLogout.setSelected(false);
        });

        // Setting 아이콘 클릭 이벤트
        fragmentMenuBinding.lilHistory.setOnClickListener(view3 -> {
            if (MainActivity.HomeFragmentHandler != null) {
                MainActivity.HomeFragmentHandler.sendEmptyMessage(2);
            }
            // Setting 아이콘 클릭시 아이콘 색 변경 이벤트
            Glide.with(MenuFragment.this).load(R.drawable.home).into(fragmentMenuBinding.iconPost);
            Glide.with(MenuFragment.this).load(R.drawable.profile).into(fragmentMenuBinding.iconAccounts);
            Glide.with(MenuFragment.this).load(R.drawable.settings_blue).into(fragmentMenuBinding.iconHistory);
            Glide.with(MenuFragment.this).load(R.drawable.sos1).into(fragmentMenuBinding.iconShareus);
            Glide.with(MenuFragment.this).load(R.drawable.logout).into(fragmentMenuBinding.iconLogout);
            // Setting 아이콘 클릭시 아이콘 색 변경 이벤트
            fragmentMenuBinding.textPost.setSelected(false);
            fragmentMenuBinding.textAccounts.setSelected(false);
            fragmentMenuBinding.textHistory.setSelected(true);
            fragmentMenuBinding.textShareus.setSelected(false);
            fragmentMenuBinding.textLogout.setSelected(false);
        });

        // SOS 아이콘 클릭 이벤트
        fragmentMenuBinding.lilShareus.setOnClickListener(view4 -> {
            if (MainActivity.HomeFragmentHandler != null) {
                MainActivity.HomeFragmentHandler.sendEmptyMessage(3);
            }
            // SOS 아이콘 클릭시 아이콘 색 변경 이벤트
            Glide.with(MenuFragment.this).load(R.drawable.home_blue).into(fragmentMenuBinding.iconPost);
            Glide.with(MenuFragment.this).load(R.drawable.profile).into(fragmentMenuBinding.iconAccounts);
            Glide.with(MenuFragment.this).load(R.drawable.settings).into(fragmentMenuBinding.iconHistory);
            Glide.with(MenuFragment.this).load(R.drawable.sos1).into(fragmentMenuBinding.iconShareus);
            Glide.with(MenuFragment.this).load(R.drawable.logout).into(fragmentMenuBinding.iconLogout);
            // Sos 아이콘 클릭시 아이콘 색 변경 이벤트
            fragmentMenuBinding.textPost.setSelected(true);
            fragmentMenuBinding.textAccounts.setSelected(false);
            fragmentMenuBinding.textHistory.setSelected(false);
            fragmentMenuBinding.textShareus.setSelected(false);
            fragmentMenuBinding.textLogout.setSelected(false);
        });

        // Logout 아이콘 클릭 이벤트
        fragmentMenuBinding.lilLogout.setOnClickListener(view5 -> {
            if (MainActivity.HomeFragmentHandler != null) {
                MainActivity.HomeFragmentHandler.sendEmptyMessage(4);
            }
            // Logout 아이콘 클릭시 아이콘 색 변경 이벤트
            Glide.with(MenuFragment.this).load(R.drawable.home_blue).into(fragmentMenuBinding.iconPost);
            Glide.with(MenuFragment.this).load(R.drawable.profile).into(fragmentMenuBinding.iconAccounts);
            Glide.with(MenuFragment.this).load(R.drawable.settings).into(fragmentMenuBinding.iconHistory);
            Glide.with(MenuFragment.this).load(R.drawable.sos1).into(fragmentMenuBinding.iconShareus);
            Glide.with(MenuFragment.this).load(R.drawable.logout).into(fragmentMenuBinding.iconLogout);
            // Logout 아이콘 클릭시 아이콘 색 변경 이벤트
            fragmentMenuBinding.textPost.setSelected(true);
            fragmentMenuBinding.textAccounts.setSelected(false);
            fragmentMenuBinding.textHistory.setSelected(false);
            fragmentMenuBinding.textShareus.setSelected(false);
            fragmentMenuBinding.textLogout.setSelected(false);
        });
    }
}
