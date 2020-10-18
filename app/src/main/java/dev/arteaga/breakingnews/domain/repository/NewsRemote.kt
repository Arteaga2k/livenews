package dev.arteaga.breakingnews.domain.repository

import dev.arteaga.breakingnews.remote.NewsDetails
import io.reactivex.Observable


interface NewsRemote {

    fun getTopHeadlines(category: String, country: String) : Observable<NewsDetails>
    fun getArticlesEverything(query: String): Observable<NewsDetails>
}