package dev.arteaga.breakingnews.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import dev.arteaga.breakingnews.R
import dev.arteaga.breakingnews.remote.NewsDetails
import kotlinx.android.synthetic.main.activity_detail_news.*


class DetailNewsActivity : AppCompatActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_news)


        // Find the toolbar view inside the activity layout

        // Find the toolbar view inside the activity layout
        val toolbar: Toolbar = findViewById<Toolbar>(R.id.toolbar)
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val articleUrl = intent.getStringExtra(INTENT_ARTICLE_URL)
            ?: throw IllegalStateException("field $INTENT_ARTICLE_URL missing in Intent")

        wvUrlContainer.webViewClient = WebViewClient()
        wvUrlContainer.settings.javaScriptEnabled = true

        wvUrlContainer.loadUrl(articleUrl)

    }

    companion object {

        private val INTENT_ARTICLE_URL = "article_url"

        fun newIntent(context: Context, article: NewsDetails.Article): Intent {
            val intent = Intent(context, DetailNewsActivity::class.java)
            intent.putExtra(INTENT_ARTICLE_URL, article.url)
            return intent
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}