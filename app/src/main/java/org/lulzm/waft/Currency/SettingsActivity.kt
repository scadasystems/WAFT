package org.lulzm.waft.Currency

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
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
 * Date : 2019-05-12 012
 * Time : 오후 9:52
 * GitHub : https://github.com/scadasystems
 * E-mail : redsmurf@lulzm.org
 */
class SettingsActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 상태표시줄
        val view = window.decorView

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (view != null) {
                // 23 버전 이상일 때 상태바 하얀 색상, 회색 아이콘
                view.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                window.statusBarColor = Color.parseColor("#f2f2f2")
            }
        } else if (Build.VERSION.SDK_INT >= 21) {
            // 21 버전 이상일 때 상태바 검은 색상, 흰색 아이콘
            window.statusBarColor = Color.BLACK
        }

        // Display the fragment as the currency_main content.
        fragmentManager.beginTransaction()
            .replace(android.R.id.content, SettingsFragment())
            .commit()

        // Enable back navigation on action bar
        val actionBar = actionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setTitle(R.string.settings)
        }
    }

    // On options currency_item selected
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Switch on currency_item id
        when (item.itemId) {
            // Home, finish
            android.R.id.home -> finish()

            else ->

                return false
        }

        return true
    }
}
