package com.example.videoplayer.repository


import android.content.Context
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.videoplayer.model.Video
import com.example.videoplayer.roomDb.VideoDatabase


class VideoRepository(val context: Context,private val videoDatabase: VideoDatabase) {
    val videoLiveData = MutableLiveData<List<Video>>()
    fun getVideos() {
        val videos = mutableListOf<Video>()
        val projection = arrayOf(
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DATA
        )
        val selection = "${MediaStore.Video.Media.BUCKET_DISPLAY_NAME}=?"
        val selectionArgs = arrayOf("WhatsApp Video")
        val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"
        val cursor = context.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection, selectionArgs, sortOrder
        )
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val title =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME))
                val image =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA))
                videos.add(Video(0,title, image))
            } while (cursor.moveToNext())
            cursor.close()
        }
        videoLiveData.postValue(videos)
    }

    fun getVideosLiveData(): LiveData<List<Video>> {
        return videoLiveData
    }

    //
    fun getallMusic():LiveData<List<Video>>{
        return videoDatabase.getMusicDao().getAllMusic()
    }


    suspend fun insertmusic(video: Video){
        videoDatabase.getMusicDao().insert(video)
    }

    fun deleteMusic(video: Video){
        videoDatabase.getMusicDao().deletemusic(video)
    }

    fun search(name:String):LiveData<List<Video>>{
        return  videoDatabase.getMusicDao().search(name)
    }

}

