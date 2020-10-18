package dev.arteaga.breakingnews.ui.activity

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codemybrainsout.ratingdialog.RatingDialog
import com.miguelcatalan.materialsearchview.MaterialSearchView
import com.miguelcatalan.materialsearchview.MaterialSearchView.SearchViewListener
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
import uk.co.imallan.jellyrefresh.JellyRefreshLayout


class NewsListActivity : AppCompatActivity(), DrawerAdapter.OnItemSelectedListener,
    NewsListAdapter.NewsClickListener {

    private val POS_GENERAL = 0
    private val POS_SPORTS = 1
    private val POS_BUSINESS = 2
    private val POS_SCIENCE = 3
    private val POS_ENTERTAINMENT = 4
    private val POS_HEALTH = 5

    private lateinit var searchView: MaterialSearchView
    private lateinit var mJellyLayout: JellyRefreshLayout
    private lateinit var screenTitles: Array<String>
    private lateinit var screenIcons: Array<Drawable?>

    private var category = ""
    private var firstBoot = false
    private var slidingRootNav: SlidingRootNav? = null

    private val newsViewModel: NewsDetailsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        setContentView(R.layout.activity_main)
        // Find the toolbar view inside the activity layout
        val toolbar: Toolbar = findViewById<Toolbar>(R.id.toolbar)
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar)


        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_menu)


        setupSearchView()
        setupRecyclerView()
        triggerGetTopHeadlines(category)
        setupMenuDrawer(savedInstanceState, toolbar)
        screenIcons = loadScreenIcons()
        screenTitles = loadScreenTitles()
        setupDrawerAdapter()

        mJellyLayout = findViewById<JellyRefreshLayout>(R.id.jelly_refresh);


        mJellyLayout.setPullToRefreshListener {
            triggerGetTopHeadlines(category)
        }

        val loadingView: View = LayoutInflater.from(this).inflate(R.layout.view_loading, null)
        mJellyLayout.setLoadingView(loadingView)


    }

    private fun setupSearchView() {
        searchView =
            findViewById<View>(R.id.search_view) as MaterialSearchView

        searchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                Log.i("query listener", query)

                if (query.isNotEmpty()) {
                    triggerGetSearchArticle(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })

        searchView.setOnSearchViewListener(object : SearchViewListener {
            override fun onSearchViewShown() {
            }

            override fun onSearchViewClosed() {

            }
        })

        fab.setOnClickListener {
            fab.visibility = View.GONE
            triggerGetTopHeadlines(category)
        }

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

    private fun triggerGetSearchArticle(query: String) {

        newsViewModel.getArticlesEverything(query).observe(this, {
            when (it) {
                is Resource.Loading -> {
                    // Toast.makeText(this, "Loading", Toast.LENGTH_SHORT).show()
                }
                is Resource.Success -> {
                    rvNewsList.adapter = NewsListAdapter(it.data.articles, this)
                    mJellyLayout!!.postDelayed({ mJellyLayout!!.isRefreshing = false }, 500)
                    fab.text = query
                    fab.visibility = View.VISIBLE
                }
                is Resource.Failure -> {
                    Log.e("Failure", "Failure: ${it.throwable.localizedMessage}")
                    Toast.makeText(
                        this,
                        "Failure: ${it.throwable.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                    mJellyLayout!!.postDelayed({ mJellyLayout!!.isRefreshing = false }, 500)
                }
            }

        })


    }

    private fun triggerGetTopHeadlines(category: String) {
        newsViewModel.getTopHeadlines(category).observe(this, {
            when (it) {
                is Resource.Loading -> {
                    // Toast.makeText(this, "Loading", Toast.LENGTH_SHORT).show()
                }
                is Resource.Success -> {
                    rvNewsList.adapter = NewsListAdapter(it.data.articles, this)

                    firstBoot = false
                    mJellyLayout!!.postDelayed({ mJellyLayout!!.isRefreshing = false }, 500)
                }
                is Resource.Failure -> {
                    Log.e("Failure", "Failure: ${it.throwable.localizedMessage}")
                    Toast.makeText(
                        this,
                        "Failure: ${it.throwable.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                    mJellyLayout!!.postDelayed({ mJellyLayout!!.isRefreshing = false }, 500)
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
        when (position) {
            POS_GENERAL -> {
                category = getString(R.string.menu_general)
            }
            POS_BUSINESS -> {
                category = getString(R.string.menu_business)
            }
            POS_HEALTH -> {
                category = getString(R.string.menu_health)
            }
            POS_ENTERTAINMENT -> {
                category = getString(R.string.menu_entertainment)
            }
            POS_SCIENCE -> {
                category = getString(R.string.menu_science)
            }
            POS_SPORTS -> {
                category = getString(R.string.menu_sports)
            }

        }
        supportActionBar!!.title = title
        if (!firstBoot)
            triggerGetTopHeadlines(category)

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu);

        val item = menu!!.findItem(R.id.action_search)
        searchView!!.setMenuItem(item)

        return super.onCreateOptionsMenu(menu)


    }
}
