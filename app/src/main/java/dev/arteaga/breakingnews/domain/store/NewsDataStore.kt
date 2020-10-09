package dev.arteaga.breakingnews.domain.store

import dev.arteaga.breakingnews.remote.NewsDetails
import io.reactivex.Completable
import io.reactivex.Observable


interface NewsDataStore {

    fun getTopHeadlines(category: String): Observable<NewsDetails>

    fun saveTopHeadlines(newsDetails: NewsDetails) : Completable

    fun deleteTopHeadlines() : Completable
}