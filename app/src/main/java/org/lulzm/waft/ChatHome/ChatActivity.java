package org.lulzm.waft.ChatHome;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.lulzm.waft.ChatAdapter.MessageAdapter;
import org.lulzm.waft.ChatModel.Message;
import org.lulzm.waft.ChatUtils.UserLastSeenTime;
import org.lulzm.waft.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import xyz.hasnat.sweettoast.SweetToast;

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
 * Time : 오후 4:18
 * GitHub : https://github.com/scadasystems
 * E-mail : redsmurf@lulzm.org
 *********************************************************/

public class ChatActivity extends AppCompatActivity {

    private String messageReceiverID;
    private String messageReceiverName;

    private Toolbar chatToolbar;
    private TextView chatUserName;
    private TextView chatUserActiveStatus, ChatConnectionTV;
    private CircleImageView chatUserImageView;

    private DatabaseReference rootReference;

    // sending message
    private ImageView send_message, send_image;
    private EditText input_user_message;
    private FirebaseAuth mAuth;
    private String messageSenderId, download_url;

    private RecyclerView messageList_ReCyVw;
    private final List<Message> messageList = new ArrayList<>();
    // for glide error
    public RequestManager mGlideRequestManager;

    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;

    private final static int GALLERY_PICK_CODE = 2;
    private StorageReference imageMessageStorageRef;

    private ConnectivityReceiver connectivityReceiver;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);
        // for glide error
        mGlideRequestManager = Glide.with(ChatActivity.this);

        // 상태표시줄
        View view = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (view != null) {
                // 23 버전 이상일 때 상태바 하얀 색상, 회색 아이콘
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                getWindow().setStatusBarColor(Color.parseColor("#f2f2f2"));
            }
        } else if (Build.VERSION.SDK_INT >= 21) {
            // 21 버전 이상일 때 상태바 검은 색상, 흰색 아이콘
            getWindow().setStatusBarColor(Color.BLACK);
        }

        // firebase
        rootReference = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        messageSenderId = mAuth.getCurrentUser().getUid();

        messageReceiverID = getIntent().getExtras().get("visitUserId").toString();
        messageReceiverName = getIntent().getExtras().get("userName").toString();

        imageMessageStorageRef = FirebaseStorage.getInstance().getReference().child("messages_image");

        // appbar / toolbar
        chatToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(chatToolbar);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        // progressbar
        progressDialog = new ProgressDialog(ChatActivity.this);
        progressDialog.setMessage(getString(R.string.image_upload));
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
//        progressDialog.show();

        LayoutInflater layoutInflater = (LayoutInflater)
                this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.chat_appbar, null);
        actionBar.setCustomView(view);

        ChatConnectionTV = findViewById(R.id.ChatConnectionTV);
        chatUserName = findViewById(R.id.chat_user_name);
        chatUserActiveStatus = findViewById(R.id.chat_active_status);
        chatUserImageView = findViewById(R.id.chat_profile_image);

        // sending message declaration
        send_message = findViewById(R.id.c_send_message_BTN);
        send_image = findViewById(R.id.c_send_image_BTN);
        input_user_message = findViewById(R.id.c_input_message);

        // setup for showing messages
        messageAdapter = new MessageAdapter(messageList, mGlideRequestManager);
        messageList_ReCyVw = findViewById(R.id.message_list);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        messageList_ReCyVw.setLayoutManager(linearLayoutManager);
        messageList_ReCyVw.setHasFixedSize(true);
        messageList_ReCyVw.setAdapter(messageAdapter);
        messageList_ReCyVw.setNestedScrollingEnabled(false);
        fetchMessages();

        chatUserName.setText(messageReceiverName);
        rootReference.child("users").child(messageReceiverID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String active_status = dataSnapshot.child("active_now").getValue().toString();
                        final String thumb_image = dataSnapshot.child("user_thumb_image").getValue().toString();
                        // show image on appbar
                        mGlideRequestManager
                                .load(thumb_image)
                                .placeholder(R.drawable.default_profile_image)
                                .into(chatUserImageView);

                        //active status
                        if (active_status.contains("true")) {
                            chatUserActiveStatus.setText(getString(R.string.messenger_active_now));
                        } else {
                            UserLastSeenTime lastSeenTime = new UserLastSeenTime();
                            long last_seen = Long.parseLong(active_status);

                            String lastSeenOnScreenTime = lastSeenTime.getTimeAgo(last_seen, getApplicationContext());
                            Log.e("lastSeenTime", lastSeenOnScreenTime);
                            if (lastSeenOnScreenTime != null) {
                                chatUserActiveStatus.setText(lastSeenOnScreenTime);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });


        /**
         *  SEND TEXT MESSAGE BUTTON
         */
        send_message.setOnClickListener(v -> {
            sendMessage();
            // 클릭시 자동으로 스크롤 끝으로
            messageList_ReCyVw.scrollToPosition(messageAdapter.getItemCount() - 1);
        });

        /** SEND IMAGE MESSAGE BUTTON */
        send_image.setOnClickListener(v -> {
            progressDialog.show();
            Intent galleryIntent = new Intent().setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, GALLERY_PICK_CODE);
            // 클릭시 자동으로 스크롤 끝으로
            messageList_ReCyVw.scrollToPosition(messageAdapter.getItemCount() - 1);
        });
    } // ending onCreate

    @Override
    protected void onResume() {
        super.onResume();
        //Register Connectivity Broadcast receiver
        connectivityReceiver = new ConnectivityReceiver();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectivityReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unregister Connectivity Broadcast receiver
        unregisterReceiver(connectivityReceiver);
    }

    @Override // for gallery picking
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //  For image sending
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            // image message sending size compressing will be placed below
            final String message_sender_reference = "messages/" + messageSenderId + "/" + messageReceiverID;
            final String message_receiver_reference = "messages/" + messageReceiverID + "/" + messageSenderId;

            DatabaseReference user_message_key = rootReference.child("messages").child(messageSenderId).child(messageReceiverID).push();
            final String message_push_id = user_message_key.getKey();

            final StorageReference file_path = imageMessageStorageRef.child(message_push_id + ".jpg");

            UploadTask uploadTask = file_path.putFile(imageUri);
            Task<Uri> uriTask = uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    SweetToast.error(ChatActivity.this, "Error: " + task.getException().getMessage());
                }
                download_url = file_path.getDownloadUrl().toString();
                return file_path.getDownloadUrl();

            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.isSuccessful()) {
                        download_url = task.getResult().toString();
                        // 현재시간
                        long now = System.currentTimeMillis();
                        Date date = new Date(now);
                        @SuppressLint("SimpleDateFormat")
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd, HH:mm");
                        String formatDate = simpleDateFormat.format(date);

                        HashMap<String, Object> message_text_body = new HashMap<>();
                        message_text_body.put("message", download_url);
                        message_text_body.put("seen", false);
                        message_text_body.put("type", "image");
                        message_text_body.put("time", ServerValue.TIMESTAMP);
                        message_text_body.put("from", messageSenderId);
                        message_text_body.put("send_time", formatDate);
                        HashMap<String, Object> messageBodyDetails = new HashMap<>();
                        messageBodyDetails.put(message_sender_reference + "/" + message_push_id, message_text_body);
                        messageBodyDetails.put(message_receiver_reference + "/" + message_push_id, message_text_body);

                        rootReference.updateChildren(messageBodyDetails, (databaseError, databaseReference) -> {
                            if (databaseError != null) {
                                Log.e("from_image_chat: ", databaseError.getMessage());
                            }
                            input_user_message.setText("");
                            messageList_ReCyVw.scrollToPosition(messageAdapter.getItemCount() - 1);
                        });
                        Log.e("tag", "Image sent successfully");
                    } else {
                        SweetToast.warning(ChatActivity.this, getString(R.string.failed_send_message));
                    }
                }
            });
        }
    }

    private void fetchMessages() {
        rootReference.child("messages").child(messageSenderId).child(messageReceiverID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (dataSnapshot.exists()) {
                            Message message = dataSnapshot.getValue(Message.class);
                            messageList.add(message);
                            messageAdapter.notifyDataSetChanged();
                            messageList_ReCyVw.scrollToPosition(messageAdapter.getItemCount() - 1);
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }


    private void sendMessage() {
        String message = input_user_message.getText().toString();
        if (TextUtils.isEmpty(message)) {
            SweetToast.info(ChatActivity.this, getString(R.string.please_send_message));
        } else {
            String message_sender_reference = "messages/" + messageSenderId + "/" + messageReceiverID;
            String message_receiver_reference = "messages/" + messageReceiverID + "/" + messageSenderId;

            DatabaseReference user_message_key = rootReference.child("messages").child(messageSenderId).child(messageReceiverID).push();
            String message_push_id = user_message_key.getKey();
            //  현재시간
            long now = System.currentTimeMillis();
            Date date = new Date(now);
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd, HH:mm");
            String formatDate = simpleDateFormat.format(date);

            HashMap<String, Object> message_text_body = new HashMap<>();
            message_text_body.put("message", message);
            message_text_body.put("seen", false);
            message_text_body.put("type", "text");
            message_text_body.put("time", ServerValue.TIMESTAMP);
            message_text_body.put("from", messageSenderId);
            message_text_body.put("send_time", formatDate);

            HashMap<String, Object> messageBodyDetails = new HashMap<>();
            messageBodyDetails.put(message_sender_reference + "/" + message_push_id, message_text_body);
            messageBodyDetails.put(message_receiver_reference + "/" + message_push_id, message_text_body);

            rootReference.updateChildren(messageBodyDetails, (databaseError, databaseReference) -> {

                if (databaseError != null) {
                    Log.e("Sending message", databaseError.getMessage());
                }
                input_user_message.setText("");
            });
        }
    }


    // Broadcast receiver for network checking
    public class ConnectivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            @SuppressLint("MissingPermission")
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            ChatConnectionTV.setVisibility(View.GONE);
            if (networkInfo != null && networkInfo.isConnected()) {
                ChatConnectionTV.setText(getString(R.string.internet_connect));
                ChatConnectionTV.setTextColor(Color.WHITE);
                ChatConnectionTV.setVisibility(View.VISIBLE);

                // LAUNCH activity after certain time period
                new Timer().schedule(new TimerTask() {
                    public void run() {
                        ChatActivity.this.runOnUiThread(() -> ChatConnectionTV.setVisibility(View.GONE));
                    }
                }, 1200);
            } else {
                ChatConnectionTV.setText(getString(R.string.internet_disconnect));
                ChatConnectionTV.setTextColor(Color.WHITE);
                ChatConnectionTV.setBackgroundColor(Color.RED);
                ChatConnectionTV.setVisibility(View.VISIBLE);
            }
        }
    }

    // editText clearFocus
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

}