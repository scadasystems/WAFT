package org.lulzm.waft;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import org.lulzm.waft.databinding.FragmentMenuBinding;

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

            fragmentMenuBinding.textPost.setTextColor(getResources().getColor(R.color.blue));
            fragmentMenuBinding.textAccounts.setTextColor(getResources().getColor(R.color.black));
            fragmentMenuBinding.textHistory.setTextColor(getResources().getColor(R.color.black));
            fragmentMenuBinding.textShareus.setTextColor(getResources().getColor(R.color.black));
            fragmentMenuBinding.textLogout.setTextColor(getResources().getColor(R.color.black));

        });

        fragmentMenuBinding.lilAccounts.setOnClickListener(view12 -> {
            if(MainActivity.HomeFragmentHandler != null)
            {
                MainActivity.HomeFragmentHandler.sendEmptyMessage(1);
            }

            Glide.with(MenuFragment.this).load(R.drawable.home).into(fragmentMenuBinding.iconPost);
            Glide.with(MenuFragment.this).load(R.drawable.profile_blue).into(fragmentMenuBinding.iconAccounts);
            Glide.with(MenuFragment.this).load(R.drawable.settings).into(fragmentMenuBinding.iconHistory);
            Glide.with(MenuFragment.this).load(R.drawable.sos1).into(fragmentMenuBinding.iconShareus);
            Glide.with(MenuFragment.this).load(R.drawable.logout).into(fragmentMenuBinding.iconLogout);

            fragmentMenuBinding.textPost.setTextColor(getResources().getColor(R.color.black));
            fragmentMenuBinding.textAccounts.setTextColor(getResources().getColor(R.color.blue));
            fragmentMenuBinding.textHistory.setTextColor(getResources().getColor(R.color.black));
            fragmentMenuBinding.textShareus.setTextColor(getResources().getColor(R.color.black));
            fragmentMenuBinding.textLogout.setTextColor(getResources().getColor(R.color.black));

        });
        fragmentMenuBinding.lilHistory.setOnClickListener(view13 -> {
            if(MainActivity.HomeFragmentHandler != null)
            {
                MainActivity.HomeFragmentHandler.sendEmptyMessage(2);
            }

            Glide.with(MenuFragment.this).load(R.drawable.home).into(fragmentMenuBinding.iconPost);
            Glide.with(MenuFragment.this).load(R.drawable.profile).into(fragmentMenuBinding.iconAccounts);
            Glide.with(MenuFragment.this).load(R.drawable.settings_blue).into(fragmentMenuBinding.iconHistory);
            Glide.with(MenuFragment.this).load(R.drawable.sos1).into(fragmentMenuBinding.iconShareus);
            Glide.with(MenuFragment.this).load(R.drawable.logout).into(fragmentMenuBinding.iconLogout);

            fragmentMenuBinding.textPost.setTextColor(getResources().getColor(R.color.black));
            fragmentMenuBinding.textAccounts.setTextColor(getResources().getColor(R.color.black));
            fragmentMenuBinding.textHistory.setTextColor(getResources().getColor(R.color.blue));
            fragmentMenuBinding.textShareus.setTextColor(getResources().getColor(R.color.black));
            fragmentMenuBinding.textLogout.setTextColor(getResources().getColor(R.color.black));
        });

        fragmentMenuBinding.lilShareus.setOnClickListener(view14 -> {
            if(MainActivity.HomeFragmentHandler != null)
            {
                MainActivity.HomeFragmentHandler.sendEmptyMessage(3);
            }
            Glide.with(MenuFragment.this).load(R.drawable.home).into(fragmentMenuBinding.iconPost);
            Glide.with(MenuFragment.this).load(R.drawable.profile).into(fragmentMenuBinding.iconAccounts);
            Glide.with(MenuFragment.this).load(R.drawable.settings).into(fragmentMenuBinding.iconHistory);
            Glide.with(MenuFragment.this).load(R.drawable.sos1).into(fragmentMenuBinding.iconShareus);
            Glide.with(MenuFragment.this).load(R.drawable.logout).into(fragmentMenuBinding.iconLogout);

            fragmentMenuBinding.textPost.setTextColor(getResources().getColor(R.color.black));
            fragmentMenuBinding.textAccounts.setTextColor(getResources().getColor(R.color.black));
            fragmentMenuBinding.textHistory.setTextColor(getResources().getColor(R.color.black));
            fragmentMenuBinding.textShareus.setTextColor(getResources().getColor(R.color.blue));
            fragmentMenuBinding.textLogout.setTextColor(getResources().getColor(R.color.black));
        });

        fragmentMenuBinding.lilLogout.setOnClickListener(view15 -> {
            if(MainActivity.HomeFragmentHandler != null)
            {
                MainActivity.HomeFragmentHandler.sendEmptyMessage(4);
            }
            Glide.with(MenuFragment.this).load(R.drawable.home).into(fragmentMenuBinding.iconPost);
            Glide.with(MenuFragment.this).load(R.drawable.profile).into(fragmentMenuBinding.iconAccounts);
            Glide.with(MenuFragment.this).load(R.drawable.settings).into(fragmentMenuBinding.iconHistory);
            Glide.with(MenuFragment.this).load(R.drawable.sos1).into(fragmentMenuBinding.iconShareus);
            Glide.with(MenuFragment.this).load(R.drawable.logout).into(fragmentMenuBinding.iconLogout);

            fragmentMenuBinding.textPost.setTextColor(getResources().getColor(R.color.black));
            fragmentMenuBinding.textAccounts.setTextColor(getResources().getColor(R.color.black));
            fragmentMenuBinding.textHistory.setTextColor(getResources().getColor(R.color.black));
            fragmentMenuBinding.textShareus.setTextColor(getResources().getColor(R.color.black));
            fragmentMenuBinding.textLogout.setTextColor(getResources().getColor(R.color.blue));
        });
    }
}
