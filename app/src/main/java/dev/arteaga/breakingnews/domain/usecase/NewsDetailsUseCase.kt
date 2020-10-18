package dev.arteaga.breakingnews.domain.usecase

import dev.arteaga.breakingnews.domain.repository.NewsRepository
import dev.arteaga.breakingnews.domain.scheduler.SchedulerProvider
import dev.arteaga.breakingnews.remote.NewsDetails
import io.reactivex.Observable


open class NewsDetailsUseCase(
    private val newsRepository: NewsRepository,
    private val schedulerProvider: SchedulerProvider.Factory
) {

    fun getTopHeadlines(category: String): Observable<NewsDetails> {
        return newsRepository.getTopHeadLines(category)
            .compose(schedulerProvider.create())
    }

    fun getQueryEverything(query: String): Observable<NewsDetails> {
        return newsRepository.getArticlesEverything(query)
            .compose(schedulerProvider.create())
    }
}