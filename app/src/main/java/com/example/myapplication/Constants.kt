package com.example.myapplication



const val API_KEY = "579a5c4bdbc096b04e86a08e2b4781c0"
const val ITEMS_PER_PAGE = 50
const val API_URL = "https://www.flickr.com/services/rest/?per_page=$ITEMS_PER_PAGE&api_key=$API_KEY&format=json&nojsoncallback=1"
const val METHOD_GET_RECENT = "flickr.photos.getRecent"
const val METHOD_PHOTOS_SEARCH = "flickr.photos.search"
const val URL_GET_RECENT = "$API_URL&method=$METHOD_GET_RECENT"
const val URL_SEARCH = "$API_URL&method=$METHOD_PHOTOS_SEARCH"
const val TIMEOUT = 30000