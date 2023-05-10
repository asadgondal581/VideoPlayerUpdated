package com.example.videoplayer.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.videoplayer.adapter.VideoAdapter
import com.example.videoplayer.databinding.FragmentGridViewBinding
import com.example.videoplayer.interfaces.onItemClick
import com.example.videoplayer.model.Video
import com.example.videoplayer.repository.VideoRepository
import com.example.videoplayer.roomDb.VideoDatabase
import com.example.videoplayer.viewModel.VideoViewModel
import com.example.videoplayer.viewModelFactory.MediaFectory


class GridViewFragment : Fragment(), onItemClick {


    private var binding: FragmentGridViewBinding? = null
    private var gridView: GridView? = null
    private var imagesViewModel: VideoViewModel? = null
    private var recyclerView: RecyclerView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            FragmentGridViewBinding.inflate(LayoutInflater.from(requireContext()), container, false)
       // gridView = binding?.gridView
        recyclerView = binding?.recyclerView
        val db = VideoDatabase.getDataBase(requireContext())
        try {
            val fileRepository = VideoRepository(requireContext(),db)
            imagesViewModel = ViewModelProvider(
                this,
                MediaFectory(fileRepository)
            ).get(VideoViewModel::class.java)
            imagesViewModel?.getVideos()
           // recyclerView?.layoutManager = GridLayoutManager(requireContext())
            imagesViewModel?.getVideoLiveData()?.observe(requireActivity(), Observer {
              //  recyclerView?.adapter = VideoAdapter(requireContext(), it,this)
            })
        } catch (e: java.lang.Exception) {
            Toast.makeText(requireContext(), "Error" + e.message, Toast.LENGTH_SHORT).show()
        }


        return binding?.root
    }

    override fun onVideoClick(video: Video) {
        TODO("Not yet implemented")
    }

    override fun onTitleClick(video: Video) {
        TODO("Not yet implemented")
    }

    override fun onShareImgVClick(video: Video) {
        TODO("Not yet implemented")
    }

    override fun onDeleteImgVClick(video: Video) {
        TODO("Not yet implemented")
    }

    override fun onLongClickListener(video: Video) {
        TODO("Not yet implemented")
    }


}