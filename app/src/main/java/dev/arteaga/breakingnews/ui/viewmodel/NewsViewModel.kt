package dev.arteaga.breakingnews.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.arteaga.breakingnews.domain.usecase.NewsDetailsUseCase
import dev.arteaga.breakingnews.remote.NewsDetails
import dev.arteaga.breakingnews.ui.Resource
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo


class NewsDetailsViewModel(
    private val newsDetailsUseCase: NewsDetailsUseCase
) : ViewModel() {

    private val disposables = CompositeDisposable()

    fun getArticlesEverything(query: String): LiveData<Resource<NewsDetails>> {

        val result = MutableLiveData<Resource<NewsDetails>>()
        result.value = Resource.Loading()

        val disposable = newsDetailsUseCase.getQueryEverything(query).subscribe(
            { newsDetails ->
                result.value = Resource.Success(newsDetails)
            },
            { onError ->
                result.value = Resource.Failure(onError)
            }
        )
        disposable.addTo(disposables)

        return result
    }

    fun getTopHeadlines(category: String): LiveData<Resource<NewsDetails>> {
        val result = MutableLiveData<Resource<NewsDetails>>()
        result.value = Resource.Loading()

        val disposable = newsDetailsUseCase.getTopHeadlines(category).subscribe(
            { newsDetails ->
                result.value = Resource.Success(newsDetails)
            },
            { onError ->
                result.value = Resource.Failure(onError)
            }
        )
        disposable.addTo(disposables)

        return result
    }


    override fun onCleared() {
        disposables.dispose()
        super.onCleared()
    }
}