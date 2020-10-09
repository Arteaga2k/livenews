package com.arteaga.breaking.news.cache.dao

import androidx.room.*
import dev.arteaga.breakingnews.cache.model.ArticleEntity
import io.reactivex.Completable
import io.reactivex.Flowable


@Dao
interface ArticlesDao {

    @Query("SELECT * from Articles")
    fun getTopHeadlines(): Flowable<List<ArticleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveTopHeadlines(newsList: List<ArticleEntity>): Completable

    @Query("Delete from Articles")
    fun deleteTopHeadlines(): Completable
}