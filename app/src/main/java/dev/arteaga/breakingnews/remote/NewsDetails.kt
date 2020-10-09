package dev.arteaga.breakingnews.remote

import androidx.annotation.Keep


@Keep
data class NewsDetails(
    val articles: List<Article>
) {
    @Keep
    data class Article(
        val author: String?,
        val title: String?,
        val description: String?,
        val url: String?,
        val urlToImage: String?,
        val publishedAt: String?,
        val content: String?
    )
}