package org.lulzm.waft.ChatSearch;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.zhaiyifan.rememberedittext.RememberEditText;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import de.hdodenhof.circleimageview.CircleImageView;
import org.lulzm.waft.ChatModel.ProfileInfo;
import org.lulzm.waft.ChatProfile.ChatProfileActivity;
import org.lulzm.waft.R;
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
 * Date : 2019-04-21                                        
 * Time : 오후 11:03                                       
 * GitHub : https://github.com/scadasystems              
 * E-mail : redsmurf@lulzm.org                           
 *********************************************************/
public class SearchActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText searchInput;
    private ImageView backButton;
    private TextView notFoundTV;

    private RecyclerView peoples_list;
    private DatabaseReference peoplesDatabaseReference;

    // for glide error -> You cannot start a load for a destroyed activity
    public RequestManager mGlideRequestManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity_search);

        // for glide error -> You cannot start a load for a destroyed activity
        mGlideRequestManager = Glide.with(getApplicationContext());

        // appbar / toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater)
                this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.chat_appbar_search, null);
        actionBar.setCustomView(view);

        searchInput = findViewById(R.id.serachInput);
        notFoundTV = findViewById(R.id.notFoundTV);
        backButton = findViewById(R.id.backButton);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchPeopleProfile(searchInput.getText().toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        backButton.setOnClickListener(v -> finish());


        // Setup recycler view
        peoples_list = findViewById(R.id.SearchList);
        peoples_list.setHasFixedSize(true);
        peoples_list.setLayoutManager(new LinearLayoutManager(this));

        peoplesDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        peoplesDatabaseReference.keepSynced(true); // for offline

    }

    private void searchPeopleProfile(final String searchString) {
        View view = getWindow().getDecorView();

        final Query searchQuery = peoplesDatabaseReference.orderByChild("search_name")
                .startAt(searchString).endAt(searchString + "\uf8ff");

        FirebaseRecyclerOptions<ProfileInfo> recyclerOptions = new FirebaseRecyclerOptions.Builder<ProfileInfo>()
                .setQuery(searchQuery, ProfileInfo.class)
                .build();

        FirebaseRecyclerAdapter<ProfileInfo, SearchPeopleVH> adapter = new FirebaseRecyclerAdapter<ProfileInfo, SearchPeopleVH>(recyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull SearchPeopleVH holder, final int position, @NonNull ProfileInfo model) {
                holder.name.setText(model.getUser_name());
                holder.status.setText(model.getUser_status());

                view.post(() -> mGlideRequestManager
                        .load(model.getUser_image())
                        .placeholder(R.drawable.default_profile_image)
                        .error(R.drawable.default_profile_image)
                        .into(holder.profile_pic));

                holder.verified_icon.setVisibility(View.GONE);
                if (model.getVerified().contains("true")){
                    holder.verified_icon.setVisibility(View.VISIBLE);
                } else {
                    holder.verified_icon.setVisibility(View.GONE);
                }

                /**on list >> clicking currency_item, then, go to single user profile*/
                holder.itemView.setOnClickListener(v -> {
                    String visit_user_id = getRef(position).getKey();
                    Intent intent = new Intent(SearchActivity.this, ChatProfileActivity.class);
                    intent.putExtra("visitUserId", visit_user_id);
                    startActivity(intent);
                });
            }

            @NonNull
            @Override
            public SearchPeopleVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_all_single_profile_display, viewGroup, false);
                return new SearchPeopleVH(view);
            }
        };
        peoples_list.setAdapter(adapter);
        adapter.startListening();
    }

    public static class SearchPeopleVH extends RecyclerView.ViewHolder{
        TextView name, status;
        CircleImageView profile_pic;
        ImageView verified_icon;
        public SearchPeopleVH(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.all_user_name);
            status = itemView.findViewById(R.id.all_user_status);
            profile_pic = itemView.findViewById(R.id.all_user_profile_img);
            verified_icon = itemView.findViewById(R.id.verifiedIcon);
        }
    }

    // Toolbar menu for clearing search history
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.chat_search_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.menu_clear_search){
            RememberEditText.clearCache(SearchActivity.this);
            SweetToast.info(this, getString(R.string.search_history_cleared_successfully));
            this.finish();
        }
        return true;
    }
}

