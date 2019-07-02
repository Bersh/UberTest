package com.example.myapplication.model.repository

import androidx.lifecycle.MutableLiveData
import com.example.myapplication.model.GifData

interface IPhotosRepository {
    /**
     * This method load requested photos asynchronously and deliver results through supplied live datas
     * This operation is not blocking.
     *
     * @param searchQuery user entered search query
     * @param pageNo current page number
     * @param imagesData LiveData to return loaded images
     * @param pagesCountData LiveData to return total pagination count
     * @param userMessageData LiveData to return user message(used in case of error)
     * @param isLoadingData LiveData to handle loading state changes. Will be true while loading opration is in progress
     *
     * @return All data returned via corresponding LiveData instances
     */
    fun getPhotosForQuery(
        searchQuery: String,
        pageNo: Int,
        imagesData: MutableLiveData<Collection<GifData>>,
        pagesCountData: MutableLiveData<Int>,
        userMessageData: MutableLiveData<String>,
        isLoadingData: MutableLiveData<Boolean>
    )

    /**
     * This method load requested photos asynchronously and deliver results through supplied live datas.
     * This operation is not blocking.
     *
     * @param pageNo current page number
     * @param imagesData LiveData to return loaded images
     * @param pagesCountData LiveData to return total pagination count
     * @param userMessageData LiveData to return user message(used in case of error)
     * @param isLoadingData LiveData to handle loading state changes. Will be true while loading opration is in progress
     *
     * @return All data returned via corresponding LiveData instances
     */
    fun getDefaultPhotos(
        pageNo: Int,
        imagesData: MutableLiveData<Collection<GifData>>,
        pagesCountData: MutableLiveData<Int>,
        userMessageData: MutableLiveData<String>,
        isLoadingData: MutableLiveData<Boolean>
    )

    /**
     * Erase all previously loaded data
     */
    fun invalidateData()
}