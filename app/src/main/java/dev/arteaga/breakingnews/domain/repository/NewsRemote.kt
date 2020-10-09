package dev.arteaga.breakingnews.domain.repository

import dev.arteaga.breakingnews.remote.NewsDetails
import io.reactivex.Observable


interface NewsRemote {

    fun getTopHeadlines(category: String) : Observable<NewsDetails>
}