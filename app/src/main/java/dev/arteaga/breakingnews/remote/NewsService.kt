package dev.arteaga.breakingnews.remote

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query


interface NewsService {

    @GET("top-headlines")
    fun getTopHeadlines(@Query("category") category: String): Observable<NewsDetails>
}