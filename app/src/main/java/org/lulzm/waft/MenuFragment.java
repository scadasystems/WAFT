package org.lulzm.waft;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
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


        fragmentMenuBinding.lilPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(MainActivity.HomeFragmentHandler != null)
                {
                    MainActivity.HomeFragmentHandler.sendEmptyMessage(0);
                }

                fragmentMenuBinding.iconPost.setImageResource(R.drawable.home_blue);
                fragmentMenuBinding.iconAccounts.setImageResource(R.drawable.qr);
                fragmentMenuBinding.iconHistory.setImageResource(R.drawable.nav);
                fragmentMenuBinding.iconSettings.setImageResource(R.drawable.money);
                fragmentMenuBinding.iconRateus.setImageResource(R.drawable.chat);
                fragmentMenuBinding.iconShareus.setImageResource(R.drawable.sos1);
                fragmentMenuBinding.iconLogout.setImageResource(R.drawable.logout);

                fragmentMenuBinding.textPost.setTextColor(getResources().getColor(R.color.blue));
                fragmentMenuBinding.textAccounts.setTextColor(getResources().getColor(R.color.black));
                fragmentMenuBinding.textHistory.setTextColor(getResources().getColor(R.color.black));
                fragmentMenuBinding.textSettings.setTextColor(getResources().getColor(R.color.black));
                fragmentMenuBinding.textRateus.setTextColor(getResources().getColor(R.color.black));
                fragmentMenuBinding.textShareus.setTextColor(getResources().getColor(R.color.black));
                fragmentMenuBinding.textLogout.setTextColor(getResources().getColor(R.color.black));

            }
        });

        fragmentMenuBinding.lilAccounts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(MainActivity.HomeFragmentHandler != null)
                {
                    MainActivity.HomeFragmentHandler.sendEmptyMessage(1);
                }

                fragmentMenuBinding.iconPost.setImageResource(R.drawable.home);
                fragmentMenuBinding.iconAccounts.setImageResource(R.drawable.qr_blue);
                fragmentMenuBinding.iconHistory.setImageResource(R.drawable.nav);
                fragmentMenuBinding.iconSettings.setImageResource(R.drawable.money);
                fragmentMenuBinding.iconRateus.setImageResource(R.drawable.chat);
                fragmentMenuBinding.iconShareus.setImageResource(R.drawable.sos1);
                fragmentMenuBinding.iconLogout.setImageResource(R.drawable.logout);

                fragmentMenuBinding.textPost.setTextColor(getResources().getColor(R.color.black));
                fragmentMenuBinding.textAccounts.setTextColor(getResources().getColor(R.color.blue));
                fragmentMenuBinding.textHistory.setTextColor(getResources().getColor(R.color.black));
                fragmentMenuBinding.textSettings.setTextColor(getResources().getColor(R.color.black));
                fragmentMenuBinding.textRateus.setTextColor(getResources().getColor(R.color.black));
                fragmentMenuBinding.textShareus.setTextColor(getResources().getColor(R.color.black));
                fragmentMenuBinding.textLogout.setTextColor(getResources().getColor(R.color.black));

            }
        });
        fragmentMenuBinding.lilHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(MainActivity.HomeFragmentHandler != null)
                {
                    MainActivity.HomeFragmentHandler.sendEmptyMessage(2);
                }

                fragmentMenuBinding.iconPost.setImageResource(R.drawable.home);
                fragmentMenuBinding.iconAccounts.setImageResource(R.drawable.qr);
                fragmentMenuBinding.iconHistory.setImageResource(R.drawable.nav_blue);
                fragmentMenuBinding.iconSettings.setImageResource(R.drawable.money);
                fragmentMenuBinding.iconRateus.setImageResource(R.drawable.chat);
                fragmentMenuBinding.iconShareus.setImageResource(R.drawable.sos1);
                fragmentMenuBinding.iconLogout.setImageResource(R.drawable.logout);

                fragmentMenuBinding.textPost.setTextColor(getResources().getColor(R.color.black));
                fragmentMenuBinding.textAccounts.setTextColor(getResources().getColor(R.color.black));
                fragmentMenuBinding.textHistory.setTextColor(getResources().getColor(R.color.blue));
                fragmentMenuBinding.textSettings.setTextColor(getResources().getColor(R.color.black));
                fragmentMenuBinding.textRateus.setTextColor(getResources().getColor(R.color.black));
                fragmentMenuBinding.textShareus.setTextColor(getResources().getColor(R.color.black));
                fragmentMenuBinding.textLogout.setTextColor(getResources().getColor(R.color.black));
            }
        });

        fragmentMenuBinding.lilSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(MainActivity.HomeFragmentHandler != null)
                {
                    MainActivity.HomeFragmentHandler.sendEmptyMessage(3);
                }

                fragmentMenuBinding.iconPost.setImageResource(R.drawable.home);
                fragmentMenuBinding.iconAccounts.setImageResource(R.drawable.qr);
                fragmentMenuBinding.iconHistory.setImageResource(R.drawable.nav);
                fragmentMenuBinding.iconSettings.setImageResource(R.drawable.money_blue);
                fragmentMenuBinding.iconRateus.setImageResource(R.drawable.chat);
                fragmentMenuBinding.iconShareus.setImageResource(R.drawable.sos1);
                fragmentMenuBinding.iconLogout.setImageResource(R.drawable.logout);

                fragmentMenuBinding.textPost.setTextColor(getResources().getColor(R.color.black));
                fragmentMenuBinding.textAccounts.setTextColor(getResources().getColor(R.color.black));
                fragmentMenuBinding.textHistory.setTextColor(getResources().getColor(R.color.black));
                fragmentMenuBinding.textSettings.setTextColor(getResources().getColor(R.color.blue));
                fragmentMenuBinding.textRateus.setTextColor(getResources().getColor(R.color.black));
                fragmentMenuBinding.textShareus.setTextColor(getResources().getColor(R.color.black));
                fragmentMenuBinding.textLogout.setTextColor(getResources().getColor(R.color.black));
            }
        });

        fragmentMenuBinding.lilRateus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(MainActivity.HomeFragmentHandler != null)
                {
                    MainActivity.HomeFragmentHandler.sendEmptyMessage(4);
                }
                fragmentMenuBinding.iconPost.setImageResource(R.drawable.home);
                fragmentMenuBinding.iconAccounts.setImageResource(R.drawable.qr);
                fragmentMenuBinding.iconHistory.setImageResource(R.drawable.nav);
                fragmentMenuBinding.iconSettings.setImageResource(R.drawable.money);
                fragmentMenuBinding.iconRateus.setImageResource(R.drawable.chat_blue);
                fragmentMenuBinding.iconShareus.setImageResource(R.drawable.sos1);
                fragmentMenuBinding.iconLogout.setImageResource(R.drawable.logout);

                fragmentMenuBinding.textPost.setTextColor(getResources().getColor(R.color.black));
                fragmentMenuBinding.textAccounts.setTextColor(getResources().getColor(R.color.black));
                fragmentMenuBinding.textHistory.setTextColor(getResources().getColor(R.color.black));
                fragmentMenuBinding.textSettings.setTextColor(getResources().getColor(R.color.black));
                fragmentMenuBinding.textRateus.setTextColor(getResources().getColor(R.color.blue));
                fragmentMenuBinding.textShareus.setTextColor(getResources().getColor(R.color.black));
                fragmentMenuBinding.textLogout.setTextColor(getResources().getColor(R.color.black));
            }
        });
        fragmentMenuBinding.lilShareus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(MainActivity.HomeFragmentHandler != null)
                {
                    MainActivity.HomeFragmentHandler.sendEmptyMessage(5);
                }
                fragmentMenuBinding.iconPost.setImageResource(R.drawable.home);
                fragmentMenuBinding.iconAccounts.setImageResource(R.drawable.qr);
                fragmentMenuBinding.iconHistory.setImageResource(R.drawable.nav);
                fragmentMenuBinding.iconSettings.setImageResource(R.drawable.money);
                fragmentMenuBinding.iconRateus.setImageResource(R.drawable.chat);
                fragmentMenuBinding.iconShareus.setImageResource(R.drawable.sos1);
                fragmentMenuBinding.iconLogout.setImageResource(R.drawable.logout);

                fragmentMenuBinding.textPost.setTextColor(getResources().getColor(R.color.black));
                fragmentMenuBinding.textAccounts.setTextColor(getResources().getColor(R.color.black));
                fragmentMenuBinding.textHistory.setTextColor(getResources().getColor(R.color.black));
                fragmentMenuBinding.textSettings.setTextColor(getResources().getColor(R.color.black));
                fragmentMenuBinding.textRateus.setTextColor(getResources().getColor(R.color.black));
                fragmentMenuBinding.textShareus.setTextColor(getResources().getColor(R.color.blue));
                fragmentMenuBinding.textLogout.setTextColor(getResources().getColor(R.color.black));
            }
        });

        fragmentMenuBinding.lilLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(MainActivity.HomeFragmentHandler != null)
                {
                    MainActivity.HomeFragmentHandler.sendEmptyMessage(6);
                }
                fragmentMenuBinding.iconPost.setImageResource(R.drawable.home);
                fragmentMenuBinding.iconAccounts.setImageResource(R.drawable.qr);
                fragmentMenuBinding.iconHistory.setImageResource(R.drawable.nav);
                fragmentMenuBinding.iconSettings.setImageResource(R.drawable.money);
                fragmentMenuBinding.iconRateus.setImageResource(R.drawable.chat);
                fragmentMenuBinding.iconShareus.setImageResource(R.drawable.sos1);
                fragmentMenuBinding.iconLogout.setImageResource(R.drawable.logout);

                fragmentMenuBinding.textPost.setTextColor(getResources().getColor(R.color.black));
                fragmentMenuBinding.textAccounts.setTextColor(getResources().getColor(R.color.black));
                fragmentMenuBinding.textHistory.setTextColor(getResources().getColor(R.color.black));
                fragmentMenuBinding.textSettings.setTextColor(getResources().getColor(R.color.black));
                fragmentMenuBinding.textRateus.setTextColor(getResources().getColor(R.color.black));
                fragmentMenuBinding.textShareus.setTextColor(getResources().getColor(R.color.black));
                fragmentMenuBinding.textLogout.setTextColor(getResources().getColor(R.color.blue));
            }
        });
    }
}
