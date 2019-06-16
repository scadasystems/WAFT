package org.lulzm.waft.SosAdapter

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

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
 * Time : 오후 12:40
 * GitHub : https://github.com/scadasystems
 * E-mail : redsmurf@lulzm.org
 */
class Datum {
    @SerializedName("Country")
    @Expose
    var country: Country? = null
    @SerializedName("Ambulance")
    @Expose
    var ambulance: Ambulance? = null
    @SerializedName("Fire")
    @Expose
    var fire: Fire? = null
    @SerializedName("Police")
    @Expose
    var police: Police? = null
}
