package org.lulzm.waft.ChatAdapter


import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.makeramen.roundedimageview.RoundedImageView
import de.hdodenhof.circleimageview.CircleImageView
import org.lulzm.waft.ChatModel.Message
import org.lulzm.waft.R

class MessageAdapter(
    private val messageList: List<Message>, // for glide error
    var mGlideRequestManager: RequestManager
) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {
    // firebase
    private var mAuth: FirebaseAuth? = null
    private var databaseReference: DatabaseReference? = null

    private var sharedPreferences: SharedPreferences? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.chat_item_messages, parent, false)
        mAuth = FirebaseAuth.getInstance()

        sharedPreferences = view.context.getSharedPreferences("change_theme", Context.MODE_PRIVATE)

        return MessageViewHolder(view)
    }

    @SuppressLint("RtlHardcoded")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val sender_UID = mAuth!!.currentUser!!.uid
        val message = messageList[position]
        val from_user_ID = message.from
        val from_message_TYPE = message.type
        val send_time = message.send_time

        databaseReference =
            FirebaseDatabase.getInstance().reference.child("users").child(from_user_ID.toString())
        databaseReference!!.keepSynced(true) // for offline
        databaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val userName = dataSnapshot.child("user_name").value!!.toString()
                    val userProfileImage = dataSnapshot.child("user_thumb_image").value!!.toString()

                    mGlideRequestManager
                        .load(userProfileImage)
                        .placeholder(R.drawable.default_profile_image)
                        .into(holder.user_profile_image)

                    holder.chat_sender.text = userName
                    holder.chat_time_stamp.text = send_time
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

        // if message type is TEXT
        if (from_message_TYPE == "text") {
            if (from_user_ID == sender_UID) {
                holder.chatItemLayout.gravity = Gravity.RIGHT
                holder.chat_background.setBackgroundResource(R.drawable.chat_sender_background)
                holder.user_profile_image.visibility = View.GONE
                holder.chat_sender.visibility = View.GONE
                holder.chat_message_image.visibility = View.GONE
                holder.chat_message.visibility = View.VISIBLE
                holder.chat_message.text = message.message
                // 테마별 글자색
                if (sharedPreferences!!.getBoolean("dark_theme", false)) {
                    holder.chat_message.setTextColor(Color.YELLOW)
                    holder.chat_time_stamp.setTextColor(Color.parseColor("#c7cc00"))
                } else {
                    holder.chat_message.setTextColor(Color.BLACK)
                    holder.chat_time_stamp.setTextColor(Color.parseColor("#212121"))
                }
            } else {
                holder.chatItemLayout.gravity = Gravity.LEFT
                holder.chat_background.setBackgroundResource(R.drawable.chat_receive_background)
                holder.user_profile_image.visibility = View.VISIBLE
                holder.chat_sender.visibility = View.VISIBLE
                holder.chat_message_image.visibility = View.GONE
                holder.chat_message.visibility = View.VISIBLE
                holder.chat_message.text = message.message
                // 테마별 글자색
                if (sharedPreferences!!.getBoolean("dark_theme", false)) {
                    holder.chat_sender.setTextColor(Color.parseColor("#c7c7c7"))
                    holder.chat_message.setTextColor(Color.WHITE)
                    holder.chat_time_stamp.setTextColor(Color.parseColor("#c7c7c7"))
                } else {
                    holder.chat_sender.setTextColor(Color.parseColor("#212121"))
                    holder.chat_message.setTextColor(Color.BLACK)
                    holder.chat_time_stamp.setTextColor(Color.parseColor("#212121"))
                }
            }
        }

        // if message type is image
        if (from_message_TYPE == "image") {
            if (from_user_ID == sender_UID) {
                holder.chatItemLayout.gravity = Gravity.RIGHT
                holder.chat_background.setBackgroundResource(R.drawable.chat_sender_background)
                holder.user_profile_image.visibility = View.GONE
                holder.chat_sender.visibility = View.GONE
                holder.chat_message.visibility = View.GONE
                holder.chat_message_image.visibility = View.VISIBLE
                mGlideRequestManager
                    .load(message.message)
                    .into(holder.chat_message_image)
                // 테마별 글자색
                if (sharedPreferences!!.getBoolean("dark_theme", false)) {
                    holder.chat_time_stamp.setTextColor(Color.parseColor("#c7cc00"))
                } else {
                    holder.chat_time_stamp.setTextColor(Color.parseColor("#212121"))
                }
            } else {
                holder.chatItemLayout.gravity = Gravity.LEFT
                holder.chat_background.setBackgroundResource(R.drawable.chat_receive_background)
                holder.user_profile_image.visibility = View.VISIBLE
                holder.chat_sender.visibility = View.VISIBLE
                holder.chat_message.visibility = View.GONE
                holder.chat_message_image.visibility = View.VISIBLE
                if (sharedPreferences!!.getBoolean("dark_theme", false)) {
                    holder.chat_sender.setTextColor(Color.parseColor("#c7c7c7"))
                    holder.chat_time_stamp.setTextColor(Color.parseColor("#c7c7c7"))
                } else {
                    holder.chat_sender.setTextColor(Color.parseColor("#212121"))
                    holder.chat_time_stamp.setTextColor(Color.parseColor("#212121"))
                }

                mGlideRequestManager
                    .load(message.message)
                    .into(holder.chat_message_image)
            }
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    inner class MessageViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        internal var chat_sender: TextView
        internal var chat_message: TextView
        internal var chat_time_stamp: TextView
        internal var chatItemLayout: LinearLayout
        internal var chat_background: LinearLayout
        internal var chat_message_image: RoundedImageView
        internal var user_profile_image: CircleImageView

        init {
            chat_background = view.findViewById(R.id.chat_background)
            chat_sender = view.findViewById(R.id.chat_sender)
            chat_message = view.findViewById(R.id.chat_message)
            chat_time_stamp = view.findViewById(R.id.chat_time_stamp)
            chatItemLayout = view.findViewById(R.id.list_item_message_layout)
            chat_message_image = view.findViewById(R.id.chat_message_image)
            user_profile_image = view.findViewById(R.id.messageUserImage)
        }
    }


}