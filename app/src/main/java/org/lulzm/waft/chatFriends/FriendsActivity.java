package org.lulzm.waft.chatFriends;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.lulzm.waft.chatHome.ChatActivity;
import org.lulzm.waft.chatModel.Friends;
import org.lulzm.waft.chatProfile.ChatProfileActivity;
import org.lulzm.waft.R;

import de.hdodenhof.circleimageview.CircleImageView;

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
 * Time : 오후 11:14                                       
 * GitHub : https://github.com/scadasystems              
 * E-mail : redsmurf@lulzm.org                           
 *********************************************************/
public class FriendsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView friend_list_RV;

    private DatabaseReference friendsDatabaseReference;
    private DatabaseReference userDatabaseReference;
    private FirebaseAuth mAuth;

    String current_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 다크모드 적용
        SharedPreferences sharedPreferences = getSharedPreferences("change_theme", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("dark_theme", false)) {
            setTheme(R.style.darktheme);
        } else {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity_friends);

        // 상태표시줄 색상 변경
        View view = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 23 버전 이상일 때 상태바 하얀 색상, 회색 아이콘
            if (sharedPreferences.getBoolean("dark_theme", false)) {
                getWindow().setStatusBarColor(Color.BLACK);
            } else {
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                getWindow().setStatusBarColor(Color.parseColor("#f2f2f2"));
            }
        } else if (Build.VERSION.SDK_INT >= 21) {
            // 21 버전 이상일 때 상태바 검은 색상, 흰색 아이콘
            getWindow().setStatusBarColor(Color.BLACK);
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Friends");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();

        friendsDatabaseReference = FirebaseDatabase.getInstance().getReference().child("friends").child(current_user_id);
        friendsDatabaseReference.keepSynced(true); // for offline

        userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        userDatabaseReference.keepSynced(true); // for offline

        // Setup recycler view
        friend_list_RV = findViewById(R.id.friendList);
        friend_list_RV.setHasFixedSize(true);
        friend_list_RV.setLayoutManager(new LinearLayoutManager(this));

        showPeopleList();
    }

    private void showPeopleList() {
        FirebaseRecyclerOptions<Friends> recyclerOptions = new FirebaseRecyclerOptions.Builder<Friends>()
                .setQuery(friendsDatabaseReference, Friends.class)
                .build();

        FirebaseRecyclerAdapter<Friends, FriendsVH> recyclerAdapter = new FirebaseRecyclerAdapter<Friends, FriendsVH>(recyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final FriendsVH holder, int position, @NonNull Friends model) {
                holder.date.setText(getString(R.string.friendshipDate) + model.getDate());

                final String userID = getRef(position).getKey();

                userDatabaseReference.child(userID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        final String userName = dataSnapshot.child("user_name").getValue().toString();
                        String userThumbPhoto = dataSnapshot.child("user_thumb_image").getValue().toString();
                        String active_status = dataSnapshot.child("active_now").getValue().toString();

                        // online active status
                        holder.active_icon.setVisibility(View.GONE);
                        if (active_status.contains("active_now")) {
                            holder.active_icon.setVisibility(View.VISIBLE);
                        } else {
                            holder.active_icon.setVisibility(View.GONE);
                        }

                        holder.name.setText(userName);
                        Glide.with(getApplicationContext())
                                .load(userThumbPhoto)
                                .placeholder(R.drawable.default_profile_image)
                                .into(holder.profile_thumb);

                        //click currency_item, 2 options in a dialogue will be appear
                        holder.itemView.setOnClickListener(v -> {
                            CharSequence options[] = new CharSequence[]{getString(R.string.send_message), userName + getString(R.string.users_profile)};

                            SharedPreferences sharedPreferences = getSharedPreferences("change_theme", MODE_PRIVATE);

                            if (sharedPreferences.getBoolean("dark_theme", false)) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(FriendsActivity.this);
                                builder.setItems(options, (dialog, which) -> {
                                    if (which == 0) {
                                        // user active status validation
                                        if (dataSnapshot.child("active_now").exists()) {

                                            Intent chatIntent = new Intent(FriendsActivity.this, ChatActivity.class);
                                            chatIntent.putExtra("visitUserId", userID);
                                            chatIntent.putExtra("userName", userName);
                                            startActivity(chatIntent);

                                        } else {
                                            userDatabaseReference.child(userID).child("active_now")
                                                    .setValue(ServerValue.TIMESTAMP).addOnSuccessListener(aVoid -> {
                                                Intent chatIntent = new Intent(FriendsActivity.this, ChatActivity.class);
                                                chatIntent.putExtra("visitUserId", userID);
                                                chatIntent.putExtra("userName", userName);
                                                startActivity(chatIntent);
                                            });
                                        }
                                    }
                                    if (which == 1) {
                                        Intent profileIntent = new Intent(FriendsActivity.this, ChatProfileActivity.class);
                                        profileIntent.putExtra("visitUserId", userID);
                                        startActivity(profileIntent);
                                    }
                                });
                                builder.show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }

            @NonNull
            @Override
            public FriendsVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_all_single_profile_display, viewGroup, false);
                return new FriendsVH(view);
            }
        };

        friend_list_RV.setAdapter(recyclerAdapter);
        recyclerAdapter.startListening();
    }

    public static class FriendsVH extends RecyclerView.ViewHolder {
        public TextView name;
        TextView date;
        CircleImageView profile_thumb;
        ImageView active_icon;

        public FriendsVH(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.all_user_name);
            date = itemView.findViewById(R.id.all_user_status);
            profile_thumb = itemView.findViewById(R.id.all_user_profile_img);
            active_icon = itemView.findViewById(R.id.activeIcon);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
