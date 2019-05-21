package org.lulzm.waft.MainFragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import org.lulzm.waft.R;

public class Fragment5 extends Fragment {

    private ListView slistview;

    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //  View view = inflater.inflate(R.layout.fragment1, container);
         View view=inflater.inflate(R.layout.fragment5, container,false);

        // setListAdapter(new MenuListAdapter(R.layout.row_menu_action_item, getActivity(), MenuActionItem.values()));
        return view;
    }



}
