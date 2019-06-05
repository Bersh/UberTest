package com.example.myapplication

import android.content.ContentValues.TAG
import android.os.AsyncTask
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.model.FlickrPhoto
import com.example.myapplication.model.PhotosResponse
import com.example.myapplication.parser.SearchImagesApiJSONParser
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.security.NoSuchAlgorithmException

class MyViewModel : ViewModel() {
    private val photosList = ArrayList<FlickrPhoto>()
    val images = MutableLiveData<List<FlickrPhoto>>()
    val page = MutableLiveData<Int>().apply { value = 1 }
    val pagesCount = MutableLiveData<Int>()
    val userMessage = MutableLiveData<String>()
    val isLoading = MutableLiveData<Boolean>().apply { value = false }

    fun getDefaultImages() {
        GetImagesAsyncTask(
            "", page.value ?: 1, images, pagesCount, photosList,
            userMessage, isLoading
        ).execute()
    }

    fun loadNextPage() {
        if (isLoading.value == true) { //loading in progress
            return
        }
        //TODO handle search query
        if (pagesCount.value ?: 1 > page.value ?: 1) {
            GetImagesAsyncTask(
                "", (page.value ?: 1) + 1, images, pagesCount, photosList,
                userMessage, isLoading
            ).execute()
            page.value = (page.value ?: 1) + 1
        } else {
            //TODO no more items top load
        }
    }

    class GetImagesAsyncTask(
        private val searchText: String, private val pageNo: Int,
        private val imagesData: MutableLiveData<List<FlickrPhoto>>,
        private val pagesCountData: MutableLiveData<Int>,
        private val photosList: ArrayList<FlickrPhoto>,
        private val userMessageData: MutableLiveData<String>,
        private val isLoadingData: MutableLiveData<Boolean>
    ) : AsyncTask<Unit, Unit, PhotosResponse?>() {

        override fun onPreExecute() {
            isLoadingData.value = true
        }

        override fun doInBackground(vararg params: Unit): PhotosResponse? {
            var searchPhotosResponse: PhotosResponse? = null
            try {
                val jsonObject = getImages(searchText, pageNo)
                val jsonParser = SearchImagesApiJSONParser()
                searchPhotosResponse = jsonParser.parseSearchListResponse(jsonObject)

            } catch (e: ClassCastException) {
                e.printStackTrace()
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return searchPhotosResponse
        }

        override fun onPostExecute(result: PhotosResponse?) {
            userMessageData.postValue("Page $pageNo loaded") //TODO remove
            result?.let {
                photosList.addAll(it.flickrPhotoList)
                imagesData.postValue(photosList)
                pagesCountData.value = it.pages
                Log.d("Ilya", "Page $pageNo loaded. Total pages: ${it.pages}")
            }
            isLoadingData.value = false
        }

        private fun getImages(searchText: String, pageNo: Int): JSONObject {
            val url =
                if (TextUtils.isEmpty(searchText))
                    URL("$URL_GET_RECENT&page=$pageNo")
                else
                    URL("$URL_SEARCH&text$searchText&page=$pageNo")
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = TIMEOUT
            conn.readTimeout = TIMEOUT
            conn.requestMethod = "GET"
            try {
                val inputStream = BufferedInputStream(conn.inputStream)

                val result = readStream(inputStream)

                Log.d(TAG, "REQUEST result = $result")

                var jsonResult = JSONObject()
                try {
                    if (result.startsWith("{")) {
                        jsonResult = JSONObject(result)
                    } else { //JSON array
                        //TODO do something
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

                return jsonResult
            } catch (e: IOException) {
                e.printStackTrace()
                return JSONObject()
            } finally {
                conn.disconnect()
            }
        }

        @Throws(IOException::class)
        private fun readStream(inputStream: InputStream): String {
            return inputStream.bufferedReader().use(BufferedReader::readText)
        }
    }
}