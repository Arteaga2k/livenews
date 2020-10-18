package dev.arteaga.breakingnews

import android.app.Application
import android.telephony.TelephonyManager
import dev.arteaga.breakingnews.di.modulesList
import dev.arteaga.breakingnews.util.NetworkUtil
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin


class BreakingNewsApp : Application() {

    companion object {
        var countryCode: String = ""
    }

    override fun onCreate() {
        super.onCreate()
        injectKoin()
    }

    private fun injectKoin() {
        startKoin {
            androidContext(this@BreakingNewsApp)
            androidLogger()
            modules(modulesList)
        }
    }
}