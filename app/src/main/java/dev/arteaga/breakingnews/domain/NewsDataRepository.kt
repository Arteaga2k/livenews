package dev.arteaga.breakingnews.domain

import dev.arteaga.breakingnews.domain.repository.NewsCache
import dev.arteaga.breakingnews.domain.repository.NewsRepository
import dev.arteaga.breakingnews.domain.store.NewsDataStore
import dev.arteaga.breakingnews.domain.store.NewsDataStoreFactory
import dev.arteaga.breakingnews.domain.store.NewsRemoteDataStore
import dev.arteaga.breakingnews.remote.NewsDetails
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import  dev.arteaga.breakingnews.BreakingNewsApp.Companion.countryCode



class NewsDataRepository(
    private val newsCache: NewsCache,
    private val newsDataStoreFactory: NewsDataStoreFactory,
    private val isNetworkAvailable: Boolean
) : NewsRepository {

    override fun getTopHeadLines(category: String): Observable<NewsDetails> {

        return Observable.zip(Observable.just(isNetworkAvailable),
            newsCache.areArticlesCached().toObservable(),
            BiFunction<Boolean, Boolean, Pair<Boolean, Boolean>> { network, areCached ->
                Pair(network, areCached)
            })
            .flatMap { pair ->
                when (val store = newsDataStoreFactory.getDataStore(pair.first, pair.second)) {
                    is NewsRemoteDataStore -> {
                        store.getTopHeadlines(category, countryCode)
                            .flatMap { newsDetails ->
                                newsDataStoreFactory.getCacheDataStore().deleteTopHeadlines()
                                    .andThen(
                                        newsDataStoreFactory.getCacheDataStore()
                                            .saveTopHeadlines(newsDetails)
                                    )
                                    .andThen(Observable.just(newsDetails))
                            }
                    }
                    else -> {
                        store.getTopHeadlines(category, countryCode)
                    }
                }
            }
    }

    override fun getArticlesEverything(query: String): Observable<NewsDetails> {
        return newsDataStoreFactory.getDataSearch().getArticlesEverything(query)
            .flatMap { newsDetails ->
                newsDataStoreFactory.getCacheDataStore().deleteTopHeadlines()
                    .andThen(newsDataStoreFactory.getCacheDataStore().saveTopHeadlines(newsDetails))
                    .andThen(Observable.just(newsDetails))
            }


    }


}