package dev.arteaga.breakingnews.domain.repository

import dev.arteaga.breakingnews.remote.NewsDetails
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single


interface NewsCache {

    fun getTopHeadlines(): Observable<NewsDetails>

    fun saveTopHeadlines(newsDetails: NewsDetails): Completable

    fun deleteTopHeadlines(): Completable

    fun areArticlesCached(): Single<Boolean>
}