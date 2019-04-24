package org.lulzm.waft.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import org.lulzm.waft.R;

public class Fragment5 extends Fragment {
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //  View view = inflater.inflate(R.layout.fragment1, container);
        View view=inflater.inflate(R.layout.chat_main_activity, container,false);
        // setListAdapter(new MenuListAdapter(R.layout.row_menu_action_item, getActivity(), MenuActionItem.values()));
        return view;
    }
}
