package org.lulzm.waft.IntroSlide;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.airbnb.lottie.LottieAnimationView;
import org.lulzm.waft.R;

public class SplashActivity extends AppCompatActivity {

    private com.airbnb.lottie.LottieAnimationView introanimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        this.introanimation = (LottieAnimationView) findViewById(R.id.intro_animation);

        introanimation.playAnimation();


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, IntroActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            }
        }, 5000);
    }
}
