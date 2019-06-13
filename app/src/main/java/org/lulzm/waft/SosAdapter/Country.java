package org.lulzm.waft.SosAdapter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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
 * Time : 오후 12:40                                       
 * GitHub : https://github.com/scadasystems              
 * E-mail : redsmurf@lulzm.org                           
 *********************************************************/
public class Country {
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("ISOCode")
    @Expose
    private String iSOCode;
    @SerializedName("ISONumeric")
    @Expose
    private String iSONumeric;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getISOCode() {
        return iSOCode;
    }

    public void setISOCode(String iSOCode) {
        this.iSOCode = iSOCode;
    }

    public String getISONumeric() {
        return iSONumeric;
    }

    public void setISONumeric(String iSONumeric) {
        this.iSONumeric = iSONumeric;
    }
}
