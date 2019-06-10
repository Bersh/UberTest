package com.example.myapplication

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.model.FlickrPhoto
import com.example.myapplication.model.repository.IPhotosRepository
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import junit.framework.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class MainViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Test
    fun testDefaultImagesLoading() {
        val applicationMock = Mockito.mock(Application::class.java)
        val viewModel = MainViewModel(applicationMock)
        val photosRepo = Mockito.mock(IPhotosRepository::class.java)
        viewModel.photosRepository = photosRepo

        viewModel.getDefaultImages()
        verify(photosRepo).getDefaultPhotos(eq(1), any(), any(), any(), any())
    }

    @Test
    fun testLoadNextPage() {
        val applicationMock = Mockito.mock(Application::class.java)
        val viewModel = MainViewModel(applicationMock)
        val photosRepo = Mockito.mock(IPhotosRepository::class.java)
        viewModel.photosRepository = photosRepo
        viewModel.pagesCount.value = 10
        val query = "aaa"
        viewModel.currentQuery.value = query
        viewModel.page.value = 1

        viewModel.loadNextPage()
        verify(photosRepo).getPhotosForQuery(eq(query), eq(2), any(), any(), any(), any())

        viewModel.loadNextPage()
        verify(photosRepo).getPhotosForQuery(eq(query), eq(3), any(), any(), any(), any())
    }

    @Test
    fun testLoadImagesByQuery() {
        val applicationMock = Mockito.mock(Application::class.java)
        val viewModel = MainViewModel(applicationMock)
        val photosRepo = Mockito.mock(IPhotosRepository::class.java)
        viewModel.photosRepository = photosRepo
        viewModel.pagesCount.value = 10
        viewModel.page.value = 1
        val query = "aaa"

        viewModel.getImagesForQuery(query)
        verify(photosRepo).getPhotosForQuery(eq(query), eq(1), any(), any(), any(), any())

        viewModel.loadNextPage()
        verify(photosRepo).getPhotosForQuery(eq(query), eq(2), any(), any(), any(), any())
    }

    @Test
    fun testNoMoreData() {
        val applicationMock = Mockito.mock(Application::class.java)
        val viewModel = MainViewModel(applicationMock)
        val photosRepo = Mockito.mock(IPhotosRepository::class.java)
        viewModel.photosRepository = photosRepo

        viewModel.loadNextPage()
        verify(applicationMock).getString(R.string.txt_all_pages_loaded)
    }
}