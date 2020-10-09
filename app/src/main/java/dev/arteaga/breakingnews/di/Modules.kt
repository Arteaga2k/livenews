package dev.arteaga.breakingnews.di

import android.content.Context
import androidx.room.Room
import com.arteaga.breaking.news.cache.NewsCacheImpl
import com.arteaga.breaking.news.cache.mapper.ArticlesMapper
import com.arteaga.breaking.news.factory.NewsDatabase
import dev.arteaga.breakingnews.BuildConfig
import dev.arteaga.breakingnews.domain.NewsDataRepository
import dev.arteaga.breakingnews.domain.repository.NewsCache
import dev.arteaga.breakingnews.domain.repository.NewsRemote
import dev.arteaga.breakingnews.domain.repository.NewsRepository
import dev.arteaga.breakingnews.domain.scheduler.SchedulerProvider
import dev.arteaga.breakingnews.domain.store.NewsCacheDataStore
import dev.arteaga.breakingnews.domain.store.NewsDataStoreFactory
import dev.arteaga.breakingnews.domain.store.NewsRemoteDataStore
import dev.arteaga.breakingnews.domain.usecase.NewsDetailsUseCase
import dev.arteaga.breakingnews.remote.NewsRemoteImpl
import dev.arteaga.breakingnews.remote.NewsService
import dev.arteaga.breakingnews.remote.util.NewsInterceptor
import dev.arteaga.breakingnews.ui.viewmodel.NewsDetailsViewModel
import dev.arteaga.breakingnews.util.AndroidSchedulerProvider
import dev.arteaga.breakingnews.util.NetworkUtil
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


val modulesList by lazy {
    listOf(
        appModule,
        viewModelModule,
        domainModule,
        dataModule,
        cacheModule,
        remoteModule
    )
}
val appModule: Module = module {
    factory<SchedulerProvider.Factory> { AndroidSchedulerProvider() }
}

val viewModelModule: Module = module {
    viewModel { NewsDetailsViewModel(newsDetailsUseCase = get()) }
}

val domainModule: Module = module {
    factory { NewsDetailsUseCase(newsRepository = get(), schedulerProvider = get()) }
}

val dataModule: Module = module {
    single<NewsRepository> {
        NewsDataRepository(
            newsDataStoreFactory = get(),
            newsCache = get(),
            isNetworkAvailable = NetworkUtil.isNetworkAvailable(androidApplication())
        )
    }
    single { NewsDataStoreFactory(newsCacheDataStore = get(), newsRemoteDataStore = get()) }
    single { NewsCacheDataStore(newsCache = get()) }
    single { NewsRemoteDataStore(newsRemote = get()) }
}

val cacheModule: Module = module {
    single<NewsCache> { NewsCacheImpl(newsDatabase = get(), articlesMapper = get()) }
    factory { ArticlesMapper() }
    single<NewsDatabase> { createRoomDb(androidApplication()) }
}

val remoteModule: Module = module {
    single<NewsRemote> { NewsRemoteImpl(newsService = get()) }
    single { createRetrofit(androidContext(), BASE_URL) }
    single<NewsService> { (get() as Retrofit).create(NewsService::class.java) }
}


private fun createRoomDb(appContext: Context): NewsDatabase {
    return Room
        .databaseBuilder(appContext, NewsDatabase::class.java, "news_db")
        .build()
}

private const val BASE_URL = "https://newsapi.org/v2/"

private lateinit var retrofit: Retrofit

private fun createRetrofit(context: Context, baseUrl: String): Retrofit {
    return Retrofit.Builder()
        .client(createOkHttpClient(context))
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
}


private fun createOkHttpClient(context: Context): OkHttpClient {


    return OkHttpClient().newBuilder()
        .addInterceptor(NewsInterceptor(context, BuildConfig.NEWS_API_KEY))
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()
}