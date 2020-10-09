package dev.arteaga.breakingnews.domain.store

import dev.arteaga.breakingnews.domain.repository.NewsRemote
import dev.arteaga.breakingnews.remote.NewsDetails
import io.reactivex.Completable
import io.reactivex.Observable


class NewsRemoteDataStore(
    private val newsRemote: NewsRemote
) : NewsDataStore {

    override fun getTopHeadlines(category: String): Observable<NewsDetails> {
        return newsRemote.getTopHeadlines(category)
    }

    override fun saveTopHeadlines(newsDetails: NewsDetails): Completable {
        throw UnsupportedOperationException("Not Supported")
    }

    override fun deleteTopHeadlines(): Completable {
        throw UnsupportedOperationException("Not Supported")
    }
}