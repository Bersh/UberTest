package com.example.myapplication.common

const val API_KEY = "LMX16pU76YvUUSHTDDUC4JZ6CZMzr31E"
const val ITEMS_PER_PAGE = 50
const val API_URL =
    "https://api.giphy.com/v1/gifs" //"?
    //"https://www.flickr.com/services/rest/?per_page=$ITEMS_PER_PAGE&api_key=$API_KEY&format=json&nojsoncallback=1"
const val METHOD_GET_RECENT = "trending"
const val DEFAULT_PARAMS = "api_key=$API_KEY&limit=$ITEMS_PER_PAGE"
const val METHOD_SEARCH = "search"
const val URL_GET_RECENT = "$API_URL/$METHOD_GET_RECENT?$DEFAULT_PARAMS"
const val URL_SEARCH = "$API_URL/$METHOD_SEARCH?$DEFAULT_PARAMS"
const val TIMEOUT = 30000