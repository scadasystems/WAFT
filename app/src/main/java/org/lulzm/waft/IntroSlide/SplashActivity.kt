package org.lulzm.waft.IntroSlide

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import org.lulzm.waft.R

class Splash : Activity() {
    var flag = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (flag) {
            setContentView(R.layout.activity_splash)
            val hd = Handler()
            hd.postDelayed(
                object : Runnable {
                    public override fun run() {
                        flag = false
                        startActivity(Intent(getApplication(), IntroActivity::class.java))
                        this@Splash.finish()
                    }
                }, 4000)
        } else {
            flag = true
            setContentView(R.layout.activity_splash)
        }
    }
}