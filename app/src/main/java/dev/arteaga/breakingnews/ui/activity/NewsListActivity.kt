package dev.arteaga.breakingnews.ui.activity

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codemybrainsout.ratingdialog.RatingDialog
import com.yarolegovich.slidingrootnav.SlidingRootNav
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder
import dev.arteaga.breakingnews.BuildConfig
import dev.arteaga.breakingnews.R
import dev.arteaga.breakingnews.remote.NewsDetails
import dev.arteaga.breakingnews.ui.Resource
import dev.arteaga.breakingnews.ui.adapter.NewsListAdapter
import dev.arteaga.breakingnews.ui.menu.DrawerAdapter
import dev.arteaga.breakingnews.ui.menu.DrawerItem
import dev.arteaga.breakingnews.ui.menu.SimpleItem
import dev.arteaga.breakingnews.ui.viewmodel.NewsDetailsViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.menu_left_drawer.*
import org.koin.android.viewmodel.ext.android.viewModel


class NewsListActivity : AppCompatActivity(), DrawerAdapter.OnItemSelectedListener,
    NewsListAdapter.NewsClickListener {

    private val POS_GENERAL = 0
    private val POS_SPORTS = 1
    private val POS_BUSINESS = 2
    private val POS_SCIENCE = 3
    private val POS_ENTERTAINMENT = 4
    private val POS_HEALTH = 5

    private lateinit var screenTitles: Array<String>
    private lateinit var screenIcons: Array<Drawable?>

    private var firstBoot = false
    private var slidingRootNav: SlidingRootNav? = null

    private val newsViewModel: NewsDetailsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Find the toolbar view inside the activity layout

        // Find the toolbar view inside the activity layout
        val toolbar: Toolbar = findViewById<Toolbar>(R.id.toolbar)
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar)


        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_menu)

        setupRecyclerView()
        triggerGetTopHeadlines("")
        setupMenuDrawer(savedInstanceState, toolbar)
        screenIcons = loadScreenIcons()
        screenTitles = loadScreenTitles()
        setupDrawerAdapter()


    }

    private fun setupDrawerAdapter() {
        val adapter = DrawerAdapter(
            listOf(
                createItemFor(POS_GENERAL)?.setChecked(true),
                createItemFor(POS_SPORTS),
                createItemFor(POS_BUSINESS),
                createItemFor(POS_SCIENCE),
                createItemFor(POS_ENTERTAINMENT),
                createItemFor(POS_HEALTH)
                //    SpaceItem(48),
            ) as List<DrawerItem<*>>?
        )
        adapter.setListener(this)

        val list = findViewById<RecyclerView>(R.id.list)
        list.isNestedScrollingEnabled = false
        list.layoutManager = GridLayoutManager(this, 2)
        list.adapter = adapter

        adapter.setSelected(POS_GENERAL)
    }

    private fun setupMenuDrawer(savedInstanceState: Bundle?, toolbar: Toolbar) {
        slidingRootNav = SlidingRootNavBuilder(this)
            .withToolbarMenuToggle(toolbar)
            .withMenuOpened(false)
            .withContentClickableWhenMenuOpened(false)
            .withSavedState(savedInstanceState)
            .withMenuLayout(R.layout.menu_left_drawer)
            .inject()

        tvAppVersion.text = getString(R.string.app_version).plus(" ").plus(BuildConfig.VERSION_NAME)

        cvShareApp.setOnClickListener {

            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(
                    Intent.EXTRA_TEXT, getString(
                        R.string.share_app_msg,
                        getString(R.string.google_play_store_root) + packageName.toString()
                    )
                )
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)

        }

        cvRateUs.setOnClickListener {
            RatingDialog.Builder(this)
                .threshold(3.toFloat())
                .onRatingBarFormSumbit { }.build().also {
                    it.show()
                }
        }

    }

    private fun setupRecyclerView() {
        rvNewsList.layoutManager = LinearLayoutManager(this)
    }

    private fun triggerGetTopHeadlines(category: String) {
        newsViewModel.getTopHeadlines(category).observe(this, {
            when (it) {
                is Resource.Loading -> {
                    Toast.makeText(this, "Loading", Toast.LENGTH_SHORT).show()
                }
                is Resource.Success -> {
                    rvNewsList.adapter = NewsListAdapter(it.data.articles, this)

                    firstBoot = false
                }
                is Resource.Failure -> {
                    Log.e("Failure","Failure: ${it.throwable.localizedMessage}", )
                    Toast.makeText(
                        this,
                        "Failure: ${it.throwable.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

    private fun createItemFor(position: Int): DrawerItem<*>? {
        return SimpleItem(screenIcons[position], screenTitles[position])
            .withIconTint(R.color.colorPrimary)
            .withTextTint(R.color.colorPrimaryDark)
            .withSelectedIconTint(R.color.colorAccent)
            .withSelectedTextTint(R.color.colorAccent)
    }


    private fun loadScreenTitles(): Array<String> {
        return resources.getStringArray(R.array.ld_activityScreenTitles)
    }

    private fun loadScreenIcons(): Array<Drawable?> {

        val typedArray = resources.obtainTypedArray(R.array.ld_activityScreenIcons)
        val icons = arrayOfNulls<Drawable>(typedArray.length())

        for (i in 0 until typedArray.length()) {
            val id = typedArray.getResourceId(i, 0)
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(this, id)
            }
        }
        typedArray.recycle()
        return icons
    }

    override fun onItemSelected(position: Int) {
        var title = ""
        when (position) {
            POS_GENERAL -> {
                title = getString(R.string.menu_general)
            }
            POS_BUSINESS -> {
                title = getString(R.string.menu_business)
            }
            POS_HEALTH -> {
                title = getString(R.string.menu_health)
            }
            POS_ENTERTAINMENT -> {
                title = getString(R.string.menu_entertainment)
            }
            POS_SCIENCE -> {
                title = getString(R.string.menu_science)
            }
            POS_SPORTS -> {
                title = getString(R.string.menu_sports)
            }

        }
        supportActionBar!!.title = title
        if (!firstBoot)
            triggerGetTopHeadlines(title)

        slidingRootNav?.closeMenu()
    }

    override fun onArticleClicked(article: NewsDetails.Article) {
        val intent = DetailNewsActivity.newIntent(this, article)
        startActivity(intent)
    }

    override fun onShareClicked(article: NewsDetails.Article) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, article.url)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }
}
