package com.example.myapplication.model.repository

import android.os.AsyncTask
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.common.TIMEOUT
import com.example.myapplication.common.URL_GET_RECENT
import com.example.myapplication.common.URL_SEARCH
import com.example.myapplication.model.FlickrPhoto
import com.example.myapplication.model.PhotosResponse
import com.example.myapplication.model.Result
import com.example.myapplication.parser.SearchImagesApiJSONParser
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.URL

class PhotosRepositoryImpl : IPhotosRepository {
    private val jsonParser = SearchImagesApiJSONParser()
    private val currentPhotosList = ArrayList<FlickrPhoto>()

    override fun getPhotosForQuery(
        searchQuery: String, pageNo: Int,
        imagesData: MutableLiveData<List<FlickrPhoto>>,
        pagesCountData: MutableLiveData<Int>,
        userMessageData: MutableLiveData<String>,
        isLoadingData: MutableLiveData<Boolean>
    ) {
        GetImagesAsyncTask(
            searchQuery, pageNo, jsonParser, currentPhotosList,
            WeakReference(imagesData), WeakReference(pagesCountData),
            WeakReference(userMessageData), WeakReference(isLoadingData)
        ).execute()
    }

    override fun getDefaultPhotos(
        pageNo: Int,
        imagesData: MutableLiveData<List<FlickrPhoto>>,
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
        private val jsonParser: SearchImagesApiJSONParser,
        private val currentPhotosList: MutableList<FlickrPhoto>,
        //These WeakReferences should not be needed as all LiveData is stored in ViewModel which survives configuration changes
        //but let's be extra careful
        private val imagesData: WeakReference<MutableLiveData<List<FlickrPhoto>>>,
        private val pagesCountData: WeakReference<MutableLiveData<Int>>,
        private val userMessageData: WeakReference<MutableLiveData<String>>,
        private val isLoadingData: WeakReference<MutableLiveData<Boolean>>
    ) : AsyncTask<Unit, Unit, Result<Exception, PhotosResponse?>>() {

        override fun onPreExecute() {
            isLoadingData.get()?.value = true
        }

        override fun doInBackground(vararg params: Unit): Result<Exception, PhotosResponse?> {
            return Result.build { jsonParser.parseSearchListResponse(fetchPhotos(searchText, pageNo)) }
        }

        override fun onPostExecute(result: Result<Exception, PhotosResponse?>) {
            when (result) {
                is Result.Value -> result.value?.let {
                    currentPhotosList.addAll(it.flickrPhotoList)
                    imagesData.get()?.value = currentPhotosList
                    pagesCountData.get()?.value = it.pages
                    Log.d(GetImagesAsyncTask::class.java.name, "Page $pageNo loaded. Total pages: ${it.pages}")
                }
                is Result.Error -> userMessageData.get()?.value = result.error.message //TODO can be more user friendly
            }

            isLoadingData.get()?.value = false

        }

        @Throws(JSONException::class)
        private fun fetchPhotos(searchQuery: String, pageNo: Int): JSONObject {
            val url =
                if (TextUtils.isEmpty(searchQuery))
                    URL("$URL_GET_RECENT&page=$pageNo")
                else
                    URL("$URL_SEARCH&text=$searchQuery&page=$pageNo")
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = TIMEOUT
            conn.readTimeout = TIMEOUT
            conn.requestMethod = "GET"
            try {
                val inputStream = BufferedInputStream(conn.inputStream)

                val result = inputStream.bufferedReader().use(BufferedReader::readText)

                Log.d(GetImagesAsyncTask::class.java.name, "REQUEST result = $result")

                return JSONObject(result)
            } catch (e: JSONException) {
                e.printStackTrace()
                throw e
            } finally {
                conn.disconnect()
            }
        }
    }
}