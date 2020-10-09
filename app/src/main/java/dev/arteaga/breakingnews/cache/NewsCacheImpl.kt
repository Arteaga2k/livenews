package com.arteaga.breaking.news.cache

import com.arteaga.breaking.news.cache.mapper.ArticlesMapper
import com.arteaga.breaking.news.factory.NewsDatabase
import dev.arteaga.breakingnews.domain.repository.NewsCache
import dev.arteaga.breakingnews.remote.NewsDetails
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single


class NewsCacheImpl(
    private val newsDatabase: NewsDatabase,
    private val articlesMapper: ArticlesMapper
) : NewsCache {

    override fun getTopHeadlines(): Observable<NewsDetails> {
        return newsDatabase.articlesDao().getTopHeadlines()
            .map {
                articlesMapper.mapFromEntity(it)
            }.toObservable()
    }

    override fun saveTopHeadlines(newsDetails: NewsDetails): Completable {
        return newsDatabase.articlesDao().saveTopHeadlines(articlesMapper.mapToEntity(newsDetails))

    }

    override fun deleteTopHeadlines(): Completable {
        return newsDatabase.articlesDao().deleteTopHeadlines()
    }

    override fun areArticlesCached(): Single<Boolean> {
        return newsDatabase.articlesDao().getTopHeadlines().isEmpty
            .map { !it }
    }
}