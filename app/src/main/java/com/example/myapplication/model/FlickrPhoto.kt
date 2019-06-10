package com.example.myapplication.model

data class FlickrPhoto(
    var id: Long,
    var owner: String,
    var secret: String,
    var server: String,
    var farm: Int,
    var title: String
) {

    val imageURL: String
        get() {
            return "https://farm$farm.static.flickr.com/$server/${id}_$secret.jpg"
        }
}
