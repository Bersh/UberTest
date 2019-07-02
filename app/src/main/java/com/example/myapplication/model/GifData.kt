package com.example.myapplication.model

import com.google.gson.annotations.SerializedName

data class GifData(
    var id: String,
    var images: ImagesInfo
) {

    val imageURL: String
        get() {
            return images.preview_gif.url
        }
}

/*
preview_gif": {
                    "url": "https://media1.giphy.com/media/BM10SaUSoT789SJkvf/giphy-preview.gif?cid=258b00025d11ff6445684d5851338c5c&rid=giphy-preview.gif",
                    "width": "124",
                    "height": "95",
                    "size": "49923"
                },
 */
