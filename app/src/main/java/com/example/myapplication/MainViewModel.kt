package com.example.myapplication

import android.app.Application
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.model.FlickrPhoto
import com.example.myapplication.model.repository.PhotosRepositoryImpl

class MyViewModel(app: Application) : AndroidViewModel(app) {

    //To avoid publishing MutableLiveData we will hide it and publish wrapping LiveData instead
    private val imagesState = MutableLiveData<List<FlickrPhoto>>()
    private val page = MutableLiveData<Int>().apply { value = 1 }
    private val pagesCount = MutableLiveData<Int>()
    private val userMessageState = MutableLiveData<String>()
    private val isLoadingState = MutableLiveData<Boolean>().apply { value = false }
    private val currentQuery = MutableLiveData<String>().apply { value = "" }

    @VisibleForTesting
    var photosRepository = PhotosRepositoryImpl()

    val images: LiveData<List<FlickrPhoto>> get() = imagesState
    val isLoading: LiveData<Boolean> get() = isLoadingState //TODO use this to show progress bar if needed
    val userMessage: LiveData<String> get() = userMessageState

    fun getDefaultImages() {
        currentQuery.value = ""
        photosRepository.getDefaultPhotos(
            page.value ?: 1, imagesState, pagesCount, userMessageState, isLoadingState
        )
    }

    fun getImagesForQuery(searchQuery: String) {
        if (!currentQuery.value.equals(searchQuery)) {
            page.value = 1
            photosRepository.invalidateData()
        }
        currentQuery.value = searchQuery
        photosRepository.getPhotosForQuery(
            searchQuery, page.value ?: 1, imagesState, pagesCount, userMessageState, isLoadingState
        )
    }

    fun loadNextPage() {
        if (isLoadingState.value == true) { //loading in progress
            return
        }
        var currentPage = page.value ?: 1
        if (pagesCount.value ?: 1 > currentPage) {
            page.value = ++currentPage
            photosRepository.getPhotosForQuery(
                currentQuery.value ?: "", currentPage,
                imagesState, pagesCount, userMessageState, isLoadingState
            )

        } else {
            userMessageState.value = getApplication<Application>().getString(R.string.txt_all_pages_loaded)
            isLoadingState.value = false
        }
    }
}