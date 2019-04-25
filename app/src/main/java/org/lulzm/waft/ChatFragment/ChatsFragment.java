package org.lulzm.waft.ChatFragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import de.hdodenhof.circleimageview.CircleImageView;
import org.lulzm.waft.ChatHome.ChatActivity;
import org.lulzm.waft.ChatModel.Friends;
import org.lulzm.waft.ChatUtils.UserLastSeenTime;
import org.lulzm.waft.R;

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
 * Time : 오후 4:09                                       
 * GitHub : https://github.com/scadasystems              
 * E-mail : redsmurf@lulzm.org                           
 *********************************************************/
public class ChatsFragment extends Fragment {

    private View view;
    private RecyclerView chat_list;

    private DatabaseReference friendsDatabaseReference;
    private DatabaseReference userDatabaseReference;
    private FirebaseAuth mAuth;

    String current_user_id;

    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.chat_fragment, container, false);

        chat_list = view.findViewById(R.id.chatList);

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();

        friendsDatabaseReference = FirebaseDatabase.getInstance().getReference().child("friends").child(current_user_id);
        userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");

        chat_list.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        chat_list.setLayoutManager(linearLayoutManager);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Friends> recyclerOptions = new FirebaseRecyclerOptions.Builder<Friends>()
                .setQuery(friendsDatabaseReference, Friends.class)
                .build();

        FirebaseRecyclerAdapter<Friends, ChatsVH> adapter = new FirebaseRecyclerAdapter<Friends, ChatsVH>(recyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final ChatsVH holder, int position, @NonNull Friends model) {
                final String userID = getRef(position).getKey();
                userDatabaseReference.child(userID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            final String userName = dataSnapshot.child("user_name").getValue().toString();
                            final String userPresence = dataSnapshot.child("active_now").getValue().toString();
                            final String userThumbPhoto = dataSnapshot.child("user_thumb_image").getValue().toString();

                            if (!userThumbPhoto.equals("default_image")) { // default image condition for new user
                                Glide.with(ChatsFragment.this)
                                        .load(userThumbPhoto)
                                        .placeholder(R.drawable.default_profile_image)
                                        .into(holder.user_photo);
                            }
                            holder.user_name.setText(userName);

                            //active status
                            holder.active_icon.setVisibility(View.GONE);
                            if (userPresence.contains("true")) {
                                holder.user_presence.setText(getString(R.string.messenger_active_now));
                                holder.active_icon.setVisibility(View.VISIBLE);
                            } else {
                                holder.active_icon.setVisibility(View.GONE);
                                UserLastSeenTime lastSeenTime = new UserLastSeenTime();
                                long last_seen = Long.parseLong(userPresence);
                                String lastSeenOnScreenTime = lastSeenTime.getTimeAgo(last_seen, getContext());
                                Log.e("lastSeenTime", lastSeenOnScreenTime);
                                if (lastSeenOnScreenTime != null) {
                                    holder.user_presence.setText(lastSeenOnScreenTime);
                                }
                            }

                            holder.itemView.setOnClickListener(v -> {
                                // user active status validation
                                if (dataSnapshot.child("active_now").exists()) {
                                    Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                    chatIntent.putExtra("visitUserId", userID);
                                    chatIntent.putExtra("userName", userName);
                                    startActivity(chatIntent);
                                } else {
                                    userDatabaseReference.child(userID).child("active_now")
                                            .setValue(ServerValue.TIMESTAMP).addOnSuccessListener(aVoid -> {
                                                Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                                chatIntent.putExtra("visitUserId", userID);
                                                chatIntent.putExtra("userName", userName);
                                                startActivity(chatIntent);
                                            });
                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

            }

            @NonNull
            @Override
            public ChatsVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_all_single_profile_display, viewGroup, false);
                return new ChatsVH(view);
            }
        };
        chat_list.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ChatsVH extends RecyclerView.ViewHolder{
        TextView user_name, user_presence;
        CircleImageView user_photo;
        ImageView active_icon;
        public ChatsVH(View itemView) {
            super(itemView);
            user_name = itemView.findViewById(R.id.all_user_name);
            user_photo = itemView.findViewById(R.id.all_user_profile_img);
            user_presence = itemView.findViewById(R.id.all_user_status);
            active_icon = itemView.findViewById(R.id.activeIcon);
        }
    }


}
