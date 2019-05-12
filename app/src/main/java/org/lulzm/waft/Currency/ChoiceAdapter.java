package org.lulzm.waft.Currency;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import org.lulzm.waft.R;

import java.util.List;

/*********************************************************
 *   $$\                  $$\             $$\      $$\   
 *   $$ |                 $$ |            $$$\    $$$ |  
 *   $$ |      $$\   $$\  $$ | $$$$$$$$\  $$$$\  $$$$ |  
 *   $$ |      $$ |  $$ | $$ | \____$$  | $$ \$\$$ $$ | 
 *   $$ |      $$ |  $$ | $$ |   $$$$ _/  $$  \$$  $$ |  
 *   $$ |      $$ |  $$ | $$ |  $$  _/    $$ | $  /$$ |  
 *   $$$$$$$$  \$$$$$$$ | $$ | $$$$$$$$\  $$ | \_/ $$ |  
 *   \_______| \______/   \__| \________| \__|     \__|  
 *
 * Project : WAFT                             
 * Created by Android Studio                           
 * Developer : Lulz_M                                    
 * Date : 2019-05-12 012                                        
 * Time : 오후 9:35                                       
 * GitHub : https://github.com/scadasystems              
 * E-mail : redsmurf@lulzm.org                           
 *********************************************************/
public class ChoiceAdapter extends BaseAdapter {
    private LayoutInflater inflater;

    private List<Integer> flags;
    private List<Integer> longNames;
    private List<Integer> selection;
    private List<String> names;
    private List<String> symbols;

    private int resource;

    // Constructor
    public ChoiceAdapter(Context context, int resource, List<Integer> flags,
                         List<String> names, List<Integer> longNames,
                         List<Integer> selection) {
        inflater = LayoutInflater.from(context);

        // Save all the parameters
        this.resource = resource;
        this.flags = flags;
        this.names = names;
        this.longNames = longNames;
        this.selection = selection;
    }

    @Override
    public int getCount() {
        return names.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // Create a new View for each currency_item referenced by the adapter
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView flag;
        TextView name;
        TextView longName;

        // Create a new view
        if (convertView == null)
            convertView = inflater.inflate(resource, parent, false);

        // Find the views
        flag = convertView.findViewById(R.id.flag);
        name = convertView.findViewById(R.id.name);
        longName = convertView.findViewById(R.id.long_name);

        // Update the views
        if (flag != null)
            flag.setImageResource(flags.get(position));

        if (name != null)
            name.setText(names.get(position));

        if (longName != null)
            longName.setText(longNames.get(position));

        // Highlight if selected
        if (selection.contains(position))
            convertView.setBackgroundResource(android.R.color.holo_blue_dark);

            // Clear highlight
        else
            convertView.setBackgroundResource(0);

        return convertView;
    }
}
