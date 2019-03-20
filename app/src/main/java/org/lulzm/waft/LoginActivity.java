package org.lulzm.waft;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

public class LoginActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private android.widget.EditText edtid;
    private android.widget.TextView textView2;
    private android.widget.TextView textView3;
    private android.widget.EditText edtpw;
    private android.widget.Button btnlogin;
    private android.widget.TextView tvjoinMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.tvjoinMember = (TextView) findViewById(R.id.tv_joinMember);
        this.btnlogin = (Button) findViewById(R.id.btn_login);
        this.edtpw = (EditText) findViewById(R.id.edt_pw);
        this.edtid = (EditText) findViewById(R.id.edt_id);


        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_main = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent_main);
            }
        });
    }
}
