package com.example.myapplication.model

import java.io.Serializable

data class PhotosResponse(var pages: Int, var flickrPhotoList: List<FlickrPhoto> = ArrayList()) : Serializable {
}