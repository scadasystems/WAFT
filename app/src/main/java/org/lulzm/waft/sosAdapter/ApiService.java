package org.lulzm.waft.sosAdapter;

import retrofit2.Call;
import retrofit2.http.GET;

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
 * Date : 2019-06-13 013                                        
 * Time : 오후 12:39                                       
 * GitHub : https://github.com/scadasystems              
 * E-mail : redsmurf@lulzm.org                           
 *********************************************************/
public interface ApiService {
    @GET("emergency-json/db.json")
    Call<SosList> getMyJSON();
}
