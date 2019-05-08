package org.lulzm.waft.MainFragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import org.lulzm.waft.R;

import java.util.ArrayList;

public class SettingAdapter extends BaseAdapter {

    private ArrayList<MyItem> myItem = new ArrayList<>();

    @Override
    public int getCount() {
        return myItem.size();
    }

    @Override
    public Object getItem(int position) {
        return myItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Context context = parent.getContext();

        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.slistview_custom,parent,false);
        }

        ImageView setting_icon = convertView.findViewById(R.id.setting_icon);
        TextView setting_name = convertView.findViewById(R.id.setting_name);

        MyItem myItem = (MyItem) getItem(position);

        setting_icon.setImageDrawable(myItem.getIcon());
        setting_name.setText(myItem.getName());

        return convertView;
    }

    public void addItem(Drawable img, String name){
        MyItem mItem = new MyItem();

        mItem.setIcon(img);
        mItem.setName(name);

        myItem.add(mItem);


    }
}
