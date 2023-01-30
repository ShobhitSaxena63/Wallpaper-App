package com.shobhit63.wallpaperworld.ui

import android.Manifest
import android.app.DownloadManager
import android.app.WallpaperManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.snackbar.Snackbar
import com.shobhit63.wallpaperworld.R
import com.shobhit63.wallpaperworld.data.SetOptions
import com.shobhit63.wallpaperworld.data.Wallpapers
import com.shobhit63.wallpaperworld.databinding.FragmentDetailBinding
import timber.log.Timber
import java.io.File
import java.io.IOException


class Detail : Fragment(){
    private lateinit var _binding:FragmentDetailBinding
    private val binding get() = _binding
    private  lateinit var viewModel:DetailViewModel
    private lateinit var downloadManager: DownloadManager
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    //    private var mListener:ItemClickListener?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id:Long = DetailArgs.fromBundle(requireArguments()).id
        val viewModelFactory = DetailViewModelFactory(id,requireActivity().application)
        viewModel = ViewModelProvider(this,viewModelFactory)[DetailViewModel::class.java]

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDetailBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.wallpapers.observe(viewLifecycleOwner) {
            setData(it)
        }
        binding.downloadBtn.setOnClickListener {
            val id:Long = DetailArgs.fromBundle(requireArguments()).id
//            val bottomSheetFragment: BottomSheetDialogFragment = ActionBottomDialogFragment()
//            bottomSheetFragment.show(requireActivity().supportFragmentManager, bottomSheetFragment.tag)
            findNavController().navigate(DetailDirections.actionDetailToBottomOptionsFragment(id))
        }
        //save to gallery
        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission())
        { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.
                viewModel.wallpapers.observe(viewLifecycleOwner) {
                    startDownload(it.src.portrait, it.alt)
                }
//                wallpaper.photographer?.let { startDownload(wallpaper.src.portrait, it) }
            } else {
                // Explain to the user that the feature is unavailable because the
                // feature requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
                Toast.makeText(requireActivity(), "Permission Denied",
                    Toast.LENGTH_LONG).show()
            }
        }


    }

    private fun startDownload(photoPath: String,fileName: String) {
        try{
            downloadManager = requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val request = DownloadManager.Request(Uri.parse(photoPath))
            request.apply {
                setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
                setTitle(fileName)
                setDescription("Download Manager")
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, File.separator+fileName+".jpg")
                downloadManager.enqueue(this)
            }
            Snackbar.make(binding.root, "Wallpaper Save to Gallery", Snackbar.LENGTH_SHORT).show()
        }catch (exception:Exception){
            Timber.e("Download Exception $exception ")
        }

    }
    private fun downloadFile(view: View,photoPath: String,fileName:String) {
        if(ActivityCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            startDownload(photoPath,fileName)
        } else {
            requestWritePermission(view)
        }
    }
    private fun requestWritePermission(view: View) {
        if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            val snack = Snackbar.make(view,"Please accept this to download file",Snackbar.LENGTH_INDEFINITE)
            snack.setAction("OK") {
//                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
//                PERMISSION_REQUEST_WRITE)
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
            snack.show()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

    }

    private fun setData(wallpaper: Wallpapers) {
        binding.view.setBackgroundColor(Color.parseColor(wallpaper.avg_color))
        binding.photographerName.text = getString(R.string.photographer_name,wallpaper.photographer)
        Glide.with(requireActivity())
            .load(wallpaper.src.portrait)
            .error(R.drawable.error_image)
            .into(binding.wallpaperImageView)

        // if user come from bottom dialog to detail then perform action acc. to user selection
        //pass the photo url only
        performOperationAccToOption(wallpaper.src.portrait, wallpaper.alt)

    }
    private fun performOperationAccToOption(photoPath: String,fileName: String){
        //buttons clicks from bottomSheet

        when(DetailArgs.fromBundle(requireArguments()).clickOn){
            SetOptions.HomeScreen.name -> {
//                Toast.makeText(requireActivity(),"SET WALLPAPER IS SELECTED",Toast.LENGTH_SHORT).show()
                setWallpaper(photoPath,SetOptions.HomeScreen)
            }
            SetOptions.LockScreen.name -> {
//                Toast.makeText(requireActivity(), "SET LOCK SCREEN IS SELECTED", Toast.LENGTH_SHORT).show()
                setWallpaper(photoPath,SetOptions.LockScreen)
            }
            SetOptions.Both.name -> {
//                Toast.makeText(requireActivity(), "SET BOTH IS SELECTED", Toast.LENGTH_SHORT).show()
                setWallpaper(photoPath,SetOptions.Both)
            }
            SetOptions.Save.name -> {
//                Toast.makeText(requireActivity(), "SAVE TO GALLERY IS SELECTED", Toast.LENGTH_SHORT).show()
                downloadFile(binding.root,photoPath,fileName)
            }

        }
    }

    private fun setWallpaper(photoPath:String,setOptions: SetOptions){
        val wallpaperManager = WallpaperManager.getInstance(requireActivity())
        Glide.with(requireContext())
            .asBitmap()
            .load(photoPath)
            .into(object : CustomTarget<Bitmap?>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap?>?
                ) {
                    try {
                        when(setOptions){
                            SetOptions.HomeScreen -> {
                                wallpaperManager.setBitmap(resource, null, true, WallpaperManager.FLAG_SYSTEM)
                                Snackbar.make(binding.root, "Wallpaper Set to Home Screen", Snackbar.LENGTH_SHORT).show()
                            }
                            SetOptions.LockScreen -> {
                                wallpaperManager.setBitmap(resource, null, true, WallpaperManager.FLAG_LOCK)
                                Snackbar.make(binding.root, "Wallpaper Set to Lock Screen ", Snackbar.LENGTH_SHORT).show()
                            }
                            SetOptions.Both -> {
                                wallpaperManager.setBitmap(resource, null, true, WallpaperManager.FLAG_LOCK or  WallpaperManager.FLAG_SYSTEM)
                                Snackbar.make(binding.root, "Wallpaper Set to Both Screens", Snackbar.LENGTH_SHORT).show()
                            }

                            else -> { wallpaperManager.setBitmap(resource) }
                        }
//
                    } catch (e: IOException) {
                        Toast.makeText(requireActivity(),"not set due to $e",Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    TODO("Not yet implemented")
                }
            })
    }

}