package org.lulzm.waft.ChatAdapter;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.makeramen.roundedimageview.RoundedImageView;
import de.hdodenhof.circleimageview.CircleImageView;
import org.lulzm.waft.ChatModel.Message;
import org.lulzm.waft.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{
    private List<Message> messageList;
    // firebase
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.chat_item_messages, parent, false);
        mAuth = FirebaseAuth.getInstance();
        return new MessageViewHolder(view);
    }

    @SuppressLint("RtlHardcoded")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {
        String sender_UID = mAuth.getCurrentUser().getUid();
        Message message = messageList.get(position);
        String from_user_ID = message.getFrom();
        String from_message_TYPE = message.getType();
        String send_time = message.getSend_time();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(from_user_ID);
        databaseReference.keepSynced(true); // for offline
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String userName = dataSnapshot.child("user_name").getValue().toString();
                    String userProfileImage = dataSnapshot.child("user_thumb_image").getValue().toString();

                    Glide.with(holder.user_profile_image.getContext())
                            .load(userProfileImage)
                            .into(holder.user_profile_image);

                    holder.chat_sender.setText(userName);
                    holder.chat_time_stamp.setText(send_time);
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        // if message type is TEXT
        if (from_message_TYPE.equals("text")){
            if (from_user_ID.equals(sender_UID)){
                /* 변경 */
                holder.chatItemLayout.setGravity(Gravity.RIGHT);
                holder.chat_background.setBackgroundColor(Color.rgb(58, 205, 255));
                holder.user_profile_image.setVisibility(View.GONE);
                holder.chat_sender.setVisibility(View.GONE);
                holder.chat_message.setText(message.getMessage());
            } else {
                /* 변경 */
                holder.chatItemLayout.setGravity(Gravity.LEFT);
                holder.chat_background.setBackgroundColor(Color.WHITE);
                holder.user_profile_image.setVisibility(View.VISIBLE);
                holder.chat_sender.setVisibility(View.VISIBLE);
                holder.chat_message.setText(message.getMessage());
            }
        }
        // if message type is image
        if (from_message_TYPE.equals("image")){
            if (from_user_ID.equals(sender_UID)){
                /* 변경 */
                holder.chatItemLayout.setGravity(Gravity.RIGHT);
                holder.chat_background.setBackgroundColor(Color.rgb(58, 205, 255));
                holder.user_profile_image.setVisibility(View.GONE);
                holder.chat_sender.setVisibility(View.GONE);
                holder.chat_message.setVisibility(View.GONE);
                holder.chat_message_image.setVisibility(View.VISIBLE);
                Glide.with(holder.chat_message_image.getContext())
                        .load(message.getMessage())
                        .into(holder.chat_message_image);

                Log.e("tag","from adapter, link : "+ message.getMessage());

            } else {
                /* 변경*/
                holder.chatItemLayout.setGravity(Gravity.LEFT);
                holder.chat_background.setBackgroundColor(Color.WHITE);
                holder.user_profile_image.setVisibility(View.VISIBLE);
                holder.chat_sender.setVisibility(View.VISIBLE);
                holder.chat_message.setVisibility(View.GONE);
                holder.chat_message_image.setVisibility(View.VISIBLE);
                Glide.with(holder.chat_message_image.getContext())
                        .load(message.getMessage())
                        .into(holder.chat_message_image);

                Log.e("tag","from adapter, link : "+ message.getMessage());
            }
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{
        TextView chat_sender, chat_message, chat_time_stamp;
        LinearLayout chatItemLayout, chat_background;
        RoundedImageView chat_message_image;
        CircleImageView user_profile_image;

        MessageViewHolder(View view){
            super(view);
            chat_background = view.findViewById(R.id.chat_background);
            chat_sender = view.findViewById(R.id.chat_sender);
            chat_message = view.findViewById(R.id.chat_message);
            chat_time_stamp = view.findViewById(R.id.chat_time_stamp);
            chatItemLayout = view.findViewById(R.id.list_item_message_layout);
            chat_message_image = view.findViewById(R.id.chat_message_image);
            user_profile_image = view.findViewById(R.id.messageUserImage);
        }
    }


}