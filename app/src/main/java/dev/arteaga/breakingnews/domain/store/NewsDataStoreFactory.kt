package dev.arteaga.breakingnews.domain.store


class NewsDataStoreFactory(
    private val newsCacheDataStore: NewsCacheDataStore,
    private val newsRemoteDataStore: NewsRemoteDataStore
) {

    fun getDataSearch(): NewsDataStore {
        return newsRemoteDataStore

    }

    fun getDataStore(isNetworkAvailable: Boolean, projectCached: Boolean): NewsDataStore {
        return if (!isNetworkAvailable && projectCached)
            newsCacheDataStore
        else newsRemoteDataStore
    }

    fun getCacheDataStore(): NewsDataStore {
        return newsCacheDataStore
    }
}