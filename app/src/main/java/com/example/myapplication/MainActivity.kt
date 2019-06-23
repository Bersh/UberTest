package com.example.myapplication

import android.os.Bundle
import android.view.KeyEvent
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo
import android.widget.TextView
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

    private val viewModel: MainViewModel by lazy {
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        layoutManager = GridLayoutManager(this, COLUMN_COUNT)
        rvImages.layoutManager = layoutManager

        viewModel.getDefaultImages()
        rvImages.adapter = adapter
        viewModel.images.observe(this,
            Observer<Collection<FlickrPhoto>> { images ->
                if (images != null) {
                    adapter.flickrPhotoList = ArrayList(images)
                } else {
                    adapter.flickrPhotoList = ArrayList()
                }
            })

        viewModel.userMessage.observe(this, Observer<String> { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.isLoading.observe(this, Observer<Boolean> { isLoading ->
            progress.visibility = if (isLoading) VISIBLE else GONE
        })

        editSearch.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    v?.let { viewModel.getImagesForQuery(it.text.toString()) }
                    return true
                }
                return false
            }
        })

        rvImages.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    val totalItemCount = layoutManager.itemCount
                    val lastVisiblePosition = layoutManager.findLastVisibleItemPosition() + 1
                    if (lastVisiblePosition >= totalItemCount) {
                        viewModel.loadNextPage()
                    }
                }
            }
        })
    }
}
