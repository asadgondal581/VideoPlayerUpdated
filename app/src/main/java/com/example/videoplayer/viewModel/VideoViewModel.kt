package com.example.videoplayer.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.videoplayer.model.Video
import com.example.videoplayer.repository.VideoRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class VideoViewModel(private val videoRepository: VideoRepository) : ViewModel() {

    val video: MutableLiveData<List<Video>> = videoRepository.videoLiveData
    val videordb: LiveData<List<Video>> = videoRepository.getallMusic()

    fun getVideos() {
        videoRepository.getVideos()
    }

    fun getVideoLiveData(): LiveData<List<Video>> {
        videoRepository.getVideosLiveData()
        return video
    }

    //room
    fun insertMusic(title: String, url: String) {
        GlobalScope.launch {
            val video = Video(null,title, url)
            videoRepository.insertmusic(video)
        }
    }

    fun deleteMusic(music: Video) {
        GlobalScope.launch {
            videoRepository.deleteMusic(music)
        }
    }

    fun search(name: String): LiveData<List<Video>> {
        return videoRepository.search(name)
    }
}