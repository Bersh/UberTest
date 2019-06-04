package com.example.myapplication

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

//Flikr
//Key: 579a5c4bdbc096b04e86a08e2b4781c0
//Secret: cde07b5bb4d02e9a

const val COLUMN_COUNT = 3
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rv_images.layoutManager = GridLayoutManager(this, COLUMN_COUNT)
    }
}
