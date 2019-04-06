package org.lulzm.waft;

import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.*;
import android.widget.Toast;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RecycleListAdapter.RecyclerViewClickListener {

    Toolbar toolbar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*리사이클러뷰 선언 및 화면 설정*/
        RecyclerView recyclerView = findViewById(R.id.recycle_View);

        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        /*cardView 아이템 이름 변경 및 추가*/
        List<CardItem> dataList = new ArrayList<>();
        dataList.add(new CardItem( "MY QR"));
        dataList.add(new CardItem( "안내"));
        dataList.add(new CardItem( "환전소"));
        dataList.add(new CardItem( "채팅방"));
        dataList.add(new CardItem( "대사관 SOS"));
        dataList.add(new CardItem( "SOS"));

        /*리사이클러뷰 어뎁터 연결*/
        RecycleListAdapter adapter = new RecycleListAdapter(dataList);
        recyclerView.setAdapter(adapter);

        adapter.setOnClickListener(this);

        /* firebase */
        mAuth = FirebaseAuth.getInstance();

        /* tool bar */
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_hamburger_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);
        // tool bar custom
        new SlidingRootNavBuilder(this)
                .withMenuLayout(R.layout.sliding_root_nav)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withDragDistance(200)
                .withRootViewScale(0.6f)
                .inject();

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    /*아이템 클릭 이벤트*/
    @Override
    public void onItemClicked(int position) {
        Toast.makeText(this, "" + position, Toast.LENGTH_SHORT).show();
    }
}
