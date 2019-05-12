package org.lulzm.waft.ChatFragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import de.hdodenhof.circleimageview.CircleImageView;
import org.lulzm.waft.ChatModel.Requests;
import org.lulzm.waft.ChatProfile.ChatProfileActivity;
import org.lulzm.waft.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class RequestsFragment extends Fragment {

    private View view;
    private RecyclerView request_list;
    private Context context;

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    String user_UId;
    private DatabaseReference userDatabaseReference;

    // for accept and cancel mechanism
    private DatabaseReference friendsDatabaseReference;
    private DatabaseReference friendReqDatabaseReference;


    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.chat_fragment_requests, container, false);

        request_list = view.findViewById(R.id.requestList);
        request_list.setHasFixedSize(true);

        mAuth = FirebaseAuth.getInstance();
        user_UId = mAuth.getCurrentUser().getUid();
        userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("friend_requests").child(user_UId);

        friendsDatabaseReference = FirebaseDatabase.getInstance().getReference().child("friends");
        friendReqDatabaseReference = FirebaseDatabase.getInstance().getReference().child("friend_requests");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        //linearLayoutManager.setStackFromEnd(true);
        request_list.setHasFixedSize(true);
        request_list.setLayoutManager(linearLayoutManager);

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Requests> recyclerOptions = new FirebaseRecyclerOptions.Builder<Requests>()
                .setQuery(databaseReference, Requests.class)
                .build();

        FirebaseRecyclerAdapter<Requests, RequestsVH> adapter = new FirebaseRecyclerAdapter<Requests, RequestsVH>(recyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final RequestsVH holder, int position, @NonNull Requests model) {
                final String userID = getRef(position).getKey();
                // handling accept/cancel button
                DatabaseReference getTypeReference = getRef(position).child("request_type").getRef();
                getTypeReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String requestType = dataSnapshot.getValue().toString();
                            holder.verified_icon.setVisibility(View.GONE);

                            if (requestType.equals("received")) {
                                holder.re_icon.setVisibility(View.VISIBLE);
                                holder.se_icon.setVisibility(View.GONE);
                                userDatabaseReference.child(userID).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final String userName = dataSnapshot.child("user_name").getValue().toString();
                                        final String userVerified = dataSnapshot.child("verified").getValue().toString();
                                        final String userThumbPhoto = dataSnapshot.child("user_thumb_image").getValue().toString();
                                        final String user_status = dataSnapshot.child("user_status").getValue().toString();

                                        holder.name.setText(userName);
                                        holder.status.setText(user_status);

                                        if (!userThumbPhoto.equals("default_image")) { // default image condition for new user
                                            Glide.with(getContext())
                                                    .load(userThumbPhoto)
                                                    .placeholder(R.drawable.default_profile_image)
                                                    .into(holder.user_photo);

//                                            Picasso.get()
//                                                    .load(userThumbPhoto)
//                                                    .networkPolicy(NetworkPolicy.OFFLINE) // for Offline
//                                                    .placeholder(R.drawable.default_profile_image)
//                                                    .into(holder.user_photo, new Callback() {
//                                                        @Override
//                                                        public void onSuccess() {
//                                                        }
//
//                                                        @Override
//                                                        public void onError(Exception e) {
//                                                            Picasso.get()
//                                                                    .load(userThumbPhoto)
//                                                                    .placeholder(R.drawable.default_profile_image)
//                                                                    .into(holder.user_photo);
//                                                        }
//                                                    });
                                        }

                                        if (userVerified.contains("true")) {
                                            holder.verified_icon.setVisibility(View.VISIBLE);
                                        }

                                        holder.itemView.setOnClickListener(v -> {
                                            CharSequence options[] = new CharSequence[]{"Accept Request", "Cancel Request", userName + "'s profile"};

                                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                            builder.setItems(options, (dialog, which) -> {

                                                if (which == 0) {
                                                    Calendar myCalendar = Calendar.getInstance();
                                                    SimpleDateFormat currentDate = new SimpleDateFormat("EEEE, dd MMM, yyyy");
                                                    final String friendshipDate = currentDate.format(myCalendar.getTime());

                                                    friendsDatabaseReference.child(user_UId).child(userID).child("date").setValue(friendshipDate)
                                                            .addOnCompleteListener(task -> friendsDatabaseReference.child(userID).child(user_UId).child("date").setValue(friendshipDate)
                                                                    .addOnCompleteListener(task1 -> {
                                                                        /**
                                                                         *  because of accepting friend request,
                                                                         *  there have no more request them. So, for delete these node
                                                                         */
                                                                        friendReqDatabaseReference.child(user_UId).child(userID).removeValue()
                                                                                .addOnCompleteListener(task11 -> {
                                                                                    if (task11.isSuccessful()) {
                                                                                        // delete from users friend_requests node, receiver >> sender > values
                                                                                        friendReqDatabaseReference.child(userID).child(user_UId).removeValue()
                                                                                                .addOnCompleteListener(task111 -> {
                                                                                                    if (task111.isSuccessful()) {
                                                                                                        // after deleting data
                                                                                                        @SuppressLint("WrongConstant")
                                                                                                        Snackbar snackbar = Snackbar
                                                                                                                .make(view, getString(org.lulzm.waft.R.string.now_your_friend), Snackbar.LENGTH_LONG);
                                                                                                        // Changing message currency_text color
                                                                                                        View sView = snackbar.getView();
                                                                                                        sView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                                                                                                        TextView textView = sView.findViewById(com.google.android.material.R.id.snackbar_text);
                                                                                                        textView.setTextColor(Color.WHITE);
                                                                                                        snackbar.show();
                                                                                                    }
                                                                                                });

                                                                                    }
                                                                                }); //

                                                                    }));
                                                }


                                                if (which == 1) {
                                                    //for cancellation, delete data from user nodes
                                                    // delete from, sender >> receiver > values
                                                    friendReqDatabaseReference.child(user_UId).child(userID).removeValue()
                                                            .addOnCompleteListener(task -> {
                                                                if (task.isSuccessful()) {
                                                                    // delete from, receiver >> sender > values
                                                                    friendReqDatabaseReference.child(userID).child(user_UId).removeValue()
                                                                            .addOnCompleteListener(task12 -> {
                                                                                if (task12.isSuccessful()) {
                                                                                    //Toast.makeText(getActivity(), "Cancel Request", Toast.LENGTH_SHORT).show();
                                                                                    @SuppressLint("WrongConstant")
                                                                                    Snackbar snackbar = Snackbar
                                                                                            .make(view, getString(org.lulzm.waft.R.string.cancel_request), Snackbar.LENGTH_LONG);
                                                                                    // Changing message currency_text color
                                                                                    View sView = snackbar.getView();
                                                                                    sView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                                                                                    TextView textView = sView.findViewById(com.google.android.material.R.id.snackbar_text);
                                                                                    textView.setTextColor(Color.WHITE);
                                                                                    snackbar.show();

                                                                                }
                                                                            });

                                                                }
                                                            });
                                                }
                                                if (which == 2) {
                                                    Intent profileIntent = new Intent(getContext(), ChatProfileActivity.class);
                                                    profileIntent.putExtra("visitUserId", userID);
                                                    startActivity(profileIntent);
                                                }

                                            });
                                            builder.show();
                                        });
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                            if (requestType.equals("sent")) {
                                holder.re_icon.setVisibility(View.GONE);
                                holder.se_icon.setVisibility(View.VISIBLE);
                                userDatabaseReference.child(userID).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final String userName = dataSnapshot.child("user_name").getValue().toString();
                                        final String userVerified = dataSnapshot.child("verified").getValue().toString();
                                        final String userThumbPhoto = dataSnapshot.child("user_thumb_image").getValue().toString();
                                        final String user_status = dataSnapshot.child("user_status").getValue().toString();

                                        holder.name.setText(userName);
                                        holder.status.setText(user_status);

                                        if (!userThumbPhoto.equals("default_image")) { // default image condition for new user
                                            Glide.with(getContext())
                                                    .load(userThumbPhoto)
                                                    .placeholder(R.drawable.default_profile_image)
                                                    .into(holder.user_photo);
                                        }

                                        if (userVerified.contains("true")) {
                                            holder.verified_icon.setVisibility(View.VISIBLE);
                                        }

                                        holder.itemView.setOnClickListener(v -> {
                                            CharSequence options[] = new CharSequence[]{getString(R.string.cancel_friend_request), userName + getString(org.lulzm.waft.R.string.users_profile)};

                                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                            builder.setItems(options, (dialog, which) -> {
                                                if (which == 0) {
                                                    //for cancellation, delete data from user nodes
                                                    // delete from, sender >> receiver > values
                                                    friendReqDatabaseReference.child(user_UId).child(userID).removeValue()
                                                            .addOnCompleteListener(task -> {
                                                                if (task.isSuccessful()) {
                                                                    // delete from, receiver >> sender > values
                                                                    friendReqDatabaseReference.child(userID).child(user_UId).removeValue()
                                                                            .addOnCompleteListener(task13 -> {
                                                                                if (task13.isSuccessful()) {
                                                                                    @SuppressLint("WrongConstant")
                                                                                    Snackbar snackbar = Snackbar
                                                                                            .make(view, getString(R.string.cancel_friend_request), Snackbar.LENGTH_LONG);
                                                                                    // Changing message currency_text color
                                                                                    View sView = snackbar.getView();
                                                                                    sView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                                                                                    TextView textView = sView.findViewById(com.google.android.material.R.id.snackbar_text);
                                                                                    textView.setTextColor(Color.WHITE);
                                                                                    snackbar.show();
                                                                                }
                                                                            });

                                                                }
                                                            });
                                                }
                                                if (which == 1) {
                                                    Intent profileIntent = new Intent(getContext(), ChatProfileActivity.class);
                                                    profileIntent.putExtra("visitUserId", userID);
                                                    startActivity(profileIntent);
                                                }
                                            });
                                            builder.show();
                                        });
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public RequestsVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_request_single, viewGroup, false);
                return new RequestsVH(view);
            }
        };
        request_list.setAdapter(adapter);
        adapter.startListening();
    }

    public static class RequestsVH extends RecyclerView.ViewHolder {
        TextView name, status;
        CircleImageView user_photo;
        ImageView re_icon, se_icon, verified_icon;

        public RequestsVH(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.r_profileName);
            user_photo = itemView.findViewById(R.id.r_profileImage);
            status = itemView.findViewById(R.id.r_profileStatus);
            re_icon = itemView.findViewById(R.id.receivedIcon);
            se_icon = itemView.findViewById(R.id.sentIcon);
            verified_icon = itemView.findViewById(R.id.verifiedIcon);
        }
    }

}