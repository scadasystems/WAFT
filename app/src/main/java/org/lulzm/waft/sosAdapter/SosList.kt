package org.lulzm.waft.sosAdapter

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

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
 * Date : 2019-06-13 013
 * Time : 오후 12:41
 * GitHub : https://github.com/scadasystems
 * E-mail : redsmurf@lulzm.org
 */
class SosList {
    @SerializedName("data")
    @Expose
    var data: ArrayList<Datum>? = null
}
