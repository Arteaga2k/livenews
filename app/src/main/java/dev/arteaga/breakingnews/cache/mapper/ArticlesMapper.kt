package com.arteaga.breaking.news.cache.mapper

import dev.arteaga.breakingnews.cache.model.ArticleEntity
import dev.arteaga.breakingnews.remote.NewsDetails


class ArticlesMapper : EntityMapper<List<ArticleEntity>, NewsDetails> {

    override fun mapToEntity(domain: NewsDetails): List<ArticleEntity> {
        val articleEntities = mutableListOf<ArticleEntity>()
        domain.articles.forEach {
            val article = ArticleEntity(
                author = it.author,
                title = it.title,
                description = it.description,
                url = it.url,
                urlToImage = it.urlToImage,
                publishedAt = it.publishedAt,
                content = it.content
            )
            articleEntities.add(article)
        }
        return articleEntities
    }

    override fun mapFromEntity(entity: List<ArticleEntity>): NewsDetails {
        val articles = mutableListOf<NewsDetails.Article>()
        entity.forEach {
            val article = NewsDetails.Article(
                author = it.author,
                title = it.title,
                description = it.description,
                url = it.url,
                urlToImage = it.urlToImage,
                publishedAt = it.publishedAt,
                content = it.content
            )
            articles.add(article)
        }
        return NewsDetails(articles)
    }
}