package dev.arteaga.breakingnews.domain.store

import dev.arteaga.breakingnews.domain.repository.NewsCache
import dev.arteaga.breakingnews.remote.NewsDetails
import io.reactivex.Completable
import io.reactivex.Observable


class NewsCacheDataStore(
    private val newsCache: NewsCache
) : NewsDataStore {

    override fun getTopHeadlines(category: String): Observable<NewsDetails> {
        return newsCache.getTopHeadlines()
    }

    override fun saveTopHeadlines(newsDetails: NewsDetails): Completable {
        return newsCache.saveTopHeadlines(newsDetails)
    }

    override fun deleteTopHeadlines(): Completable {
        return newsCache.deleteTopHeadlines()
    }
}