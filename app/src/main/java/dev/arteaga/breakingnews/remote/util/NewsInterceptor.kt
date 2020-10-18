package dev.arteaga.breakingnews.remote.util

import android.content.Context
import android.content.Context.TELEPHONY_SERVICE
import android.telephony.TelephonyManager
import android.util.Log
import dev.arteaga.breakingnews.util.NetworkUtil
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException


class NewsInterceptor constructor(private val context: Context, private val apiKey: String) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
       /* val tm = context.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        var countryCodeValue = tm.networkCountryIso

        countryCodeValue = if (NetworkUtil.isCountryAvailable(context, countryCodeValue)) countryCodeValue else "us"

        Log.i("country ", countryCodeValue)*/


        var request = chain.request()
        val url = request.url.newBuilder()
           //.addQueryParameter("country", countryCodeValue)
            .addQueryParameter("apiKey", apiKey)
            .build()
        request = request.newBuilder().url(url).build()
        return chain.proceed(request)
    }
}