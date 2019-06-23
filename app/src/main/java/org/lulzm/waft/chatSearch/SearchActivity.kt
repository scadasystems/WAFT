package org.lulzm.waft.chatSearch

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.zhaiyifan.rememberedittext.RememberEditText
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import de.hdodenhof.circleimageview.CircleImageView
import org.lulzm.waft.R
import org.lulzm.waft.chatModel.ProfileInfo
import org.lulzm.waft.chatProfile.ChatProfileActivity
import xyz.hasnat.sweettoast.SweetToast

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
 * Date : 2019-04-21
 * Time : 오후 11:03
 * GitHub : https://github.com/scadasystems
 * E-mail : redsmurf@lulzm.org
 */
class SearchActivity : AppCompatActivity() {

    private var toolbar: Toolbar? = null
    private var searchInput: EditText? = null
    private var backButton: ImageView? = null
    private var notFoundTV: TextView? = null

    private var peoples_list: RecyclerView? = null
    private var peoplesDatabaseReference: DatabaseReference? = null

    // for glide error -> You cannot start a load for a destroyed activity
    var mGlideRequestManager: RequestManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // 다크모드 적용
        val sharedPreferences = getSharedPreferences("change_theme", Context.MODE_PRIVATE)
        if (sharedPreferences.getBoolean("dark_theme", false)) {
            setTheme(R.style.darktheme)
        } else {
            setTheme(R.style.AppTheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_activity_search)

        // 상태표시줄 색상 변경
        val view2 = window.decorView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 23 버전 이상일 때 상태바 하얀 색상, 회색 아이콘
            if (sharedPreferences.getBoolean("dark_theme", false)) {
                window.statusBarColor = Color.BLACK
            } else {
                view2.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                window.statusBarColor = Color.parseColor("#f2f2f2")
            }
        } else if (Build.VERSION.SDK_INT >= 21) {
            // 21 버전 이상일 때 상태바 검은 색상, 흰색 아이콘
            window.statusBarColor = Color.BLACK
        }

        // for glide error -> You cannot start a load for a destroyed activity
        mGlideRequestManager = Glide.with(applicationContext)

        // appbar / toolbar
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar!!.setDisplayShowCustomEnabled(true)

        val layoutInflater =
            this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.chat_appbar_search, null)
        actionBar.customView = view

        searchInput = findViewById(R.id.serachInput)
        notFoundTV = findViewById(R.id.notFoundTV)
        backButton = findViewById(R.id.backButton)
        searchInput!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                searchPeopleProfile(searchInput!!.text.toString().toLowerCase())
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        backButton!!.setOnClickListener { v -> finish() }


        // Setup recycler view
        peoples_list = findViewById(R.id.SearchList)
        peoples_list!!.setHasFixedSize(true)
        peoples_list!!.layoutManager = LinearLayoutManager(this)

        peoplesDatabaseReference = FirebaseDatabase.getInstance().reference.child("users")
        peoplesDatabaseReference!!.keepSynced(true) // for offline

    }

    private fun searchPeopleProfile(searchString: String) {
        val view = window.decorView

        val searchQuery = peoplesDatabaseReference!!.orderByChild("search_name")
            .startAt(searchString).endAt(searchString + "\uf8ff")

        val recyclerOptions = FirebaseRecyclerOptions.Builder<ProfileInfo>()
            .setQuery(searchQuery, ProfileInfo::class.java)
            .build()

        val adapter =
            object : FirebaseRecyclerAdapter<ProfileInfo, SearchPeopleVH>(recyclerOptions) {
                override fun onBindViewHolder(
                    holder: SearchPeopleVH,
                    position: Int,
                    model: ProfileInfo
                ) {
                    holder.name.text = model.user_name
                    holder.status.text = model.user_status

                    view.post {
                        mGlideRequestManager
                            ?.load(model.user_image)
                            ?.placeholder(R.drawable.default_profile_image)
                            ?.error(R.drawable.default_profile_image)
                            ?.into(holder.profile_pic)
                    }

                    holder.verified_icon.visibility = View.GONE
                    if (model.verified!!.contains("true")) {
                        holder.verified_icon.visibility = View.VISIBLE
                    } else {
                        holder.verified_icon.visibility = View.GONE
                    }

                    /** on list >> clicking currency_item, then, go to single user profile  */
                    holder.itemView.setOnClickListener { v ->
                        val visit_user_id = getRef(position).key
                        SweetToast.info(applicationContext, visit_user_id, 4000)
                        val intent = Intent(this@SearchActivity, ChatProfileActivity::class.java)
                        intent.putExtra("visitUserId", visit_user_id)
                        startActivity(intent)
                    }
                }

                override fun onCreateViewHolder(
                    viewGroup: ViewGroup,
                    viewType: Int
                ): SearchPeopleVH {
                    val view = LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.chat_all_single_profile_display, viewGroup, false)
                    return SearchPeopleVH(view)
                }
            }
        peoples_list!!.adapter = adapter
        adapter.startListening()
    }

    class SearchPeopleVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var name: TextView
        internal var status: TextView
        internal var profile_pic: CircleImageView
        internal var verified_icon: ImageView

        init {
            name = itemView.findViewById(R.id.all_user_name)
            status = itemView.findViewById(R.id.all_user_status)
            profile_pic = itemView.findViewById(R.id.all_user_profile_img)
            verified_icon = itemView.findViewById(R.id.verifiedIcon)
        }
    }

    // Toolbar menu for clearing search history
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.chat_search_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        if (item.itemId == R.id.menu_clear_search) {
            RememberEditText.clearCache(this@SearchActivity)
            SweetToast.info(this, getString(R.string.search_history_cleared_successfully))
            this.finish()
        }
        return true
    }
}

