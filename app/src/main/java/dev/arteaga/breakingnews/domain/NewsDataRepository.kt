package dev.arteaga.breakingnews.domain

import dev.arteaga.breakingnews.domain.repository.NewsCache
import dev.arteaga.breakingnews.domain.repository.NewsRepository
import dev.arteaga.breakingnews.domain.store.NewsDataStoreFactory
import dev.arteaga.breakingnews.domain.store.NewsRemoteDataStore
import dev.arteaga.breakingnews.remote.NewsDetails
import io.reactivex.Observable
import io.reactivex.functions.BiFunction


class NewsDataRepository(
    private val newsCache: NewsCache,
    private val newsDataStoreFactory: NewsDataStoreFactory,
    private val isNetworkAvailable: Boolean
) : NewsRepository {

    override fun getTopHeadLines(category: String): Observable<NewsDetails> {

        return Observable.zip(Observable.just(isNetworkAvailable), newsCache.areArticlesCached().toObservable(),
            BiFunction<Boolean, Boolean, Pair<Boolean, Boolean>> { network, areCached ->
                Pair(network, areCached)
            })
            .flatMap { pair ->
                when (val store = newsDataStoreFactory.getDataStore(pair.first, pair.second)) {
                    is NewsRemoteDataStore -> {
                        store.getTopHeadlines(category)
                            .flatMap { newsDetails ->
                                newsDataStoreFactory.getCacheDataStore().deleteTopHeadlines()
                                    .andThen(newsDataStoreFactory.getCacheDataStore().saveTopHeadlines(newsDetails))
                                    .andThen(Observable.just(newsDetails))
                            }
                    }
                    else -> {
                        store.getTopHeadlines(category)
                    }
                }
            }
    }
}