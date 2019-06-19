package org.lulzm.waft.mainFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.airbnb.lottie.LottieAnimationView;
import org.lulzm.waft.R;

public class Fragment4 extends Fragment {
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //  View view = inflater.inflate(R.layout.fragment1, container);
        View view=inflater.inflate(R.layout.fragment4, container,false);

        LottieAnimationView lottie_intro = view.findViewById(R.id.intro_animation);
        lottie_intro.setAnimation("exchange_money.json");
        lottie_intro.playAnimation();
        // setListAdapter(new MenuListAdapter(R.layout.row_menu_action_item, getActivity(), MenuActionItem.values()));
        return view;
    }
}
