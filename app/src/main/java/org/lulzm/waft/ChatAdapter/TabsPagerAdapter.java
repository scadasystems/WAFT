package org.lulzm.waft.ChatAdapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import org.lulzm.waft.ChatFragment.ChatsFragment;

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
 * Date : 2019-04-19                                        
 * Time : 오후 4:08                                       
 * GitHub : https://github.com/scadasystems              
 * E-mail : redsmurf@lulzm.org                           
 *********************************************************/
public class TabsPagerAdapter extends FragmentPagerAdapter {


    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                ChatsFragment chatsFragment = new ChatsFragment();
                return chatsFragment;
            case 1:
                RequestsFragment requestsFragment = new RequestsFragment();
                return requestsFragment;
            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return 2; // 2 is total fragment number (e.x- Chats, Requests)
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "CHATS"; // ChatsFragment
            case 1:
                return "REQUESTS"; // ttttRequestsFragment
            default:
                return null;
        }
        //return super.getPageTitle(position);
    }
}
