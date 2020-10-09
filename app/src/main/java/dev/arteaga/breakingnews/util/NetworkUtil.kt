package dev.arteaga.breakingnews.util

import dev.arteaga.breakingnews.R
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import java.util.*


object NetworkUtil {

    fun isNetworkAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }

    fun isCountryAvailable(context: Context, countryCode: String): Boolean {
        val countryList =
            context.resources.getStringArray(R.array.countryCodes).toList()

        return countryList.contains(countryCode)

    }
}