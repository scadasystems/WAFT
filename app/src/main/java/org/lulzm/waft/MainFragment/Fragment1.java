package org.lulzm.waft.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import org.lulzm.waft.R;

public class Fragment1 extends Fragment {

    ImageView qr_icon, nav_icon, money_icon, chat_icon, notic1, notic2 ;

    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //  View view = inflater.inflate(R.layout.fragment1, container);
        View view=inflater.inflate(R.layout.fragment1, container,false);

        qr_icon=view.findViewById(R.id.qr_icon);
        nav_icon=view.findViewById(R.id.nav_icon);
        money_icon=view.findViewById(R.id.money_icon);
        chat_icon=view.findViewById(R.id.chat_icon);

        Glide.with(this).load(R.drawable.qr_bt).into(qr_icon);
        Glide.with(this).load(R.drawable.nav_bt).into(nav_icon);
        Glide.with(this).load(R.drawable.money_blue2).into(money_icon);
        Glide.with(this).load(R.drawable.chat_bt).into(chat_icon);
/*        Glide.with(this).load(R.drawable.qr_blue).into(qr_icon);
        Glide.with(this).load(R.drawable.qr_blue).into(qr_icon);*/


        // setListAdapter(new MenuListAdapter(R.layout.row_menu_action_item, getActivity(), MenuActionItem.values()));
        return view;
    }
}
