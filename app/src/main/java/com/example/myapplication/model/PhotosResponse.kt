package com.example.myapplication.model

import java.io.Serializable

data class PhotosResponse(var pagination: PaginationInfo, var data: List<GifData> = ArrayList()) : Serializable {
}