package com.arteaga.breaking.news.factory

import androidx.room.Database
import androidx.room.RoomDatabase
import com.arteaga.breaking.news.cache.dao.ArticlesDao
import dev.arteaga.breakingnews.cache.model.ArticleEntity


@Database(entities = [ArticleEntity::class], version = 1, exportSchema = false)
abstract class NewsDatabase : RoomDatabase() {

    abstract fun articlesDao(): ArticlesDao

}