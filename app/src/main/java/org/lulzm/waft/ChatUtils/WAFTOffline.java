package org.lulzm.waft.ChatUtils;

import android.app.Application;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

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
 * Date : 2019-04-21                                        
 * Time : 오후 11:35                                       
 * GitHub : https://github.com/scadasystems              
 * E-mail : redsmurf@lulzm.org                           
 *********************************************************/
public class WAFTOffline extends Application {

    private DatabaseReference userDatabaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser currentOnlineUser;

    @Override
    public void onCreate() {

        super.onCreate();

        //  all strings >> load offline
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        //  all images >> load offline
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this, Integer.MAX_VALUE));
        Picasso builtPicasso = builder.build();
        builtPicasso.setIndicatorsEnabled(true);
        builtPicasso.setLoggingEnabled(true);

        Picasso.setSingletonInstance(builtPicasso);


        // ONLINE STATUS
        mAuth = FirebaseAuth.getInstance();
        currentOnlineUser = mAuth.getCurrentUser();

        if (currentOnlineUser != null){
            String user_u_id = mAuth.getCurrentUser().getUid();

            userDatabaseReference
                    = FirebaseDatabase.getInstance().getReference().child("users").child(user_u_id);

            userDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    userDatabaseReference.child("active_now").onDisconnect().setValue(ServerValue.TIMESTAMP);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }


    }
}
