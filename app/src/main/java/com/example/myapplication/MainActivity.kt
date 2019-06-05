package com.example.myapplication

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.adapter.ImagesAdapter
import com.example.myapplication.model.FlickrPhoto
import kotlinx.android.synthetic.main.activity_main.*

const val COLUMN_COUNT = 3

class MainActivity : AppCompatActivity() {
    private var adapter = ImagesAdapter()
    private lateinit var layoutManager: GridLayoutManager

    private val viewModel: MyViewModel by lazy {
        ViewModelProviders.of(this).get(MyViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        layoutManager = GridLayoutManager(this, COLUMN_COUNT)
        rvImages.layoutManager = layoutManager

        viewModel.getDefaultImages()
        rvImages.adapter = adapter
        viewModel.images.observe(this,
            Observer<List<FlickrPhoto>> { images ->
                images?.let {
                    adapter.flickrPhotoList = it
                }
            })

        viewModel.userMessage.observe(this, Observer<String> { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        })

        rvImages.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    val totalItemCount = layoutManager.itemCount
                    val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()
                    if (lastVisiblePosition >= (totalItemCount - (COLUMN_COUNT * 2))) {
                        viewModel.loadNextPage()
                    }
                }
            }
        })
    }
}
