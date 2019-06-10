package com.example.myapplication

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.myapplication.model.repository.IPhotosRepository
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
    }
}