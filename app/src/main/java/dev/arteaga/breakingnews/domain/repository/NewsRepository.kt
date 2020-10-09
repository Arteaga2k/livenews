package dev.arteaga.breakingnews.domain.repository

import dev.arteaga.breakingnews.remote.NewsDetails
import io.reactivex.Observable

interface NewsRepository {

    fun getTopHeadLines(category: String): Observable<NewsDetails>
}