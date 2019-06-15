package org.lulzm.waft.ChatAdapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import org.lulzm.waft.ChatFragment.ChatsFragment
import org.lulzm.waft.ChatFragment.RequestsFragment
import org.lulzm.waft.R

/*********************************************************
 * $$\                  $$\             $$\      $$\
 * $$ |                 $$ |            $$$\    $$$ |
 * $$ |      $$\   $$\  $$ | $$$$$$$$\  $$$$\  $$$$ |
 * $$ |      $$ |  $$ | $$ | \____$$  | $$ \$\$$ $$ |
 * $$ |      $$ |  $$ | $$ |   $$$$ _/  $$  \$$  $$ |
 * $$ |      $$ |  $$ | $$ |  $$  _/    $$ | $  /$$ |
 * $$$$$$$$  \$$$$$$$ | $$ | $$$$$$$$\  $$ | \_/ $$ |
 * \_______| \______/   \__| \________| \__|     \__|
 *
 * Project : WAFT
 * Created by Android Studio
 * Developer : Lulz_M
 * Date : 2019-04-19
 * Time : 오후 4:08
 * GitHub : https://github.com/scadasystems
 * E-mail : redsmurf@lulzm.org
 */
class TabsPagerAdapter(fm: FragmentManager, internal var context: Context) :
    FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                return ChatsFragment()
            }
            1 -> {
                return RequestsFragment()
            }
            else -> return Fragment()
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
            0 -> return context.getString(R.string.chat_room) // ChatsFragment
            1 -> return context.getString(R.string.request_room) // RequestsFragment
            else -> return null
        }
    }
}
