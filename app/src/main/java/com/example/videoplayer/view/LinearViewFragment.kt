package com.example.videoplayer.view

import android.Manifest
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.videoplayer.R
import com.example.videoplayer.adapter.VideoAdapter
import com.example.videoplayer.databinding.FragmentLinearViewBinding
import com.example.videoplayer.databinding.RenameDialogBinding
import com.example.videoplayer.interfaces.onItemClick
import com.example.videoplayer.model.Video
import com.example.videoplayer.repository.VideoRepository
import com.example.videoplayer.roomDb.VideoDatabase
import com.example.videoplayer.viewModel.VideoViewModel
import com.example.videoplayer.viewModelFactory.MediaFectory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File


class LinearViewFragment : Fragment(), onItemClick, View.OnClickListener {

    private var binding: FragmentLinearViewBinding? = null
    private var recyclerView: RecyclerView? = null
    private var videoViewModel: VideoViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            FragmentLinearViewBinding.inflate(
                LayoutInflater.from(requireContext()),
                container,
                false
            )
        recyclerView = binding?.recyclerView
        requestPermissions()
        val db = VideoDatabase.getDataBase(requireContext())
        binding?.floatingActionButton?.setOnClickListener {
            findNavController().navigate(R.id.thirdFragment)
        }
        try {
            val fileRepository = VideoRepository(requireContext(), db)
            videoViewModel = ViewModelProvider(
                this,
                MediaFectory(fileRepository)
            ).get(VideoViewModel::class.java)
            videoViewModel?.getVideos()
            recyclerView?.layoutManager = LinearLayoutManager(requireContext())
            videoViewModel?.getVideoLiveData()?.observe(requireActivity(), Observer {
                recyclerView?.adapter = VideoAdapter(requireContext(), it, this)
            })
        } catch (e: java.lang.Exception) {
            Toast.makeText(requireContext(), "Error" + e.message, Toast.LENGTH_SHORT).show()
        }


        binding?.searchmusic?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    search(query)
                } else {
                    Toast.makeText(requireContext(), "No data Found", Toast.LENGTH_SHORT).show()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null)
                    search(newText)
                return true
            }
        })
        return binding?.root
    }

    companion object {
        const val PERMISSION_REQUEST_CODE = 100
    }

    //check permission
    private fun requestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, PERMISSION_REQUEST_CODE)
        }
    }

    //interfaces
    override fun onVideoClick(video: Video) {
        val player = VideoPlayerFragment.newInstance(video.url)
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.activity_main_nav_host_fragment, player)
            ?.addToBackStack(null)?.commit()
        // requireActivity().startService(Intent(this, VideoService::class.java))
        //startService(Intent(context, VideoService::class.java)
    }

    override fun onTitleClick(video: Video) {
        Toast.makeText(requireContext(), "Title Clicked!${video.title}", Toast.LENGTH_SHORT).show()
    }

    override fun onShareImgVClick(video: Video) {
        try {
            val title = video.title
            val url = video.url
            val video = Video(0, title, url)
            videoViewModel?.insertMusic(title, url)
            Toast.makeText(requireContext(), "Data inserted", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error" + e.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDeleteImgVClick(video: Video) {
        val filePath = video.url
        val isDeleted = deleteFileFromStorage2(filePath)
        if (isDeleted) {
            Toast.makeText(requireContext(), "${video.title} is Deleted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Error Occurred", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onLongClickListener(video: Video) {
        val customDialog = LayoutInflater.from(context).inflate(R.layout.rename_dialog, null)
        val bindingRF = RenameDialogBinding.bind(customDialog)
        val dialog = MaterialAlertDialogBuilder(requireContext()).setView(customDialog)
            .setCancelable(false)
            .setPositiveButton("Rename") { self, _ ->
                self.dismiss()
                val currenFile = File(video.url)
                val newName = bindingRF.renameField.text
                if (newName != null && currenFile.exists() && newName.toString()
                        .isNotEmpty()
                ) {
                    val newFile = File(
                        currenFile.parentFile,
                        newName.toString() + "." + currenFile.extension
                    )
                    Log.d("IsFileRename", "popUpMenu: $newName")

                    if (currenFile.renameTo(newFile)) {
                        Toast.makeText(context, newName.toString(), Toast.LENGTH_SHORT).show()
                        MediaScannerConnection.scanFile(
                            context,
                            arrayOf(newFile.toString()),
                            arrayOf("video/*"),
                            null
                        )
                        video.title = newFile.name
                        video.url = newFile.path

                    } else {
                        Toast.makeText(context, "Error Occurred", Toast.LENGTH_SHORT)
                            .show()
                    }

                } else {
                    Toast.makeText(context, "Access Denied", Toast.LENGTH_SHORT).show()
                }

            }
            .setNegativeButton("Cancel") { self, _ ->
                self.dismiss()
            }
            .create()
        dialog.show()
        bindingRF.renameField.text = SpannableStringBuilder(video.title)
    }

    private fun deleteFileFromStorage2(filePath: String): Boolean {
        val file = File(filePath)
        if (file.exists()) {
            return file.delete()
        }
        return false
    }

    fun search(name: String) {
        val searchtext = binding?.searchmusic
        try {
            videoViewModel?.search(name)?.observe(this, Observer {
                Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()

            })
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error Finding!! :" + e.message, Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onClick(view: View?) {
//        if (view === start) {
//            startService(Intent(this, VideoService::class.java))
//        } else if (view === stop) {
//
//            // stopping the service
//            stopService(Intent(this, VideoService::class.java))
//        }
    }
}