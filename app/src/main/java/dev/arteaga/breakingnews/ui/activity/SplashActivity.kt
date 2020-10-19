package dev.arteaga.breakingnews.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import  dev.arteaga.breakingnews.BreakingNewsApp.Companion.countryCode

import dev.arteaga.breakingnews.R
import dev.arteaga.breakingnews.util.NetworkUtil
import java.util.*
import kotlin.concurrent.schedule

class SplashActivity : AppCompatActivity() {

    private val splashScreenDuration = 2000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Hide the status bar.
        //  window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        // status bar is hidden, so hide that too if necessary.
        // actionBar?.hide()

        setCountryLocaleCode()
        scheduleSplashScreen()

    }

    private fun setCountryLocaleCode() {
        val tm = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        var countryCodeValue = tm.networkCountryIso

        countryCode =
            if (NetworkUtil.isCountryAvailable(this, countryCodeValue)) countryCodeValue else "us"

    }

    private fun scheduleSplashScreen() {

        Timer("SettingUp", false).schedule(splashScreenDuration) {
            startActivity(Intent(this@SplashActivity, NewsListActivity::class.java))
            finish()
        }
    }

}