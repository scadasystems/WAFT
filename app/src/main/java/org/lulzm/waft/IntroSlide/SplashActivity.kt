package org.lulzm.waft.IntroSlide

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import org.lulzm.waft.R

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val lottie_intro = findViewById<LottieAnimationView>(R.id.intro_animation)
        lottie_intro.setAnimation("loading_skyline.json")
        lottie_intro.playAnimation()


        val handler = Handler()
        handler.postDelayed({
            val intent = Intent(this@SplashActivity, IntroActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }, 5000)
    }
}
