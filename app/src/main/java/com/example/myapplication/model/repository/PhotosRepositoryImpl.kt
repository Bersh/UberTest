package com.example.myapplication.model.repository

import android.os.AsyncTask
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.COLUMN_COUNT
import com.example.myapplication.api.ApiManager
import com.example.myapplication.model.FlickrPhoto
import com.example.myapplication.model.PhotosResponse
import com.example.myapplication.model.Result
import java.lang.ref.WeakReference

class PhotosRepositoryImpl : IPhotosRepository {
    private val currentPhotosList = LinkedHashSet<FlickrPhoto>()

    override fun getPhotosForQuery(
        searchQuery: String, pageNo: Int,
        imagesData: MutableLiveData<Collection<FlickrPhoto>>,
        pagesCountData: MutableLiveData<Int>,
        userMessageData: MutableLiveData<String>,
        isLoadingData: MutableLiveData<Boolean>
    ) {
        GetImagesAsyncTask(
            searchQuery, pageNo, currentPhotosList,
            WeakReference(imagesData), WeakReference(pagesCountData),
            WeakReference(userMessageData), WeakReference(isLoadingData)
        ).execute()
    }

    override fun getDefaultPhotos(
        pageNo: Int,
        imagesData: MutableLiveData<Collection<FlickrPhoto>>,
        pagesCountData: MutableLiveData<Int>,
        userMessageData: MutableLiveData<String>,
        isLoadingData: MutableLiveData<Boolean>
    ) {
        getPhotosForQuery("", pageNo, imagesData, pagesCountData, userMessageData, isLoadingData)
    }

    override fun invalidateData() {
        currentPhotosList.clear()
    }

    /**
     * This can be done better with coroutines. Not sure if it is considered a third-party library
     */
    class GetImagesAsyncTask(
        private val searchText: String,
        private val pageNo: Int,
        private val currentPhotosList: MutableCollection<FlickrPhoto>,
        //These WeakReferences should not be needed as all LiveData is stored in ViewModel which survives configuration changes
        //but let's be extra careful
        private val imagesData: WeakReference<MutableLiveData<Collection<FlickrPhoto>>>,
        private val pagesCountData: WeakReference<MutableLiveData<Int>>,
        private val userMessageData: WeakReference<MutableLiveData<String>>,
        private val isLoadingData: WeakReference<MutableLiveData<Boolean>>
    ) : AsyncTask<Unit, Unit, Result<Exception, PhotosResponse?>>() {
        private val apiManager = ApiManager

        override fun onPreExecute() {
            isLoadingData.get()?.value = true
        }

        override fun doInBackground(vararg params: Unit): Result<Exception, PhotosResponse?> {
            return apiManager.fetchPhotos(searchText, pageNo)
        }

        override fun onPostExecute(result: Result<Exception, PhotosResponse?>) {
            when (result) {
                is Result.Value -> result.value?.let {
                    currentPhotosList.addAll(it.flickrPhotoList)
                    var i = 1
                    while(currentPhotosList.size % COLUMN_COUNT != 0) {
                        currentPhotosList.remove(it.flickrPhotoList[it.flickrPhotoList.size - i++])
                    }
                    imagesData.get()?.value = currentPhotosList
                    pagesCountData.get()?.value = it.pages
                    Log.d(GetImagesAsyncTask::class.java.name, "Page $pageNo loaded. Total pages: ${it.pages}")
                }
                is Result.Error -> userMessageData.get()?.value = result.error.message
            }

            isLoadingData.get()?.value = false
        }
    }
}