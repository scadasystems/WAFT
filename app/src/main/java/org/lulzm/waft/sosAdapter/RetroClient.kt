package org.lulzm.waft.sosAdapter

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
object RetroClient {
    private val ROOT_URL = "https://storage.googleapis.com/"

    private val retrofitInstance: Retrofit
        get() = Retrofit.Builder()
            .baseUrl(ROOT_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val apiService: ApiService
        get() = retrofitInstance.create(ApiService::class.java)
}
