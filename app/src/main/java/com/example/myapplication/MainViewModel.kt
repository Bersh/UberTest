package com.example.myapplication

import android.app.Application
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.model.FlickrPhoto
import com.example.myapplication.model.repository.IPhotosRepository
import com.example.myapplication.model.repository.PhotosRepositoryImpl

class MainViewModel(app: Application) : AndroidViewModel(app) {
    @VisibleForTesting
    val currentQuery = MutableLiveData<String>().apply { value = "" }
    @VisibleForTesting
    val userMessage = MutableLiveData<String>()
    @VisibleForTesting
    var photosRepository: IPhotosRepository = PhotosRepositoryImpl()
    @VisibleForTesting
    val isLoading = MutableLiveData<Boolean>().apply { value = false }
    @VisibleForTesting
    val images = MutableLiveData<List<FlickrPhoto>>()
    @VisibleForTesting
    val page = MutableLiveData<Int>().apply { value = 1 }
    @VisibleForTesting
    val pagesCount = MutableLiveData<Int>()

    fun getDefaultImages() {
        currentQuery.value = ""
        photosRepository.getDefaultPhotos(
            page.value ?: 1, images, pagesCount, userMessage, isLoading
        )
    }

    fun getImagesForQuery(searchQuery: String) {
        if (!currentQuery.value.equals(searchQuery)) {
            page.value = 1
            photosRepository.invalidateData()
        }
        currentQuery.value = searchQuery
        photosRepository.getPhotosForQuery(
            searchQuery, page.value ?: 1, images, pagesCount, userMessage, isLoading
        )
    }

    fun loadNextPage() {
        if (isLoading.value == true) { //loading in progress
            return
        }
        var currentPage = page.value ?: 1
        if (pagesCount.value ?: 1 > currentPage) {
            page.value = ++currentPage
            photosRepository.getPhotosForQuery(
                currentQuery.value ?: "", currentPage,
                images, pagesCount, userMessage, isLoading
            )
        } else {
            userMessage.value = getApplication<Application>().getString(R.string.txt_all_pages_loaded)
            isLoading.value = false
        }
    }
}