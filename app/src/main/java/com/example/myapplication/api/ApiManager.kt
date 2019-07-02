package com.example.myapplication.api

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.TextUtils
import android.util.Log
import androidx.annotation.WorkerThread
import com.example.myapplication.common.ITEMS_PER_PAGE
import com.example.myapplication.common.TIMEOUT
import com.example.myapplication.common.URL_GET_RECENT
import com.example.myapplication.common.URL_SEARCH
import com.example.myapplication.model.PhotosResponse
import com.example.myapplication.model.Result
import com.example.myapplication.model.repository.PhotosRepositoryImpl
import com.google.gson.Gson
import org.json.JSONException
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL

object ApiManager {
    //    private val jsonParser: SearchImagesApiJSONParser = SearchImagesApiJSONParser()
    private val gson = Gson()

    @WorkerThread
    fun fetchPhotos(searchText: String, pageNo: Int): Result<Exception, PhotosResponse?> {
        return Result.build { gson.fromJson(fetchPhotosInfo(searchText, pageNo), PhotosResponse::class.java) }
    }

    @WorkerThread
    fun getBitmap(url: String): Bitmap? {
        val imageUrl = URL(url)
        val conn = imageUrl.openConnection() as HttpURLConnection
        conn.requestMethod = "GET"
        val bmOptions = BitmapFactory.Options()
        bmOptions.inSampleSize = 1
        conn.connectTimeout = TIMEOUT
        conn.readTimeout = TIMEOUT
        conn.instanceFollowRedirects = true
        conn.doInput = true
        conn.connect()
        try {
            return BitmapFactory.decodeStream(conn.inputStream, null, bmOptions)
        } finally {
            conn.inputStream.close()
        }
    }

    @Throws(JSONException::class)
    private fun fetchPhotosInfo(searchQuery: String, pageNo: Int): String {
        val offset = ITEMS_PER_PAGE * pageNo
        val url =
            if (TextUtils.isEmpty(searchQuery))
                URL("$URL_GET_RECENT&offset=&offset=$offset")
            else
                URL("$URL_SEARCH&q=$searchQuery&offset=$offset")
        val conn = url.openConnection() as HttpURLConnection
        conn.connectTimeout = TIMEOUT
        conn.readTimeout = TIMEOUT
        conn.requestMethod = "GET"
        try {
            val inputStream = BufferedInputStream(conn.inputStream)

            val result = inputStream.bufferedReader().use(BufferedReader::readText)

            Log.d(PhotosRepositoryImpl.GetImagesAsyncTask::class.java.name, "REQUEST result = $result")

            return result
        } finally {
            conn.disconnect()
        }
    }

}