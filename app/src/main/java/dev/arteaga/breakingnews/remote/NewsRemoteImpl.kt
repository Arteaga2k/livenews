package dev.arteaga.breakingnews.remote

import dev.arteaga.breakingnews.domain.repository.NewsRemote
import io.reactivex.Observable


class NewsRemoteImpl(
    private val newsService: NewsService
) : NewsRemote {

    override fun getTopHeadlines(category: String): Observable<NewsDetails> {
        return newsService.getTopHeadlines(category)
    }
}