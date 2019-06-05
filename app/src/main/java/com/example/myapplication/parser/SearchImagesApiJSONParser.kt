package com.example.myapplication.parser

import com.example.myapplication.model.FlickrPhoto
import com.example.myapplication.model.PhotosResponse
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

const val KEY_PHOTOS_JSON = "photos"
const val KEY_STAT = "stat"
const val KEY_OK = "ok"
const val KEY_PAGES = "pages"
const val KEY_PHOTOS_ARRAY = "photo"
const val KEY_PHOTO_ID = "id"
const val KEY_OWNER = "owner"
const val KEY_SECRET = "secret"
const val KEY_SERVER = "server"
const val KEY_FARM = "farm"
const val KEY_TITLE = "title"

class SearchImagesApiJSONParser {

    fun parseSearchListResponse(jsonObject: JSONObject): PhotosResponse? {
        try {
            if (jsonObject.has(KEY_STAT)) {
                if (jsonObject.getString(KEY_STAT).contentEquals(KEY_OK)) {
                    val resultObject = jsonObject.getJSONObject(KEY_PHOTOS_JSON)
                    val jsonArray = resultObject.getJSONArray(KEY_PHOTOS_ARRAY)
                    return PhotosResponse(
                        resultObject.getInt(KEY_PAGES),
                        parseSearchArray(jsonArray)
                    )
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return null
    }

    private fun parseSearchArray(jsonArray: JSONArray): List<FlickrPhoto> {
        val flickrPhotoList = ArrayList<FlickrPhoto>()
        try {
            for (i in 0 until jsonArray.length()) {
                val jsonObj = jsonArray.getJSONObject(i)
                val flickrPhoto = FlickrPhoto(
                    jsonObj.getString(KEY_PHOTO_ID),
                    jsonObj.getString(KEY_OWNER),
                    jsonObj.getString(KEY_SECRET),
                    jsonObj.getString(KEY_SERVER),
                    jsonObj.getInt(KEY_FARM),
                    jsonObj.getString(KEY_TITLE)
                )
                flickrPhotoList.add(flickrPhoto)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return flickrPhotoList
    }
}